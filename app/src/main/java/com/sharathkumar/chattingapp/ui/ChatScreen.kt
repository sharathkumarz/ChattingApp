package com.sharathkumar.chattingapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors
import com.sharathkumar.chattingapp.R
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChattingScreen(user: Contact) {
    val senderNumber = user.phone
    val senderName = user.username
    val chatViewModel : ChatViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    var chat by remember { mutableStateOf("") }
    val receiverNumber = homeViewModel.userProfile.collectAsState().value.number
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val receivedMessages by chatViewModel.receivedMessages.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTimestamp = LocalDateTime.now().format(formatter)
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(Message1(sender = "", receiver = "", message = "", timestamp = "")) }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            chatViewModel.getUserChat(senderNumber,receiverNumber)
           // Log.i("do","${chatViewModel.getUserChat(senderNumber,receiverNumber)}")
        }
    }

    LaunchedEffect(receivedMessages.size) {
        if (receivedMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.scrollToItem(receivedMessages.size - 1)
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.primary)
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with profile picture and user name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = backgroundColor.primary, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = senderName,
                    fontSize = 30.sp,
                    color = textColor.primary
                )
            }

            // Chat messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
                    .weight(1f)
                    .padding(12.dp)
            ) {
                items(receivedMessages) { user ->
                    if (user.sender == receiverNumber && user.receiver == senderNumber) {
                        // Message from the receiver
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        showDialog = true
                                        selectedUser = user
                                    }
                                )
                            ,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(
                                        color = colorResource(id = R.color.whatsapp_sent_bubble),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = user.message,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = user.timestamp,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    } else if (user.sender == senderNumber && user.receiver == receiverNumber) {
                        // Message from the sender
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        showDialog = true
                                        selectedUser = user
                                    }
                                ),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(
                                        color = colorResource(id = R.color.whatsapp_sent_bubble),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = user.message,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = user.timestamp,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Message input field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = chat,
                    onValueChange = { chat = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .background(
                            color = Color.Gray,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    placeholder = { Text("Type a message...") },
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        containerColor = Color.Transparent,
                        unfocusedLabelColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        try {
                            chatViewModel.sendMessage(Message(
                                sender = receiverNumber,
                                receiver = senderNumber,
                                message = chat,
                                timestamp = formattedTimestamp
                            ))

                            chat = ""
                            coroutineScope.launch {
   //                             listState.animateScrollToItem(receivedMessages.size - 1)
                            }
                        } catch (e: Exception) {
                            Log.e("Message", "${e.message}")
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            AlertDialogMessage(
                showDialog = showDialog,
                selectedUser = selectedUser ,
                onDismiss = { showDialog = false },
                onConfirm = {
                    coroutineScope.launch {
                        chatViewModel.deleteMessage(messageId = selectedUser.messageId)
                    }
                }
            )
        }
    }
}


