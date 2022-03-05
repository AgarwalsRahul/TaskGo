package com.example.tasks.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.BaseApplication
import com.example.tasks.domain.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class SessionManager @Inject constructor(
    val application: BaseApplication,
    val googleSignInOptions: GoogleSignInOptions
) {

    private val _cachedUser = MutableLiveData<User?>()
    val cachedUser: LiveData<User?>
        get() = _cachedUser

    fun login(newValue: User) {
        setValue(newValue)
    }


    @DelicateCoroutinesApi
    fun logout() {
        try {
            val client = GoogleSignIn.getClient(application, googleSignInOptions);
            client.signOut()
        } catch (e: Exception) {

        } finally {
            setValue(null)
        }

    }

    fun setValue(newValue: User?) {
        GlobalScope.launch(Dispatchers.Main) {
            if (_cachedUser.value != newValue) {
                _cachedUser.value = newValue
            }

        }
    }
}