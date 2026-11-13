
package samuel.oliveira.precifica_clima.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orcamentos")
data class OrcamentoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val cliente: String,
    val cep: String,
    val servico: String,

    val horas: Double,
    val distanciaKm: Double,
    val materiais: Double,
    val outrosCustos: Double,

    val custoHora: Double,
    val custoMaoObra: Double,
    val custoDeslocamento: Double,
    val custoTotal: Double,

    val margemPercentual: Double,
    val precoRecomendado: Double,

    val criadoEm: Long = System.currentTimeMillis()
)


