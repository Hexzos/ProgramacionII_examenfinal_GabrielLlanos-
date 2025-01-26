package com.gllprog2examen.registrodecobros.data

import android.app.Application
import androidx.room.Room

class AplicacionRegistroBD : Application() {

    val db by lazy { Room.databaseBuilder(
        this, RegistroBD::class.java, "registros.db").build() }
    val registroDao by lazy {db.registroDao()}



}