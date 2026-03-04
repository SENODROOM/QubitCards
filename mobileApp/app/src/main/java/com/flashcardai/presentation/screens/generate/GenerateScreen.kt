package com.flashcardai.presentation.screens.generate

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashcardai.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    onBack: () -> Unit,
    onNavigateToQuiz: (Long) -> Unit,
    viewModel: GenerateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate when deck is saved
    LaunchedEffect(uiState.savedDeckId) {
        uiState.savedDeckId?.let { onNavigateToQuiz(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Flashcard Generator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "generate_step"
            ) { step ->
                when (step) {
                    GenerateStep.INPUT -> InputStep(
                        uiState = uiState,
                        onTopicChange = viewModel::updateTopic,
                        onCountChange = viewModel::updateCardCount,
                        onDifficultyChange = viewModel::updateDifficulty,
                        onGenerate = viewModel::generateCards
                    )
                    GenerateStep.GENERATING -> GeneratingStep(topic = uiState.topic)
                    GenerateStep.PREVIEW -> PreviewStep(
                        uiState = uiState,
                        onDeckTitleChange = viewModel::updateDeckTitle,
                        onRemoveCard = viewModel::removePreviewCard,
                        onBack = viewModel::goBackToInput,
                        onSave = viewModel::saveDeck
                    )
                    GenerateStep.SAVING -> SavingStep()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputStep(
    uiState: GenerateUiState,
    onTopicChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onGenerate: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Hero card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(DeepPurple, Color(0xFFAB47BC)))
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text("🤖", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "What do you want to learn today?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Type any topic and AI will generate perfect flashcards for you instantly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        item {
            // Topic input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Topic", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = uiState.topic,
                    onValueChange = onTopicChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("e.g. Photosynthesis, World War II, Python basics...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    isError = uiState.errorMessage != null,
                    supportingText = uiState.errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onGenerate()
                    }),
                    singleLine = true
                )
            }
        }

        item {
            // Card count slider
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Number of Cards", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepPurple.copy(alpha = 0.1f)
                    ) {
                        Text(
                            "${uiState.cardCount}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = DeepPurple,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Slider(
                    value = uiState.cardCount.toFloat(),
                    onValueChange = { onCountChange(it.toInt()) },
                    valueRange = 5f..20f,
                    steps = 14,
                    colors = SliderDefaults.colors(thumbColor = DeepPurple, activeTrackColor = DeepPurple)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("5", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("20", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            // Difficulty selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Difficulty", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Difficulty.entries.forEach { diff ->
                        val selected = uiState.difficulty == diff
                        val emoji = when (diff) { Difficulty.EASY -> "🟢"; Difficulty.MEDIUM -> "🟡"; else -> "🔴" }
                        FilterChip(
                            selected = selected,
                            onClick = { onDifficultyChange(diff) },
                            label = {
                                Text(
                                    "$emoji ${diff.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
                enabled = uiState.topic.isNotBlank()
            ) {
                Icon(Icons.Filled.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text("Generate Flashcards", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun GeneratingStep(topic: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤖", fontSize = 80.sp)
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(color = DeepPurple, modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
        Spacer(Modifier.height(24.dp))
        Text(
            "Generating flashcards...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "AI is crafting cards about\n\"$topic\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewStep(
    uiState: GenerateUiState,
    onDeckTitleChange: (String) -> Unit,
    onRemoveCard: (Int) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✅", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${uiState.generatedCards.size} cards generated!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Deck Title", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = uiState.deckTitle,
                        onValueChange = onDeckTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter deck name") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        item {
            Text(
                "Preview Cards (swipe to remove)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        itemsIndexed(uiState.generatedCards) { index, card ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepPurple,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            card.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        Divider()
                        Spacer(Modifier.height(6.dp))
                        Text(
                            card.answer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onRemoveCard(index) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Regenerate")
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    enabled = uiState.generatedCards.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save & Study", fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SavingStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💾", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator(color = DeepPurple)
        Spacer(Modifier.height(16.dp))
        Text("Saving your deck...", style = MaterialTheme.typography.titleMedium)
    }
}
