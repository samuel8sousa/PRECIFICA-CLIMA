
package samuel.oliveira.precifica_clima.ui.historico

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.local.AppDatabase

class HistoricoFragment :
    Fragment(R.layout.fragment_historico) {

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

        viewLifecycleOwner.lifecycleScope.launch {

            val dao = AppDatabase
                .getDatabase(requireContext())
                .orcamentoDao()

            val registros = dao.listar()

            // Localiza a mensagem de histórico vazio
            val mensagem = view.findViewById<TextView>(
                R.id.txtHistoricoVazio
            )

            // Mostra a mensagem somente se não houver orçamentos
            mensagem.visibility = if (registros.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            val moeda = NumberFormat.getCurrencyInstance(
                Locale("pt", "BR")
            )

            val orcamentos = registros.map { item ->

                OrcamentoResumo(
                    cliente = item.cliente,
                    servico = item.servico,
                    valor = moeda.format(item.precoRecomendado)
                )
            }

            recycler.adapter = OrcamentoAdapter(orcamentos)
        }
    }
}
