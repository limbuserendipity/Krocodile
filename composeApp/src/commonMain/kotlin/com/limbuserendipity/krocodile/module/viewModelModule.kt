package com.limbuserendipity.krocodile.module

import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::SigInViewModel)
}