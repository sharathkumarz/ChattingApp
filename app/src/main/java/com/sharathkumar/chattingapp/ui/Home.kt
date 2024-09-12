package com.sharathkumar.chattingapp.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Transaction
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors
import com.sharathkumar.chattingapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(onClick: (Contact) -> Unit,onNewUser:() -> Unit,onUpdateProfile: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application))
    val userdata by homeViewModel.userProfile.collectAsState()
    val chatContacts by homeViewModel.chatContacts.collectAsState()
    val addUserPhoto by remember { mutableIntStateOf(R.drawable.img_1) }
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(Contact(username = "", phone = "")) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = backgroundColor.primary)
        .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Welcome,\n ${userdata.username}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f) // Take available space
            )

            // Add User button
            Button(
                onClick = {onNewUser()},
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.BGreen), // Green
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large
            ) {

                Spacer(modifier = Modifier.width(4.dp))
                Text("Add User", fontSize = 16.sp)
            }

            // Update Profile button
            Button(
                onClick = {onUpdateProfile()},
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.Bblue), // Blue
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large
            ) {

                Spacer(modifier = Modifier.width(4.dp))
                Text("Update Profile", fontSize = 16.sp)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(chatContacts) { user ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    onClick(Contact(username = user.username, phone = user.phone))
                                },
                                onLongClick = {
                                    showDialog = true
                                    selectedUser = user
                                }
                            ),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = addUserPhoto),
                            contentDescription = "user",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = user.username, fontSize = 20.sp, color = textColor.primary)


                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = Color.Gray,
                        thickness = 0.205.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }
        AlertDialog(
            showDialog = showDialog,
            selectedUser = selectedUser,
            onDismiss = { showDialog = false },
            onConfirm = {
                homeViewModel.deleteUserContact(selectedUser.phone,userdata.number)
            }
        )
    }
}

class HomeViewModel(application: Application) :  AndroidViewModel(application) {

    private val chatViewModel = ChatViewModel(application)
    private val _chatContacts = MutableStateFlow<List<Contact>>(emptyList())
    val chatContacts: StateFlow<List<Contact>> = _chatContacts

    private var _userProfile = MutableStateFlow(UserProfile(username = "", number = ""))
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val contacts: Map<String, String> = getContacts(application.contentResolver)
    private val db = AppDatabase.getDatabase(application)
    private val contactDao = db.contactDao()
    private val userDao = db.userDao()

    init {
        loadUserContacts()
        loadUserProfile()
        observeNewMessages()
    }

    // Observe incoming messages and update contacts list
    private fun observeNewMessages() {
        viewModelScope.launch {
            chatViewModel.receivedMessages.collect { messages ->
                messages.forEach { message ->
                    val sender = message.sender
                    val contactName = contacts[sender] ?: sender

                    if (_chatContacts.value.none { it.phone == sender }) {
                        val newUser = Contact(username = contactName, phone = sender)
                        _chatContacts.value += newUser
                        contactDao.insertContact(newUser)
                    }
                }
            }
        }
    }

    // Load user contacts from the database
    private fun loadUserContacts() {
        viewModelScope.launch {
            _chatContacts.value = contactDao.getAllContacts()
        }
    }

    // Add a new contact to the database
    fun addUserContact(contact: Contact) {
        viewModelScope.launch {
            if (!_chatContacts.value.contains(contact)) {
                _chatContacts.value += contact
                contactDao.insertContact(contact)
            }
        }
    }

    // Delete a contact and associated chat
    @Transaction
    suspend fun deleteUserAndChat(senderId: String, receiverId: String) {
        contactDao.deleteContact(senderId)
        chatViewModel.deleteChat(senderId, receiverId)
    }

    // Public method to delete a contact and refresh the contacts list
    fun deleteUserContact(senderId: String, receiverId: String) {
        viewModelScope.launch {
            deleteUserAndChat(senderId, receiverId)
            loadUserContacts()
        }
    }

    // Load the current user's profile data
    private fun loadUserProfile() {
        viewModelScope.launch {
            val profile = userDao.getUserProfile()
            profile?.let { _userProfile.value = it }
        }
    }

    // Update the user's profile data
    fun updateUserProfile(userData: UserProfile) {
        viewModelScope.launch {
            _userProfile.value = userData
            userDao.saveUserProfile(userData)
        }
    }
}

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}