package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.BorderStroke
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
import java.text.SimpleDateFormat
import java.util.*

// 🎨 Cores do tema Dark
private val DarkBackground = Color(0xFF0D0D0D)
private val DarkSurface = Color(0xFF1A1A1A)
private val RedPrimary = Color(0xFFE84545)
private val RedSecondary = Color(0xFFCC3333)
private val WhiteText = Color(0xFFFFFFFF)
private val GrayText = Color(0xFFB3B3B3)
private val DarkCard = Color(0xFF262626)

// 🎨 Paleta de cores diversificada para os gradientes
private val BlueGradient = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
private val GreenGradient = listOf(Color(0xFF059669), Color(0xFF10B981))
private val PurpleGradient = listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6))
private val OrangeGradient = listOf(Color(0xFFEA580C), Color(0xFFFB923C))
private val TealGradient = listOf(Color(0xFF0D9488), Color(0xFF14B8A6))
private val PinkGradient = listOf(Color(0xFFBE185D), Color(0xFFEC4899))
private val IndigoGradient = listOf(Color(0xFF3730A3), Color(0xFF6366F1))
private val YellowGradient = listOf(Color(0xFFD97706), Color(0xFFFBBF24))

// ✅ Badge passado usa cinza escuro
private val DarkGrayGradient = listOf(Color(0xFF374151), Color(0xFF4B5563))

// Mapeamento de tipos de evento para gradientes (SEM VERMELHO)
private val tipoEventoGradientes = mapOf(
    "Treino" to BlueGradient,
    "Jogo" to OrangeGradient,  // ✅ Mudou de vermelho para laranja
    "Reunião" to GreenGradient,
    "Evento Social" to PurpleGradient,
    "Amistoso" to TealGradient,
    "Outro" to IndigoGradient
)

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
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(RedPrimary.copy(alpha = 0.3f), DarkCard)
                    ),
                    RoundedCornerShape(60.dp)
                ),
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
    val alpha = if (isPassado) 0.6f else 1f

    // Obtém o gradiente baseado no tipo de evento (SEM VERMELHO)
    val gradientColors = tipoEventoGradientes[evento.tipoEvento]
        ?: when (evento.id.hashCode() % 8) {
            0 -> BlueGradient
            1 -> GreenGradient
            2 -> PurpleGradient
            3 -> OrangeGradient
            4 -> TealGradient
            5 -> PinkGradient
            6 -> YellowGradient
            else -> IndigoGradient
        }

    // Aplica alpha nas cores do gradiente
    val gradientColorsWithAlpha = gradientColors.map { it.copy(alpha = alpha) }
    val cardGradient = Brush.linearGradient(colors = gradientColorsWithAlpha)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = gradientColors.first().copy(alpha = 0.3f)
            )
            .background(cardGradient, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header com badge de status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
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
                        Text(
                            text = evento.dataEvento,
                            style = MaterialTheme.typography.bodySmall,
                            color = WhiteText.copy(alpha = 0.8f)
                        )
                    }
                }

                // ✅ Badge de status com cores diferentes
                Box(
                    modifier = Modifier
                        .background(
                            brush = if (isPassado)
                                Brush.horizontalGradient(DarkGrayGradient) // ✅ Cinza escuro
                            else
                                Brush.horizontalGradient(GreenGradient), // ✅ Verde
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isPassado) "PASSADO" else "PRÓXIMO",
                        style = MaterialTheme.typography.labelSmall,
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Divider(color = WhiteText.copy(alpha = 0.2f), thickness = 1.dp)

            // Informações principais
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow(Icons.Default.Schedule, "Horário", evento.horario)
                if (evento.local.isNotBlank()) {
                    InfoRow(Icons.Default.LocationOn, "Local", evento.local)
                }
            }

            // Descrição (se houver)
            if (evento.descricao.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = evento.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhiteText.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // ✅ Botões de ação corrigidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão Editar - Azul
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WhiteText,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.6f),
                                Color(0xFF60A5FA).copy(alpha = 0.8f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF60A5FA)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Editar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Botão Excluir - Vermelho
                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = RedSecondary
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(RedSecondary, RedPrimary)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFEF2F2)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Excluir",
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    // Define cor baseada no tipo de ícone
    val iconColor = when (icon) {
        Icons.Default.Schedule -> Color(0xFFFB923C) // Laranja
        Icons.Default.LocationOn -> Color(0xFF10B981) // Verde
        else -> WhiteText.copy(alpha = 0.8f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = WhiteText.copy(alpha = 0.8f),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = WhiteText,
            fontWeight = FontWeight.Medium
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
                            Icon(Icons.Default.ArrowDropDown, "Dropdown", tint = RedPrimary)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
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

                OutlinedTextField(
                    value = dataText,
                    onValueChange = { dataText = formatarDataManual(it) },
                    label = { Text("Data (DD/MM/AAAA)", color = GrayText) },
                    placeholder = { Text("31/12/2024", color = GrayText.copy(0.5f)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, "Data", tint = RedPrimary) },
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
                    value = horarioText,
                    onValueChange = { horarioText = formatarHorarioManual(it) },
                    label = { Text("Horário (HH:MM)", color = GrayText) },
                    placeholder = { Text("14:30", color = GrayText.copy(0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, "Horário", tint = Color(0xFFFB923C)) },
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
                    leadingIcon = { Icon(Icons.Default.LocationOn, "Local", tint = Color(0xFF10B981)) },
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
                    onSave(Evento(
                        id = evento?.id ?: "",
                        tipoEvento = tipoEvento,
                        dataEvento = dataText,
                        horario = horarioText.ifBlank { "Não informado" },
                        local = local,
                        descricao = descricao
                    ))
                },
                enabled = dataText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
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

@Composable
private fun DialogConfirmarExclusao(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Confirmar Exclusão", color = WhiteText, fontWeight = FontWeight.Bold) },
        text = { Text("Tem certeza que deseja excluir este evento?", color = GrayText) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = RedSecondary)) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GrayText) }
        }
    )
}

private fun formatarDataManual(input: String): String {
    val digitos = input.filter { it.isDigit() }.take(8)
    return when (digitos.length) {
        0 -> ""
        1, 2 -> digitos
        3, 4 -> "${digitos.substring(0, 2)}/${digitos.substring(2)}"
        else -> "${digitos.substring(0, 2)}/${digitos.substring(2, 4)}/${digitos.substring(4)}"
    }
}

private fun formatarHorarioManual(input: String): String {
    val digitos = input.filter { it.isDigit() }.take(4)
    return when (digitos.length) {
        0 -> ""
        1, 2 -> digitos
        else -> "${digitos.substring(0, 2)}:${digitos.substring(2)}"
    }
}

fun Evento.isEventoFuturo(): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val eventoDateTime = dateFormat.parse("$dataEvento $horario")
        eventoDateTime?.after(Date()) ?: false
    } catch (e: Exception) {
        false
    }
}

fun Evento.getIconePorTipo(): String {
    return when (tipoEvento) {
        "Treino" -> "⚽"
        "Jogo" -> "🥅"
        "Reunião" -> "👥"
        "Evento Social" -> "🎉"
        "Amistoso" -> "🤝"
        else -> "📅"
    }
}