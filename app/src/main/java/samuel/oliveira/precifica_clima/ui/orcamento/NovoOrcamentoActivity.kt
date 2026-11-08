package samuel.oliveira.precifica_clima.ui.orcamento

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

import samuel.oliveira.precifica_clima.R

class NovoOrcamentoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_novo_orcamento)

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

            val cliente = campoCliente.text.toString()

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

            startActivity(intent)
        }
    }
}
