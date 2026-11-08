package samuel.oliveira.precifica_clima.ui.orcamento

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import samuel.oliveira.precifica_clima.R

class ResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resultado)

        val cliente = intent.getStringExtra("CLIENTE")
            ?: "Não informado"

        val servico = intent.getStringExtra("SERVICO")
            ?: "Não informado"

        val txtCliente = findViewById<TextView>(
            R.id.txtResultadoCliente
        )

        val txtServico = findViewById<TextView>(
            R.id.txtResultadoServico
        )

        txtCliente.text = cliente
        txtServico.text = servico

        val btnEditar = findViewById<Button>(
            R.id.btnEditarOrcamento
        )

        val btnSalvar = findViewById<Button>(
            R.id.btnSalvarOrcamento
        )

        btnEditar.setOnClickListener {
            finish()
        }

        btnSalvar.setOnClickListener {
            Toast.makeText(
                this,
                "Salvamento com Room será integrado na próxima entrega.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}


