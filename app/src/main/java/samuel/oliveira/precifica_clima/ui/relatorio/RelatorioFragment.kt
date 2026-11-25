
package samuel.oliveira.precifica_clima.ui.relatorio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.local.AppDatabase

class RelatorioFragment :
    Fragment(R.layout.fragment_relatorio) {

    private var mesSelecionado = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val anterior = view.findViewById<Button>(
            R.id.btnMesAnterior
        )

        val proximo = view.findViewById<Button>(
            R.id.btnProximoMes
        )

        anterior.setOnClickListener {
            mesSelecionado.add(Calendar.MONTH, -1)
            carregarRelatorio()
        }

        proximo.setOnClickListener {

            val proximoMes = (
                    mesSelecionado.clone() as Calendar
                    ).apply {
                    add(Calendar.MONTH, 1)
                }

            if (
                inicioDoMes(proximoMes) <=
                inicioDoMes(Calendar.getInstance())
            ) {
                mesSelecionado = proximoMes
                carregarRelatorio()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarRelatorio()
    }

    // ==========================================
    // CALCULAR INÍCIO DO MÊS
    // ==========================================

    private fun inicioDoMes(
        calendario: Calendar
    ): Long {

        return (calendario.clone() as Calendar).apply {

            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

        }.timeInMillis
    }

    // ==========================================
    // CARREGAR RELATÓRIO
    // ==========================================

    private fun carregarRelatorio() {

        val tela = view ?: return

        val txtMes = tela.findViewById<TextView>(
            R.id.txtMesSelecionado
        )

        val txtRecebido = tela.findViewById<TextView>(
            R.id.txtTotalRecebido
        )

        val txtCustos = tela.findViewById<TextView>(
            R.id.txtCustosEstimados
        )

        val txtLucro = tela.findViewById<TextView>(
            R.id.txtLucroEstimado
        )

        val txtServicos = tela.findViewById<TextView>(
            R.id.txtServicosPagos
        )

        val txtPendentes = tela.findViewById<TextView>(
            R.id.txtOrcamentosPendentes
        )

        val txtValorPendente = tela.findViewById<TextView>(
            R.id.txtValorPendente
        )

        val btnProximo = tela.findViewById<Button>(
            R.id.btnProximoMes
        )

        val inicio = inicioDoMes(mesSelecionado)

        val fim = (
                mesSelecionado.clone() as Calendar
                ).apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, 1)
            }.timeInMillis

        val formato = SimpleDateFormat(
            "MMMM 'de' yyyy",
            Locale.forLanguageTag("pt-BR")
        )

        txtMes.text = formato.format(
            Date(inicio)
        ).replaceFirstChar {
            it.uppercase(Locale.forLanguageTag("pt-BR"))
        }

        val inicioAtual = inicioDoMes(
            Calendar.getInstance()
        )

        btnProximo.isEnabled = inicio < inicioAtual

        val moeda = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("pt-BR")
        )

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .orcamentoDao()

                // Recebimentos efetivos
                val recebido = dao.somarRecebimentos(
                    inicio,
                    fim
                )

                // Custos dos serviços pagos
                val custos = dao.somarCustosRecebidos(
                    inicio,
                    fim
                )

                // Lucro estimado
                val lucro = recebido - custos

                // Quantidade de serviços pagos
                val servicos = dao.contarServicosRecebidos(
                    inicio,
                    fim
                )

                // Orçamentos pendentes criados no mês
                val pendentes = dao.contarPendentesDoMes(
                    inicio,
                    fim
                )

                // Valor aguardando recebimento
                val valorPendente =
                    dao.somarValorPendenteDoMes(
                        inicio,
                        fim
                    )

                // ==================================
                // ATUALIZAR INTERFACE
                // ==================================

                txtRecebido.text = moeda.format(recebido)

                txtCustos.text =
                    "Custos estimados: ${moeda.format(custos)}"

                txtLucro.text =
                    "Lucro estimado: ${moeda.format(lucro)}"

                txtServicos.text =
                    "Serviços pagos: $servicos"

                txtPendentes.text =
                    "Orçamentos pendentes: $pendentes"

                txtValorPendente.text =
                    "Valor pendente: ${moeda.format(valorPendente)}"

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                Log.e(
                    "RelatorioFragment",
                    "Erro ao carregar relatório",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "Não foi possível carregar o relatório.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
