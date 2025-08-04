package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SigInScreen(
    viewModel: SigInViewModel = koinViewModel()
){

    val counter = viewModel.counter.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(430.dp)
                .background(Color(0xffababab))
                .clickable{
                    viewModel.increment()
                }
        ){
            Text(
                text = counter.value.toString()
            )
        }
    }



}




