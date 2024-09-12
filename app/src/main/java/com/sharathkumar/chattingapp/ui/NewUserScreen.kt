package com.sharathkumar.chattingapp.ui

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUser() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors
    var newUser by remember { mutableStateOf(UserProfile(username = "", number = "")) }

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
//        Button(
//            onClick = {
//                userPreferences.saveUserProfile(UserProfile(username = newUser.username, number = newUser.number)) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            shape = MaterialTheme.shapes.medium
//        ) {
//            Text("Add User", fontSize = 18.sp, color = textColor.primary)
//        }

        // Delete User button
        OutlinedButton(
            onClick = { //userPreferences.deleteUserProfile(context = context, username = newUser.username)
                 },
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Delete User", fontSize = 18.sp, color = textColor.primary)
        }
    }
}