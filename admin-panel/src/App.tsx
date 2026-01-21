import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'react-hot-toast'
import { useAuthStore } from './store/authStore'
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

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
})

interface ProtectedRouteProps {
  children: React.ReactNode
}

function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, token } = useAuthStore()
  
  // Check for token in localStorage as fallback
  const hasToken = isAuthenticated || token || localStorage.getItem('adminToken')
  
  if (!hasToken) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <MainLayout>
                  <Routes>
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
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
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                  </Routes>
                </MainLayout>
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
      <Toaster 
        position="top-right" 
        toastOptions={{
          duration: 3000,
          style: {
            borderRadius: '10px',
            background: '#333',
            color: '#fff',
          },
          success: {
            iconTheme: {
              primary: '#22c55e',
              secondary: '#fff',
            },
          },
          error: {
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </QueryClientProvider>
  )
}

export default App
