package com.gllprog2examen.registrodecobros.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro")
data class Registro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medidor: Long,
    val fecha: String,
    val tipoMedidor: String
)
