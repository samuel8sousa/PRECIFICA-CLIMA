package samuel.oliveira.precifica_clima.data.repository

import android.content.Context
import samuel.oliveira.precifica_clima.domain.model.PerfilFinanceiro

class PerfilRepository(context: Context) {

    private val prefs = context.getSharedPreferences(
        "perfil_financeiro",
        Context.MODE_PRIVATE
    )

    fun salvar(perfil: PerfilFinanceiro) {
        prefs.edit()
            .putString("remuneracao", perfil.remuneracao.toString())
            .putString("custos", perfil.custosFixos.toString())
            .putString("reserva", perfil.reserva.toString())
            .putString("horas", perfil.horasFaturaveis.toString())
            .putString("custoKm", perfil.custoKm.toString())
            .putString("margem", perfil.margemPercentual.toString())
            .apply()
    }

    fun carregar(): PerfilFinanceiro {
        fun numero(chave: String, padrao: Double): Double {
            return prefs.getString(chave, null)
                ?.toDoubleOrNull() ?: padrao
        }

        return PerfilFinanceiro(
            remuneracao = numero("remuneracao", 3000.0),
            custosFixos = numero("custos", 800.0),
            reserva = numero("reserva", 200.0),
            horasFaturaveis = numero("horas", 100.0),
            custoKm = numero("custoKm", 1.10),
            margemPercentual = numero("margem", 30.0)
        )
    }
}


