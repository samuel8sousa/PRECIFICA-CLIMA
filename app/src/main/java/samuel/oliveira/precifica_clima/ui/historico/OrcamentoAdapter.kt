package samuel.oliveira.precifica_clima.ui.historico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import samuel.oliveira.precifica_clima.R

data class OrcamentoResumo(
    val cliente: String,
    val servico: String,
    val valor: String
)

class OrcamentoAdapter(
    private val orcamentos: List<OrcamentoResumo>
) : RecyclerView.Adapter<OrcamentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val cliente: TextView =
            view.findViewById(R.id.txtCliente)

        val servico: TextView =
            view.findViewById(R.id.txtServico)

        val valor: TextView =
            view.findViewById(R.id.txtValor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_orcamento,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val orcamento = orcamentos[position]

        holder.cliente.text = orcamento.cliente
        holder.servico.text = orcamento.servico
        holder.valor.text = orcamento.valor
    }

    override fun getItemCount(): Int {
        return orcamentos.size
    }
}

