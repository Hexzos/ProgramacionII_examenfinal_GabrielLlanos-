package com.gllprog2examen.registrodecobros.paginas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.gllprog2examen.registrodecobros.ui.ListaRegistrosVM
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gllprog2examen.registrodecobros.R
import com.gllprog2examen.registrodecobros.data.Registro
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class InicioActivity : ComponentActivity() {

    private val listaRegistrosVM: ListaRegistrosVM by viewModels { ListaRegistrosVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRegistroCobrosUI(listaRegistrosVM)
        }
    }

    override fun onResume() {
        super.onResume()
        listaRegistrosVM.obtenerRegistros() // Actualiza los registros al volver a la actividad
    }
}

@Composable
fun AppRegistroCobrosUI(listaRegistrosVM: ListaRegistrosVM) {
    val context = LocalContext.current

    // Se ejecuta una vez al iniciar el composable
    LaunchedEffect(Unit) {
        listaRegistrosVM.obtenerRegistros()
    }

    ListaInicio(
        listaRegistrosVM = listaRegistrosVM,
        onNavigateToForm = { navigateToForm(context) }
    )
}

fun navigateToForm(context: android.content.Context) {
    val intent = Intent(context, FormularioActivity::class.java)
    context.startActivity(intent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListaInicio(listaRegistrosVM: ListaRegistrosVM, onNavigateToForm: () -> Unit) {
    val registros = listaRegistrosVM.registros
    val locale = Locale("es", "CL")
    val decimalFormat = remember { DecimalFormat("#,###", DecimalFormatSymbols(locale)) }

    val noRecordsText = stringResource(id = R.string.no_registros)

    val tipoMedidorToStrAndIcon = mapOf(
        "AGUA" to Pair(stringResource(id = R.string.medidor_agua), R.drawable.outline_water_drop_24),
        "LUZ" to Pair(stringResource(id = R.string.medidor_luz), R.drawable.baseline_lightbulb_outline_24),
        "GAS" to Pair(stringResource(id = R.string.medidor_gas), R.drawable.outline_local_fire_department_24)
    )

    var registroParaEliminar by remember { mutableStateOf<Registro?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = Color(0xFFE1BEE7)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir")
            }
        },
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 50.dp),
        content = { paddingValues ->
            if (registros.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = noRecordsText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(registros) { registro ->
                        Column {
                            Spacer(modifier = Modifier.height(8.dp)) // Primer Spacer
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .combinedClickable(
                                        onClick = { /* do nothing */ },
                                        onLongClick = { registroParaEliminar = registro }
                                    )
                            ) {
                                // Forzar los valores a español para coincidencia exacta
                                val tipoMedidor = when (registro.tipoMedidor) {
                                    "WATER" -> "AGUA"
                                    "LIGHT" -> "LUZ"
                                    else -> registro.tipoMedidor
                                }

                                tipoMedidorToStrAndIcon[tipoMedidor]?.let { (label, iconResource) ->
                                    Icon(
                                        painter = painterResource(id = iconResource),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.width(100.dp) // Ajustar el ancho para alineación fija
                                    )
                                }

                                Text(
                                    text = decimalFormat.format(registro.medidor),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(80.dp) // Ajustar el ancho para alineación fija
                                )
                                Text(
                                    text = registro.fecha,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                        }
                    }
                }

                registroParaEliminar?.let {
                    AlertDialog(
                        onDismissRequest = { registroParaEliminar = null },
                        title = {
                            Text(text = stringResource(id = R.string.confirmar_eliminar_titulo))
                        },
                        text = {
                            Text(text = stringResource(id = R.string.confirmar_eliminar_mensaje))
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    listaRegistrosVM.eliminarRegistro(it)
                                    registroParaEliminar = null
                                    listaRegistrosVM.obtenerRegistros() // Actualiza los registros
                                }
                            ) {
                                Text(text = stringResource(id = R.string.eliminar))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { registroParaEliminar = null }
                            ) {
                                Text(text = stringResource(id = R.string.cancelar))
                            }
                        }
                    )
                }
            }
        }
    )
}
