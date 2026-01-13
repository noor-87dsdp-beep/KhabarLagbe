import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'react-hot-toast'
import MainLayout from './components/Layout/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import ZoneManagement from './pages/zones/ZoneManagement'
import RestaurantManagement from './pages/restaurants/RestaurantManagement'

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
                    <Route path="/restaurants" element={<RestaurantManagement />} />
                    {/* Add other routes as they are implemented */}
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
