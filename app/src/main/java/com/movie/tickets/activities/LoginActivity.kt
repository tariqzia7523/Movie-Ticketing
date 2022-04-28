package com.movie.tickets.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.movie.tickets.R
import com.movie.tickets.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var TAG: String? = null
    var progressDialog: ProgressDialog? = null
    lateinit var binding : ActivityLoginBinding
    var passwrdvisibility : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog= ProgressDialog(this);
        progressDialog!!.setMessage(getString(R.string.please_wait));
        mAuth = FirebaseAuth.getInstance();

        binding.signinBtn.setOnClickListener {
            progressDialog!!.show()
            if(!binding.email.text.toString().equals("") || !binding.password.text.toString().equals(""))
                loginCall()

        }

        binding.visiblePasswrd.setOnClickListener {
            if(passwrdvisibility){
                passwrdvisibility=false
                binding.visiblePasswrd.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                binding.password.setTransformationMethod(PasswordTransformationMethod())
                binding.password.setSelection(binding.password.text.toString().length);
            }else{
                passwrdvisibility=true
                binding.visiblePasswrd.setImageResource(R.drawable.ic_baseline_visibility_24)
                binding.password.setTransformationMethod(null)
                binding.password.setSelection(binding.password.text.toString().length);

            }
        }


    }

    private fun loginCall() {
        mAuth!!.signInWithEmailAndPassword(binding.email.text.toString().trim(), binding.password.text.toString().trim())
            .addOnCompleteListener(this@LoginActivity) { task ->
                progressDialog!!.dismiss()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    startActivity(Intent(this@LoginActivity, MivieListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}