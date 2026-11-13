
package samuel.oliveira.precifica_clima.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

import samuel.oliveira.precifica_clima.data.local.entity.OrcamentoEntity

@Dao
interface OrcamentoDao {

    @Insert
    suspend fun inserir(
        orcamento: OrcamentoEntity
    ): Long

    @Query("SELECT * FROM orcamentos ORDER BY criadoEm DESC")
    suspend fun listar(): List<OrcamentoEntity>

    @Query("SELECT * FROM orcamentos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): OrcamentoEntity?

    @Update
    suspend fun atualizar(orcamento: OrcamentoEntity)

    @Delete
    suspend fun excluir(orcamento: OrcamentoEntity)
}


