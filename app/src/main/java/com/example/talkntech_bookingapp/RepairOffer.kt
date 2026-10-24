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
    val id: String = "",
    val orderId: Int = 0,
    val offerTitle: String = "",
    val customerName: String = "",
    val phoneModel: String = "",
    val address: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "Pending"
)
