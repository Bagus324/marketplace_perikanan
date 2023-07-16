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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BeliSuksesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeliSuksesBinding

    private lateinit var ikanRecyclerView: RecyclerView
    private lateinit var ikanArrayList: ArrayList<RiwayatBelanjaLoad>
    private lateinit var riwayatAdapter: RiwayatBelanjaAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeliSuksesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        ikanRecyclerView = binding.makananListView
        ikanRecyclerView.layoutManager = LinearLayoutManager(this)
        ikanRecyclerView.setHasFixedSize(true)

        ikanArrayList = arrayListOf()
        riwayatAdapter = RiwayatBelanjaAdapter(ikanArrayList, "hasil_belanja")

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

        binding.BtnCofirm.setOnClickListener {
            val email = firebaseAuth.currentUser!!.email.toString()
//            val updating =
//            updating.update("barusan", "0")
            val ids = ArrayList<String>()
            CoroutineScope(Dispatchers.IO).launch {
                val personQuery = db.collection("riwayat_belanja")
                    .whereEqualTo("user_id", email)
                    .whereEqualTo("barusan", "1")
                    .get()
                    .await()

                for (document in personQuery) {
                    ids.add(document.id)
                }
//                Toast.makeText(this@BeliSuksesActivity, id, Toast.LENGTH_LONG).show()
                for (id in ids){
//                    if (db.collection("riwayat_belanja").document(id)!=null){
//                        Log.e("id", id)
//                    }
                    val updating = db.collection("riwayat_belanja").document(id)
                    updating.update("barusan", "0").addOnSuccessListener {
                        Toast.makeText(this@BeliSuksesActivity, "sukses update", Toast.LENGTH_LONG).show()
                    }
                }
//                if (personQuery.documents.isNotEmpty()) {
//                    for (document in personQuery) {
//                        try {
//                            db.collection("data_ikan").document(document.id).update("barusan", "0")
//                        }
//                        // Exception kotlin
//                        catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(this@BeliSuksesActivity, "Kosong", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//                else {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@BeliSuksesActivity,
//                            "No fishes matched the query.", Toast.LENGTH_LONG).show()
//                    }
//                }
            }
            val intent = Intent(this, UsersMainActivity::class.java)
            startActivity(intent)
        }



    }

    private fun load_data() {
        ikanArrayList.clear()
        db = FirebaseFirestore.getInstance()
        val email = firebaseAuth.currentUser!!.email.toString()
        db.collection("riwayat_belanja").whereEqualTo("barusan", "1").whereEqualTo("user_id", email).
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
                        ikanArrayList.add(dc.document.toObject(RiwayatBelanjaLoad::class.java))
                }
                riwayatAdapter.notifyDataSetChanged()
            }
        })
    }











}