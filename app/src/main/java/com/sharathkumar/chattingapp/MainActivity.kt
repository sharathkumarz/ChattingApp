package com.sharathkumar.chattingapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.sharathkumar.chattingapp.ui.AppDatabase
import com.sharathkumar.chattingapp.ui.ChattingScreen
import com.sharathkumar.chattingapp.ui.Contact
import com.sharathkumar.chattingapp.ui.ContactsScreen
import com.sharathkumar.chattingapp.ui.HomeScreen
import com.sharathkumar.chattingapp.ui.NewUser
import com.sharathkumar.chattingapp.ui.UpdateProfile
import com.sharathkumar.chattingapp.ui.UserProfile
import com.sharathkumar.chattingapp.ui.theme.ChattingAppTheme


val LightColors = lightColorScheme(
    primary = Color.White,
    primaryContainer = Color.White
)

val DarkColors = darkColorScheme(
    primary = Color.Black,

)


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChattingAppTheme {
                Nav(context = this)
                //ContactsScreen()
            }
        }
    }
}


enum class Screen {
    Home,
    ChatScreen,
    NewUser,
    UpdateProfile,
    Contacts
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Nav(context:Context) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var hasBeenShown by remember { mutableStateOf(prefs.getBoolean("NewUser", false)) }
    val navViewModel: NavViewModel = viewModel()

    if(!hasBeenShown){
        navViewModel.currentScreen.value = Screen.NewUser
    }

    // Handle back navigation when on ChatScreen or NewUser
    BackHandler(enabled = navViewModel.currentScreen.value == Screen.ChatScreen ||
            navViewModel.currentScreen.value == Screen.NewUser ||
            navViewModel.currentScreen.value == Screen.Contacts ||
            navViewModel.currentScreen.value == Screen.UpdateProfile) {
        navViewModel.navigateToHome()
    }

    // Use Crossfade for smooth screen transitions
    Crossfade(targetState = navViewModel.currentScreen.value, label = "") { screen ->
        when (screen) {
            Screen.Home -> {
                HomeScreen(
                    onClick = { user ->
                        navViewModel.navigateToChat(user)
                    },
                    onNewUser = {
                        navViewModel.navigateToContactScreen()
                    },
                    onUpdateProfile = {
                        navViewModel.navigateToUpdateProfile()
                    }
                )
            }

            Screen.ChatScreen -> {
                ChattingScreen(navViewModel.selectedUser.value)
            }
            Screen.NewUser -> {
                NewUser(onClick = {
                    prefs.edit().putBoolean("NewUser",true).apply()
                      hasBeenShown = true
                    navViewModel.navigateToHome()
                })
            }
            Screen.UpdateProfile ->{
                UpdateProfile()
            }
            Screen.Contacts ->{
                ContactsScreen(onClick = { user->
                    navViewModel.navigateToChat(user)
                })
            }
        }
    }
}


class NavViewModel : ViewModel() {
    var currentScreen = mutableStateOf(Screen.Home)
    val selectedUser = mutableStateOf(Contact(username = "", phone = ""))

    fun navigateToHome() {
        currentScreen.value = Screen.Home
    }

    fun navigateToChat(user:Contact) {
        selectedUser.value = user
        currentScreen.value = Screen.ChatScreen
    }

    fun navigateToNewUser() {
        currentScreen.value = Screen.NewUser
    }
    fun navigateToUpdateProfile() {
        currentScreen.value = Screen.UpdateProfile
    }
    fun navigateToContactScreen(){
        currentScreen.value = Screen.Contacts
    }

}


