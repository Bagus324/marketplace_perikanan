package com.pnj.marketplace_perikanan.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.pnj.marketplace_perikanan.ikan.IkanAdapter
import com.pnj.marketplace_perikanan.databinding.ActivityBeliSuksesBinding
import com.pnj.marketplace_perikanan.databinding.ActivityRiwayatBelanjaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RiwayatPerbelanjaanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBelanjaBinding

    private lateinit var ikanRecyclerView: RecyclerView
    private lateinit var riwayatArrayList: ArrayList<RiwayatBelanjaLoad>
    private lateinit var riwayatAdapter: RiwayatBelanjaAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRiwayatBelanjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        ikanRecyclerView = binding.makananListView
        ikanRecyclerView.layoutManager = LinearLayoutManager(this)
        ikanRecyclerView.setHasFixedSize(true)

        riwayatArrayList = arrayListOf()
        riwayatAdapter = RiwayatBelanjaAdapter(riwayatArrayList, "hasil_belanja")

        ikanRecyclerView.adapter = riwayatAdapter

        load_data()
//        swipeDelete()

//        val intent = intent
//        val nama_ikan = intent.getStringExtra("nama_ikan").toString()
//        val stok_ikan = intent.getStringExtra("stok_ikan").toString()
//        val harga_ikan = intent.getStringExtra("harga_ikan").toString()
//        val jumlah = intent.getStringExtra("jumlah_beli").toString()
//        val total = intent.getStringExtra("total").toString()

//        binding.TxtEditNama.setText(nama_ikan)
//        binding.TxtEditStok.setText(stok_ikan)
//        binding.TxtEditHarga.setText(harga_ikan)
        // TextChangedListener




    }

    private fun load_data() {
        riwayatArrayList.clear()
        db = FirebaseFirestore.getInstance()
        val email = firebaseAuth.currentUser!!.email.toString()
        db.collection("riwayat_belanja").whereEqualTo("barusan", "0").whereEqualTo("user_id", email).orderBy("time").orderBy("time").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED)
                        riwayatArrayList.add(dc.document.toObject(RiwayatBelanjaLoad::class.java))
                    Log.e("document", dc.document.toString())
                }
                riwayatAdapter.notifyDataSetChanged()
            }
        })
    }











}