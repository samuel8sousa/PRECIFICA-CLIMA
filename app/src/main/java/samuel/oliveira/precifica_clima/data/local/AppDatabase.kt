
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
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orcamentoDao(): OrcamentoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migração 1 -> 2: endereço
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN logradouro TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN complemento TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN bairro TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN cidade TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN uf TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // Migração 2 -> 3: número da residência
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN numero TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // Migração 3 -> 4: recebimentos
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN valorRecebido REAL NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE orcamentos ADD COLUMN dataRecebimento INTEGER"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "precifica_clima.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
