package com.nristekk.apps.architecturelab.database.task

import android.util.Log
import androidx.annotation.WorkerThread
import com.nristekk.apps.architecturelab.database.items.User
import com.nristekk.apps.architecturelab.database.items.UserDao
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

class UserRepos(private val userDao: UserDao) {


    /*
    * Get all user as 'Flow' to be the result.
    */
    fun getAll() : Flow<List<User>> = userDao.getAll()

    /*
    * Get all user as 'Observable' to be the result.
    */
    fun getAllObservable() : Observable<List<User>> = userDao.getAllObservable()



    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllCoroutines() : List<User> = userDao.getAllCoroutines()



    //Update User under coroutines
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) = userDao.insert(user)



    //Update multiple users using coroutines
    @Suppress("RedundantSuspendModifier")
    suspend fun updateUsers(users: List<User>) = userDao.updateUsers(users)


    //Update individual using coroutines
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(user: User) = userDao.update(user)


    //Update User under coroutines
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() = userDao.deleteAll()




}