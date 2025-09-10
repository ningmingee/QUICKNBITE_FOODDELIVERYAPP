package com.example.quicknbiteapp.ui.customer.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.quicknbiteapp.viewModel.CartViewModel
import kotlin.random.Random
import com.example.quicknbiteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    cartViewModel: CartViewModel,
    onGameEnd: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var targetX by remember { mutableStateOf(Random.nextInt(50, 300)) }
    var targetY by remember { mutableStateOf(Random.nextInt(100, 500)) }
    var attemptsLeft by remember { mutableStateOf(10) } // Max attempts
    var gameFinished by remember { mutableStateOf(false) }
    var didWin by remember { mutableStateOf(false) }

    val pointsAwarded = 10
    val winningScore = 5

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.game_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!gameFinished) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .offset(x = targetX.dp, y = targetY.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                if (attemptsLeft > 0) {
                                    score++
                                    attemptsLeft--
                                    targetX = Random.nextInt(50, 300)
                                    targetY = Random.nextInt(100, 500)

                                    if (score >= winningScore) {
                                        gameFinished = true
                                        didWin = true
                                        cartViewModel.addPoints(pointsAwarded)
                                    } else if (attemptsLeft == 0) {
                                        gameFinished = true
                                        didWin = false
                                    }
                                }
                            }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (didWin) {
                            Text(
                                stringResource(R.string.you_won),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                stringResource(R.string.points_awarded, pointsAwarded),
                                fontSize = 22.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }  else {
                            Text(
                                stringResource(R.string.you_lost),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                stringResource(R.string.score_reached, score),
                                fontSize = 22.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onGameEnd,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.back_button),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Score & attempts display
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Score: $score",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Attempts left: $attemptsLeft",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
