package com.nristekk.apps.architecturelab.database.items

import androidx.room.*
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM user_table")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM user_table")
    fun getAllObservable(): Observable<List<User>>

    @Query("SELECT * FROM user_table")
    suspend fun getAllCoroutines():List<User>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun updateUsers(users: List<User>)

    @Update
    suspend fun update(user:User)


    @Query("DELETE FROM user_table")
    suspend fun deleteAll()


}