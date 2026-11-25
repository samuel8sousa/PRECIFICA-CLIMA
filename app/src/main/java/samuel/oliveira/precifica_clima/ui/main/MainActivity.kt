
package samuel.oliveira.precifica_clima.ui.main

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.ui.inicio.InicioFragment
import samuel.oliveira.precifica_clima.ui.historico.HistoricoFragment
import samuel.oliveira.precifica_clima.ui.perfil.PerfilFragment
import samuel.oliveira.precifica_clima.ui.relatorio.RelatorioFragment
import samuel.oliveira.precifica_clima.ui.orcamento.NovoOrcamentoActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Recuperar preferência de tema
        val preferencias = getSharedPreferences(
            "precifica_config",
            MODE_PRIVATE
        )

        val modo = if (preferencias.contains("modo_escuro")) {
            if (preferencias.getBoolean("modo_escuro", false)) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        } else {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(modo)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navigation = findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )

        navigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {
                    mostrarFragment(InicioFragment())
                    true
                }

                R.id.nav_novo -> {
                    val intent = Intent(
                        this,
                        NovoOrcamentoActivity::class.java
                    )

                    startActivity(intent)
                    false
                }

                R.id.nav_historico -> {
                    mostrarFragment(HistoricoFragment())
                    true
                }

                R.id.nav_perfil -> {
                    mostrarFragment(PerfilFragment())
                    true
                }

                R.id.nav_relatorio -> {
                    mostrarFragment(RelatorioFragment())
                    true
                }

                else -> false
            }
        }

        // Tela inicial
        if (savedInstanceState == null) {
            mostrarFragment(InicioFragment())
        }

        // Voltar para início após salvar orçamento
        if (intent.getBooleanExtra("ABRIR_INICIO", false)) {

            navigation.selectedItemId = R.id.nav_inicio
        }
    }

    private fun mostrarFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
