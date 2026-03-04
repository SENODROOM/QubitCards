package com.flashcardai.presentation.screens.quiz

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashcardai.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    deckId: Long,
    onBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(deckId) { viewModel.loadDeck(deckId) }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DeepPurple)
        }
        return
    }

    if (uiState.isCompleted) {
        QuizCompletedScreen(
            uiState = uiState,
            onRestart = viewModel::restartQuiz,
            onResetProgress = viewModel::resetProgress,
            onBack = onBack
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.deck?.title ?: "Study",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::resetProgress) {
                        Icon(Icons.Outlined.Refresh, "Reset progress")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // Progress
            ProgressSection(uiState = uiState)

            Spacer(Modifier.height(24.dp))

            // Flashcard
            uiState.currentCard?.let { card ->
                FlipCard(
                    question = card.question,
                    answer = card.answer,
                    isFlipped = uiState.isFlipped,
                    isMastered = card.isMastered,
                    onFlip = viewModel::flipCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Show answer button
            if (!uiState.isFlipped) {
                Button(
                    onClick = viewModel::flipCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Outlined.Visibility, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Show Answer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else {
                // Mastered button
                val card = uiState.currentCard
                val isMastered = card?.isMastered == true
                Button(
                    onClick = { card?.let { viewModel.toggleMastered(it.id) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMastered) Green else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isMastered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(if (isMastered) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isMastered) "Mastered! ✓" else "Mark as Mastered",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = viewModel::previousCard,
                    enabled = uiState.currentIndex > 0,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.currentIndex > 0)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        Icons.Filled.ChevronLeft, "Previous",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${uiState.currentIndex + 1} / ${uiState.cards.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "tap card to flip",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = viewModel::nextCard,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DeepPurple)
                ) {
                    Icon(
                        Icons.Filled.ChevronRight, "Next",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(uiState: QuizUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${uiState.masteredCount} mastered",
                style = MaterialTheme.typography.labelMedium,
                color = Green,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${(uiState.progress * 100).toInt()}% complete",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = DeepPurple,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun FlipCard(
    question: String,
    answer: String,
    isFlipped: Boolean,
    isMastered: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable { onFlip() }
    ) {
        if (rotation <= 90f) {
            // Front face - Question
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isMastered) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Green.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "✓ Mastered",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Green,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "QUESTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepPurple,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    }
                    Text(
                        "Tap to reveal",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Back face - Answer (mirrored)
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(DeepPurple, Color(0xFF9C6FFF))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "ANSWER",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            answer,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCompletedScreen(
    uiState: QuizUiState,
    onRestart: () -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit
) {
    val score = if (uiState.cards.isNotEmpty())
        (uiState.masteredCount * 100) / uiState.cards.size
    else 0

    val emoji = when {
        score >= 80 -> "🏆"
        score >= 60 -> "👏"
        score >= 40 -> "💪"
        else -> "📚"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "You went through ${uiState.cards.size} cards",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // Stats card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ResultStat(value = "${uiState.cards.size}", label = "Total Cards")
                ResultStat(value = "$score%", label = "Mastered Rate")
                ResultStat(value = "${uiState.masteredInSession.size}", label = "This Session")
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple)
        ) {
            Icon(Icons.Filled.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Study Again", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.Home, null)
            Spacer(Modifier.width(8.dp))
            Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ResultStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = DeepPurple)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
