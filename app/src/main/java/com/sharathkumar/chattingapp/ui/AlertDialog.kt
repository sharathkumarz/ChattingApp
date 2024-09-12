package com.sharathkumar.chattingapp.ui


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp


@Composable
fun AlertDialog(
    showDialog: Boolean,
    selectedUser: Contact?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            },
            title = { Text(text = "Remove") },
            text = { Text(text = " ${selectedUser.username}..?", fontSize = 20.sp) }

        )
    }
}


@Composable
fun AlertDialogMessage(
    showDialog: Boolean,
    selectedUser: Message1?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            },
            title = { Text(text = "Remove") },
            text = { Text(text = " ${selectedUser.message}..?", fontSize = 20.sp) }

        )
    }
}
