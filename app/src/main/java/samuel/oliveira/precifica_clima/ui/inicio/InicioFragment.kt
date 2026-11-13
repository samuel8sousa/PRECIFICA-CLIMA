
package samuel.oliveira.precifica_clima.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView

import java.text.NumberFormat
import java.util.Locale

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.repository.PerfilRepository
import samuel.oliveira.precifica_clima.ui.orcamento.NovoOrcamentoActivity

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // ======================================
        // 1. LOCALIZAR OS BOTÕES
        // ======================================

        val btnNovo = view.findViewById<Button>(
            R.id.btnNovoOrcamento
        )

        val btnPerfil = view.findViewById<Button>(
            R.id.btnAjustarPerfil
        )

        // ======================================
        // 2. ABRIR A TELA DE NOVO ORÇAMENTO
        // ======================================

        btnNovo.setOnClickListener {

            val intent = Intent(
                requireContext(),
                NovoOrcamentoActivity::class.java
            )

            startActivity(intent)
        }

        // ======================================
        // 3. ABRIR A TELA DE PERFIL
        // ======================================

        btnPerfil.setOnClickListener {

            val navigation = requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottomNavigation
                )

            navigation.selectedItemId = R.id.nav_perfil
        }
    }

    // ======================================
    // 4. ATUALIZAR AO RETORNAR PARA O INÍCIO
    // ======================================

    override fun onResume() {
        super.onResume()

        val tela = view ?: return

        atualizarIndicadores(tela)
    }

    // ======================================
    // 5. CONSULTAR OS DADOS SALVOS
    // ======================================

    private fun atualizarIndicadores(tela: View) {

        val perfil = PerfilRepository(
            requireContext()
        ).carregar()

        val moeda = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("pt-BR")
        )

        // Evita divisão por zero
        val valorHora = if (perfil.horasFaturaveis > 0) {
            perfil.calcularCustoHora()
        } else {
            0.0
        }

        // Atualiza o custo da hora
        tela.findViewById<TextView>(
            R.id.txtValorHora
        ).text = "${moeda.format(valorHora)}/h"

        // Atualiza a meta mensal
        tela.findViewById<TextView>(
            R.id.txtMetaMensal
        ).text = "Meta mensal: ${moeda.format(perfil.remuneracao)}"
    }
}
