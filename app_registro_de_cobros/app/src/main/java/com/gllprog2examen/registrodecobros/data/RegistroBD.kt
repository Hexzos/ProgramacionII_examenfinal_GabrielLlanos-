package com.gllprog2examen.registrodecobros.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Registro::class], version = 1, exportSchema = false)
abstract class RegistroBD : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
}
