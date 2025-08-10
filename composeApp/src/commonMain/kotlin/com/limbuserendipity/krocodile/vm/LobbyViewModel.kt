package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import com.limbuserendipity.krocodile.game.GameClient

class LobbyViewModel(
    client : GameClient
) : ViewModel(){

    val lobbyState = client.lobbyState



}