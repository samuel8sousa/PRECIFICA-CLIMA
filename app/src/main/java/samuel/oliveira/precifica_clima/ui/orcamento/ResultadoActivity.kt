
package samuel.oliveira.precifica_clima.ui.orcamento

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Locale
import android.content.Intent
import samuel.oliveira.precifica_clima.ui.main.MainActivity
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
        val numero = intent.getStringExtra("NUMERO") ?: ""

        val horas = intent.getDoubleExtra(
            "HORAS",
            Double.NaN
        )

        val distancia = intent.getDoubleExtra(
            "DISTANCIA",
            Double.NaN
        )

        val materiais = intent.getDoubleExtra(
            "MATERIAIS",
            Double.NaN
        )

        val outros = intent.getDoubleExtra(
            "OUTROS",
            Double.NaN
        )

        // Dados recebidos da consulta ViaCEP
        val logradouro =
            intent.getStringExtra("LOGRADOURO") ?: ""

        val complemento =
            intent.getStringExtra("COMPLEMENTO") ?: ""

        val bairro =
            intent.getStringExtra("BAIRRO") ?: ""

        val cidade =
            intent.getStringExtra("CIDADE") ?: ""

        val uf =
            intent.getStringExtra("UF") ?: ""

        // ID usado para identificar uma edição
        val orcamentoId = intent.getLongExtra(
            "ORCAMENTO_ID",
            -1L
        )

        val modoEdicao = orcamentoId != -1L

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

        fun preencher(id: Int, texto: String) {
            findViewById<TextView>(id).text = texto
        }

        // ==========================================
        // 6. MOSTRAR CLIENTE E SERVIÇO
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
        // 9. MOSTRAR A MARGEM
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
        // 10. BOTÃO PARA VOLTAR AO FORMULÁRIO
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

        // Personaliza o texto quando for uma edição
        btnSalvar.text = if (modoEdicao) {
            "Salvar alterações"
        } else {
            "Salvar orçamento"
        }

        btnSalvar.setOnClickListener {

            // Impede vários cliques simultâneos
            btnSalvar.isEnabled = false

            // ======================================
            // 12. CRIAR O REGISTRO DO ORÇAMENTO
            // ======================================

            val orcamento = OrcamentoEntity(
                cliente = cliente,
                cep = cep,
                logradouro = logradouro,
                numero = numero,
                complemento = complemento,
                bairro = bairro,
                cidade = cidade,
                uf = uf,
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

            // ======================================
            // 13. SALVAR OU ATUALIZAR NO ROOM
            // ======================================

            lifecycleScope.launch {

                try {

                    val banco = AppDatabase.getDatabase(
                        applicationContext
                    )

                    val dao = banco.orcamentoDao()

                    if (!modoEdicao) {

                        // --------------------------
                        // NOVO ORÇAMENTO
                        // --------------------------

                        dao.inserir(orcamento)

                        Toast.makeText(
                            this@ResultadoActivity,
                            "Orçamento salvo com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        // --------------------------
                        // EDITAR ORÇAMENTO EXISTENTE
                        // --------------------------

                        // Recupera o registro original
                        val original = dao.buscarPorId(
                            orcamentoId
                        ) ?: throw IllegalStateException(
                            "Orçamento não encontrado para edição."
                        )

                        // Atualiza os dados, preservando
                        // o ID e a data de criação
                        val orcamentoAtualizado = orcamento.copy(
                            id = original.id,
                            criadoEm = original.criadoEm
                        )

                        dao.atualizar(orcamentoAtualizado)

                        Toast.makeText(
                            this@ResultadoActivity,
                            "Orçamento atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ==========================================
                    // VOLTAR AO MENU PRINCIPAL APÓS SALVAR
                    // ==========================================

                    val intentInicio = Intent(
                        this@ResultadoActivity,
                        MainActivity::class.java
                    ).apply {

                        // Limpa as telas anteriores do orçamento
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                        // Informa que deve abrir a aba Início
                        putExtra("ABRIR_INICIO", true)
                    }

                    startActivity(intentInicio)
                    finish()

                } catch (e: CancellationException) {
                    throw e

                } catch (e: Exception) {

                    Log.e(
                        "ResultadoActivity",
                        "Erro ao salvar ou atualizar orçamento no Room",
                        e
                    )

                    // Permite tentar novamente
                    btnSalvar.isEnabled = true

                    Toast.makeText(
                        this@ResultadoActivity,
                        "Erro ao salvar o orçamento.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
