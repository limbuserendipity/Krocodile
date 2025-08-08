package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.SigInForm
import com.limbuserendipity.krocodile.util.Space
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.toString


class SigInScreen() : Screen {

    @Composable
    override fun Content() {
        val viewModel: SigInViewModel = koinViewModel()
        val navigator = LocalNavigator.currentOrThrow
        val counter = viewModel.counter.collectAsState()
        val player = viewModel.playerState.collectAsState()
        var ipAddress by remember {
            mutableStateOf("")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFf2ffff))
        ) {
            Text(text = player.value?.name ?: "null")
            Text(text = player.value?.id ?: "null")
            Text(text = player.value?.roomId.toString())

            SigInForm(
                value = ipAddress,
                onValueChange = { ipAddress = it }
            )

            8.dp.Space()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
                    .padding(2.dp)
                    .background(Color(0xFFfcba03))
                    .padding(2.dp)
                    .clickable {
                        viewModel.newPlayer()
                    }
            ){
                Text(
                    text = "SigIn"
                )
            }

        }
    }

}




