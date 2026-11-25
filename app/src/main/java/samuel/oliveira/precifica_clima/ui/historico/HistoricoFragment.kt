
package samuel.oliveira.precifica_clima.ui.historico

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Locale

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.local.AppDatabase
import samuel.oliveira.precifica_clima.ui.orcamento.NovoOrcamentoActivity

class HistoricoFragment : Fragment(R.layout.fragment_historico) {

    // ==========================================
    // 1. CONFIGURAR A TELA
    // ==========================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(
            R.id.recyclerOrcamentos
        )

        recycler.layoutManager =
            LinearLayoutManager(requireContext())

        val btnLimparHistorico = view.findViewById<Button>(
            R.id.btnLimparHistorico
        )

        btnLimparHistorico.setOnClickListener {
            confirmarLimpezaHistorico()
        }
    }

    // ==========================================
    // 2. ATUALIZAR AO RETORNAR À TELA
    // ==========================================

    override fun onResume() {
        super.onResume()
        carregarOrcamentos()
    }

    // ==========================================
    // 3. CARREGAR ORÇAMENTOS
    // ==========================================

    private fun carregarOrcamentos() {

        val tela = view ?: return

        val recycler = tela.findViewById<RecyclerView>(
            R.id.recyclerOrcamentos
        )

        val mensagem = tela.findViewById<TextView>(
            R.id.txtHistoricoVazio
        )

        val btnLimpar = tela.findViewById<Button>(
            R.id.btnLimparHistorico
        )

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .orcamentoDao()

                val registros = dao.listar()

                mensagem.visibility =
                    if (registros.isEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                btnLimpar.isEnabled = registros.isNotEmpty()

                val moeda = NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("pt-BR")
                )

                val orcamentos = registros.map { item ->

                    OrcamentoResumo(
                        id = item.id,
                        cliente = item.cliente,
                        servico = item.servico,
                        valor = moeda.format(
                            item.precoRecomendado
                        ),
                        recebido = item.dataRecebimento != null
                    )
                }

                // ==================================
                // 4. CONFIGURAR ADAPTER
                // ==================================

                recycler.adapter = OrcamentoAdapter(
                    orcamentos = orcamentos,

                    // EDITAR
                    onEditarClick = { id ->

                        val intent = Intent(
                            requireContext(),
                            NovoOrcamentoActivity::class.java
                        )

                        intent.putExtra("ORCAMENTO_ID", id)

                        startActivity(intent)
                    },

                    // GOOGLE MAPS
                    onRotaClick = { id ->
                        abrirRota(id)
                    },

                    // WHATSAPP
                    onCompartilharClick = { id ->
                        compartilharOrcamento(id)
                    },

                    // EXCLUIR
                    onExcluirClick = { id ->
                        confirmarExclusao(id)
                    },

                    // REGISTRAR OU DESFAZER RECEBIMENTO
                    onRecebimentoClick = { id, jaRecebido ->
                        confirmarAlteracaoRecebimento(
                            id,
                            jaRecebido
                        )
                    }
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                Log.e(
                    "HistoricoFragment",
                    "Erro ao carregar histórico",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "Não foi possível carregar o histórico.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 5. CONFIRMAR RECEBIMENTO
    // ==========================================

    private fun confirmarAlteracaoRecebimento(
        id: Long,
        jaRecebido: Boolean
    ) {

        val titulo =
            if (jaRecebido) {
                "Desfazer recebimento?"
            } else {
                "Confirmar pagamento?"
            }

        val mensagem =
            if (jaRecebido) {
                "O recebimento será removido do relatório mensal. Deseja continuar?"
            } else {
                "O pagamento integral do valor recomendado será registrado com a data de hoje. Deseja continuar?"
            }

        val acao =
            if (jaRecebido) {
                "Desfazer"
            } else {
                "Confirmar"
            }

        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensagem)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton(acao) { _, _ ->

                alterarRecebimento(
                    id,
                    jaRecebido
                )
            }
            .show()
    }

    // ==========================================
    // 6. ALTERAR STATUS DO RECEBIMENTO
    // ==========================================

    private fun alterarRecebimento(
        id: Long,
        jaRecebido: Boolean
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .orcamentoDao()

                val alterados = if (jaRecebido) {

                    dao.desfazerRecebimento(id)

                } else {

                    dao.marcarComoRecebido(
                        id,
                        System.currentTimeMillis()
                    )
                }

                if (alterados > 0) {

                    val mensagem =
                        if (jaRecebido) {
                            "Recebimento desfeito."
                        } else {
                            "Pagamento registrado com sucesso!"
                        }

                    Toast.makeText(
                        requireContext(),
                        mensagem,
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Não foi possível atualizar o recebimento.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                carregarOrcamentos()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                Log.e(
                    "HistoricoFragment",
                    "Erro ao atualizar recebimento",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "Erro ao atualizar pagamento.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 7. ABRIR ROTA ATÉ O CLIENTE
    // ==========================================

    private fun abrirRota(id: Long) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .orcamentoDao()

                val orcamento = dao.buscarPorId(id)

                if (orcamento == null) {

                    Toast.makeText(
                        requireContext(),
                        "Orçamento não encontrado.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                if (
                    orcamento.logradouro.isBlank() ||
                    orcamento.cidade.isBlank() ||
                    orcamento.uf.isBlank()
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Endereço incompleto. Edite o orçamento.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                // Inclui o número da residência
                val destino = listOf(
                    orcamento.logradouro,
                    orcamento.numero,
                    orcamento.complemento,
                    orcamento.bairro,
                    orcamento.cidade,
                    orcamento.uf,
                    orcamento.cep,
                    "Brasil"
                )
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                abrirGoogleMaps(destino)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                Log.e(
                    "HistoricoFragment",
                    "Erro ao recuperar rota",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "Não foi possível abrir a rota.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 8. ABRIR GOOGLE MAPS
    // ==========================================

    private fun abrirGoogleMaps(destino: String) {

        val uri = Uri.Builder()
            .scheme("https")
            .authority("www.google.com")
            .path("/maps/dir/")
            .appendQueryParameter("api", "1")
            .appendQueryParameter("destination", destino)
            .appendQueryParameter("travelmode", "driving")
            .appendQueryParameter("dir_action", "navigate")
            .build()

        val intentMaps = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {

            startActivity(intentMaps)

        } catch (e: ActivityNotFoundException) {

            val intentNavegador = Intent(
                Intent.ACTION_VIEW,
                uri
            )

            try {

                startActivity(intentNavegador)

            } catch (erro: ActivityNotFoundException) {

                Toast.makeText(
                    requireContext(),
                    "Nenhum aplicativo disponível para abrir a rota.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 9. COMPARTILHAR ORÇAMENTO
    // ==========================================

    private fun compartilharOrcamento(id: Long) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .orcamentoDao()

                val orcamento = dao.buscarPorId(id)

                if (orcamento == null) {

                    Toast.makeText(
                        requireContext(),
                        "Orçamento não encontrado.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                val moeda = NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("pt-BR")
                )

                val mensagem = buildString {

                    appendLine("🔧 *ORÇAMENTO - PRECIFICA CLIMA*")
                    appendLine()

                    appendLine(
                        "Olá! Seguem as informações do orçamento solicitado:"
                    )

                    appendLine()

                    appendLine("*Cliente:* ${orcamento.cliente}")
                    appendLine("*Serviço:* ${orcamento.servico}")

                    appendLine()

                    appendLine("━━━━━━━━━━━━━━━━")
                    appendLine("*VALOR TOTAL DO SERVIÇO:*")

                    appendLine(
                        "*${moeda.format(orcamento.precoRecomendado)}*"
                    )

                    appendLine("━━━━━━━━━━━━━━━━")
                    appendLine()

                    appendLine("Agradecemos pela preferência!")
                    appendLine()

                    appendLine(
                        "_Orçamento elaborado pelo Precifica Clima._"
                    )
                }

                val intentCompartilhar = Intent(
                    Intent.ACTION_SEND
                ).apply {

                    type = "text/plain"

                    putExtra(
                        Intent.EXTRA_TEXT,
                        mensagem
                    )
                }

                startActivity(
                    Intent.createChooser(
                        intentCompartilhar,
                        "Compartilhar orçamento"
                    )
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                Log.e(
                    "HistoricoFragment",
                    "Erro ao compartilhar orçamento",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "Não foi possível compartilhar o orçamento.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 10. CONFIRMAR EXCLUSÃO INDIVIDUAL
    // ==========================================

    private fun confirmarExclusao(id: Long) {

        AlertDialog.Builder(requireContext())
            .setTitle("Excluir orçamento?")
            .setMessage(
                "Deseja realmente excluir este orçamento? " +
                        "Essa ação não poderá ser desfeita."
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { _, _ ->

                viewLifecycleOwner.lifecycleScope.launch {

                    try {

                        val dao = AppDatabase
                            .getDatabase(requireContext())
                            .orcamentoDao()

                        val orcamento = dao.buscarPorId(id)

                        if (orcamento == null) {

                            Toast.makeText(
                                requireContext(),
                                "Orçamento não encontrado.",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@launch
                        }

                        dao.excluir(orcamento)

                        Toast.makeText(
                            requireContext(),
                            "Orçamento excluído com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        carregarOrcamentos()

                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: Exception) {

                        Log.e(
                            "HistoricoFragment",
                            "Erro ao excluir orçamento",
                            e
                        )

                        Toast.makeText(
                            requireContext(),
                            "Não foi possível excluir o orçamento.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .show()
    }

    // ==========================================
    // 11. LIMPAR TODO O HISTÓRICO
    // ==========================================

    private fun confirmarLimpezaHistorico() {

        AlertDialog.Builder(requireContext())
            .setTitle("Limpar todo o histórico?")
            .setMessage(
                "Todos os orçamentos salvos serão excluídos " +
                        "permanentemente. Essa ação não poderá ser desfeita."
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir todos") { _, _ ->

                viewLifecycleOwner.lifecycleScope.launch {

                    try {

                        val dao = AppDatabase
                            .getDatabase(requireContext())
                            .orcamentoDao()

                        val quantidade = dao.excluirTodos()

                        Toast.makeText(
                            requireContext(),
                            "$quantidade orçamento(s) excluído(s).",
                            Toast.LENGTH_SHORT
                        ).show()

                        carregarOrcamentos()

                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: Exception) {

                        Log.e(
                            "HistoricoFragment",
                            "Erro ao limpar histórico",
                            e
                        )

                        Toast.makeText(
                            requireContext(),
                            "Não foi possível limpar o histórico.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .show()
    }
}
