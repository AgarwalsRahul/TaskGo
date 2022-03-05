package com.example.tasks.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gitbrowser.presentation.common.visible
import com.example.tasks.R
import com.example.tasks.ui.todo.MainActivity
import com.example.tasks.databinding.ActivityLoginBinding
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.User
import com.example.tasks.util.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val RC_SIGN_IN = 1

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInIntent: Intent

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions);

        binding.loginButton.setOnClickListener {
            signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(this) { dataState ->
            when (dataState) {
                is DataState.Success<User?> -> {
                    displayProgressBar(false)
                    if(dataState.data!=null){

                        sessionManager.login(dataState.data)
                    }else{
                        binding.loginButton.visible()
                    }
                }
                is DataState.Error -> {
                    displayProgressBar(false)
                    displaySnackBar(dataState.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        }



        sessionManager.cachedUser.observe(this) {
            if (it != null && it.email.isNotEmpty()) {
                navToMainActivity()
            }
        }
    }

    private fun displayProgressBar(isDisplayed: Boolean) {
        binding.progressBar.visibility = if (isDisplayed) View.VISIBLE else View.GONE
    }

    private fun displaySnackBar(message: String) {
        val snackbar = Snackbar.make(
            findViewById(R.id.main_container),
            message,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            if (completedTask.isComplete) {
                val account = completedTask.result
                account?.let { googleAccount ->
                    googleAccount.email?.let {
                        viewModel.setStateEvent(LoginStateEvent.LoginEvent(it))
                    }
                }


            }
        } catch (e: ApiException) {

            Log.w("LoginActivity", "signInResult:failed code=" + e.localizedMessage)
            displaySnackBar(e.message ?: "Failed to login. Please try again")
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.setStateEvent(LoginStateEvent.CheckSignedInUserEvent)
    }

    private fun navToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}