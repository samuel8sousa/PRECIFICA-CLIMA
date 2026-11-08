package samuel.oliveira.precifica_clima.ui.perfil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

import samuel.oliveira.precifica_clima.R

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val botaoSalvar = view.findViewById<Button>(
            R.id.btnSalvarPerfil
        )

        botaoSalvar.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Tela pronta. Persistência será integrada na próxima etapa.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}


