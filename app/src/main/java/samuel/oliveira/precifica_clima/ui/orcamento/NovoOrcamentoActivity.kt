package samuel.oliveira.precifica_clima.ui.orcamento

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast

import samuel.oliveira.precifica_clima.R

class NovoOrcamentoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_novo_orcamento)

        val btnVoltarTela = findViewById<ImageButton>(
            R.id.btnVoltarTela
        )

        btnVoltarTela.setOnClickListener {
            finish()
        }
        val etapaUm = findViewById<LinearLayout>(
            R.id.etapaUm
        )

        val etapaDois = findViewById<LinearLayout>(
            R.id.etapaDois
        )

        val btnContinuar = findViewById<Button>(
            R.id.btnContinuar
        )

        val btnVoltar = findViewById<Button>(
            R.id.btnVoltarEtapa
        )

        val btnCalcular = findViewById<Button>(
            R.id.btnCalcular
        )

        val campoCliente = findViewById<EditText>(
            R.id.edtCliente
        )

        val campoHoras = findViewById<EditText>(
            R.id.edtHorasServico
        )

        val rbInstalacao = findViewById<RadioButton>(
            R.id.rbInstalacao
        )

        btnContinuar.setOnClickListener {

            val nome = campoCliente.text.toString().trim()
            val horas = campoHoras.text.toString()
                .replace(",", ".")
                .toDoubleOrNull()

            if (nome.isBlank()) {
                campoCliente.error = "Informe o cliente"
                return@setOnClickListener
            }

            if (horas == null || horas <= 0) {
                campoHoras.error = "Informe horas válidas"
                return@setOnClickListener
            }

            etapaUm.visibility = View.GONE
            etapaDois.visibility = View.VISIBLE
        }

        btnVoltar.setOnClickListener {
            etapaDois.visibility = View.GONE
            etapaUm.visibility = View.VISIBLE
        }

        btnCalcular.setOnClickListener {
            fun lerNumero(id: Int): Double? {
                return findViewById<EditText>(id)
                    .text.toString()
                    .replace(",", ".")
                    .toDoubleOrNull()
            }

            val horas = lerNumero(R.id.edtHorasServico)
            val distancia = lerNumero(R.id.edtDistancia)
            val materiais = lerNumero(R.id.edtMateriais)
            val outros = lerNumero(R.id.edtOutrosCustos)
            val cliente = campoCliente.text.toString()

            if (horas == null || !horas.isFinite() || horas <= 0) {
                Toast.makeText(
                    this,
                    "Informe um tempo de execução válido.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val custos = listOf(distancia, materiais, outros)

            if (custos.any {
                    it == null || !it.isFinite() || it < 0
                }) {
                Toast.makeText(
                    this,
                    "Confira distância e custos informados.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val servico = if (rbInstalacao.isChecked) {
                "Instalação de Split"
            } else {
                "Higienização preventiva"
            }

            val intent = Intent(
                this,
                ResultadoActivity::class.java
            )

            intent.putExtra("CLIENTE", cliente)
            intent.putExtra("SERVICO", servico)
            intent.putExtra(
                "CEP",
                findViewById<EditText>(R.id.edtCep).text.toString()
            )

            intent.putExtra("HORAS", horas)
            intent.putExtra("DISTANCIA", distancia!!)
            intent.putExtra("MATERIAIS", materiais!!)
            intent.putExtra("OUTROS", outros!!)

            startActivity(intent)
        }
    }
}
