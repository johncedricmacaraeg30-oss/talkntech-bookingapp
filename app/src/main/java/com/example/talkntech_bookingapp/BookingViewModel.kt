package com.example.talkntech_bookingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val ordersRef = db.collection("orders")

    private val _orders = MutableStateFlow<List<BookingOrder>>(emptyList())
    val orders: StateFlow<List<BookingOrder>> = _orders.asStateFlow()

    init {
        fetchOrders()
    }

    // CREATE (Add Order to Firestore)
    fun addOrder(offer: RepairOffer, name: String, model: String, address: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val docRef = ordersRef.document()
                val newOrder = BookingOrder(
                    id = docRef.id,
                    orderId = (System.currentTimeMillis() % 100000).toInt(),
                    offerTitle = offer.title,
                    customerName = name,
                    phoneModel = model,
                    address = address.ifBlank { "Pickup Address" },
                    totalAmount = offer.price,
                    status = "Pending Tech Dispatch"
                )
                docRef.set(newOrder).await()
                fetchOrders()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // READ (Fetch Orders from Firestore)
    fun fetchOrders() {
        viewModelScope.launch {
            try {
                val snapshot = ordersRef.get().await()
                val list = snapshot.toObjects(BookingOrder::class.java)
                _orders.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // UPDATE (Update Order Status or Details)
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                ordersRef.document(orderId).update("status", newStatus).await()
                fetchOrders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // DELETE (Remove Order from Firestore)
    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            try {
                ordersRef.document(orderId).delete().await()
                fetchOrders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
