
package samuel.oliveira.precifica_clima.ui.perfil

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import com.google.android.material.switchmaterial.SwitchMaterial

import samuel.oliveira.precifica_clima.R
import samuel.oliveira.precifica_clima.domain.model.PerfilFinanceiro
import samuel.oliveira.precifica_clima.data.repository.PerfilRepository

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // ====================================
        // 1. REPOSITÓRIO DO PERFIL FINANCEIRO
        // ====================================

        val repositorio = PerfilRepository(requireContext())

        // ====================================
        // 2. IDENTIFICAR OS CAMPOS DO XML
        // ====================================

        val remuneracao = view.findViewById<EditText>(
            R.id.edtRemuneracao
        )

        val custos = view.findViewById<EditText>(
            R.id.edtCustos
        )

        val reserva = view.findViewById<EditText>(
            R.id.edtReserva
        )

        val horas = view.findViewById<EditText>(
            R.id.edtHoras
        )

        val custoKm = view.findViewById<EditText>(
            R.id.edtCustoKm
        )

        val margem = view.findViewById<EditText>(
            R.id.edtMargem
        )

        val salvar = view.findViewById<Button>(
            R.id.btnSalvarPerfil
        )

        // ====================================
        // 3. CARREGAR OS DADOS SALVOS
        // ====================================

        val perfil = repositorio.carregar()

        remuneracao.setText(perfil.remuneracao.toString())
        custos.setText(perfil.custosFixos.toString())
        reserva.setText(perfil.reserva.toString())
        horas.setText(perfil.horasFaturaveis.toString())
        custoKm.setText(perfil.custoKm.toString())
        margem.setText(perfil.margemPercentual.toString())

        // ====================================
        // 4. SALVAR AS CONFIGURAÇÕES
        // ====================================

        salvar.setOnClickListener {

            fun lerNumero(campo: EditText): Double? {
                return campo.text.toString()
                    .trim()
                    .replace(",", ".")
                    .toDoubleOrNull()
            }

            val valores = listOf(
                remuneracao,
                custos,
                reserva,
                horas,
                custoKm,
                margem
            ).map { campo ->
                lerNumero(campo)
            }

            // Verifica campos vazios, inválidos ou negativos
            if (valores.any {
                    it == null || !it.isFinite() || it < 0
                }) {

                Toast.makeText(
                    requireContext(),
                    "Confira os valores informados.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val novoPerfil = PerfilFinanceiro(
                remuneracao = valores[0]!!,
                custosFixos = valores[1]!!,
                reserva = valores[2]!!,
                horasFaturaveis = valores[3]!!,
                custoKm = valores[4]!!,
                margemPercentual = valores[5]!!
            )

            // Verifica as regras do perfil
            if (
                novoPerfil.horasFaturaveis <= 0 ||
                novoPerfil.margemPercentual >= 100
            ) {

                Toast.makeText(
                    requireContext(),
                    "Horas devem ser maiores que zero e margem menor que 100%.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // Armazena os valores no dispositivo
            repositorio.salvar(novoPerfil)

            Toast.makeText(
                requireContext(),
                "Perfil financeiro salvo com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        }

        // ====================================
        // 5. CONFIGURAÇÃO DO MODO ESCURO
        // ====================================

        val preferencias = requireContext()
            .getSharedPreferences(
                "precifica_config",
                Context.MODE_PRIVATE
            )

        val switchModoEscuro =
            view.findViewById<SwitchMaterial>(
                R.id.switchModoEscuro
            )

        val temaEscuroAtual =
            (resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        // Recupera a preferência do usuário
        switchModoEscuro.isChecked =
            preferencias.getBoolean(
                "modo_escuro",
                temaEscuroAtual
            )

        // Detecta mudanças no botão do modo escuro
        switchModoEscuro.setOnCheckedChangeListener {
                _, ativado ->

            // Salva a escolha
            preferencias.edit()
                .putBoolean("modo_escuro", ativado)
                .apply()

            // Aplica o tema escolhido
            AppCompatDelegate.setDefaultNightMode(
                if (ativado) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }
}
