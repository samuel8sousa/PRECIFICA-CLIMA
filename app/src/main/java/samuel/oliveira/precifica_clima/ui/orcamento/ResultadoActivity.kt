
package samuel.oliveira.precifica_clima.ui.orcamento

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Locale

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.local.AppDatabase
import samuel.oliveira.precifica_clima.data.local.entity.OrcamentoEntity
import samuel.oliveira.precifica_clima.data.repository.PerfilRepository
import samuel.oliveira.precifica_clima.domain.calculation.CalculadoraPreco

class ResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resultado)

        // ==========================================
        // 1. RECEBER OS DADOS DO FORMULÁRIO
        // ==========================================

        val cliente = intent.getStringExtra("CLIENTE") ?: ""
        val servico = intent.getStringExtra("SERVICO") ?: ""
        val cep = intent.getStringExtra("CEP") ?: ""

        val horas = intent.getDoubleExtra(
            "HORAS", Double.NaN
        )

        val distancia = intent.getDoubleExtra(
            "DISTANCIA", Double.NaN
        )

        val materiais = intent.getDoubleExtra(
            "MATERIAIS", Double.NaN
        )

        val outros = intent.getDoubleExtra(
            "OUTROS", Double.NaN
        )

        // ==========================================
        // 2. VALIDAR OS DADOS RECEBIDOS
        // ==========================================

        val valoresValidos =
            horas.isFinite() && horas > 0 &&
                    distancia.isFinite() && distancia >= 0 &&
                    materiais.isFinite() && materiais >= 0 &&
                    outros.isFinite() && outros >= 0

        if (
            cliente.isBlank() ||
            servico.isBlank() ||
            !valoresValidos
        ) {
            Toast.makeText(
                this,
                "Dados do orçamento inválidos.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // ==========================================
        // 3. CARREGAR O PERFIL FINANCEIRO
        // ==========================================

        val perfil = PerfilRepository(this).carregar()

        val perfilValido =
            perfil.remuneracao.isFinite() &&
                    perfil.remuneracao >= 0 &&
                    perfil.custosFixos.isFinite() &&
                    perfil.custosFixos >= 0 &&
                    perfil.reserva.isFinite() &&
                    perfil.reserva >= 0 &&
                    perfil.custoKm.isFinite() &&
                    perfil.custoKm >= 0 &&
                    perfil.horasFaturaveis.isFinite() &&
                    perfil.horasFaturaveis > 0 &&
                    perfil.margemPercentual.isFinite() &&
                    perfil.margemPercentual >= 0 &&
                    perfil.margemPercentual < 100

        if (!perfilValido) {
            Toast.makeText(
                this,
                "Confira as configurações do perfil financeiro.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // ==========================================
        // 4. CALCULAR O ORÇAMENTO
        // ==========================================

        val resultado = CalculadoraPreco.calcular(
            perfil = perfil,
            horas = horas,
            distanciaKm = distancia,
            materiais = materiais,
            outrosCustos = outros
        )

        if (
            !resultado.custoTotal.isFinite() ||
            !resultado.precoRecomendado.isFinite()
        ) {
            Toast.makeText(
                this,
                "Não foi possível calcular este orçamento.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // ==========================================
        // 5. FORMATAR OS VALORES EM REAIS
        // ==========================================

        val localeBrasil = Locale.forLanguageTag("pt-BR")

        val moeda = NumberFormat.getCurrencyInstance(
            localeBrasil
        )

        // Função auxiliar para preencher TextViews
        fun preencher(id: Int, texto: String) {
            findViewById<TextView>(id).text = texto
        }

        // ==========================================
        // 6. MOSTRAR O CLIENTE E O SERVIÇO
        // ==========================================

        preencher(
            R.id.txtResultadoCliente,
            cliente
        )

        preencher(
            R.id.txtResultadoServico,
            servico
        )

        // ==========================================
        // 7. MOSTRAR A COMPOSIÇÃO DOS CUSTOS
        // ==========================================

        preencher(
            R.id.txtMaoObra,
            "Mão de obra: ${moeda.format(resultado.maoObra)}"
        )

        preencher(
            R.id.txtMateriaisResultado,
            "Materiais: ${moeda.format(resultado.materiais)}"
        )

        preencher(
            R.id.txtDeslocamentoResultado,
            "Deslocamento: ${moeda.format(resultado.deslocamento)}"
        )

        preencher(
            R.id.txtOutrosResultado,
            "Outros custos: ${moeda.format(resultado.outrosCustos)}"
        )

        preencher(
            R.id.txtCustoTotal,
            "Custo total: ${moeda.format(resultado.custoTotal)}"
        )

        // ==========================================
        // 8. MOSTRAR O PREÇO RECOMENDADO
        // ==========================================

        preencher(
            R.id.txtPrecoRecomendado,
            moeda.format(resultado.precoRecomendado)
        )

        // ==========================================
        // 9. MOSTRAR A MARGEM UTILIZADA
        // ==========================================

        preencher(
            R.id.txtMargemResultado,
            String.format(
                localeBrasil,
                "Margem aplicada: %.1f%%",
                perfil.margemPercentual
            )
        )

        // ==========================================
        // 10. CONFIGURAR O BOTÃO VOLTAR
        // ==========================================

        val btnEditar = findViewById<Button>(
            R.id.btnEditarOrcamento
        )

        btnEditar.setOnClickListener {
            finish()
        }

        // ==========================================
        // 11. CONFIGURAR O BOTÃO SALVAR
        // ==========================================

        val btnSalvar = findViewById<Button>(
            R.id.btnSalvarOrcamento
        )

        btnSalvar.setOnClickListener {

            // Impede vários cliques simultâneos
            btnSalvar.isEnabled = false

            // Cria o registro que será salvo no Room
            val orcamento = OrcamentoEntity(
                cliente = cliente,
                cep = cep,
                servico = servico,
                horas = horas,
                distanciaKm = distancia,
                materiais = materiais,
                outrosCustos = outros,
                custoHora = resultado.custoHora,
                custoMaoObra = resultado.maoObra,
                custoDeslocamento = resultado.deslocamento,
                custoTotal = resultado.custoTotal,
                margemPercentual = perfil.margemPercentual,
                precoRecomendado = resultado.precoRecomendado
            )

            // Inicia o salvamento com corrotina
            lifecycleScope.launch {

                try {
                    val banco = AppDatabase.getDatabase(
                        applicationContext
                    )

                    banco.orcamentoDao().inserir(orcamento)

                    Toast.makeText(
                        this@ResultadoActivity,
                        "Orçamento salvo com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } catch (e: Exception) {

                    Log.e(
                        "ResultadoActivity",
                        "Erro ao salvar orçamento no Room",
                        e
                    )

                    btnSalvar.isEnabled = true

                    Toast.makeText(
                        this@ResultadoActivity,
                        "Erro ao salvar orçamento.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
