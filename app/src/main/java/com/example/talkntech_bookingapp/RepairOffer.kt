package com.example.talkntech_bookingapp

data class RepairOffer(
    val id: Int,
    val title: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val soldCount: String,
    val imageRes: Int,
    val description: String
)

data class BookingOrder(
    val orderId: Int,
    val offerTitle: String,
    val customerName: String,
    val phoneModel: String,
    val address: String,
    val totalAmount: Double,
    val status: String = "Pending"
)