
package samuel.oliveira.precifica_clima.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

import samuel.oliveira.precifica_clima.data.local.entity.OrcamentoEntity

@Dao
interface OrcamentoDao {

    // ==========================================
    // CADASTRAR ORÇAMENTO
    // ==========================================

    @Insert
    suspend fun inserir(
        orcamento: OrcamentoEntity
    ): Long

    // ==========================================
    // LISTAR ORÇAMENTOS
    // ==========================================

    @Query("SELECT * FROM orcamentos ORDER BY criadoEm DESC")
    suspend fun listar(): List<OrcamentoEntity>

    // ==========================================
    // BUSCAR ORÇAMENTO PELO ID
    // ==========================================

    @Query("SELECT * FROM orcamentos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): OrcamentoEntity?

    // ==========================================
    // ATUALIZAR ORÇAMENTO
    // ==========================================

    @Update
    suspend fun atualizar(orcamento: OrcamentoEntity): Int

    // ==========================================
    // EXCLUIR ORÇAMENTO
    // ==========================================

    @Delete
    suspend fun excluir(orcamento: OrcamentoEntity)

    // ==========================================
    // EXCLUIR TODOS OS ORÇAMENTOS
    // ==========================================

    @Query("DELETE FROM orcamentos")
    suspend fun excluirTodos(): Int

    // ==========================================
    // REGISTRAR RECEBIMENTO
    // ==========================================

    @Query("""
        UPDATE orcamentos
        SET valorRecebido = precoRecomendado,
            dataRecebimento = :data
        WHERE id = :id
        AND dataRecebimento IS NULL
    """)
    suspend fun marcarComoRecebido(
        id: Long,
        data: Long
    ): Int

    // ==========================================
    // DESFAZER RECEBIMENTO
    // ==========================================

    @Query("""
        UPDATE orcamentos
        SET valorRecebido = 0,
            dataRecebimento = NULL
        WHERE id = :id
    """)
    suspend fun desfazerRecebimento(
        id: Long
    ): Int

    // ==========================================
    // TOTAL RECEBIDO NO MÊS
    // ==========================================

    @Query("""
        SELECT COALESCE(SUM(valorRecebido), 0)
        FROM orcamentos
        WHERE dataRecebimento >= :inicio
        AND dataRecebimento < :fim
    """)
    suspend fun somarRecebimentos(
        inicio: Long,
        fim: Long
    ): Double

    // ==========================================
    // CUSTOS DOS SERVIÇOS RECEBIDOS NO MÊS
    // ==========================================

    @Query("""
        SELECT COALESCE(SUM(custoTotal), 0)
        FROM orcamentos
        WHERE dataRecebimento >= :inicio
        AND dataRecebimento < :fim
    """)
    suspend fun somarCustosRecebidos(
        inicio: Long,
        fim: Long
    ): Double

    // ==========================================
    // QUANTIDADE DE SERVIÇOS RECEBIDOS NO MÊS
    // ==========================================

    @Query("""
        SELECT COUNT(*)
        FROM orcamentos
        WHERE dataRecebimento >= :inicio
        AND dataRecebimento < :fim
    """)
    suspend fun contarServicosRecebidos(
        inicio: Long,
        fim: Long
    ): Int

    // ==========================================
    // VALOR TOTAL DOS ORÇAMENTOS PENDENTES
    // CRIADOS NO MÊS
    // ==========================================

    @Query("""
        SELECT COALESCE(SUM(precoRecomendado), 0)
        FROM orcamentos
        WHERE criadoEm >= :inicio
        AND criadoEm < :fim
        AND dataRecebimento IS NULL
    """)
    suspend fun somarValorPendenteDoMes(
        inicio: Long,
        fim: Long
    ): Double

    // ==========================================
    // QUANTIDADE DE ORÇAMENTOS PENDENTES
    // CRIADOS NO MÊS
    // ==========================================

    @Query("""
        SELECT COUNT(*)
        FROM orcamentos
        WHERE criadoEm >= :inicio
        AND criadoEm < :fim
        AND dataRecebimento IS NULL
    """)
    suspend fun contarPendentesDoMes(
        inicio: Long,
        fim: Long
    ): Int
}
