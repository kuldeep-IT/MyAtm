package com.kuldeep.atm1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kuldeep.atm1.data.Transactions

@Database(
    entities = [Transactions::class],
    version = 1
)
abstract class TransactionDB : RoomDatabase(){

    abstract fun getAllTransactions(): AtmDao

    companion object{

        @Volatile
        private var instance: TransactionDB? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK)
        {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TransactionDB::class.java,
                "article_db.db"
            ).build()
    }

}