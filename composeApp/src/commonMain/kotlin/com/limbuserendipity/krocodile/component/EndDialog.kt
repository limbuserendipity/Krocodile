package com.limbuserendipity.krocodile.component

import androidx.compose.runtime.Composable
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.model.EndVariant
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.PlayerData
import com.limbuserendipity.krocodile.vm.PathInfo

@Composable
fun EndDialog(
    endState: GameState.End,
    paths: List<PathInfo>,
    onContinue: () -> Unit,
    onDismissRequest: () -> Unit,
    players: List<PlayerData>,
    onNewGame: () -> Unit,
    onLeave: () -> Unit,
    closeAnswers: List<ChatMessageData>
) {
    when (val variant = endState.endVariant) {
        is EndVariant.GuessedWord -> {
            VictoryDialog(
                time = variant.time.toFloat(),
                paths = paths,
                winnerName = variant.winnerName,
                guessedWord = variant.guessedWord,
                onContinue = onContinue,
                onDismissRequest = onDismissRequest
            )
        }

        is EndVariant.FailedWord -> {
            TimeUpFailedDialog(
                time = variant.time.toFloat(),
                paths = paths,
                guessedWord = variant.guessedWord,
                closeAnswers = closeAnswers,
                onContinue = onContinue,
                onDismissRequest = onDismissRequest
            )
        }

        is EndVariant.GameEnd -> {
            GameEndDialog(
                winnerName = variant.winnerName,
                players = players,
                onNewGame = onNewGame,
                onLeave = onLeave,
                onDismissRequest = onDismissRequest
            )
        }
    }
}