package com.flashcardai.presentation.screens.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashcardai.domain.model.FlashCard
import com.flashcardai.presentation.theme.DeepPurple
import com.flashcardai.presentation.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCardsScreen(
    deckId: Long,
    onBack: () -> Unit,
    viewModel: ManageCardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var cardToDelete by remember { mutableStateOf<FlashCard?>(null) }

    LaunchedEffect(deckId) { viewModel.loadDeck(deckId) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.deck?.title ?: "Manage Cards", fontWeight = FontWeight.Bold)
                        Text(
                            "${uiState.cards.size} cards",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddDialog,
                containerColor = DeepPurple
            ) {
                Icon(Icons.Filled.Add, "Add card", tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepPurple)
            }
        } else if (uiState.cards.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🃏", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(16.dp))
                    Text("No cards yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap + to add your first card", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 88.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(uiState.cards, key = { _, c -> c.id }) { index, card ->
                    CardItem(
                        index = index + 1,
                        card = card,
                        onEdit = { viewModel.showEditDialog(card) },
                        onDelete = { cardToDelete = card }
                    )
                }
            }
        }
    }

    // Add / Edit dialog
    if (uiState.showAddDialog) {
        CardEditDialog(
            card = uiState.editingCard,
            onDismiss = viewModel::dismissDialog,
            onSave = viewModel::saveCard
        )
    }

    // Delete confirmation
    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Delete Card?") },
            text = { Text("This card will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteCard(card); cardToDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { cardToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CardItem(
    index: Int,
    card: FlashCard,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                color = if (card.isMastered) Green.copy(alpha = 0.15f) else DeepPurple.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (card.isMastered) "✓" else "$index",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (card.isMastered) Green else DeepPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(card.question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Divider()
                Spacer(Modifier.height(6.dp))
                Text(
                    card.answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(4.dp))
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Edit, "Edit", modifier = Modifier.size(18.dp), tint = DeepPurple)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Delete, "Delete", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardEditDialog(
    card: FlashCard?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var question by remember { mutableStateOf(card?.question ?: "") }
    var answer by remember { mutableStateOf(card?.answer ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (card != null) "Edit Card" else "New Card", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(question.trim(), answer.trim()) },
                enabled = question.isNotBlank() && answer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple)
            ) { Text(if (card != null) "Save" else "Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
