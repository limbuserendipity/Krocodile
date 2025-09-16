package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.direction_up_2
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatInput(
    onSendMessage: (String) -> Unit
) {
    var input by remember {
        mutableStateOf("")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { value ->
                input = value
            },
            placeholder = { Text("Введите сообщение...") },
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(0.5f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (input.isNotBlank()) onSendMessage(input)
                input = ""
            })
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (input.isNotBlank()) onSendMessage(input)
                input = ""
            },
            enabled = input.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.direction_up_2),
                contentDescription = "Send"
            )
        }
    }
}