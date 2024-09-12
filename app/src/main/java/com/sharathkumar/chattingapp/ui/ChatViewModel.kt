package com.sharathkumar.chattingapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var _receivedMessages = MutableStateFlow<List<Message1>>(emptyList())
    val receivedMessages: StateFlow<List<Message1>> = _receivedMessages

    private val db = AppDatabase.getDatabase(application)
    private val messageDao = db.messageDao()
    private val userDao = db.userDao()

    private val client = HttpClient {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    private var webSocketSession: WebSocketSession? = null

    init {
        initializeWebSocketConnection()
    }

    /**
     * Initializes the WebSocket connection and starts receiving messages
     */
    private fun initializeWebSocketConnection() {
        viewModelScope.launch {
            try {
                val userId = userDao.getUserProfile()?.number
                val url = "ws://192.168.177.131:8080/chat?userId=$userId"

                client.webSocket(url) {
                    webSocketSession = this
                    Log.i("WebSocketClient", "Connected to WebSocket server")

                    // Receive messages from the WebSocket
                    for (msg in incoming) {
                        when (msg) {
                            is Frame.Text -> {
                                val receivedText = msg.readText()
                                handleReceivedMessage(receivedText)
                            }
                            else -> Log.i("WebSocketClient", "Unsupported message type received")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket Error", "Error in WebSocket connection: ${e.message}")
            } finally {
                Log.i("WebSocket", "WebSocket connection closed.")
                webSocketSession = null
            }
        }
    }

    /**
     * Handles received WebSocket message and updates the state
     */
    private suspend fun handleReceivedMessage(receivedText: String) {
        val receivedMessage = Json.decodeFromString<Message>(receivedText)
        saveMessageToDatabase(receivedMessage)

        _receivedMessages.value += Message1(
            sender = receivedMessage.sender,
            receiver = receivedMessage.receiver,
            message = receivedMessage.message,
            timestamp = receivedMessage.timestamp
        )

        Log.i("WebSocketClient", "Received message: ${receivedMessage.message}")
    }

    /**
     * Sends a message via WebSocket and updates the UI
     */
    fun sendMessage(message: Message) {
        viewModelScope.launch {
            try {
                webSocketSession?.send(Frame.Text(Json.encodeToString(message)))

                _receivedMessages.value += Message1(
                    sender = message.sender,
                    receiver = message.receiver,
                    message = message.message,
                    timestamp = message.timestamp
                )
                saveMessageToDatabase(message)

                Log.i("WebSocketClient", "Message sent: ${message.message}")
            } catch (e: Exception) {
                Log.e("WebSocket Error", "Error sending message: ${e.message}")
            }
        }
    }

    /**
     * Saves a message to the local Room database
     */
    private suspend fun saveMessageToDatabase(message: Message) {
        messageDao.insertMessage(
            Message1(
                sender = message.sender,
                receiver = message.receiver,
                message = message.message,
                timestamp = message.timestamp
            )
        )
    }

    /**
     * Deletes all chat between the specified sender and receiver
     */
    suspend fun deleteChat(senderId: String, receiverId: String) {
        messageDao.deleteUserChat(senderId, receiverId)
    }

    /**
     * Loads chat between the specified sender and receiver
     */
    suspend fun getUserChat(senderId: String, receiverId: String) {
        _receivedMessages.value = messageDao.getUserChat(senderId, receiverId)
    }

    /**
     * Returns all chat from the database
     */
    suspend fun getAllChat(): List<Message1> {
        return messageDao.getAllChat()
    }

    /**
     * Deletes a specific message by its ID
     */
    suspend fun deleteMessage(messageId: Int) {
        messageDao.deleteMessage(messageId)
        _receivedMessages.value = _receivedMessages.value.filter { it.messageId != messageId }
    }

    override fun onCleared() {
        super.onCleared()
        closeWebSocketConnection()
        client.close()
    }

    /**
     * Closes the WebSocket connection gracefully
     */
    private fun closeWebSocketConnection() {
        viewModelScope.launch {
            webSocketSession?.close()
        }
        Log.i("WebSocketClient", "WebSocket client closed")
    }
}




