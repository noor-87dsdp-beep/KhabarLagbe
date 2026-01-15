import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'react-hot-toast'
import MainLayout from './components/Layout/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import ZoneManagement from './pages/zones/ZoneManagement'
import RestaurantManagement from './pages/restaurants/RestaurantManagement'
import UserManagement from './pages/users/UserManagement'
import RiderManagement from './pages/riders/RiderManagement'
import OrderManagement from './pages/orders/OrderManagement'
import PaymentManagement from './pages/payments/PaymentManagement'
import PromoCodeManagement from './pages/promo-codes/PromoCodeManagement'
import Analytics from './pages/analytics/Analytics'
import Settings from './pages/settings/Settings'

const queryClient = new QueryClient()

function App() {
  // For demo purposes, assume user is logged in
  const isAuthenticated = true

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/*"
            element={
              isAuthenticated ? (
                <MainLayout>
                  <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/zones" element={<ZoneManagement />} />
                    <Route path="/users" element={<UserManagement />} />
                    <Route path="/restaurants" element={<RestaurantManagement />} />
                    <Route path="/riders" element={<RiderManagement />} />
                    <Route path="/orders" element={<OrderManagement />} />
                    <Route path="/payments" element={<PaymentManagement />} />
                    <Route path="/promo-codes" element={<PromoCodeManagement />} />
                    <Route path="/analytics" element={<Analytics />} />
                    <Route path="/settings" element={<Settings />} />
                  </Routes>
                </MainLayout>
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" />
    </QueryClientProvider>
  )
}

export default App
