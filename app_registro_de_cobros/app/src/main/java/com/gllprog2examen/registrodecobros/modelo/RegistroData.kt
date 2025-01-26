package com.gllprog2examen.registrodecobros.modelo


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RegistroData(
    val medidor: Long,
    val fecha: String,
    val tipoMedidor: String)

object RegistroDataStore {
    private val _registros = MutableStateFlow(listOf<RegistroData>())
    val registros: StateFlow<List<RegistroData>> get() = _registros

    fun addRegistro(registro: RegistroData) {
        _registros.value = _registros.value + registro
    }
}
