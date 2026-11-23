
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
    // 1. CONFIGURAR A TELA DO HISTÓRICO
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

        // Botão para limpar todo o histórico
        val btnLimparHistorico = view.findViewById<Button>(
            R.id.btnLimparHistorico
        )

        btnLimparHistorico.setOnClickListener {
            confirmarLimpezaHistorico()
        }
    }

    // ==========================================
    // 2. ATUALIZAR AO RETORNAR AO HISTÓRICO
    // ==========================================

    override fun onResume() {
        super.onResume()
        carregarOrcamentos()
    }

    // ==========================================
    // 3. CARREGAR ORÇAMENTOS DO ROOM
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

                // Exibir mensagem se não houver orçamentos
                mensagem.visibility = if (registros.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Desabilitar limpeza se o histórico estiver vazio
                btnLimpar.isEnabled = registros.isNotEmpty()

                // Formatar valores em reais
                val moeda = NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("pt-BR")
                )

                // Converter registros do banco em cards
                val orcamentos = registros.map { item ->

                    OrcamentoResumo(
                        id = item.id,
                        cliente = item.cliente,
                        servico = item.servico,
                        valor = moeda.format(
                            item.precoRecomendado
                        )
                    )
                }

                // ==================================
                // 4. CONFIGURAR O ADAPTER
                // ==================================

                recycler.adapter = OrcamentoAdapter(
                    orcamentos = orcamentos,

                    // EDITAR
                    onEditarClick = { id ->

                        val intent = Intent(
                            requireContext(),
                            NovoOrcamentoActivity::class.java
                        )

                        intent.putExtra(
                            "ORCAMENTO_ID",
                            id
                        )

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

                    // EXCLUIR UM ORÇAMENTO
                    onExcluirClick = { id ->
                        confirmarExclusao(id)
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
    // 5. ABRIR ROTA ATÉ O CLIENTE
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

                // Validar endereço
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

                val destino = listOf(
                    orcamento.logradouro,
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
    // 6. ABRIR GOOGLE MAPS
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

        // Tentar abrir o aplicativo Google Maps
        val intentMaps = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {

            startActivity(intentMaps)

        } catch (e: ActivityNotFoundException) {

            // Tentar abrir pelo navegador
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
    // 7. COMPARTILHAR ORÇAMENTO
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

                // Apenas informações importantes para o cliente
                val mensagem = buildString {

                    appendLine("🔧 *ORÇAMENTO - PRECIFICA CLIMA*")
                    appendLine()

                    appendLine(
                        "Olá! Seguem as informações do orçamento solicitado:"
                    )

                    appendLine()

                    appendLine(
                        "*Cliente:* ${orcamento.cliente}"
                    )

                    appendLine(
                        "*Serviço:* ${orcamento.servico}"
                    )

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

                // Compartilhamento nativo do Android
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
    // 8. CONFIRMAR EXCLUSÃO INDIVIDUAL
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

                        // Excluir somente o registro selecionado
                        dao.excluir(orcamento)

                        Toast.makeText(
                            requireContext(),
                            "Orçamento excluído com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Atualizar o RecyclerView
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
    // 9. CONFIRMAR LIMPEZA DO HISTÓRICO
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

                        // Excluir todos os registros
                        val quantidade = dao.excluirTodos()

                        Toast.makeText(
                            requireContext(),
                            "$quantidade orçamento(s) excluído(s).",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Atualizar a lista e a mensagem de vazio
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
