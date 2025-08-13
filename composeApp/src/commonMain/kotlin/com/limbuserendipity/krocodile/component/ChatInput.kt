package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.util.Space

@Composable
fun ChatInput(
    onSendClick : (String) -> Unit
){

    var message by remember {
        mutableStateOf("")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            value = message,
            onValueChange = { value ->
                message = value
            },
            singleLine = true
        )

        32.dp.Space()

        Button(
            onClick = {
                onSendClick(message)
                message = "ChatMessage"
            }
        ){
            Text(
                text = "Send"
            )
        }
    }

}