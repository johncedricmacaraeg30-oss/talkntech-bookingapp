# Firebase Firestore CRUD Implementation Guide for Jetpack Compose

This guide walks you through implementing Create, Read, Update, and Delete (CRUD) operations using Cloud Firestore in your Jetpack Compose app (`TalknTech`).

## 1. Data Model (`BookingItem.kt`)

Create a data class representing your Firestore document.

```kotlin
data class BookingItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
```

---

## 2. ViewModel (`BookingViewModel.kt`)

The ViewModel handles interaction with Cloud Firestore (`FirebaseFirestore.getInstance()`).

```kotlin
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
    private val collectionRef = db.collection("bookings")

    private val _bookings = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookings: StateFlow<List<BookingItem>> = _bookings.asStateFlow()

    init {
        fetchBookings()
    }

    // CREATE
    fun addBooking(title: String, description: String) {
        viewModelScope.launch {
            try {
                val newDocRef = collectionRef.document()
                val item = BookingItem(
                    id = newDocRef.id,
                    title = title,
                    description = description
                )
                newDocRef.set(item).await()
                fetchBookings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // READ (Real-time listener or one-time fetch)
    fun fetchBookings() {
        viewModelScope.launch {
            try {
                val snapshot = collectionRef.get().await()
                val list = snapshot.toObjects(BookingItem::class.java)
                _bookings.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // UPDATE
    fun updateBooking(id: String, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            try {
                collectionRef.document(id).update(
                    mapOf(
                        "title" to newTitle,
                        "description" to newDescription
                    )
                ).await()
                fetchBookings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // DELETE
    fun deleteBooking(id: String) {
        viewModelScope.launch {
            try {
                collectionRef.document(id).delete().await()
                fetchBookings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
```

---

## 3. Compose UI (`BookingScreen.kt`)

A simple UI to view, add, and delete bookings.

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BookingScreen(viewModel: BookingViewModel = viewModel()) {
    val bookings by viewModel.bookings.collectAsState()
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("TalknTech Bookings (Firestore CRUD)", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = descInput,
            onValueChange = { descInput = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (titleInput.isNotBlank()) {
                    viewModel.addBooking(titleInput, descInput)
                    titleInput = ""
                    descInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Booking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = booking.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = booking.description, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(onClick = { viewModel.deleteBooking(basinId = booking.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
```
