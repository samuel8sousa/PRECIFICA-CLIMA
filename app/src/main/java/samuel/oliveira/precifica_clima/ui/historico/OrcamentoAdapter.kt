
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
    val valor: String,
    val recebido: Boolean
)

class OrcamentoAdapter(
    private val orcamentos: List<OrcamentoResumo>,
    private val onEditarClick: (Long) -> Unit,
    private val onRotaClick: (Long) -> Unit,
    private val onCompartilharClick: (Long) -> Unit,
    private val onExcluirClick: (Long) -> Unit,
    private val onRecebimentoClick: (Long, Boolean) -> Unit
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

        val btnRecebimento: Button =
            view.findViewById(R.id.btnMarcarRecebido)
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

        holder.btnEditar.setOnClickListener {
            onEditarClick(orcamento.id)
        }

        holder.btnVerRota.setOnClickListener {
            onRotaClick(orcamento.id)
        }

        holder.btnCompartilhar.setOnClickListener {
            onCompartilharClick(orcamento.id)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClick(orcamento.id)
        }

        // Atualizar texto conforme o pagamento
        holder.btnRecebimento.text =
            if (orcamento.recebido) {
                "Desfazer recebimento"
            } else {
                "Marcar como recebido"
            }

        holder.btnRecebimento.setOnClickListener {
            onRecebimentoClick(
                orcamento.id,
                orcamento.recebido
            )
        }
    }

    override fun getItemCount(): Int {
        return orcamentos.size
    }
}
