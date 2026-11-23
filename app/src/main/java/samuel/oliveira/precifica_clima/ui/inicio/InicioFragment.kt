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

        // ==========================================
        // 1. IDENTIFICAR BOTÕES PRINCIPAIS
        // ==========================================

        val btnNovo = view.findViewById<Button>(
            R.id.btnNovoOrcamento
        )

        val btnPerfil = view.findViewById<Button>(
            R.id.btnAjustarPerfil
        )

        // ==========================================
        // 2. IDENTIFICAR SERVIÇOS DISPONÍVEIS
        // ==========================================

        val cardInstalacao = view.findViewById<View>(
            R.id.cardServicoInstalacao
        )

        val cardHigienizacao = view.findViewById<View>(
            R.id.cardServicoHigienizacao
        )

        // ==========================================
        // 3. FUNÇÃO PARA ABRIR NOVO ORÇAMENTO
        // ==========================================

        fun abrirNovoOrcamento(servico: String? = null) {

            val intent = Intent(
                requireContext(),
                NovoOrcamentoActivity::class.java
            )

            // Envia o serviço escolhido, quando existir
            if (servico != null) {
                intent.putExtra(
                    "SERVICO_PRESELECIONADO",
                    servico
                )
            }

            startActivity(intent)
        }

        // ==========================================
        // 4. BOTÃO NOVO ORÇAMENTO
        // ==========================================

        btnNovo.setOnClickListener {

            // Abre com a seleção padrão
            abrirNovoOrcamento()
        }

        // ==========================================
        // 5. BOTÃO AJUSTAR PERFIL
        // ==========================================

        btnPerfil.setOnClickListener {

            requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottomNavigation
                )
                .selectedItemId = R.id.nav_perfil
        }

        // ==========================================
        // 6. SERVIÇO: INSTALAÇÃO DE SPLIT
        // ==========================================

        cardInstalacao.setOnClickListener {

            abrirNovoOrcamento(
                "Instalação de Split"
            )
        }

        // ==========================================
        // 7. SERVIÇO: HIGIENIZAÇÃO PREVENTIVA
        // ==========================================

        cardHigienizacao.setOnClickListener {

            abrirNovoOrcamento(
                "Higienização preventiva"
            )
        }
    }

    // ==========================================
    // 8. ATUALIZAR INFORMAÇÕES FINANCEIRAS
    // ==========================================

    override fun onResume() {
        super.onResume()

        val tela = view ?: return

        atualizarIndicadores(tela)
    }

    private fun atualizarIndicadores(tela: View) {

        val perfil = PerfilRepository(
            requireContext()
        ).carregar()

        val moeda = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("pt-BR")
        )

        val valorHora = if (
            perfil.horasFaturaveis > 0
        ) {
            perfil.calcularCustoHora()
        } else {
            0.0
        }

        tela.findViewById<TextView>(
            R.id.txtValorHora
        ).text = "${moeda.format(valorHora)}/h"

        tela.findViewById<TextView>(
            R.id.txtMetaMensal
        ).text = "Meta mensal: ${moeda.format(perfil.remuneracao)}"
    }
}
