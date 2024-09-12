package com.sharathkumar.chattingapp.ui

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors
import com.sharathkumar.chattingapp.R
import io.ktor.http.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfile() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application))
    val userdata by homeViewModel.userProfile.collectAsState()
    var newUser by remember { mutableStateOf(userdata) }
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors
    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var profileImageUri by remember { mutableStateOf(Uri.parse(newUser.profile)) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = rememberImagePainter(
            data = profileImageUri,
           builder ={
               error(R.drawable.img_1)
           }
        ) ,
            contentDescription = "userProfile" ,
            modifier = Modifier
                .clip(CircleShape)
                .size(200.dp)
                .background(color = Color.Gray, shape = CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentScale = ContentScale.Crop

        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create / Update New Profile",
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
                    Toast.makeText(context,"Input Username",Toast.LENGTH_SHORT).show()
                }else if(newUser.number.length != 10){
                    Toast.makeText(context,"Invalid Number",Toast.LENGTH_SHORT).show()
                }else {
                        homeViewModel.updateUserProfile(
                            UserProfile(
                                username = newUser.username,
                                number =  newUser.number,
                                profile = profileImageUri.toString()
                            )
                        )

                    keyBoardController?.hide()
                    focusManager.clearFocus()
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.Bblue), // Green
                contentColor = Color.White)

            ) {
            Text("Update Profile", fontSize = 18.sp, color = textColor.primary)
        }

    }
}