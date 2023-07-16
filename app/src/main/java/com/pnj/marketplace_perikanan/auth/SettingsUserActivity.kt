package com.pnj.marketplace_perikanan.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pnj.marketplace_perikanan.databinding.ActivityKeranjangBinding
import com.pnj.marketplace_perikanan.databinding.ActivitySettingsBinding
import com.pnj.marketplace_perikanan.databinding.ActivitySettingsUserBinding
import com.pnj.marketplace_perikanan.keranjang.KeranjangActivity
import com.pnj.marketplace_perikanan.users.RiwayatPerbelanjaanActivity

class SettingsUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.username.setText(firebaseAuth.currentUser!!.email.toString())
        binding.btnUbahPass.setOnClickListener {
            val new_password = binding.txtUbahPass.text.toString()
            edit_password(new_password)
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.btnCart.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }
        binding.btnLog.setOnClickListener {
            val intent = Intent(this, RiwayatPerbelanjaanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun edit_password(new_password: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val new_password = new_password

        user!!.updatePassword(new_password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Password Berhasil Diubah", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Password Gagal Diubah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}