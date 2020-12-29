package com.nristekk.apps.architecturelab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nristekk.apps.architecturelab.database.items.User
import com.nristekk.apps.architecturelab.database.items.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = [User::class], version = 1)
abstract class LabDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: LabDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LabDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabDatabase::class.java,
                    "word_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.userDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutines.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(userDao: UserDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            userDao.deleteAll()
            val bob = User("Bob","It's Bob",  35,"Bob@bob.bo")
            val alice = User( "Alice", "It's Alice", 34, "ALICE@alice")
            val carol = User( "Carol", "It's Carol", 25,"Carol@carol")
            userDao.insert(bob)
            userDao.insert(alice)
            userDao.insert(carol)
        }
    }
}