package com.gllprog2examen.registrodecobros.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroDao {
    @Insert
    suspend fun insertar(registro: Registro)

    @Query("SELECT * FROM registro")
    suspend fun obtenerTodos(): List<Registro>

    @Delete
    suspend fun eliminar(registro: Registro)
}
