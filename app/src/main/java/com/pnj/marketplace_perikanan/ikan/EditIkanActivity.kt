package com.pnj.marketplace_perikanan.ikan

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pnj.marketplace_perikanan.MainActivity
import com.pnj.marketplace_perikanan.databinding.ActivityEditIkanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class EditIkanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditIkanBinding
    private val db = FirebaseFirestore.getInstance()

    private var dataGambar: Bitmap? = null
    private val REQ_CAM = 101
    private lateinit var imgUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditIkanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (curr_ikan) = setDefaultValue()


        binding.BtnEditIkan.setOnClickListener {
            val new_data_ikan = newIkan()
            updateIkan(curr_ikan as Ikan, new_data_ikan)

            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }

        binding.BtnImgIkan.setOnClickListener {
            openCamera()
        }

        showFoto()
    }

    fun setDefaultValue(): Array<Any> {
        val intent = intent
        val nama_ikan = intent.getStringExtra("nama_ikan").toString()
        val stok_ikan = intent.getStringExtra("stok_ikan").toString()
        val deskripsi_ikan = intent.getStringExtra("deskripsi_ikan").toString()
        val harga_ikan = intent.getStringExtra("harga_ikan").toString()

        binding.TxtEditDeskripsi.setText(deskripsi_ikan)
        binding.TxtEditNama.setText(nama_ikan)
        binding.TxtEditStok.setText(stok_ikan)
        binding.TxtEditHarga.setText(harga_ikan)



        val curr_ikan = Ikan(nama_ikan, deskripsi_ikan, stok_ikan, harga_ikan)
        return arrayOf(curr_ikan)

    }

    fun newIkan(): Map<String, Any> {
        var deskripsi_ikan : String = binding.TxtEditDeskripsi.text.toString()
        var nama_ikan : String = binding.TxtEditNama.text.toString()
        var stok_ikan : String = binding.TxtEditStok.text.toString()
        var harga_ikan : String = binding.TxtEditHarga.text.toString()


        if (dataGambar != null) {
            uploadPictFirebase(dataGambar!!, "foto_${nama_ikan}")

        }

        val ikan = mutableMapOf<String, Any>()
        ikan["nama_ikan"] = nama_ikan
        ikan["deskripsi_ikan"] = deskripsi_ikan
        ikan["stok_ikan"] = stok_ikan
        ikan["harga_ikan"] = harga_ikan

        return ikan

    }

    private fun updateIkan(ikan: Ikan, newIkanMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
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
                        db.collection("data_ikan").document(document.id).set(
                            newIkanMap,
                            SetOptions.merge()
                        )
                    }
                    // Exception kotlin
                    catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditIkanActivity,
                                e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditIkanActivity,
                        "No fishes matched the query.", Toast.LENGTH_LONG).show()
                }
            }
        }

    fun showFoto() {
        val intent = intent
        val nama_ikan =intent.getStringExtra("nama_ikan").toString()

        val storageRef = FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${nama_ikan}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.BtnImgIkan.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            binding.BtnImgIkan.setImageBitmap(dataGambar)
        }
    }

    private fun uploadPictFirebase(img_bitmap: Bitmap, file_name: String) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("foto_ikan/${file_name}.jpg")
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val img = baos.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result.let { Uri ->
                            imgUri = Uri
                            binding.BtnImgIkan.setImageBitmap(img_bitmap)
                        }
                    }
                }
            }
    }



}
