package com.example.talkntech_bookingapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TikTokShopScreen(
    offers: List<RepairOffer>,
    orders: List<BookingOrder>,
    onPlaceOrder: (RepairOffer, String, String, String) -> Unit,
    onDeleteOrder: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedOfferForBooking by remember { mutableStateOf<RepairOffer?>(null) }
    var showOrdersList by remember { mutableStateOf(false) }

    val categories = listOf("All", "LCD/Display", "Battery", "Motherboard", "Software")

    val filteredOffers = offers.filter {
        if (selectedCategory == "All") true else it.category.equals(
            selectedCategory,
            ignoreCase = true
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111827))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TalkNTech Booking App",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF22C55E)
                        )
                        Text(
                            text = "On-Demand Home Service & Express Repair Deals",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Button(
                        onClick = { showOrdersList = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "My Orders (${orders.size})", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Button(
                            onClick = { selectedCategory = category },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF22C55E) else Color(
                                    0xFF1F2937
                                ),
                                contentColor = if (isSelected) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = category, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOffers, key = { it.id }) { offer ->
                    OfferShopCard(
                        offer = offer,
                        onBookClick = { selectedOfferForBooking = offer }
                    )
                }
            }
        }
    }

    selectedOfferForBooking?.let { offer ->
        CheckoutModal(
            offer = offer,
            onDismiss = { selectedOfferForBooking = null },
            onConfirmOrder = { name, model, address ->
                onPlaceOrder(offer, name, model, address)
                selectedOfferForBooking = null
            }
        )
    }

    if (showOrdersList) {
        OrdersListModal(
            orders = orders,
            onDismiss = { showOrdersList = false },
            onDeleteOrder = onDeleteOrder
        )
    }
}

@Composable
fun OfferShopCard(
    offer: RepairOffer,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column {
            Image(
                painter = painterResource(id = offer.imageRes),
                contentDescription = offer.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = offer.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = offer.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "★ ${offer.rating}", color = Color(0xFFF59E0B), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${offer.soldCount} repaired",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₱${offer.price.toInt()}",
                        color = Color(0xFF22C55E),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(text = "Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutModal(
    offer: RepairOffer,
    onDismiss: () -> Unit,
    onConfirmOrder: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = {
            Column {
                Text(
                    text = "TalkNTech Express Checkout",
                    color = Color(0xFF22C55E),
                    fontSize = 14.sp
                )
                Text(
                    text = offer.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Est. Price: ₱${offer.price.toInt()}",
                    color = Color(0xFFF59E0B),
                    fontSize = 14.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name", color = Color.Gray) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Device Model (e.g. iPhone 11)", color = Color.Gray) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Home Address for Pickup/Repair", color = Color.Gray) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && model.isNotBlank()) {
                        onConfirmOrder(name, model, address)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
            ) {
                Text("Confirm Repair Order", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun OrdersListModal(
    orders: List<BookingOrder>,
    onDismiss: () -> Unit,
    onDeleteOrder: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = {
            Text(text = "TalkNTech My Orders", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            if (orders.isEmpty()) {
                Text(text = "No active orders yet.", color = Color.Gray)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    orders.forEach { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = order.offerTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Client: ${order.customerName} (${order.phoneModel})",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Address: ${order.address}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status: ${order.status}",
                                        color = Color(0xFF22C55E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "₱${order.totalAmount.toInt()}",
                                            color = Color(0xFFF59E0B),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onDeleteOrder(order.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                            modifier = Modifier.height(28.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Delete", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
