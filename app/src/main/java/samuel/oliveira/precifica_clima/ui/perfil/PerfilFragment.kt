package samuel.oliveira.precifica_clima.ui.perfil

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import com.google.android.material.switchmaterial.SwitchMaterial

import samuel.oliveira.precifica_clima.R

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera as preferências salvas
        val preferencias = requireContext()
            .getSharedPreferences(
                "precifica_config",
                Context.MODE_PRIVATE
            )

        // Localiza o botão de modo escuro
        val switchModoEscuro =
            view.findViewById<SwitchMaterial>(
                R.id.switchModoEscuro
            )

        // Verifica o tema atual do sistema
        val sistemaEscuro =
            (resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        // Recupera a escolha anterior do usuário
        switchModoEscuro.isChecked =
            preferencias.getBoolean(
                "modo_escuro",
                sistemaEscuro
            )

        // Detecta quando o usuário altera o botão
        switchModoEscuro.setOnCheckedChangeListener {
                _, ativado ->

            // Salva a preferência
            preferencias.edit()
                .putBoolean("modo_escuro", ativado)
                .apply()

            // Altera o tema do aplicativo
            AppCompatDelegate.setDefaultNightMode(
                if (ativado) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        // Botão de salvar perfil
        val botaoSalvar = view.findViewById<Button>(
            R.id.btnSalvarPerfil
        )

        botaoSalvar.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Configurações do perfil!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
