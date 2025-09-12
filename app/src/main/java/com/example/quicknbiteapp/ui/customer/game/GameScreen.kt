package com.example.quicknbiteapp.ui.customer.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.quicknbiteapp.viewModel.CartViewModel
import kotlin.random.Random
import com.example.quicknbiteapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    cartViewModel: CartViewModel,
    onGameEnd: () -> Unit
) {
    var playsLeft by remember { mutableIntStateOf(5) }

    var score by remember { mutableIntStateOf(0) }
    var targetX by remember { mutableIntStateOf(Random.nextInt(50, 300)) }
    var targetY by remember { mutableIntStateOf(Random.nextInt(100, 500)) }
    var gameFinished by remember { mutableStateOf(false) }
    var didWin by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var gameStarted by remember { mutableStateOf(false) }

    val pointsAwarded = 10
    val winningClicks = 20

    // Timer effect
    LaunchedEffect(gameStarted, gameFinished) {
        if (gameStarted && !gameFinished) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
                if (timeLeft == 0 && !gameFinished) {
                    gameFinished = true
                    didWin = score >= winningClicks
                    if (didWin) {
                        playsLeft--
                        cartViewModel.addPoints(pointsAwarded)
                    } else {
                        playsLeft--
                    }
                }
            }
        }
    }

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (playsLeft <= 0) {
                // Out of plays
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.play_left),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!gameFinished) {
                        if (!gameStarted) {
                            // Start screen
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Tap Start! You need $winningClicks clicks in 10 seconds.\nYou have $playsLeft plays left today.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = {
                                        gameStarted = true
                                        score = 0
                                        timeLeft = 10
                                        targetX = Random.nextInt(50, 300)
                                        targetY = Random.nextInt(100, 500)
                                    }
                                ) {
                                    Text(stringResource(R.string.start_game),
                                    )
                                }
                            }
                        } else {
                            // Game in progress
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .offset(x = targetX.dp, y = targetY.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .clickable {
                                        if (!gameFinished) {
                                            score++
                                            targetX = Random.nextInt(50, 300)
                                            targetY = Random.nextInt(100, 500)

                                            if (score >= winningClicks) {
                                                gameFinished = true
                                                didWin = true
                                                playsLeft--
                                                cartViewModel.addPoints(pointsAwarded)
                                            }
                                        }
                                    }
                            )
                        }
                    } else {
                        // Game over screen
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
                            } else {
                                Text(
                                    stringResource(R.string.you_lost),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    "Score: $score / $winningClicks",
                                    fontSize = 22.sp,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    // Reset to start screen (not full navigation)
                                    gameFinished = false
                                    gameStarted = false
                                    didWin = false
                                    score = 0
                                    timeLeft = 10
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(stringResource(R.string.play_again))
                            }
                        }
                    }

                    // HUD (top-right)
                    Column(
                        modifier = Modifier.align(Alignment.TopEnd),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (gameStarted && !gameFinished) {
                            Text(
                                "Time left: ${timeLeft}s",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text("Clicks: $score / $winningClicks")
                        }
                    }
                }
            }
        }
    }
}
