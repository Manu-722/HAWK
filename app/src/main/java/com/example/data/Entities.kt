package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val powerWatts: Int,
    val coilsCount: Int,
    val imageUrl: String,
    val warrantyMonths: Int,
    val safetyFeatures: String,
    val controlType: String,
    val isFeatured: Boolean = false,
    val category: String = "Induction Cooker"
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val fullName: String,
    val passwordHash: String,
    val isAdmin: Boolean = false,
    val phoneNumber: String = "",
    val details: String = "",
    val pictureUrl: String = ""
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val userEmail: String,
    val userName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val productId: Int,
    val quantity: Int
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val orderDate: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val paymentMethod: String,
    val paybillReference: String,
    val status: String // "Pending Approval", "Shipped", "Delivered"
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productTitle: String,
    val price: Double,
    val quantity: Int
)
