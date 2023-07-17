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
import com.pnj.marketplace_perikanan.auth.SignInActivity
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.ikan.IkanAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.pnj.marketplace_perikanan.databinding.ActivityBeliGagalBinding
import com.pnj.marketplace_perikanan.databinding.ActivityLoadingBinding
import java.util.Timer
import java.util.TimerTask
//import kotlinx.android.synthetic.main.activity_splas_screen.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    private lateinit var ikanArrayList: ArrayList<Ikan>
    private lateinit var ikanAdapter: IkanAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ikanArrayList = arrayListOf()
        ikanAdapter = IkanAdapter(ikanArrayList, "hasil_belanja")
        val intent = Intent(this, SignInActivity::class.java)
        binding.IVLoad.alpha = 0f
        binding.IVLoad.animate().setDuration(3000).alpha(1f).withEndAction{
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        // TextChangedListener



    }








}