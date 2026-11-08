package samuel.oliveira.precifica_clima.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.ui.orcamento.NovoOrcamentoActivity

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val btnNovo = view.findViewById<Button>(
            R.id.btnNovoOrcamento
        )

        val btnPerfil = view.findViewById<Button>(
            R.id.btnAjustarPerfil
        )

        btnNovo.setOnClickListener {
            val intent = Intent(
                requireContext(),
                NovoOrcamentoActivity::class.java
            )

            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val navigation = requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottomNavigation
                )

            navigation.selectedItemId = R.id.nav_perfil
        }
    }
}


