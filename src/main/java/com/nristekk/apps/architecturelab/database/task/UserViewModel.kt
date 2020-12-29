package com.nristekk.apps.architecturelab.database.task

import android.util.Log
import androidx.lifecycle.*
import com.nristekk.apps.architecturelab.database.items.User
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */

class UserViewModel(private val repository: UserRepos) : ViewModel() {



    /* Using LiveData and caching what allWords returns has several benefits:
    * - We can put an observer on the data (instead of polling for changes) and only update the
    * the UI when the data actually changes.
    * - Repository is completely separated from the UI through the ViewModel.
    */
    fun getAll():LiveData<List<User>> = repository.getAll().asLiveData()


    //get All Users as Observable data fashion
    fun getAllObservable() : Observable<List<User>> = repository.getAllObservable()


    //get All users under coroutines usage
    suspend  fun getAllCoroutines():List<User> = repository.getAllCoroutines()



    /*
     * Launching a new coroutines to insert the data in a non-blocking way
     */
    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }


    /*
    * Suspend function to update user for specific logic using
    * Functional type as parameter
    */
    suspend fun transformList(inList:List<User>, operation:(User)-> User):List<User>{
        return inList.map(operation).toList()
    }


    /*
    * Suspend function to update users (List of users)
    */
    suspend fun updateUsers(users: List<User>) = repository.updateUsers(users)



    //Launching a new coroutines to update one user in a non-blocking way
    fun update(user:User) = viewModelScope.launch {
        repository.update(user)
    }


    //Launching a new coroutines to delete the data in a non-blocking way
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }


}


class UserViewModelFactory(private val repository: UserRepos) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}