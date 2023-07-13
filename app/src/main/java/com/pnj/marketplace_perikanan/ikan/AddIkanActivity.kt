package com.pnj.marketplace_perikanan.ikan

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.marketplace_perikanan.MainActivity
import com.pnj.marketplace_perikanan.databinding.ActivityAddIkanBinding
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddIkanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIkanBinding
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    private val REQ_CAM = 101
    private lateinit var imgUri: Uri
    private var dataGambar: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddIkanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TxtAddTglLahir.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    binding.TxtAddTglLahir.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)
                }, year, month, day)

            dpd.show()

            binding.BtnAddIkan.setOnClickListener {
                addIkan()
            }
        }

        binding.BtnImgIkan.setOnClickListener {
            openCamera()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            binding.BtnImgIkan.setImageBitmap(dataGambar)
        }
    }

    private fun addIkan() {
        var nik: String = binding.TxtAddNIK.text.toString()
        var nama: String = binding.TxtAddNama.text.toString()
        var tgl_lahir: String = binding.TxtAddTglLahir.text.toString()

        var jk: String = ""
        if(binding.RdnEditJKL.isChecked) {
            jk = "Laki - Laki"
        }
        else if (binding.RdnEditJKP.isChecked) {
            jk = "Perempuan"
        }

        var penyakit = ArrayList<String>()
        if (binding.ChkDiabetes.isChecked) {
            penyakit.add("diabetes")
        }
        if (binding.ChkJantung.isChecked) {
            penyakit.add("jantung")
        }
        if (binding.ChkAsma.isChecked) {
            penyakit.add("asma")
        }

        val penyakit_string = penyakit.joinToString("|")

        val ikan: MutableMap<String, Any> = HashMap()
        ikan["nik"] = nik
        ikan["nama"] = nama
        ikan["tgl_lahir"] = tgl_lahir
        ikan["jenis_kelamin"] = jk
        ikan["penyakit_bawaan"] = penyakit_string

        if (dataGambar != null) {
            uploadPictFirebase(dataGambar!!, "${nik}_${nama}")

            firestoreDatabase.collection("ikan").add(ikan)
                .addOnSuccessListener {
                    val intentMain = Intent(this, MainActivity::class.java)
                    startActivity(intentMain)
                }
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
