package com.gllprog2examen.registrodecobros.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gllprog2examen.registrodecobros.data.AplicacionRegistroBD
import com.gllprog2examen.registrodecobros.data.Registro
import com.gllprog2examen.registrodecobros.data.RegistroDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaRegistrosVM(private val registroDao: RegistroDao) : ViewModel() {

    var registros by mutableStateOf(listOf<Registro>())

    init {
        obtenerRegistros()
    }

    fun insertarRegistro(registro: Registro) {
        viewModelScope.launch(Dispatchers.IO) {
            registroDao.insertar(registro)
            obtenerRegistros() // Actualiza la lista de registros
        }
    }

    fun obtenerRegistros() {
        viewModelScope.launch(Dispatchers.IO) {
            val registrosObtenidos = registroDao.obtenerTodos()
            withContext(Dispatchers.Main) {
                registros = registrosObtenidos
            }
        }
    }

    fun eliminarRegistro(registro: Registro) {
        viewModelScope.launch(Dispatchers.IO) {
            registroDao.eliminar(registro)
            obtenerRegistros() // Actualiza la lista de registros después de eliminar
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacionRegistroBD = (this[APPLICATION_KEY] as AplicacionRegistroBD)
                ListaRegistrosVM(aplicacionRegistroBD.registroDao)
            }
        }
    }
}
