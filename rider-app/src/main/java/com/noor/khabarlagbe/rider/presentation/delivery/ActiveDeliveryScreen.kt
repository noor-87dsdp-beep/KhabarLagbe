package com.noor.khabarlagbe.rider.presentation.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveDeliveryScreen(
    navController: NavController,
    viewModel: ActiveDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val order by viewModel.order.collectAsState()
    
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpDialogType by remember { mutableStateOf(OtpDialogType.PICKUP) }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is DeliveryUiState.Completed -> {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                }
            }
            is DeliveryUiState.NoActiveOrder -> {
                navController.popBackStack()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সক্রিয় ডেলিভারি") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            uiState is DeliveryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            order != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Map Placeholder
                    MapPlaceholder()
                    
                    // Order Details Bottom Sheet
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            StatusCard(
                                order = order!!,
                                onUpdateStatus = { newStatus ->
                                    when (newStatus) {
                                        OrderStatus.PICKED_UP -> {
                                            otpDialogType = OtpDialogType.PICKUP
                                            showOtpDialog = true
                                        }
                                        OrderStatus.DELIVERED -> {
                                            otpDialogType = OtpDialogType.DELIVERY
                                            showOtpDialog = true
                                        }
                                        else -> viewModel.updateStatus(newStatus)
                                    }
                                }
                            )
                        }
                        
                        item {
                            EarningsCard(deliveryFee = order!!.deliveryFee)
                        }
                        
                        item {
                            LocationDetailsCard(order = order!!)
                        }
                        
                        item {
                            Text(
                                text = "অর্ডার আইটেম",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(order!!.items) { item ->
                            OrderItemCard(item = item)
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("কোন সক্রিয় ডেলিভারি নেই")
                }
            }
        }
        
        if (showOtpDialog) {
            OtpDialog(
                type = otpDialogType,
                onDismiss = { showOtpDialog = false },
                onVerify = { otp ->
                    if (otpDialogType == OtpDialogType.PICKUP) {
                        viewModel.verifyPickupOtp(otp) {
                            showOtpDialog = false
                        }
                    } else {
                        viewModel.verifyDeliveryOtp(otp) {
                            showOtpDialog = false
                        }
                    }
                }
            )
        }
        
        if (uiState is DeliveryUiState.Error) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { /* Retry */ }) {
                        Text("আবার চেষ্টা করুন")
                    }
                }
            ) {
                Text((uiState as DeliveryUiState.Error).message)
            }
        }
    }
}

@Composable
fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
            Text(
                text = "Mapbox Map বা Google Maps এখানে দেখানো হবে",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "রাইডার লোকেশন, রেস্টুরেন্ট ও কাস্টমার মার্কার সহ রুট",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatusCard(
    order: RiderOrder,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "অর্ডার স্ট্যাটাস",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            val statusText = when (order.status) {
                OrderStatus.ACCEPTED -> "গৃহীত - রেস্টুরেন্টে যান"
                OrderStatus.ARRIVED_AT_RESTAURANT -> "রেস্টুরেন্টে পৌঁছেছেন"
                OrderStatus.PICKED_UP -> "পিক আপ সম্পন্ন - কাস্টমারে যান"
                OrderStatus.ON_THE_WAY -> "কাস্টমারের দিকে যাচ্ছেন"
                else -> order.status.name
            }
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            when (order.status) {
                OrderStatus.ACCEPTED -> {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.ARRIVED_AT_RESTAURANT) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Store, null)
                        Spacer(Modifier.width(8.dp))
                        Text("রেস্টুরেন্টে পৌঁছেছি")
                    }
                }
                OrderStatus.ARRIVED_AT_RESTAURANT -> {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.PICKED_UP) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ShoppingBag, null)
                        Spacer(Modifier.width(8.dp))
                        Text("অর্ডার পিক আপ করুন (OTP)")
                    }
                }
                OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, null)
                        Spacer(Modifier.width(8.dp))
                        Text("ডেলিভারি সম্পন্ন করুন (OTP)")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun EarningsCard(deliveryFee: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ডেলিভারি ফি",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "৳${deliveryFee.toInt()}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.Payments,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun LocationDetailsCard(order: RiderOrder) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LocationItem(
                icon = Icons.Default.Restaurant,
                title = order.restaurantName,
                subtitle = order.restaurantAddress,
                phone = null,
                color = MaterialTheme.colorScheme.primary
            )
            
            Divider()
            
            LocationItem(
                icon = Icons.Default.LocationOn,
                title = order.customerName,
                subtitle = order.deliveryAddress,
                phone = order.customerPhone,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LocationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    phone: String?,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (phone != null) {
            IconButton(onClick = { /* Call */ }) {
                Icon(Icons.Default.Phone, "Call", tint = color)
            }
        }
    }
}

@Composable
fun OrderItemCard(item: com.noor.khabarlagbe.rider.domain.model.OrderItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.quantity}x",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "৳${item.price.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpDialog(
    type: OtpDialogType,
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit
) {
    var otp by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (type == OtpDialogType.PICKUP) "পিক আপ OTP"
                else "ডেলিভারি OTP"
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (type == OtpDialogType.PICKUP)
                        "রেস্টুরেন্ট থেকে OTP নিন"
                    else
                        "কাস্টমার থেকে OTP নিন"
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onVerify(otp) }) {
                Text("যাচাই করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

enum class OtpDialogType {
    PICKUP, DELIVERY
}
