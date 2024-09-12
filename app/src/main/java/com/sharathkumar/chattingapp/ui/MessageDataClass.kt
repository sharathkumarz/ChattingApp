package com.sharathkumar.chattingapp.ui

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "ChattingApp")
data class Message1(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,      // Auto-generated primary key
    val sender: String,          // Sender's ID or username
    val receiver: String,        // Receiver's ID or username
    val message: String,         // Message content
    val timestamp: String        // Timestamp of the message (can be Date or Long as well)
)

@Serializable
data class Message(
    val sender: String,          // Sender's ID or username
    val receiver: String,        // Receiver's ID or username
    val message: String,         // Message content
    val timestamp: String        // Timestamp of the message (can be Date or Long as well)
)

@Entity(tableName = "userdata")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,  // Fixed ID to ensure there's only one entry
    var username: String,
    var number: String,
    var profile: String ="" // Profile image URL
)

@Serializable
@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,
    var username: String,
    var phone: String,
    var lastMessage:String="",
    var lastseen:String =""
)







