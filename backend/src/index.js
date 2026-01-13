const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    message: 'KhabarLagbe API is running',
    timestamp: new Date().toISOString()
  });
});

// API Routes (stubs)
app.get('/api/restaurants', (req, res) => {
  res.json({ 
    success: true, 
    message: 'Restaurants endpoint - coming soon',
    data: []
  });
});

app.get('/api/orders', (req, res) => {
  res.json({ 
    success: true, 
    message: 'Orders endpoint - coming soon',
    data: []
  });
});

app.post('/api/auth/login', (req, res) => {
  res.json({ 
    success: true, 
    message: 'Login endpoint - coming soon'
  });
});

app.post('/api/auth/register', (req, res) => {
  res.json({ 
    success: true, 
    message: 'Register endpoint - coming soon'
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ 
    success: false, 
    message: 'Endpoint not found' 
  });
});

// Error handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ 
    success: false, 
    message: 'Internal server error' 
  });
});

app.listen(PORT, () => {
  console.log(`🚀 KhabarLagbe Backend API running on port ${PORT}`);
});
