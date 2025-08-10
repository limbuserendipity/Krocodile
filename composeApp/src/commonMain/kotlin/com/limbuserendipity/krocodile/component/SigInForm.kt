package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SigInForm(
    ip: String,
    onIpChange: (String) -> Unit,
    username : String,
    onUsernameChange: (String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = {
                Text(
                    text = "Username:"
                )
            },
            singleLine = true
        )

        8.dp.Space()

        OutlinedTextField(
            value = ip,
            onValueChange = onIpChange,
            label = {
                Text(
                    text = "Ip address:"
                )
            },
            singleLine = true
        )

    }
}