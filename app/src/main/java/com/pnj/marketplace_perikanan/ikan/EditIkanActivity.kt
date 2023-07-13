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

        val (year, month, day, curr_ikan) = setDefaultValue()

        binding.TxtEditTglLahir.setOnClickListener {
            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    binding.TxtEditTglLahir.setText(
                        "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                }, year.toString().toInt(), month.toString().toInt(), day.toString().toInt()
            )
            dpd.show()
        }

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
        val nik = intent.getStringExtra("nik").toString()
        val nama = intent.getStringExtra("nama").toString()
        val tgl_lahir = intent.getStringExtra("tgl_lahir").toString()
        val jenis_kelamin = intent.getStringExtra("jenis_kelamin").toString()
        val penyakit_bawaan = intent.getStringExtra("penyakit_bawaan").toString()

        binding.TxtEditNIK.setText(nik)
        binding.TxtEditNama.setText(nama)
        binding.TxtEditTglLahir.setText(tgl_lahir)

        val tgl_split = intent.getStringExtra("tgl_lahir")
            .toString().split("-").toTypedArray()
        val year = tgl_split[0].toInt()
        val month = tgl_split[1].toInt() - 1
        val day = tgl_split[2].toInt()

        if (jenis_kelamin == "Laki - Laki") {
            binding.RdnEditJKL.isChecked = true
        }
        else if (jenis_kelamin == "Perempuan") {
            binding.RdnEditJKP.isChecked = true
        }

        val penyakit = penyakit_bawaan.split("|").toTypedArray()
        for (p in penyakit) {
            if (p == "diabetes") {
                binding.ChkEditDiabetes.isChecked = true
            }
            else if (p == "jantung") {
                binding.ChkEditJantung.isChecked = true
            }
            else if (p == "asma") {
                binding.ChkEditAsma.isChecked = true
            }
        }

        val curr_ikan = Ikan(nik, nama, tgl_lahir, jenis_kelamin, penyakit_bawaan)
        return arrayOf(year, month, day, curr_ikan)

    }

    fun newIkan(): Map<String, Any> {
        var nik : String = binding.TxtEditNIK.text.toString()
        var nama : String = binding.TxtEditNama.text.toString()
        var tgl_lahir : String = binding.TxtEditTglLahir.text.toString()

        var jk : String = ""
        if (binding.RdnEditJKL.isChecked) {
            jk = "Laki - Laki"
        }
        else if (binding.RdnEditJKP.isChecked) {
            jk ="Perempuan"
        }

        var penyakit = ArrayList<String>()
        if  (binding.ChkEditDiabetes.isChecked) {
            penyakit.add("diabetes")
        }
        if (binding.ChkEditJantung.isChecked) {
            penyakit.add("jantung")
        }
        if (binding.ChkEditAsma.isChecked) {
            penyakit.add("asma")
        }

        if (dataGambar != null) {
            uploadPictFirebase(dataGambar!!, "${nik}_${nama}")

        }

        val penyakit_string = penyakit.joinToString("|")

        val ikan = mutableMapOf<String, Any>()
        ikan["nik"] = nik
        ikan["nama"] = nama
        ikan["tgl_lahir"] = tgl_lahir
        ikan["jenis_kelamin"] = jk
        ikan["penyakit_bawaan"] = penyakit_string

        return ikan

    }

    private fun updateIkan(ikan: Ikan, newIkanMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = db.collection("ikan")
                .whereEqualTo("nik", ikan.nik)
                .whereEqualTo("nama", ikan.nama)
                .whereEqualTo("jenis_kelamin", ikan.jenis_kelamin)
                .whereEqualTo("tgl_lahir",ikan.tgl_lahir)
                .whereEqualTo("penyakit_bawaan", ikan.penyakit_bawaan)
                .get()
                .await()

            if (personQuery.documents.isNotEmpty()) {
                for (document in personQuery) {
                    try {
                        db.collection("ikan").document(document.id).set(
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
        val nik = intent.getStringExtra("nik").toString()
        val nama =intent.getStringExtra("nama").toString()

        val storageRef = FirebaseStorage.getInstance().reference.child("img_ikan/${nik}_${nama}.jpg")
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
        val ref = FirebaseStorage.getInstance().reference.child("img_ikan/${file_name}.jpg")
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
