package com.sharathkumar.chattingapp.ui

import android.Manifest
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharathkumar.chattingapp.DarkColors
import com.sharathkumar.chattingapp.LightColors


@Composable
fun ContactsScreen(onClick: (Contact) -> Unit) {
    val context = LocalContext.current
    val homeViewModel:HomeViewModel = viewModel()
    var hasPermission by remember { mutableStateOf(false) }
    var contactsMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val darkMode = isSystemInDarkTheme()
    val backgroundColor = if (darkMode) DarkColors else LightColors
    val textColor = if (darkMode) LightColors else DarkColors

   //  Permission launcher to request contact access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            contactsMap = getContacts(context.contentResolver)
               // .toMap() // Convert list to map

        }
    }

    // Request permission on first load
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.primary)
            .padding(16.dp)
    ) {
        if (hasPermission) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val sortedContacts = contactsMap.entries.sortedBy { it.value }
                items(sortedContacts) { entry ->
                    ContactItem(
                        name = entry.value,
                        phoneNumber = entry.key,
                        onClick = {
                            onClick(Contact(username = entry.value, phone = entry.key))
                            homeViewModel.addUserContact(Contact(username = entry.value, phone = entry.key))

                        },
                        textColor = textColor.primary
                    )
                }
            }
        } else {
            Text(
                text = "Permission required to access contacts",
                color = textColor.primary,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ContactItem(
    name: String,
    phoneNumber: String,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "Name: $name",
            color = textColor,
            fontSize = 25.sp
        )
        Text(
            text = "Phone: $phoneNumber",
            color = textColor,
            fontSize = 20.sp
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

// Function to fetch contacts from the ContentResolver and return as a Map
fun getContacts(contentResolver: ContentResolver): Map<String, String> {
    val contactsMap = mutableMapOf<String, String>()

    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameIndex) ?: "Unknown"
            val rawPhone = cursor.getString(phoneIndex) ?: ""
            val cleanedPhone = rawPhone.replace(Regex("\\D"), "").takeLast(10)

            if (cleanedPhone.isNotEmpty()) {
                contactsMap[cleanedPhone] = name
            }
        }
    }

    return contactsMap
}
