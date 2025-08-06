package com.limbuserendipity.krocodile.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.Space(){
    Spacer(modifier = Modifier.size(this))
}