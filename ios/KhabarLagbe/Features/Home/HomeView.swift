import SwiftUI

struct HomeView: View {
    @State private var searchQuery = ""
    @State private var selectedLocation = "Gulshan 2, Dhaka"
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Location Selector
                LocationSelectorView(location: $selectedLocation)
                    .padding(.horizontal)
                
                // Search Bar
                SearchBarView(text: $searchQuery)
                    .padding(.horizontal)
                
                // Promotional Banner
                PromoBannerView()
                    .padding(.horizontal)
                
                // Categories
                CategoryScrollView()
                
                // Restaurants
                VStack(spacing: 12) {
                    Text("জনপ্রিয় রেস্টুরেন্ট")
                        .font(.title2)
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal)
                    
                    ForEach(0..<5) { index in
                        RestaurantCardView(
                            name: "রেস্টুরেন্ট \(index + 1)",
                            cuisine: "বাংলাদেশী, ভারতীয়",
                            rating: 4.5,
                            deliveryTime: "30-40 মিনিট",
                            deliveryFee: "৳৩০"
                        )
                        .padding(.horizontal)
                    }
                }
            }
            .padding(.vertical)
        }
        .navigationTitle("খাবারলাগবে")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct LocationSelectorView: View {
    @Binding var location: String
    
    var body: some View {
        HStack {
            Image(systemName: "location.fill")
                .foregroundColor(.orange)
            Text(location)
                .font(.subheadline)
            Spacer()
            Image(systemName: "chevron.down")
                .font(.caption)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct SearchBarView: View {
    @Binding var text: String
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("রেস্টুরেন্ট বা খাবার খুঁজুন...", text: $text)
            if !text.isEmpty {
                Button(action: { text = "" }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.gray)
                }
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct PromoBannerView: View {
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.orange, Color.red],
                startPoint: .leading,
                endPoint: .trailing
            )
            .cornerRadius(12)
            
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("🎉 বিশেষ অফার")
                        .font(.headline)
                        .foregroundColor(.white)
                    Text("৫০% ছাড় পান প্রথম অর্ডারে")
                        .font(.subheadline)
                        .foregroundColor(.white.opacity(0.9))
                }
                Spacer()
            }
            .padding()
        }
        .frame(height: 100)
    }
}

struct CategoryScrollView: View {
    let categories = ["বিরিয়ানি", "চাইনিজ", "ফাস্ট ফুড", "মিষ্টি", "কাবাব", "পিৎজা"]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(categories, id: \.self) { category in
                    CategoryChip(title: category)
                }
            }
            .padding(.horizontal)
        }
    }
}

struct CategoryChip: View {
    let title: String
    
    var body: some View {
        Text(title)
            .font(.subheadline)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color.orange.opacity(0.1))
            .foregroundColor(.orange)
            .cornerRadius(20)
    }
}

struct RestaurantCardView: View {
    let name: String
    let cuisine: String
    let rating: Double
    let deliveryTime: String
    let deliveryFee: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Restaurant Image Placeholder
            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(height: 160)
                .cornerRadius(12)
                .overlay(
                    Image(systemName: "photo")
                        .font(.largeTitle)
                        .foregroundColor(.gray)
                )
            
            VStack(alignment: .leading, spacing: 4) {
                Text(name)
                    .font(.headline)
                
                Text(cuisine)
                    .font(.subheadline)
                    .foregroundColor(.gray)
                
                HStack {
                    HStack(spacing: 2) {
                        Image(systemName: "star.fill")
                            .font(.caption)
                            .foregroundColor(.yellow)
                        Text(String(format: "%.1f", rating))
                            .font(.caption)
                    }
                    
                    Text("•")
                        .foregroundColor(.gray)
                    
                    Text(deliveryTime)
                        .font(.caption)
                        .foregroundColor(.gray)
                    
                    Text("•")
                        .foregroundColor(.gray)
                    
                    Text(deliveryFee)
                        .font(.caption)
                        .foregroundColor(.gray)
                }
            }
            .padding(.horizontal, 8)
        }
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 2)
    }
}

#Preview {
    NavigationStack {
        HomeView()
    }
}
