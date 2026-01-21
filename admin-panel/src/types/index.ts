// User Types
export interface User {
  _id: string;
  name: string;
  phone: string;
  email?: string;
  role: 'customer' | 'restaurant' | 'rider' | 'admin';
  isVerified: boolean;
  isBanned: boolean;
  createdAt: string;
  updatedAt: string;
  lastLogin?: string;
  addresses?: Address[];
  orderCount?: number;
  totalSpent?: number;
}

export interface Address {
  _id: string;
  label: string;
  street: string;
  area: string;
  city: string;
  location: GeoLocation;
  isDefault: boolean;
}

export interface GeoLocation {
  type: 'Point';
  coordinates: [number, number]; // [lng, lat]
}

// Restaurant Types
export interface Restaurant {
  _id: string;
  name: string;
  nameBn: string;
  phone: string;
  email: string;
  logo?: string;
  coverImage?: string;
  location: GeoLocation;
  address: {
    street: string;
    area: string;
    thana: string;
    district: string;
  };
  rating: number;
  totalReviews: number;
  totalOrders: number;
  isActive: boolean;
  isOpen: boolean;
  approvalStatus: 'pending' | 'approved' | 'rejected';
  rejectionReason?: string;
  openingHours: OpeningHours;
  cuisineTypes: string[];
  minimumOrder: number;
  deliveryTime: string;
  commission: number;
  zone?: Zone;
  createdAt: string;
  updatedAt: string;
}

export interface OpeningHours {
  monday: DayHours;
  tuesday: DayHours;
  wednesday: DayHours;
  thursday: DayHours;
  friday: DayHours;
  saturday: DayHours;
  sunday: DayHours;
}

export interface DayHours {
  isOpen: boolean;
  openTime: string;
  closeTime: string;
}

export interface MenuItem {
  _id: string;
  name: string;
  nameBn: string;
  description: string;
  price: number;
  image?: string;
  category: string;
  isAvailable: boolean;
  isPopular: boolean;
  preparationTime: number;
  variants?: MenuVariant[];
  addons?: MenuAddon[];
}

export interface MenuVariant {
  name: string;
  price: number;
}

export interface MenuAddon {
  name: string;
  price: number;
}

// Rider Types
export interface Rider {
  _id: string;
  name: string;
  phone: string;
  email?: string;
  avatar?: string;
  vehicleType: 'bicycle' | 'motorcycle' | 'car';
  vehicleNumber?: string;
  licenseNumber?: string;
  zone: string;
  status: 'available' | 'busy' | 'offline' | 'on_break';
  rating: number;
  totalDeliveries: number;
  totalEarnings: number;
  isApproved: boolean;
  isActive: boolean;
  documents: RiderDocument[];
  currentLocation?: GeoLocation;
  createdAt: string;
  updatedAt: string;
}

export interface RiderDocument {
  type: 'nid' | 'license' | 'vehicle_registration';
  url: string;
  verified: boolean;
  verifiedAt?: string;
}

// Order Types
export interface Order {
  _id: string;
  orderNumber: string;
  userId: User;
  restaurantId: Restaurant;
  riderId?: Rider;
  items: OrderItem[];
  status: OrderStatus;
  subtotal: number;
  deliveryFee: number;
  discount: number;
  vat: number;
  totalAmount: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  deliveryAddress: Address;
  specialInstructions?: string;
  estimatedDeliveryTime?: string;
  actualDeliveryTime?: string;
  timeline: OrderTimeline[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  menuItem: MenuItem;
  quantity: number;
  price: number;
  variant?: string;
  addons?: string[];
  specialInstructions?: string;
}

export type OrderStatus =
  | 'pending'
  | 'confirmed'
  | 'preparing'
  | 'ready'
  | 'picked_up'
  | 'on_the_way'
  | 'delivered'
  | 'cancelled';

export interface OrderTimeline {
  status: OrderStatus;
  timestamp: string;
  note?: string;
}

// Payment Types
export interface Payment {
  _id: string;
  order: Order;
  user: User;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  gateway?: {
    provider: string;
    transactionId?: string;
    reference?: string;
  };
  refundedAt?: string;
  refundReason?: string;
  createdAt: string;
  updatedAt: string;
}

export type PaymentMethod = 'bkash' | 'nagad' | 'rocket' | 'card' | 'cod';
export type PaymentStatus = 'pending' | 'initiated' | 'success' | 'failed' | 'refunded';

// Promo Code Types
export interface PromoCode {
  _id: string;
  code: string;
  description: string;
  type: 'percentage' | 'fixed';
  value: number;
  minOrderAmount: number;
  maxDiscount?: number;
  usageCount: number;
  usageLimit?: {
    total?: number;
    perUser: number;
  };
  validFrom: string;
  validUntil: string;
  isActive: boolean;
  applicableRestaurants?: string[];
  applicableCategories?: string[];
  createdAt: string;
  updatedAt: string;
}

// Zone Types
export interface Zone {
  _id: string;
  name: string;
  nameBn: string;
  polygon: {
    type: 'Polygon';
    coordinates: number[][][];
  };
  center: GeoLocation;
  deliveryFee: number;
  perKmFee: number;
  estimatedTime: string;
  isActive: boolean;
  restaurantCount: number;
  riderCount: number;
  createdAt: string;
  updatedAt: string;
}

// Analytics Types
export interface DashboardStats {
  totalOrders: number;
  totalRevenue: number;
  totalUsers: number;
  totalRestaurants: number;
  totalRiders: number;
  activeOrders: number;
  pendingApprovals: {
    restaurants: number;
    riders: number;
  };
  todayStats: {
    orders: number;
    revenue: number;
    newUsers: number;
  };
}

export interface RevenueData {
  date: string;
  revenue: number;
  orders: number;
}

export interface TopRestaurant {
  _id: string;
  name: string;
  totalOrders: number;
  totalRevenue: number;
  rating: number;
}

export interface TopRider {
  _id: string;
  name: string;
  totalDeliveries: number;
  totalEarnings: number;
  rating: number;
}

// Settings Types
export interface PlatformSettings {
  restaurantCommission: number;
  deliveryFee: number;
  perKmFee: number;
  minimumOrderAmount: number;
  vatPercentage: number;
  paymentGateways: {
    bkash: boolean;
    nagad: boolean;
    rocket: boolean;
    sslcommerz: boolean;
    cod: boolean;
  };
  maintenanceMode: boolean;
  allowRestaurantRegistration: boolean;
  allowRiderRegistration: boolean;
}

// Pagination Types
export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    pages: number;
  };
}

export interface PaginationParams {
  page?: number;
  limit?: number;
  search?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

// Admin Auth Types
export interface Admin {
  _id: string;
  name: string;
  email: string;
  role: 'super_admin' | 'admin' | 'moderator';
  avatar?: string;
  permissions: string[];
  lastLogin?: string;
  createdAt: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  admin: Admin;
}
