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
        /*
        1) First call → Instance is null → Room builds the DB → Instance is updated ✅
        2) Later calls → Instance is not null anymore → it returns the cached instance
        Because building a Room database is expensive, and we don’t want to do it unless we need it.
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
                Room.databaseBuilder(
                    context.applicationContext, // Use context.applicationContext instead of context when you’re creating something that lives longer than an Activity, like a Room database. It's safer, prevents memory leaks, and is considered best practice.
                    InventoryDatabase::class.java,
                    "item_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it } // stores the DB so future calls reuse it.
            }
            /*
            This is a thread-safe, lazy-loaded singleton function.

            It checks:

            If Instance already exists → return it ✅

            If not → enter synchronized block to safely create the DB instance (only 1 thread at a time can go in here).
             */
        }
    }
}