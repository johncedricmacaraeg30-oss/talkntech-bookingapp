package com.example.talkntech_bookingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.talkntech_bookingapp.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val repairOffers = remember {
                listOf(
                    RepairOffer(
                        1,
                        "LCD / OLED Replacement",
                        "LCD/Display",
                        1500.0,
                        4.9,
                        "1.2k",
                        R.drawable.img,
                        "Original & High Grade Replacement"
                    ),
                    RepairOffer(
                        2,
                        "High-Cap Battery Swap",
                        "Battery",
                        800.0,
                        4.8,
                        "850",
                        R.drawable.img,
                        "Solve Fast Draining & Swelling"
                    ),
                    RepairOffer(
                        3,
                        "Motherboard IC Repair",
                        "Motherboard",
                        2500.0,
                        5.0,
                        "430",
                        R.drawable.img,
                        "Shortboard & No Power Fix"
                    ),
                    RepairOffer(
                        4,
                        "Water Damage Revival",
                        "Motherboard",
                        1200.0,
                        4.7,
                        "620",
                        R.drawable.img,
                        "Ultrasonic Cleaning & Diagnostics"
                    ),
                    RepairOffer(
                        5,
                        "System Unlocking & Flash",
                        "Software",
                        600.0,
                        4.9,
                        "2.1k",
                        R.drawable.img,
                        "Bootloop, FRP & Hanging Fix"
                    ),
                    RepairOffer(
                        6,
                        "Charging Port Replacement",
                        "Battery",
                        500.0,
                        4.8,
                        "940",
                        R.drawable.img,
                        "Fix Loose & Slow Charging"
                    )
                )
            }

            var orderList by remember { mutableStateOf(listOf<BookingOrder>()) }

            MaterialTheme {
                Surface {
                    TikTokShopScreen(
                        offers = repairOffers,
                        orders = orderList,
                        onPlaceOrder = { offer, name, model, address ->
                            val nextOrderId = orderList.size + 1
                            val newOrder = BookingOrder(
                                orderId = nextOrderId,
                                offerTitle = offer.title,
                                customerName = name,
                                phoneModel = model,
                                address = address.ifBlank { "Pickup Address" },
                                totalAmount = offer.price,
                                status = "Pending Tech Dispatch"
                            )
                            orderList = orderList + newOrder
                        }
                    )
                }
            }
        }
    }
}