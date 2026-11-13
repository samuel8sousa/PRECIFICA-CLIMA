package samuel.oliveira.precifica_clima.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import samuel.oliveira.precifica_clima.data.local.entity.OrcamentoEntity
import samuel.oliveira.precifica_clima.data.local.dao.OrcamentoDao

@Database(
    entities = [OrcamentoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orcamentoDao(): OrcamentoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "precifica_clima.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}


