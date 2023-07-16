package com.pnj.marketplace_perikanan.keranjang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pnj.marketplace_perikanan.databinding.ActivityKeranjangBinding
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.ikan.IkanAdapter
import com.pnj.marketplace_perikanan.users.RiwayatBelanja
import com.pnj.marketplace_perikanan.users.RiwayatBelanjaAdapter
import com.pnj.marketplace_perikanan.users.RiwayatBelanjaLoad
import com.pnj.marketplace_perikanan.users.UsersMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.HashMap

class KeranjangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKeranjangBinding

    private lateinit var ikanRecyclerView: RecyclerView
    private lateinit var ikanArrayList: ArrayList<Ikan>
    private lateinit var ikanAdapter: IkanAdapter
    private lateinit var riwayatArrayList: ArrayList<RiwayatBelanjaLoad>
    private lateinit var riwayatAdapter: RiwayatBelanjaAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var keranjangAdapter: KeranjangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ikanRecyclerView = binding.makananListView
        ikanRecyclerView.layoutManager = LinearLayoutManager(this)
        ikanRecyclerView.setHasFixedSize(true)

        ikanArrayList = arrayListOf()
        riwayatArrayList = arrayListOf()
        ikanAdapter = IkanAdapter(ikanArrayList,"keranjang")
        riwayatAdapter = RiwayatBelanjaAdapter(riwayatArrayList,"keranjang")
        keranjangAdapter = KeranjangAdapter(riwayatArrayList, ikanArrayList,"hasil_belanja")

        ikanRecyclerView.adapter = keranjangAdapter

        load_data()
        swipeDelete()

        binding.BtnCheckout.setOnClickListener {
//            Log.e("array", riwayatArrayList[0].nama_ikan.toString())
            val email = firebaseAuth.currentUser!!.email.toString()
//            val updating =
//            updating.update("barusan", "0")
            val ids = ArrayList<String>()
            val stoks : MutableMap<String, Any> = HashMap()
            val nama_ikans = ArrayList<String>()
            CoroutineScope(Dispatchers.IO).launch {
                val personQuery = db.collection("riwayat_belanja")
                    .whereEqualTo("user_id", email)
                    .whereEqualTo("barusan", "1")
                    .get()
                    .await()
//                Log.e("query", personQuery.toString())
                var i = 0
                for (document in personQuery) {
                    ids.add(document.id)
                    if (nama_ikans.size == 0){
                        nama_ikans.add( 0 , document.getString("nama_ikan").toString())
                    }else{
                        if (nama_ikans.contains(document.getString("nama_ikan").toString())){
                            continue
                        } else{
                            nama_ikans.add( i , document.getString("nama_ikan").toString())

                        }
                    }
                    i++
//                    Log.e("stok", document.getString("stok_ikan").toString())
                }

                for (document in personQuery){
                    val hasKey = stoks.containsKey(document.getString("nama_ikan").toString())
                    if (hasKey){
                        for(nama in nama_ikans){
                            if (nama == document.getString("nama_ikan").toString()){
                                var x = stoks[document.getString("nama_ikan").toString()]
                                var y = document.getString("jumlah").toString()
                                stoks[document.getString("nama_ikan").toString()] = (x.toString().toInt()+y.toInt()).toString()
                            }
                        }

                    }
                    else {
                        stoks[document.getString("nama_ikan").toString()] = document.getString("jumlah").toString()
                    }
                }
//                Log.e("doc", stoks.toString() + nama_ikans.toString())
//                Log.e("doc", stoks.toString() + nama_ikans.toString())

//                Toast.makeText(this@BeliSuksesActivity, id, Toast.LENGTH_LONG).show()
                for (id in ids){
//                    if (db.collection("riwayat_belanja").document(id)!=null){
//                        Log.e("id", id)
//                    }
                    val updating = db.collection("riwayat_belanja").document(id)
                    updating.update("barusan", "0").addOnSuccessListener {
                        Toast.makeText(this@KeranjangActivity, "sukses update keranjang", Toast.LENGTH_LONG).show()

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
                val ids = ArrayList<String>()

                    for (nama in nama_ikans){
                        var stok = 0
                        val personQuery = db.collection("data_ikan").document(nama)
                        for (ikan in ikanArrayList){
                            if (ikan.nama_ikan.toString() == nama){
                                stok = ikan.stok_ikan.toString().toInt()
                                personQuery.update("stok_ikan", (stok - stoks[nama].toString().toInt()).toString())
                            }
                        }
                    }

            }
            val intent = Intent(this, UsersMainActivity::class.java)
            startActivity(intent)
        }



    }

    private fun load_data() {
        ikanArrayList.clear()
        riwayatArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("data_ikan").
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
                        ikanArrayList.add(dc.document.toObject(Ikan::class.java))
                }
                keranjangAdapter.notifyDataSetChanged()
            }
        })
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
                        riwayatArrayList.add(dc.document.toObject(RiwayatBelanjaLoad::class.java))
                }
                keranjangAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun deleteIkan(ikan: RiwayatBelanjaLoad, doc_id: String) {
        val email = firebaseAuth.currentUser!!.email.toString()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah ${ikan.nama_ikan} ingin dihapus ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                lifecycleScope.launch {
                    db.collection("riwayat_belanja")
                        .document(doc_id).delete()


                    deleteFoto("foto_ikan_riwayat/foto_ikan_riwayat_${ikan.nama_ikan}_${email}_${ikan.time}.jpg")

                    Toast.makeText(
                        applicationContext,
                        ikan.nama_ikan.toString() + "is deleted",
                        Toast.LENGTH_LONG
                    ).show()
                    load_data()

                }

            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
                load_data()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun swipeDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT)  {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                lifecycleScope.launch {
                    val ikan = riwayatArrayList[position]
                    val email = firebaseAuth.currentUser!!.email.toString()
                    val personQuery = db.collection("riwayat_belanja")
                        .whereEqualTo("barusan", "1")
                        .whereEqualTo("user_id", email)
                        .get()
                        .await()

                    if (personQuery.documents.isNotEmpty()) {
                        for (document in personQuery) {
                            try {
                                deleteIkan(ikan, document.id)
                                load_data()
                            }
                            catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        applicationContext,
                                        e.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Ikan yang ingin di hapus tidak ditemukan",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }).attachToRecyclerView(ikanRecyclerView)

    }

    private fun deleteFoto(file_name: String){
        val storage = Firebase.storage
        val storageRef = storage.reference
        val deleteFileRef = storageRef.child(file_name)

        if (deleteFileRef != null) {
            deleteFileRef.delete().addOnSuccessListener {
                Log.e("deleted", "success")
            }.addOnFailureListener{
                Log.e("deleted", "failed")
            }
        }

    }







}