package com.limbuserendipity.krocodile.module

import com.limbuserendipity.krocodile.navigation.AppNavigatorImpl
import org.koin.dsl.module

val navigationModule = module {
    factory { AppNavigatorImpl() }
}


