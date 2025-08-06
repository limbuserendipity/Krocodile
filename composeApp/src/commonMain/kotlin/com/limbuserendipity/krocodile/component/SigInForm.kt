package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.util.Space

@Composable
fun SigInForm(
    value : String,
    onValueChange : (String) -> Unit
){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {

        Text(
            text = "Ip address:"
        )

        4.dp.Space()

        TextField(
            value = value,
            onValueChange = onValueChange
        )

    }

}