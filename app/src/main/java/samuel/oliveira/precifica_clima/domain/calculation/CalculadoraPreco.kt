
package samuel.oliveira.precifica_clima.domain.calculation

import samuel.oliveira.precifica_clima.domain.model.PerfilFinanceiro

data class ResultadoPreco(
    val custoHora: Double,
    val maoObra: Double,
    val materiais: Double,
    val deslocamento: Double,
    val outrosCustos: Double,
    val custoTotal: Double,
    val precoRecomendado: Double
)

object CalculadoraPreco {

    fun calcular(
        perfil: PerfilFinanceiro,
        horas: Double,
        distanciaKm: Double,
        materiais: Double,
        outrosCustos: Double
    ): ResultadoPreco {

        require(horas > 0)
        require(distanciaKm >= 0)
        require(materiais >= 0)
        require(outrosCustos >= 0)
        require(perfil.margemPercentual >= 0)
        require(perfil.margemPercentual < 100)

        val custoHora = perfil.calcularCustoHora()

        val maoObra = horas * custoHora

        val deslocamento =
            distanciaKm * perfil.custoKm

        val custoTotal =
            maoObra +
                    deslocamento +
                    materiais +
                    outrosCustos

        val margem =
            perfil.margemPercentual / 100.0

        val precoRecomendado =
            custoTotal / (1.0 - margem)

        return ResultadoPreco(
            custoHora = custoHora,
            maoObra = maoObra,
            materiais = materiais,
            deslocamento = deslocamento,
            outrosCustos = outrosCustos,
            custoTotal = custoTotal,
            precoRecomendado = precoRecomendado
        )
    }
}
