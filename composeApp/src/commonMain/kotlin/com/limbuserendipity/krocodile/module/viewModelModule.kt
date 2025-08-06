package com.limbuserendipity.krocodile.module

import com.limbuserendipity.krocodile.vm.LobbyViewModel
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {


    viewModel{
        SigInViewModel(get())
    }

    viewModelOf(::LobbyViewModel)

}
