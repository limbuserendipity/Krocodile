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
    onDismissRequest: () -> Unit,
    players: List<PlayerData>,
    closeAnswers: List<ChatMessageData>
) {
    when (val variant = endState.endVariant) {
        is EndVariant.GuessedWord -> {

            WinnerGuessedDialog(
                guessedWord = variant.guessedWord,
                winnerName = variant.winnerName,
                time = variant.time.toFloat(),
                paths = paths,
                onDismissRequest = onDismissRequest
            )

        }

        is EndVariant.FailedWord -> {

            NoWinnerDialog(
                time = variant.time.toFloat(),
                paths = paths,
                guessedWord = variant.guessedWord,
                closeAnswers = closeAnswers,
                onDismissRequest = onDismissRequest,
            )

        }

        is EndVariant.GameEnd -> {
            GameEndDialog(
                winner = players.first { it.name == variant.winnerName },
                players = players,
                onDismissRequest = onDismissRequest
            )
        }
    }
}