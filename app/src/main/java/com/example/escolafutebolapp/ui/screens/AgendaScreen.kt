package com.example.escolafutebolapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
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
private val DarkGrayGradient = listOf(Color(0xFF374151), Color(0xFF4B5563))

// Mapeamento de tipos de evento para gradientes
private val tipoEventoGradientes = mapOf(
    "Treino" to BlueGradient,
    "Jogo" to OrangeGradient,
    "Reunião" to GreenGradient,
    "Evento Social" to PurpleGradient,
    "Amistoso" to TealGradient,
    "Outro" to IndigoGradient
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    navController: NavController,
    userId: String,
    userTipo: String, // ✅ Adiciona tipo de usuário
    viewModel: AgendaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var eventoParaEditar by remember { mutableStateOf<Evento?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventoParaExcluir by remember { mutableStateOf<Evento?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ CORREÇÃO: Passa o tipo de usuário também
    LaunchedEffect(userId, userTipo) {
        viewModel.setCurrentUser(userId, userTipo)
        viewModel.carregarEventos()
    }

    // ✅ Mostra mensagens de sucesso/erro com Snackbar
    LaunchedEffect(state.mensagemSucesso, state.erro) {
        state.mensagemSucesso?.let { mensagem ->
            snackbarHostState.showSnackbar(
                message = mensagem,
                duration = SnackbarDuration.Short
            )
            viewModel.limparMensagens()
        }

        state.erro?.let { erro ->
            snackbarHostState.showSnackbar(
                message = erro,
                duration = SnackbarDuration.Long
            )
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (state.erro != null) RedSecondary else GreenGradient.first(),
                    contentColor = WhiteText,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
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
        // ✅ CORREÇÃO: Só mostra FAB se usuário pode editar
        floatingActionButton = {
            if (viewModel.usuarioPodeEditar()) {
                FloatingActionButton(
                    onClick = {
                        eventoParaEditar = null
                        showDialog = true
                    },
                    containerColor = RedPrimary,
                    contentColor = WhiteText,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, "Adicionar Evento")
                }
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
                    EstadoCarregando()
                }

                state.eventos.isEmpty() && state.erro == null -> {
                    EstadoVazio(podeEditar = viewModel.usuarioPodeEditar())
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
                        // Seção de eventos futuros
                        if (eventosFuturos.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Próximos Eventos",
                                    count = eventosFuturos.size
                                )
                            }
                        }

                        items(eventosFuturos) { evento ->
                            CardEvento(
                                evento = evento,
                                isPassado = false,
                                podeEditar = viewModel.usuarioPodeEditar(), // ✅ Passa permissão
                                onEdit = {
                                    if (viewModel.usuarioPodeEditar()) {
                                        eventoParaEditar = evento
                                        showDialog = true
                                    }
                                },
                                onDelete = {
                                    if (viewModel.usuarioPodeEditar()) {
                                        eventoParaExcluir = evento
                                        showDeleteDialog = true
                                    }
                                }
                            )
                        }

                        // Seção de eventos passados
                        if (eventosPassados.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Eventos Passados",
                                    count = eventosPassados.size
                                )
                            }
                        }

                        items(eventosPassados) { evento ->
                            CardEvento(
                                evento = evento,
                                isPassado = true,
                                podeEditar = viewModel.usuarioPodeEditar(), // ✅ Passa permissão
                                onEdit = {
                                    if (viewModel.usuarioPodeEditar()) {
                                        eventoParaEditar = evento
                                        showDialog = true
                                    }
                                },
                                onDelete = {
                                    if (viewModel.usuarioPodeEditar()) {
                                        eventoParaExcluir = evento
                                        showDeleteDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ✅ Dialog com callbacks integrados
    if (showDialog) {
        DialogEvento(
            evento = eventoParaEditar,
            onDismiss = { showDialog = false },
            onSave = { evento ->
                if (eventoParaEditar != null) {
                    viewModel.atualizarEvento(
                        evento = evento,
                        onSuccess = { showDialog = false },
                        onError = { /* Mantém dialog aberto */ }
                    )
                } else {
                    viewModel.salvarEvento(
                        evento = evento,
                        onSuccess = { showDialog = false },
                        onError = { /* Mantém dialog aberto */ }
                    )
                }
            }
        )
    }

    if (showDeleteDialog && eventoParaExcluir != null) {
        DialogConfirmarExclusao(
            nomeEvento = eventoParaExcluir!!.tipoEvento,
            onConfirm = {
                viewModel.excluirEvento(
                    id = eventoParaExcluir!!.id,
                    onSuccess = {
                        showDeleteDialog = false
                        eventoParaExcluir = null
                    }
                )
            },
            onDismiss = {
                showDeleteDialog = false
                eventoParaExcluir = null
            }
        )
    }
}

@Composable
fun DialogConfirmarExclusao(nomeEvento: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Aviso",
                    tint = RedPrimary
                )
                Text(
                    "Confirmar Exclusão",
                    color = WhiteText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                "Tem certeza que deseja excluir o evento \"$nomeEvento\"? Esta ação não pode ser desfeita.",
                color = GrayText,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = RedSecondary.copy(alpha = 0.5f)
                    )
                    .background(
                        brush = Brush.horizontalGradient(listOf(RedSecondary, RedPrimary)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onConfirm() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        modifier = Modifier.size(20.dp),
                        tint = WhiteText
                    )
                    Text(
                        "EXCLUIR",
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .background(
                        color = DarkCard,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDismiss() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "CANCELAR",
                    color = GrayText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
private fun EstadoCarregando() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = RedPrimary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Carregando eventos...",
            color = GrayText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ✅ CORREÇÃO: EstadoVazio com mensagem condicional
@Composable
private fun EstadoVazio(podeEditar: Boolean) {
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
            text = if (podeEditar) {
                "Clique no botão + para adicionar seu primeiro evento"
            } else {
                "Aguarde até que um administrador adicione eventos"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
        Box(
            modifier = Modifier
                .background(RedPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge,
                color = RedPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ✅ CORREÇÃO: CardEvento com botões condicionais
@Composable
private fun CardEvento(
    evento: Evento,
    isPassado: Boolean,
    podeEditar: Boolean, // ✅ Nova propriedade
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val alpha = if (isPassado) 0.6f else 1f

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
            // Header
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

                Box(
                    modifier = Modifier
                        .background(
                            brush = if (isPassado)
                                Brush.horizontalGradient(DarkGrayGradient)
                            else
                                Brush.horizontalGradient(GreenGradient),
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

            // Informações
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow(Icons.Default.Schedule, "Horário", evento.horario)
                if (evento.local.isNotBlank()) {
                    InfoRow(Icons.Default.LocationOn, "Local", evento.local)
                }
            }

            // Descrição
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

            // ✅ CORREÇÃO: Só mostra botões se pode editar
            if (podeEditar) {
                Spacer(Modifier.height(8.dp))

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                        Text("Editar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                        Text("Excluir", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    val iconColor = when (icon) {
        Icons.Default.Schedule -> Color(0xFFFB923C)
        Icons.Default.LocationOn -> Color(0xFF10B981)
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
    var showDatePicker by remember { mutableStateOf(false) }

    val tiposEvento = listOf("Treino", "Jogo", "Reunião", "Evento Social", "Amistoso", "Outro")

    // ✅ Validação de formulário
    val formularioValido = dataText.isNotBlank() &&
            local.isNotBlank() &&
            (horarioText.isBlank() || horarioText.matches(Regex("\\d{2}:\\d{2}")))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    if (evento != null) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint = RedPrimary
                )
                Text(
                    if (evento != null) "Editar Evento" else "Novo Evento",
                    color = WhiteText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                // ✅ Campo de data com DatePicker
                OutlinedTextField(
                    value = dataText,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Data *", color = GrayText) },
                    placeholder = { Text("Clique para selecionar a data", color = GrayText.copy(0.5f)) },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, "Data", tint = RedPrimary)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, "Selecionar data", tint = RedPrimary)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayText,
                        unfocusedContainerColor = if (dataText.isBlank()) DarkCard else DarkSurface
                    ),
                    supportingText = {
                        if (dataText.isBlank()) {
                            Text("Campo obrigatório", color = RedPrimary)
                        }
                    }
                )

                OutlinedTextField(
                    value = horarioText,
                    onValueChange = { horarioText = it },
                    label = { Text("Horário (HH:MM) - Opcional", color = GrayText) },
                    placeholder = { Text("14:30", color = GrayText.copy(0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, "Horário", tint = Color(0xFFFB923C)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (horarioText.isNotBlank() && !horarioText.matches(Regex("\\d{2}:\\d{2}"))) {
                            Text("Formato: HH:MM", color = RedPrimary)
                        }
                    },
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
                    label = { Text("Local *", color = GrayText) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, "Local", tint = Color(0xFF10B981)) },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (local.isBlank()) {
                            Text("Campo obrigatório", color = RedPrimary)
                        }
                    },
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
                    label = { Text("Descrição (opcional)", color = GrayText) },
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
            // ✅ Botão Salvar estilizado - SEMPRE VISÍVEL
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (formularioValido) 8.dp else 0.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = if (formularioValido) RedPrimary.copy(alpha = 0.5f) else Color.Transparent
                    )
                    .background(
                        brush = if (formularioValido)
                            Brush.horizontalGradient(listOf(RedSecondary, RedPrimary))
                        else
                            Brush.horizontalGradient(listOf(Color(0xFF666666), Color(0xFF888888))),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = formularioValido) {
                        if (formularioValido) {
                            onSave(
                                Evento(
                                    id = evento?.id ?: "",
                                    tipoEvento = tipoEvento,
                                    dataEvento = dataText,
                                    horario = horarioText.ifBlank { "Não informado" },
                                    local = local,
                                    descricao = descricao
                                )
                            )
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Salvar",
                        modifier = Modifier.size(20.dp),
                        tint = if (formularioValido) WhiteText else GrayText
                    )
                    Text(
                        "Salvar",
                        color = if (formularioValido) WhiteText else GrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        dismissButton = {
            // ✅ Botão Cancelar estilizado
            Box(
                modifier = Modifier
                    .background(
                        color = DarkCard,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancelar",
                        modifier = Modifier.size(20.dp),
                        tint = GrayText
                    )
                    Text(
                        "Cancelar",
                        color = GrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    )

    // ✅ DatePicker Dialog estilizado
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

        // Dialog customizado para o DatePicker
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    "Selecionar Data",
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            // ✅ APENAS propriedades que existem oficialmente
                            containerColor = DarkSurface,
                            titleContentColor = WhiteText,
                            headlineContentColor = WhiteText,
                            weekdayContentColor = GrayText,
                            subheadContentColor = GrayText,
                            yearContentColor = WhiteText,
                            selectedYearContentColor = WhiteText,
                            selectedYearContainerColor = RedPrimary,
                            dayContentColor = WhiteText,
                            selectedDayContentColor = WhiteText,
                            selectedDayContainerColor = RedPrimary,
                            todayContentColor = RedPrimary,
                            todayDateBorderColor = RedPrimary
                        ),
                        modifier = Modifier.height(400.dp)
                    )

                    // Data selecionada preview
                    if (confirmEnabled.value) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkCard, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Data selecionada: ${
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        .format(Date(datePickerState.selectedDateMillis!!))
                                }",
                                color = WhiteText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = if (confirmEnabled.value) 8.dp else 0.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            brush = if (confirmEnabled.value)
                                Brush.horizontalGradient(listOf(RedSecondary, RedPrimary))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF666666), Color(0xFF888888))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = confirmEnabled.value) {
                            if (confirmEnabled.value) {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    dataText = dateFormat.format(Date(millis))
                                }
                                showDatePicker = false
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CONFIRMAR",
                        color = if (confirmEnabled.value) WhiteText else GrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .background(
                            color = DarkCard,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showDatePicker = false }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CANCELAR",
                        color = GrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}