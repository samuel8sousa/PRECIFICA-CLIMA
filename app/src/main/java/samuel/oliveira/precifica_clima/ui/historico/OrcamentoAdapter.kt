
package samuel.oliveira.precifica_clima.ui.historico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import samuel.oliveira.precifica_clima.R

data class OrcamentoResumo(
    val id: Long,
    val cliente: String,
    val servico: String,
    val valor: String
)

class OrcamentoAdapter(
    private val orcamentos: List<OrcamentoResumo>,
    private val onEditarClick: (Long) -> Unit,
    private val onRotaClick: (Long) -> Unit,
    private val onCompartilharClick: (Long) -> Unit,
    private val onExcluirClick: (Long) -> Unit
) : RecyclerView.Adapter<OrcamentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val cliente: TextView =
            view.findViewById(R.id.txtCliente)

        val servico: TextView =
            view.findViewById(R.id.txtServico)

        val valor: TextView =
            view.findViewById(R.id.txtValor)

        val btnEditar: Button =
            view.findViewById(R.id.btnEditar)

        val btnVerRota: Button =
            view.findViewById(R.id.btnVerRota)

        val btnCompartilhar: Button =
            view.findViewById(R.id.btnCompartilhar)

        val btnExcluir: Button =
            view.findViewById(R.id.btnExcluir)
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

        // Editar
        holder.btnEditar.setOnClickListener {
            onEditarClick(orcamento.id)
        }

        // Google Maps
        holder.btnVerRota.setOnClickListener {
            onRotaClick(orcamento.id)
        }

        // WhatsApp
        holder.btnCompartilhar.setOnClickListener {
            onCompartilharClick(orcamento.id)
        }

        // Excluir
        holder.btnExcluir.setOnClickListener {
            onExcluirClick(orcamento.id)
        }
    }

    override fun getItemCount(): Int {
        return orcamentos.size
    }
}
