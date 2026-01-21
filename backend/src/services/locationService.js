const Zone = require('../models/Zone');

class LocationService {
  constructor() {
    // Earth's radius in kilometers
    this.EARTH_RADIUS_KM = 6371;
  }

  // Calculate distance between two coordinates using Haversine formula
  calculateDistance(lat1, lon1, lat2, lon2) {
    const dLat = this.toRad(lat2 - lat1);
    const dLon = this.toRad(lon2 - lon1);

    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.toRad(lat1)) *
        Math.cos(this.toRad(lat2)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = this.EARTH_RADIUS_KM * c;

    return Math.round(distance * 100) / 100; // Round to 2 decimal places
  }

  toRad(degrees) {
    return degrees * (Math.PI / 180);
  }

  // Calculate estimated delivery time based on distance
  calculateDeliveryTime(distanceKm) {
    // Average speed: 25 km/h in Dhaka traffic
    const avgSpeedKmH = 25;
    const prepTime = 15; // minutes for food preparation

    const travelTimeMinutes = Math.ceil((distanceKm / avgSpeedKmH) * 60);
    const totalTime = prepTime + travelTimeMinutes;

    // Add buffer time (10-15 minutes)
    const minTime = totalTime;
    const maxTime = totalTime + 15;

    return {
      min: minTime,
      max: maxTime,
      formatted: `${minTime}-${maxTime} min`,
    };
  }

  // Calculate delivery fee based on distance
  calculateDeliveryFee(distanceKm, zone = null) {
    // Default fee structure (in paisa)
    const baseFee = zone?.deliveryFee || 3000; // ৳30
    const perKmFee = zone?.perKmFee || 1000; // ৳10 per km
    const minFee = 3000; // ৳30 minimum
    const maxFee = 15000; // ৳150 maximum

    // Calculate fee
    let fee = baseFee + Math.ceil(distanceKm) * perKmFee;

    // Apply min/max constraints
    fee = Math.max(fee, minFee);
    fee = Math.min(fee, maxFee);

    return {
      amount: fee,
      formatted: `৳${(fee / 100).toFixed(0)}`,
      breakdown: {
        base: baseFee,
        distance: Math.ceil(distanceKm) * perKmFee,
      },
    };
  }

  // Check if a point is within a polygon (delivery zone)
  isPointInPolygon(point, polygon) {
    const [lng, lat] = point;
    const vertices = polygon.coordinates[0];
    let inside = false;

    for (let i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
      const xi = vertices[i][0],
        yi = vertices[i][1];
      const xj = vertices[j][0],
        yj = vertices[j][1];

      const intersect =
        yi > lat !== yj > lat && lng < ((xj - xi) * (lat - yi)) / (yj - yi) + xi;

      if (intersect) inside = !inside;
    }

    return inside;
  }

  // Find delivery zone for a location
  async findDeliveryZone(lat, lng) {
    try {
      const zones = await Zone.find({ isActive: true });

      for (const zone of zones) {
        if (this.isPointInPolygon([lng, lat], zone.polygon)) {
          return zone;
        }
      }

      return null;
    } catch (error) {
      console.error('Find zone error:', error);
      return null;
    }
  }

  // Check if delivery is available for a location
  async isDeliveryAvailable(lat, lng) {
    const zone = await this.findDeliveryZone(lat, lng);
    return {
      available: zone !== null,
      zone: zone,
    };
  }

  // Get nearby restaurants within radius
  async getNearbyRestaurantsQuery(lat, lng, radiusKm = 10) {
    return {
      location: {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [parseFloat(lng), parseFloat(lat)],
          },
          $maxDistance: radiusKm * 1000, // Convert to meters
        },
      },
    };
  }

  // Get nearby riders within radius
  async getNearbyRidersQuery(lat, lng, radiusKm = 5) {
    return {
      currentLocation: {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [parseFloat(lng), parseFloat(lat)],
          },
          $maxDistance: radiusKm * 1000,
        },
      },
      status: 'available',
    };
  }

  // Calculate optimal rider for an order
  async findOptimalRider(restaurantLat, restaurantLng, deliveryLat, deliveryLng, availableRiders) {
    if (!availableRiders || availableRiders.length === 0) {
      return null;
    }

    // Score each rider based on distance to restaurant
    const scoredRiders = availableRiders.map((rider) => {
      const riderCoords = rider.currentLocation?.coordinates || [0, 0];
      const distanceToRestaurant = this.calculateDistance(
        riderCoords[1],
        riderCoords[0],
        restaurantLat,
        restaurantLng
      );

      // Calculate total trip distance
      const deliveryDistance = this.calculateDistance(
        restaurantLat,
        restaurantLng,
        deliveryLat,
        deliveryLng
      );

      return {
        rider,
        distanceToRestaurant,
        totalDistance: distanceToRestaurant + deliveryDistance,
        score: distanceToRestaurant * 0.7 + (5 - rider.rating) * 0.3, // Lower is better
      };
    });

    // Sort by score (lower is better)
    scoredRiders.sort((a, b) => a.score - b.score);

    return scoredRiders[0]?.rider || null;
  }

  // Get center point of multiple coordinates
  getCenterPoint(coordinates) {
    if (!coordinates || coordinates.length === 0) {
      return null;
    }

    let sumLat = 0;
    let sumLng = 0;

    coordinates.forEach(([lng, lat]) => {
      sumLat += lat;
      sumLng += lng;
    });

    return {
      lat: sumLat / coordinates.length,
      lng: sumLng / coordinates.length,
    };
  }

  // Format address for Bangladesh
  formatAddress(address) {
    const parts = [];

    if (address.houseNo) parts.push(`House ${address.houseNo}`);
    if (address.roadNo) parts.push(`Road ${address.roadNo}`);
    if (address.area) parts.push(address.area);
    if (address.thana) parts.push(address.thana);
    if (address.district) parts.push(address.district);

    return parts.join(', ');
  }

  // Validate Bangladesh coordinates
  isValidBangladeshCoordinates(lat, lng) {
    // Bangladesh bounding box
    const BD_BOUNDS = {
      minLat: 20.74,
      maxLat: 26.63,
      minLng: 88.01,
      maxLng: 92.68,
    };

    return (
      lat >= BD_BOUNDS.minLat &&
      lat <= BD_BOUNDS.maxLat &&
      lng >= BD_BOUNDS.minLng &&
      lng <= BD_BOUNDS.maxLng
    );
  }
}

module.exports = new LocationService();
