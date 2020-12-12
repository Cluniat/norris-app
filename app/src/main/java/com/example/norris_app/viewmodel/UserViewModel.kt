package com.example.norris_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.norris_app.database.UserDao
import com.example.norris_app.model.User
import kotlinx.coroutines.*
import org.mindrot.jbcrypt.BCrypt

class UserViewModel(val db: UserDao, application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        Log.i("UserViewModel", "created")
        addAdminUser()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("UserViewModel", "destroyed")
        viewModelJob.cancel()
    }

    /**
     * Create admin user (lpf stand for 'le plus fort' ;) )
     */
    private fun addAdminUser() {
        uiScope.launch {
            val userAdmin = User()
            userAdmin.lastname = "Norris"
            userAdmin.firstname = "Chuck"
            userAdmin.birthdayDate = "30/01/1997"
            userAdmin.gender = 0
            userAdmin.email = "chuck@norris.lpf"
            userAdmin.password = BCrypt.hashpw("cn", BCrypt.gensalt())
            insert(userAdmin)
        }
    }

    /**
     * insert user
     */
    private suspend fun insert(user: User): Long {
        var id = 0L
        withContext(Dispatchers.IO) {
            id = db.insert(user)
        }
        return id
    }

    /**
     * get a user with its id
     */
    fun getUserById(id: Long, callback: suspend (user: User?) -> Unit): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                callback(db.get(id))
            }
        }
    }

    /**
     * check if email and password correspond to an existing user
     */
    fun checkLogin(
        login: String,
        password: String,
        callback: suspend (isValid: Boolean, user: User?) -> Unit
    ): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                val user = db.getByEmail(login)
                if (user != null && BCrypt.checkpw(password, user.password)) {
                    callback(true, user)
                } else {
                    callback(false, null)
                }
            }
        }
    }

    /**
     * create user
     */
    fun createUser(user: User, callback: suspend (isCreated: Boolean) -> Unit): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                //Verify email address uniqueness
                if (db.getByEmail(user.email.toString()) == null) {
                    insert(user)
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
    }

    /**
     * Verify email address format
     */
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email != ""
    }

    /**
     * update user
     */
    fun updateUser(user: User, callback: suspend (user: User) -> Unit): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                db.update(user)
                callback(user)
            }
        }
    }

    /**
     * add quote to user's favourite list
     */
    fun addFavourites(
        userID: Long,
        quote: String,
        callback: suspend (isUpdated: Boolean, isLimitReached: Boolean, alreadyExist: Boolean) -> Unit
    ): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                val user = db.get(userID)
                if (user != null) {
                    when {
                        //Verify favourite list length
                        user.favourites!!.split('~').size >= 11 -> callback(false, true, false)
                        // Verify quote uniqueness
                        user.favourites!!.contains(quote) -> callback(false, false, true)
                        else -> {
                            user.favourites += "~$quote"
                            db.update(user)
                            callback(true, false, false)
                        }
                    }
                } else {
                    callback(false, false, false)
                }

            }
        }
    }

    /**
     * remove quote from user's favourite list
     */
    fun removeFavourite(
        userID: Long,
        quote: String,
        callback: suspend (isRemoved: Boolean, user: User?) -> Unit
    ): Job {
        return uiScope.launch {
            withContext(Dispatchers.IO) {
                val user = db.get(userID)
                if (user != null) {
                    user.favourites = user.favourites!!.replace("~$quote", "")
                    db.update(user)
                    callback(true, user)
                } else {
                    callback(false, null)
                }
            }
        }
    }
}