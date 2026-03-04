package com.flashcardai.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashcardai.domain.model.Deck
import com.flashcardai.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGenerate: () -> Unit,
    onNavigateToQuiz: (Long) -> Unit,
    onNavigateToManage: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var deckToDelete by remember { mutableStateOf<Deck?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.deletedDeckMessage) {
        uiState.deletedDeckMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToGenerate,
                icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
                text = { Text("AI Generate", fontWeight = FontWeight.Bold) },
                containerColor = DeepPurple,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            HomeHeader()

            // Stats Banner
            if (uiState.decks.isNotEmpty()) {
                StatsBanner(decks = uiState.decks)
            }

            // Deck List
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DeepPurple)
                }
            } else if (uiState.decks.isEmpty()) {
                EmptyHomeState(onGenerate = onNavigateToGenerate)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            "My Decks",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(uiState.decks, key = { it.id }) { deck ->
                        DeckCard(
                            deck = deck,
                            onStudy = { onNavigateToQuiz(deck.id) },
                            onManage = { onNavigateToManage(deck.id) },
                            onDelete = { deckToDelete = deck }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Delete confirmation dialog
    deckToDelete?.let { deck ->
        AlertDialog(
            onDismissRequest = { deckToDelete = null },
            title = { Text("Delete Deck?") },
            text = { Text("\"${deck.title}\" and all its cards will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteDeck(deck); deckToDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deckToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(colors = listOf(DeepPurple, Color(0xFF9C6FFF)))
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡", fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "FlashCard AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Study smarter with AI-powered flashcards",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatsBanner(decks: List<Deck>) {
    val totalCards = decks.sumOf { it.cardCount }
    val totalMastered = decks.sumOf { it.masteredCount }
    val progress = if (totalCards > 0) totalMastered.toFloat() / totalCards else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = "${decks.size}", label = "Decks", icon = "📚")
            StatDivider()
            StatItem(value = "$totalCards", label = "Cards", icon = "🃏")
            StatDivider()
            StatItem(value = "$totalMastered", label = "Mastered", icon = "✅")
            StatDivider()
            StatItem(value = "${(progress * 100).toInt()}%", label = "Progress", icon = "📈")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatDivider() {
    Divider(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckCard(
    deck: Deck,
    onStudy: () -> Unit,
    onManage: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val progress = if (deck.cardCount > 0) deck.masteredCount.toFloat() / deck.cardCount else 0f

    val deckColors = listOf(
        Pair(DeepPurple, Color(0xFF9C6FFF)),
        Pair(Coral, Color(0xFFFF8FAB)),
        Pair(Teal, Color(0xFF80DEEA)),
        Pair(Color(0xFF26A69A), Color(0xFF80CBC4)),
    )
    val colorPair = deckColors[(deck.id % deckColors.size).toInt()]

    Card(
        onClick = onStudy,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(colorPair.first, colorPair.second))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (deck.isAiGenerated) "🤖" else "📖",
                        fontSize = 22.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        deck.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${deck.cardCount} cards · ${deck.masteredCount} mastered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (deck.isAiGenerated) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeepPurple.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "AI",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Study") },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = { expanded = false; onStudy() }
                    )
                    DropdownMenuItem(
                        text = { Text("Manage Cards") },
                        leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                        onClick = { expanded = false; onManage() }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) },
                        onClick = { expanded = false; onDelete() }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = colorPair.first,
                trackColor = colorPair.first.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onManage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = onStudy,
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPair.first),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Study Now", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeState(onGenerate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📚", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "No decks yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap the AI Generate button to create your first flashcard deck instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onGenerate,
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Icon(Icons.Filled.AutoAwesome, null)
            Spacer(Modifier.width(8.dp))
            Text("Generate with AI", fontWeight = FontWeight.Bold)
        }
    }
}
