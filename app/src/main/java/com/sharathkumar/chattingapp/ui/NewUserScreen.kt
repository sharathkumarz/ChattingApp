package com.sharathkumar.chattingapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUser(onClick: () -> Unit) {
    val context = LocalContext.current
    val homeViewModel:HomeViewModel = viewModel()
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors
    var newUser by remember { mutableStateOf(UserProfile(username = "", number = "")) }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create New User",
            fontSize = 24.sp,
            color = textColor.primary,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Username input
        OutlinedTextField(
            value = newUser.username,
            onValueChange = { newUser = newUser.copy(username = it) },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = textColor.primary,
                containerColor = Color.Transparent,
                unfocusedLabelColor = textColor.primary,
                unfocusedIndicatorColor = textColor.primary,
                focusedIndicatorColor = textColor.primary
            )
        )

        // Number input
        OutlinedTextField(
            value = newUser.number,
            onValueChange = { newUser = newUser.copy(number = it) },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = textColor.primary,
                containerColor = Color.Transparent,
                unfocusedLabelColor = textColor.primary,
                unfocusedIndicatorColor = textColor.primary,
                focusedIndicatorColor = textColor.primary
            )
        )

        // Add User button
        Button(
            onClick = {
                if(newUser.username == ""){
                    Toast.makeText(context,"Input Username", Toast.LENGTH_SHORT).show()
                }else if(newUser.number.length != 10){
                    Toast.makeText(context,"Invalid Number", Toast.LENGTH_SHORT).show()
                }else {
                    homeViewModel.updateUserProfile(
                        UserProfile(
                            username = newUser.username,
                            number =  newUser.number
                        )
                    )
                    onClick()

                    keyBoardController?.hide()
                    focusManager.clearFocus()
                }

                //userPreferences.saveUserProfile(UserProfile(username = newUser.username, number = newUser.number))
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Add User", fontSize = 18.sp, color = textColor.primary)
        }

    }
}