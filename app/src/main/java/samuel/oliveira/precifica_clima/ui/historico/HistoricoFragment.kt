package samuel.oliveira.precifica_clima.ui.historico

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import samuel.oliveira.precifica_clima.R

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

        val orcamentos = listOf(
            OrcamentoResumo(
                "Maria Souza",
                "Instalação Split - 12.000 BTUs",
                "R$ 478,12"
            ),
            OrcamentoResumo(
                "João Pereira",
                "Higienização preventiva",
                "R$ 180,00"
            ),
            OrcamentoResumo(
                "Carlos Lima",
                "Instalação Split - 9.000 BTUs",
                "R$ 390,00"
            )
        )

        recycler.layoutManager = LinearLayoutManager(
            requireContext()
        )

        recycler.adapter = OrcamentoAdapter(orcamentos)
    }
}


