package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */

//The class you define is abstract because Room creates the implementation for you.
/*
Specify the Item as the only class with the list of entities.
Set the version as 1. Whenever you change the schema of the database table, you have to increase the version number.
Set exportSchema to false so as not to keep schema version history backups.
*/
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        /*
        The value of a volatile variable is never cached, and all reads and writes are to and from the main memory.
        These features help ensure the value of Instance is always up to date and is the same for all execution threads
        */
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDataBase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            /*
            Multiple threads can potentially ask for a database instance at the same time, which results
            in two databases instead of one. This issue is known as a race condition. Wrapping the code to get the
            database inside a synchronized block means that only one thread of execution at a time can enter this
            block of code, which makes sure the database only gets initialized once.
            */
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it}
            }
        }
    }
}