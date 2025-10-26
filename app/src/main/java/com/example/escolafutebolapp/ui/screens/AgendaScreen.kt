package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escolafutebolapp.models.Evento
import com.example.escolafutebolapp.viewmodel.AgendaViewModel

// 🎨 Cores do tema Dark
private val DarkBackground = Color(0xFF0D0D0D)
private val DarkSurface = Color(0xFF1A1A1A)
private val RedPrimary = Color(0xFFE84545)
private val RedSecondary = Color(0xFFCC3333)
private val WhiteText = Color(0xFFFFFFFF)
private val GrayText = Color(0xFFB3B3B3)
private val DarkCard = Color(0xFF262626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    navController: NavController,
    viewModel: AgendaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var eventoParaEditar by remember { mutableStateOf<Evento?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventoParaExcluir by remember { mutableStateOf<Evento?>(null) }

    LaunchedEffect(Unit) {
        viewModel.carregarEventos()
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Minha Agenda",
                        color = WhiteText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = WhiteText)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.carregarEventos() },
                        enabled = !state.carregando
                    ) {
                        Icon(Icons.Default.Refresh, "Recarregar", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventoParaEditar = null
                    showDialog = true
                },
                containerColor = RedPrimary,
                contentColor = WhiteText
            ) {
                Icon(Icons.Default.Add, "Adicionar Evento")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkSurface, DarkBackground)
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                state.carregando -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = RedPrimary)
                        Spacer(Modifier.height(16.dp))
                        Text("Carregando eventos...", color = GrayText)
                    }
                }

                state.erro != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌ Erro ao carregar",
                            style = MaterialTheme.typography.headlineSmall,
                            color = RedPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(state.erro!!, color = GrayText)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.carregarEventos() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedPrimary
                            )
                        ) {
                            Text("Tentar Novamente")
                        }
                    }
                }

                state.eventos.isEmpty() -> {
                    EstadoVazio()
                }

                else -> {
                    val (eventosFuturos, eventosPassados) = state.eventos.partition {
                        it.isEventoFuturo()
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(eventosFuturos) { evento ->
                            CardEvento(
                                evento = evento,
                                isPassado = false,
                                onEdit = {
                                    eventoParaEditar = evento
                                    showDialog = true
                                },
                                onDelete = {
                                    eventoParaExcluir = evento
                                    showDeleteDialog = true
                                }
                            )
                        }

                        items(eventosPassados) { evento ->
                            CardEvento(
                                evento = evento,
                                isPassado = true,
                                onEdit = {
                                    eventoParaEditar = evento
                                    showDialog = true
                                },
                                onDelete = {
                                    eventoParaExcluir = evento
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        DialogEvento(
            evento = eventoParaEditar,
            onDismiss = { showDialog = false },
            onSave = { evento ->
                if (eventoParaEditar != null) {
                    viewModel.atualizarEvento(evento)
                } else {
                    viewModel.salvarEvento(evento)
                }
                showDialog = false
            }
        )
    }

    if (showDeleteDialog && eventoParaExcluir != null) {
        DialogConfirmarExclusao(
            onConfirm = {
                viewModel.excluirEvento(eventoParaExcluir!!.id)
                showDeleteDialog = false
                eventoParaExcluir = null
            },
            onDismiss = {
                showDeleteDialog = false
                eventoParaExcluir = null
            }
        )
    }
}

@Composable
fun DialogConfirmarExclusao(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
private fun EstadoVazio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(DarkCard, RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📅", fontSize = 64.sp)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Nenhum evento cadastrado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Clique no botão + para adicionar seu primeiro evento",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText
        )
    }
}

@Composable
private fun CardEvento(
    evento: Evento,
    isPassado: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val alpha = if (isPassado) 0.5f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard.copy(alpha = alpha)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header com ícone e título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(RedPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = evento.getIconePorTipo(),
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = evento.tipoEvento,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )
                    if (isPassado) {
                        Text(
                            text = "Evento Passado",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }
                }
            }

            Divider(color = GrayText.copy(alpha = 0.3f), thickness = 1.dp)

            // Informações
            InfoRow(Icons.Default.DateRange, "Data", evento.dataEvento)
            InfoRow(Icons.Default.Schedule, "Horário", evento.horario)
            if (evento.local.isNotBlank()) {
                InfoRow(Icons.Default.LocationOn, "Local", evento.local)
            }

            if (evento.descricao.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = evento.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // Botões
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WhiteText
                    )
                ) {
                    Icon(Icons.Default.Edit, "Editar", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar")
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedSecondary
                    )
                ) {
                    Icon(Icons.Default.Delete, "Excluir", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Excluir")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = RedPrimary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = WhiteText
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogEvento(
    evento: Evento?,
    onDismiss: () -> Unit,
    onSave: (Evento) -> Unit
) {
    var tipoEvento by remember { mutableStateOf(evento?.tipoEvento ?: "Treino") }

    // ✅ VARIÁVEIS MANUAIS para data e horário
    var dataText by remember { mutableStateOf(evento?.dataEvento ?: "") }
    var horarioText by remember { mutableStateOf(evento?.horario ?: "") }

    var local by remember { mutableStateOf(evento?.local ?: "") }
    var descricao by remember { mutableStateOf(evento?.descricao ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val tiposEvento = listOf("Treino", "Jogo", "Reunião", "Evento Social", "Amistoso", "Outro")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                if (evento != null) "Editar Evento" else "Novo Evento",
                color = WhiteText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dropdown de tipo
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = tipoEvento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Evento", color = GrayText) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                "Dropdown",
                                tint = RedPrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WhiteText,
                            unfocusedTextColor = WhiteText,
                            focusedBorderColor = RedPrimary,
                            unfocusedBorderColor = GrayText
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DarkCard)
                    ) {
                        tiposEvento.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo, color = WhiteText) },
                                onClick = {
                                    tipoEvento = tipo
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ✅ CAMPO DATA COM FORMATAÇÃO MANUAL
                OutlinedTextField(
                    value = dataText,
                    onValueChange = { newValue ->
                        dataText = formatarDataManual(newValue)
                    },
                    label = { Text("Data (DD/MM/AAAA)", color = GrayText) },
                    placeholder = { Text("31/12/2024") },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, "Data", tint = RedPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayText
                    )
                )

                // ✅ CAMPO HORÁRIO COM FORMATAÇÃO MANUAL
                OutlinedTextField(
                    value = horarioText,
                    onValueChange = { newValue ->
                        horarioText = formatarHorarioManual(newValue)
                    },
                    label = { Text("Horário (HH:MM)", color = GrayText) },
                    placeholder = { Text("14:30") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, "Horário", tint = RedPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayText
                    )
                )

                OutlinedTextField(
                    value = local,
                    onValueChange = { local = it },
                    label = { Text("Local", color = GrayText) },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, "Local", tint = RedPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayText
                    )
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição", color = GrayText) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayText
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val novoEvento = Evento(
                        id = evento?.id ?: "",
                        tipoEvento = tipoEvento,
                        dataEvento = dataText,
                        horario = if (horarioText.isNotBlank()) horarioText else "Não informado",
                        local = local,
                        descricao = descricao
                    )
                    onSave(novoEvento)
                },
                enabled = dataText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary
                )
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrayText)
            }
        }
    )
}

// ✅ FUNÇÕES MANUAIS DE FORMATAÇÃO (melhoradas)
private fun formatarDataManual(input: String): String {
    val clean = input.filter { it.isDigit() }
    if (clean.length > 8) return input

    return buildString {
        for (i in clean.indices) {
            when (i) {
                2, 4 -> append("/")
            }
            append(clean[i])
        }
    }
}

private fun formatarHorarioManual(input: String): String {
    val clean = input.filter { it.isDigit() }
    if (clean.length > 4) return input

    return buildString {
        for (i in clean.indices) {
            when (i) {
                2 -> append(":")
            }
            append(clean[i])
        }
    }
}