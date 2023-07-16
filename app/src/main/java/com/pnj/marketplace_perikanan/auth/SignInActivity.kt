package com.pnj.marketplace_perikanan.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pnj.marketplace_perikanan.MainActivity
import com.pnj.marketplace_perikanan.databinding.ActivitySignInBinding
import com.pnj.marketplace_perikanan.users.UsersMainActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPass.text.toString()
            signin_firebase(email, password)
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signin_firebase(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    var emailCurr = firebaseAuth.currentUser!!.email.toString()
                    var emailListed = emailCurr.toList()
                    var idx = 0
                    for (x in emailListed){
//                        Log.e("index @ ", x.toString())
                        if (x.toString() == "@") {
                            break
                        }
                        idx += 1
                    }
                    var domainEmail = emailCurr.substring(idx)
                    Log.e("domain", domainEmail)
                    if (domainEmail == "@admin.com"){
                        Toast.makeText(this, "berhasil login sebagain admin", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "berhasil login sebagain user", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, UsersMainActivity::class.java)
                        startActivity(intent)
                    }


                }
                else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
        else {
            Toast.makeText(this, "Lengkapi Input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            var emailCurr = firebaseAuth.currentUser!!.email.toString()
            var emailListed = emailCurr.toList()
            var idx = 0
            for (x in emailListed){
//                        Log.e("index @ ", x.toString())
                if (x.toString() == "@") {
                    break
                }
                idx += 1
            }
            var domainEmail = emailCurr.substring(idx)
            Log.e("domain", domainEmail)
            if (domainEmail == "@admin.com"){
                Toast.makeText(this, "berhasil login sebagai admin", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else{
                Toast.makeText(this, "berhasil login sebagai user", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UsersMainActivity::class.java)
                startActivity(intent)
            }
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }
}