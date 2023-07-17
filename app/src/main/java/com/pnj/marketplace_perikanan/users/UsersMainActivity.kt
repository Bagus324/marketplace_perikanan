package com.pnj.marketplace_perikanan.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pnj.marketplace_perikanan.auth.SettingsUserActivity
import com.pnj.marketplace_perikanan.chat.ChatActivity
import com.pnj.marketplace_perikanan.databinding.UserActivityMainBinding
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.ikan.IkanAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.pnj.marketplace_perikanan.R

class UsersMainActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityMainBinding

    private lateinit var ikanRecyclerView: RecyclerView
    private val ikanArrayList: ArrayList<Ikan> = arrayListOf()
    private val filterIkanArrayList: ArrayList<Ikan> = arrayListOf()
    private lateinit var ikanAdapter: IkanAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ikanRecyclerView = binding.makananListView
        ikanRecyclerView.layoutManager = LinearLayoutManager(this)
        ikanRecyclerView.setHasFixedSize(true)

        load_data()

        ikanAdapter = IkanAdapter(filterIkanArrayList,"main")

        ikanRecyclerView.adapter = ikanAdapter


        swipeDelete()


        // TextChangedListener
        binding.txtSearchIkan.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = p0.toString()

                if (keyword.isNotEmpty()) {
                    search_data(keyword)
                }
                else {
                    load_data()

                }

            }


            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_bottom_home -> {
                    val intent = Intent(this, UsersMainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_bottom_setting -> {
                    val intent = Intent(this, SettingsUserActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_bottom_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }



    }

    private fun load_data() {
        ikanArrayList.clear()
        filterIkanArrayList.clear()
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
                    if (dc.type == DocumentChange.Type.ADDED){
                        ikanArrayList.add(dc.document.toObject(Ikan::class.java))
                        filterIkanArrayList.add(dc.document.toObject(Ikan::class.java))
                    }

                }
                ikanAdapter.notifyDataSetChanged()
            }
        })
    }


    private fun search_data(keyword : String) {
//        ikanArrayList.clear()

        db = FirebaseFirestore.getInstance()
        filterIkanArrayList.clear()

        if (keyword != "") {
            filterIkanArrayList.addAll(ikanArrayList.filter{ x -> x.nama_ikan!!.contains(keyword)
            } as ArrayList<Ikan>)
//            ikanArrayList = filterIkanArrayList
        } else {
            filterIkanArrayList.addAll(ikanArrayList)
        }
        Log.e("filter",filterIkanArrayList.toString())
        ikanAdapter.notifyDataSetChanged()


//        val query = db.collection("data_ikan")
//            .orderBy("nama_ikan")
//            .startAt("lele")
//            .get()

//        val query = db.collection("data_ikan")
//            .whereEqualTo("nama_ikan", keyword)
//            .get()
//
//        query.addOnSuccessListener {
//            ikanArrayList.clear()
//            for (document in it) {
//                ikanArrayList.add(document.toObject(Ikan::class.java))
//            }
//            ikanAdapter.notifyDataSetChanged()
//            Log.e("array", ikanArrayList.toString()+"query"+query.toString())
//        }.addOnFailureListener{
//            Log.e("array", ikanArrayList.toString()+"query"+query.toString())
//        }

    }

    private fun deleteIkan(ikan: Ikan, doc_id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah ${ikan.nama_ikan} ingin dihapus ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                lifecycleScope.launch {
                    db.collection("data_ikan")
                        .document(doc_id).delete()


                    deleteFoto("foto_ikan/foto_${ikan.nama_ikan}.jpg")

                    Toast.makeText(
                        applicationContext,
                        ikan.nama_ikan.toString() + " is deleted",
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
                    val ikan = ikanArrayList[position]
                    val personQuery = db.collection("data_ikan")
                        .whereEqualTo("nama_ikan", ikan.nama_ikan)
                        .whereEqualTo("deskripsi_ikan", ikan.deskripsi_ikan)
                        .whereEqualTo("stok_ikan", ikan.stok_ikan)
                        .whereEqualTo("harga_ikan", ikan.harga_ikan)
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