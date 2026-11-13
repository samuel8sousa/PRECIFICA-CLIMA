
package samuel.oliveira.precifica_clima.domain.model

data class PerfilFinanceiro(
    val remuneracao: Double,
    val custosFixos: Double,
    val reserva: Double,
    val horasFaturaveis: Double,
    val custoKm: Double,
    val margemPercentual: Double
) {
    fun calcularCustoHora(): Double {
        require(horasFaturaveis > 0)

        return (remuneracao + custosFixos + reserva) / horasFaturaveis
    }
}


