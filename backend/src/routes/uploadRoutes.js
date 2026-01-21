const express = require('express');
const router = express.Router();
const uploadController = require('../controllers/uploadController');
const upload = require('../middleware/upload');
const { auth } = require('../middleware/auth');

// Restaurant images
router.post(
  '/restaurant',
  auth,
  upload.single('image', 'restaurants'),
  uploadController.uploadImage
);

router.post(
  '/restaurant/multiple',
  auth,
  upload.multiple('images', 5, 'restaurants'),
  uploadController.uploadMultipleImages
);

// Menu item images
router.post(
  '/menu',
  auth,
  upload.single('image', 'menu'),
  uploadController.uploadImage
);

// User profile images
router.post(
  '/profile',
  auth,
  upload.single('image', 'users'),
  uploadController.uploadImage
);

// Rider documents and images
router.post(
  '/rider/document',
  auth,
  upload.single('document', 'riders'),
  uploadController.uploadImage
);

// Delivery proof images
router.post(
  '/delivery/proof',
  auth,
  upload.single('proof', 'deliveries'),
  uploadController.uploadImage
);

// File management
router.delete('/:filename', auth, uploadController.deleteFile);
router.get('/:filename/info', auth, uploadController.getFileInfo);

module.exports = router;
