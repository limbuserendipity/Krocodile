package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SigInViewModel() : ViewModel(){

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    // Increment method
    fun increment() {
        _counter.value += 1
    }

    // Decrement method
    fun decrement() {
        _counter.value -= 1
    }

}