
package samuel.oliveira.precifica_clima.ui.orcamento

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.data.local.AppDatabase
import samuel.oliveira.precifica_clima.data.remote.viacep.RetrofitClient

class NovoOrcamentoActivity : AppCompatActivity() {

    // -1L = novo orçamento
    // Outro ID = edição de orçamento existente
    private var orcamentoId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_novo_orcamento)

        // ==========================================
        // 1. IDENTIFICAR AS ETAPAS
        // ==========================================

        val etapaUm = findViewById<LinearLayout>(
            R.id.etapaUm
        )

        val etapaEndereco = findViewById<LinearLayout>(
            R.id.etapaEndereco
        )

        val etapaDois = findViewById<LinearLayout>(
            R.id.etapaDois
        )

        // ==========================================
        // 2. IDENTIFICAR OS BOTÕES
        // ==========================================

        val btnVoltarTela = findViewById<ImageButton>(
            R.id.btnVoltarTela
        )

        val btnContinuar = findViewById<Button>(
            R.id.btnContinuar
        )

        val btnVoltarEndereco = findViewById<Button>(
            R.id.btnVoltarEndereco
        )

        val btnContinuarEndereco = findViewById<Button>(
            R.id.btnContinuarEndereco
        )

        val btnVoltarEtapa = findViewById<Button>(
            R.id.btnVoltarEtapa
        )

        val btnCalcular = findViewById<Button>(
            R.id.btnCalcular
        )

        // ==========================================
        // 3. IDENTIFICAR OS CAMPOS
        // ==========================================

        val campoCliente = findViewById<EditText>(
            R.id.edtCliente
        )

        val campoHoras = findViewById<EditText>(
            R.id.edtHorasServico
        )

        // Endereço
        val campoCep = findViewById<EditText>(
            R.id.edtCep
        )

        val campoLogradouro = findViewById<EditText>(
            R.id.edtLogradouro
        )

        val campoNumero = findViewById<EditText>(
            R.id.edtNumero
        )

        val campoComplemento = findViewById<EditText>(
            R.id.edtComplemento
        )

        val campoBairro = findViewById<EditText>(
            R.id.edtBairro
        )

        val campoCidade = findViewById<EditText>(
            R.id.edtCidade
        )

        val campoUf = findViewById<EditText>(
            R.id.edtUf
        )

        // Custos
        val campoDistancia = findViewById<EditText>(
            R.id.edtDistancia
        )

        val campoMateriais = findViewById<EditText>(
            R.id.edtMateriais
        )

        val campoOutros = findViewById<EditText>(
            R.id.edtOutrosCustos
        )

        // Tipo de serviço
        val rbInstalacao = findViewById<RadioButton>(
            R.id.rbInstalacao
        )

        val rbHigienizacao = findViewById<RadioButton>(
            R.id.rbHigienizacao
        )

        // ==========================================
        // 4. IDENTIFICAR MODO DE EDIÇÃO
        // ==========================================

        orcamentoId = intent.getLongExtra(
            "ORCAMENTO_ID",
            -1L
        )

        // ==========================================
        // 5. SERVIÇO PRÉ-SELECIONADO
        // ==========================================

        val servicoSelecionado = intent.getStringExtra(
            "SERVICO_PRESELECIONADO"
        )

        if (orcamentoId == -1L) {

            when (servicoSelecionado) {

                "Instalação de Split" -> {
                    rbInstalacao.isChecked = true
                }

                "Higienização preventiva" -> {
                    rbHigienizacao.isChecked = true
                }
            }
        }

        // ==========================================
        // 6. FUNÇÃO PARA TROCAR DE ETAPA
        // ==========================================

        fun mostrarEtapa(numeroEtapa: Int) {

            etapaUm.visibility =
                if (numeroEtapa == 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            etapaEndereco.visibility =
                if (numeroEtapa == 2) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            etapaDois.visibility =
                if (numeroEtapa == 3) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        // Iniciar pela etapa 1
        mostrarEtapa(1)

        // ==========================================
        // 7. VOLTAR À TELA ANTERIOR
        // ==========================================

        btnVoltarTela.setOnClickListener {
            finish()
        }

        // ==========================================
        // 8. CONFIGURAR BUSCA DE CEP
        // ==========================================

        configurarBuscaCep()

        // ==========================================
        // 9. CARREGAR ORÇAMENTO PARA EDIÇÃO
        // ==========================================

        if (orcamentoId != -1L) {

            // Impede avançar antes de carregar os dados
            btnContinuar.isEnabled = false

            lifecycleScope.launch {

                try {

                    val banco = AppDatabase.getDatabase(
                        applicationContext
                    )

                    val orcamento = banco
                        .orcamentoDao()
                        .buscarPorId(orcamentoId)

                    if (orcamento == null) {

                        Toast.makeText(
                            this@NovoOrcamentoActivity,
                            "Orçamento não encontrado.",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                        return@launch
                    }

                    // ------------------------------
                    // CLIENTE E TEMPO
                    // ------------------------------

                    campoCliente.setText(
                        orcamento.cliente
                    )

                    campoHoras.setText(
                        orcamento.horas.toString()
                    )

                    // ------------------------------
                    // ENDEREÇO
                    // ------------------------------

                    campoCep.setText(
                        orcamento.cep
                    )

                    campoLogradouro.setText(
                        orcamento.logradouro
                    )

                    // CORREÇÃO:
                    // Recuperar o número salvo no Room
                    campoNumero.setText(
                        orcamento.numero
                    )

                    campoComplemento.setText(
                        orcamento.complemento
                    )

                    campoBairro.setText(
                        orcamento.bairro
                    )

                    campoCidade.setText(
                        orcamento.cidade
                    )

                    campoUf.setText(
                        orcamento.uf
                    )

                    // ------------------------------
                    // CUSTOS
                    // ------------------------------

                    campoDistancia.setText(
                        orcamento.distanciaKm.toString()
                    )

                    campoMateriais.setText(
                        orcamento.materiais.toString()
                    )

                    campoOutros.setText(
                        orcamento.outrosCustos.toString()
                    )

                    // ------------------------------
                    // SERVIÇO
                    // ------------------------------

                    when (orcamento.servico) {

                        "Instalação de Split" -> {
                            rbInstalacao.isChecked = true
                        }

                        "Higienização preventiva" -> {
                            rbHigienizacao.isChecked = true
                        }
                    }

                } catch (e: CancellationException) {
                    throw e

                } catch (e: Exception) {

                    Log.e(
                        "NovoOrcamentoActivity",
                        "Erro ao carregar orçamento",
                        e
                    )

                    Toast.makeText(
                        this@NovoOrcamentoActivity,
                        "Erro ao carregar o orçamento.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                } finally {

                    btnContinuar.isEnabled = true
                }
            }
        }

        // ==========================================
        // 10. ETAPA 1 -> ETAPA 2
        // CLIENTE E SERVIÇO -> ENDEREÇO
        // ==========================================

        btnContinuar.setOnClickListener {

            val cliente = campoCliente.text
                .toString()
                .trim()

            val horas = campoHoras.text
                .toString()
                .trim()
                .replace(",", ".")
                .toDoubleOrNull()

            // Validar cliente
            if (cliente.isBlank()) {

                campoCliente.error =
                    "Informe o nome do cliente"

                campoCliente.requestFocus()

                return@setOnClickListener
            }

            // Validar horas
            if (
                horas == null ||
                !horas.isFinite() ||
                horas <= 0
            ) {

                campoHoras.error =
                    "Informe uma quantidade válida de horas"

                campoHoras.requestFocus()

                return@setOnClickListener
            }

            mostrarEtapa(2)
        }

        // ==========================================
        // 11. ETAPA 2 -> ETAPA 1
        // ==========================================

        btnVoltarEndereco.setOnClickListener {
            mostrarEtapa(1)
        }

        // ==========================================
        // 12. ETAPA 2 -> ETAPA 3
        // VALIDAR ENDEREÇO
        // ==========================================

        btnContinuarEndereco.setOnClickListener {

            val cep = campoCep.text
                .toString()
                .filter { it.isDigit() }

            val logradouro = campoLogradouro.text
                .toString()
                .trim()

            val numeroImovel = campoNumero.text
                .toString()
                .trim()

            val bairro = campoBairro.text
                .toString()
                .trim()

            val cidade = campoCidade.text
                .toString()
                .trim()

            val uf = campoUf.text
                .toString()
                .trim()

            // ------------------------------
            // VALIDAR CEP
            // ------------------------------

            if (cep.length != 8) {

                campoCep.error =
                    "Informe um CEP com 8 dígitos"

                campoCep.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR RUA
            // ------------------------------

            if (logradouro.isBlank()) {

                campoLogradouro.error =
                    "Informe a rua"

                campoLogradouro.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR NÚMERO DA RESIDÊNCIA
            // ------------------------------

            if (numeroImovel.isBlank()) {

                campoNumero.error =
                    "Informe o número ou S/N"

                campoNumero.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR BAIRRO
            // ------------------------------

            if (bairro.isBlank()) {

                campoBairro.error =
                    "Informe o bairro"

                campoBairro.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR CIDADE
            // ------------------------------

            if (cidade.isBlank()) {

                campoCidade.error =
                    "Informe a cidade"

                campoCidade.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR UF
            // ------------------------------

            if (
                uf.length != 2 ||
                !uf.all { it.isLetter() }
            ) {

                campoUf.error =
                    "Informe uma UF válida"

                campoUf.requestFocus()

                return@setOnClickListener
            }

            // Endereço validado
            mostrarEtapa(3)
        }

        // ==========================================
        // 13. ETAPA 3 -> ETAPA 2
        // ==========================================

        btnVoltarEtapa.setOnClickListener {
            mostrarEtapa(2)
        }

        // ==========================================
        // 14. CALCULAR ORÇAMENTO
        // ==========================================

        btnCalcular.setOnClickListener {

            // Função auxiliar para ler números
            fun lerNumero(campo: EditText): Double? {

                return campo.text
                    .toString()
                    .trim()
                    .replace(",", ".")
                    .toDoubleOrNull()
            }

            val horas = lerNumero(
                campoHoras
            )

            val distancia = lerNumero(
                campoDistancia
            )

            val materiais = lerNumero(
                campoMateriais
            )

            val outros = lerNumero(
                campoOutros
            )

            val cliente = campoCliente.text
                .toString()
                .trim()

            // CORREÇÃO:
            // Ler o número diretamente do EditText
            // também neste clique.
            val numeroImovel = campoNumero.text
                .toString()
                .trim()

            // ------------------------------
            // VALIDAR HORAS
            // ------------------------------

            if (
                horas == null ||
                !horas.isFinite() ||
                horas <= 0
            ) {

                Toast.makeText(
                    this,
                    "Informe um tempo de execução válido.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR CUSTOS
            // ------------------------------

            val distanciaValida = distancia?.takeIf {
                it.isFinite() && it >= 0
            }

            val materiaisValidos = materiais?.takeIf {
                it.isFinite() && it >= 0
            }

            val outrosValidos = outros?.takeIf {
                it.isFinite() && it >= 0
            }

            if (
                distanciaValida == null ||
                materiaisValidos == null ||
                outrosValidos == null
            ) {

                Toast.makeText(
                    this,
                    "Confira a distância e os custos.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // ------------------------------
            // VALIDAR NÚMERO DA RESIDÊNCIA
            // ------------------------------

            if (numeroImovel.isBlank()) {

                campoNumero.error =
                    "Informe o número da residência"

                mostrarEtapa(2)

                campoNumero.requestFocus()

                return@setOnClickListener
            }

            // ------------------------------
            // IDENTIFICAR SERVIÇO
            // ------------------------------

            val servico = if (rbInstalacao.isChecked) {
                "Instalação de Split"
            } else {
                "Higienização preventiva"
            }

            // ======================================
            // 15. ENVIAR DADOS À RESULTADOACTIVITY
            // ======================================

            val intentResultado = Intent(
                this,
                ResultadoActivity::class.java
            ).apply {

                // Cliente e serviço
                putExtra(
                    "CLIENTE",
                    cliente
                )

                putExtra(
                    "SERVICO",
                    servico
                )

                // CEP
                putExtra(
                    "CEP",
                    campoCep.text.toString()
                        .filter { it.isDigit() }
                )

                // ------------------------------
                // DADOS FINANCEIROS
                // ------------------------------

                putExtra(
                    "HORAS",
                    horas
                )

                putExtra(
                    "DISTANCIA",
                    distanciaValida
                )

                putExtra(
                    "MATERIAIS",
                    materiaisValidos
                )

                putExtra(
                    "OUTROS",
                    outrosValidos
                )

                // ------------------------------
                // ENDEREÇO COMPLETO
                // ------------------------------

                putExtra(
                    "LOGRADOURO",
                    campoLogradouro.text
                        .toString()
                        .trim()
                )

                // CORREÇÃO PRINCIPAL:
                // Envia o número que foi digitado
                putExtra(
                    "NUMERO",
                    numeroImovel
                )

                putExtra(
                    "COMPLEMENTO",
                    campoComplemento.text
                        .toString()
                        .trim()
                )

                putExtra(
                    "BAIRRO",
                    campoBairro.text
                        .toString()
                        .trim()
                )

                putExtra(
                    "CIDADE",
                    campoCidade.text
                        .toString()
                        .trim()
                )

                putExtra(
                    "UF",
                    campoUf.text
                        .toString()
                        .trim()
                        .uppercase()
                )

                // ------------------------------
                // ID PARA EDIÇÃO
                // ------------------------------

                putExtra(
                    "ORCAMENTO_ID",
                    orcamentoId
                )
            }

            startActivity(intentResultado)
        }
    }

    // ==========================================
    // 16. CONSULTAR ENDEREÇO PELO VIACEP
    // ==========================================

    private fun configurarBuscaCep() {

        val campoCep = findViewById<EditText>(
            R.id.edtCep
        )

        val campoLogradouro = findViewById<EditText>(
            R.id.edtLogradouro
        )

        val campoComplemento = findViewById<EditText>(
            R.id.edtComplemento
        )

        val campoBairro = findViewById<EditText>(
            R.id.edtBairro
        )

        val campoCidade = findViewById<EditText>(
            R.id.edtCidade
        )

        val campoUf = findViewById<EditText>(
            R.id.edtUf
        )

        val btnBuscar = findViewById<Button>(
            R.id.btnBuscarCep
        )

        btnBuscar.setOnClickListener {

            val cep = campoCep.text
                .toString()
                .filter { it.isDigit() }

            if (cep.length != 8) {

                campoCep.error =
                    "Informe um CEP com 8 dígitos"

                campoCep.requestFocus()

                return@setOnClickListener
            }

            // Impedir consultas duplicadas
            btnBuscar.isEnabled = false

            lifecycleScope.launch {

                try {

                    val endereco = RetrofitClient
                        .viaCepApi
                        .consultarCep(cep)

                    // Conferir se o CEP não mudou
                    // durante a requisição
                    val cepAtual = campoCep.text
                        .toString()
                        .filter { it.isDigit() }

                    if (cepAtual != cep) {
                        return@launch
                    }

                    if (endereco.erro == true) {

                        Toast.makeText(
                            this@NovoOrcamentoActivity,
                            "CEP não encontrado.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        // Preencher endereço encontrado
                        campoLogradouro.setText(
                            endereco.logradouro.orEmpty()
                        )

                        campoComplemento.setText(
                            endereco.complemento.orEmpty()
                        )

                        campoBairro.setText(
                            endereco.bairro.orEmpty()
                        )

                        campoCidade.setText(
                            endereco.localidade.orEmpty()
                        )

                        campoUf.setText(
                            endereco.uf.orEmpty()
                        )

                        // O ViaCEP não informa o número
                        // do imóvel. Esse campo permanece
                        // para preenchimento manual.

                        Toast.makeText(
                            this@NovoOrcamentoActivity,
                            "Endereço encontrado! Informe o número.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: CancellationException) {
                    throw e

                } catch (e: Exception) {

                    Log.e(
                        "NovoOrcamentoActivity",
                        "Falha ao consultar ViaCEP",
                        e
                    )

                    Toast.makeText(
                        this@NovoOrcamentoActivity,
                        "Não foi possível consultar o CEP.",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {

                    btnBuscar.isEnabled = true
                }
            }
        }
    }
}
