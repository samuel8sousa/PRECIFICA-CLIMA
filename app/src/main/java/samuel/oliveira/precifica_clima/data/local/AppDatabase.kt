
package samuel.oliveira.precifica_clima.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import samuel.oliveira.precifica_clima.data.local.dao.OrcamentoDao
import samuel.oliveira.precifica_clima.data.local.entity.OrcamentoEntity

@Database(
    entities = [OrcamentoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orcamentoDao(): OrcamentoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ==========================================
        // MIGRAÇÃO 1 -> 2
        // ADICIONAR CAMPOS DO ENDEREÇO
        // ==========================================

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN logradouro TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN complemento TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN bairro TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN cidade TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN uf TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // ==========================================
        // MIGRAÇÃO 2 -> 3
        // ADICIONAR NÚMERO DA RESIDÊNCIA
        // ==========================================

        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "ALTER TABLE orcamentos " +
                            "ADD COLUMN numero TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // ==========================================
        // CRIAR OU RECUPERAR O BANCO DE DADOS
        // ==========================================

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "precifica_clima.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
