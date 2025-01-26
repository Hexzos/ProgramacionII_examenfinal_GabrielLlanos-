package com.gllprog2examen.registrodecobros

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.gllprog2examen.registrodecobros.paginas.InicioActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
        finish()
    }
}
