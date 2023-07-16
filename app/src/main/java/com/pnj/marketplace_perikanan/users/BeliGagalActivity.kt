package com.pnj.marketplace_perikanan.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pnj.marketplace_perikanan.R
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.ikan.IkanAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.pnj.marketplace_perikanan.databinding.ActivityBeliGagalBinding
import java.util.Timer
import java.util.TimerTask

class BeliGagalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeliGagalBinding

    private lateinit var ikanArrayList: ArrayList<Ikan>
    private lateinit var ikanAdapter: IkanAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var timer: Timer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeliGagalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ikanArrayList = arrayListOf()
        ikanAdapter = IkanAdapter(ikanArrayList, "hasil_belanja")
        val intent = Intent(this, UsersMainActivity::class.java)
        val textView = findViewById<TextView>(R.id.TVCountdown)
        object : CountDownTimer(11000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                val count = millisUntilFinished / 1000
                textView.setText("Beralih ke halaman utama dalam (" + count + ") detik")
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                textView.setText("Beralih!")
                startActivity(intent)
            }
        }.start()

        // TextChangedListener

        binding.BtnCofirm.setOnClickListener {
            startActivity(intent)
        }


    }








}