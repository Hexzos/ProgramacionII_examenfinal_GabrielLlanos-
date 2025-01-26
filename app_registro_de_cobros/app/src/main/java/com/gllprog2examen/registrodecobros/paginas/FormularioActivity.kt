package com.gllprog2examen.registrodecobros.paginas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gllprog2examen.registrodecobros.R
import com.gllprog2examen.registrodecobros.data.Registro
import com.gllprog2examen.registrodecobros.ui.ListaRegistrosVM
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormularioActivity : ComponentActivity() {

    private val listaRegistrosVM: ListaRegistrosVM by viewModels { ListaRegistrosVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormularioScreen(onBack = { finish() }, listaRegistrosVM = listaRegistrosVM)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormularioScreen(onBack: () -> Unit, listaRegistrosVM: ListaRegistrosVM) {
        var medidor by remember { mutableStateOf("") }
        var fecha by remember { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        val current = LocalContext.current
        val calendar = Calendar.getInstance()

        LaunchedEffect(datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis?.let {
                calendar.timeInMillis = it
                calendar.add(Calendar.DAY_OF_MONTH, 1)  // Ajuste para evitar el desfase
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fecha = dateFormat.format(calendar.time)
                showDatePicker = false  // Cerrar el calendario después de seleccionar una fecha
            }
        }

        var tipoMedidor by remember { mutableStateOf("AGUA") }
        val tipos = listOf(
            stringResource(id = R.string.medidor_agua),
            stringResource(id = R.string.medidor_luz),
            stringResource(id = R.string.medidor_gas)
        )

        val isMedidorValid by remember { derivedStateOf { medidor.all {
            it.isDigit() } && medidor.isNotEmpty() } }
        val isFechaValid by remember { derivedStateOf {
            fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) } }

        val buttonTextCalcularRegistro = stringResource(id = R.string.btn_text_calcular_registro)
        val medidorLabel = stringResource(id = R.string.medidor_input_text)
        val fechaLabel = stringResource(id = R.string.fecha_input_text)
        val tipoMedidorLabel = stringResource(id = R.string.tipo_medidor)
        val errorMedidorInvalido = stringResource(id = R.string.error_medidor_invalido)
        val errorFechaInvalida = stringResource(id = R.string.error_fecha_invalida)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )
                    },
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            TextField(
                                value = medidor,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                        medidor = newValue
                                    }
                                },
                                label = { Text(text = medidorLabel) },
                                isError = !isMedidorValid,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = fecha,
                                    onValueChange = { },
                                    label = { Text(text = fechaLabel) },
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Select date"
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                )

                                if (showDatePicker) {
                                    Popup(
                                        onDismissRequest = { showDatePicker = false },
                                        alignment = Alignment.TopStart
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(elevation = 4.dp)
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(16.dp)
                                        ) {
                                            DatePicker(
                                                state = datePickerState,
                                                showModeToggle = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    }

                    item {
                        Column {
                            if (!isMedidorValid) {
                                Text(
                                    text = errorMedidorInvalido,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            if (!isFechaValid) {
                                Text(
                                    text = errorFechaInvalida,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = tipoMedidorLabel,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    items(tipos) { tipo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = tipoMedidor == tipo,
                                onClick = { tipoMedidor = tipo }
                            )
                            Text(text = tipo)
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    if (isMedidorValid && isFechaValid) {
                                        val nuevoRegistro = Registro(
                                            medidor = medidor.toLongOrNull() ?: 0L,
                                            fecha = LocalDate.parse(fecha).toString(),
                                            tipoMedidor = tipoMedidor
                                        )
                                        listaRegistrosVM.insertarRegistro(nuevoRegistro)
                                        onBack()
                                    }
                                },
                            ) {
                                Text(buttonTextCalcularRegistro)
                            }
                        }
                    }
                }
            }
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun FormularioScreenPreview() {
        FormularioScreen(onBack = {}, listaRegistrosVM = viewModel(factory = ListaRegistrosVM.Factory))
    }
}
