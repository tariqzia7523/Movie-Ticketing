package com.movie.tickets.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.movie.tickets.R
import com.movie.tickets.databinding.ActivityLoginBinding


class SignUpActiivty : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    lateinit var binding : ActivityLoginBinding
    var progressDialog: ProgressDialog? = null
    val TAG = "***SignUp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance();

        progressDialog= ProgressDialog(this);
        progressDialog!!.setMessage(getString(R.string.please_wait));

//        binding.signupCall.text = getString(R.string.already_have_an_account)
//        binding.signupCall.setOnClickListener {
//            startActivity(Intent(this@SignUpActiivty,LoginActivity::class.java))
//            finish()
//        }

        binding.signinBtn.text = getString(R.string.signUp)
        binding.signinBtn.setOnClickListener {
            progressDialog!!.show()
            signUpCall()
        }
    }

    private fun signUpCall() {
        if(binding.email.text.toString().equals("") || binding.password.text.toString().equals("")){
            Toast.makeText(this@SignUpActiivty, getString(R.string.add_all_data), Toast.LENGTH_SHORT).show()
            return
        }
        progressDialog!!.show()
        Log.e(TAG,"email "+ binding.email.text.toString())
        Log.e(TAG,"password "+ binding.password.text.toString())
        mAuth!!.createUserWithEmailAndPassword(binding.email.text.toString().trim(), binding.password.text.toString().trim())
            .addOnCompleteListener(this@SignUpActiivty,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        progressDialog!!.dismiss()
                        startActivity(Intent(this@SignUpActiivty,MainActivity::class.java))
                        finish()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@SignUpActiivty, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        progressDialog!!.dismiss()
                    }
                })
    }
}