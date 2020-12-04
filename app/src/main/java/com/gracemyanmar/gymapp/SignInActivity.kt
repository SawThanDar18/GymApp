package com.gracemyanmar.gymapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
        }
    }

    lateinit var constraintLayout: ConstraintLayout
    lateinit var txt_layout: ConstraintLayout
    lateinit var tilEmail: TextInputLayout
    lateinit var tilPassword: TextInputLayout
    lateinit var titEmail: TextInputEditText
    lateinit var titPassword: TextInputEditText
    lateinit var btnFirebaseSignIn: Button

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        constraintLayout = findViewById(R.id.constraintLayout)
        txt_layout = findViewById(R.id.txt_layout)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)

        titEmail = findViewById(R.id.titEmail)
        titPassword = findViewById(R.id.titPassword)
        btnFirebaseSignIn = findViewById(R.id.btnFirebaseSignIn)

        firebaseAuth = FirebaseAuth.getInstance()

        setUpFadeTransition()
        signInUser()

    }

    private fun signInUser () {

        btnFirebaseSignIn.setOnClickListener{

            if (isNetworkConnected()) {
                firebaseLogIn()
            } else {
                Snackbar.make(constraintLayout, R.string.internet_connection_error_msg, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseLogIn () {

        var email = titEmail.text.toString()
        var password = titPassword.text.toString()

        if (email.isEmpty()){
            titEmail.setError(R.string.email_error_msg.toString())
            titEmail.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            titEmail.setError(R.string.email_invaild_msg.toString())
            titEmail.requestFocus()
            return
        }

        if (password.isEmpty()){
            titPassword.setError(R.string.password_error_msg.toString())
            titPassword.requestFocus()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    startActivity(DashBoardActivity.newIntent(this))
                    finish()
                } else {

                    task.exception!!.message?.let {
                        Snackbar.make(constraintLayout, it, Snackbar.LENGTH_LONG).show()
                    }

                }
            })
    }

    fun setUpFadeTransition() {

        txt_layout.visibility = View.VISIBLE

        val animator = ObjectAnimator.ofFloat(txt_layout, View.ALPHA, 0f, 1f)
        animator.addListener(object: AnimatorListenerAdapter(){
            @SuppressLint("ResourceType")
            override fun onAnimationEnd(animation: Animator?) {
                val aniFade: Animation = AnimationUtils.loadAnimation(applicationContext, R.animator.fade_in)
                tilEmail.visibility = View.VISIBLE
                tilEmail.startAnimation(aniFade)
                tilPassword.visibility = View.VISIBLE
                tilPassword.startAnimation(aniFade)
                btnFirebaseSignIn.visibility = View.VISIBLE
                btnFirebaseSignIn.startAnimation(aniFade)

            }
        })

        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 2000
        animator.start()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}