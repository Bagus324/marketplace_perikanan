package com.pnj.marketplace_perikanan.ikan

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.pnj.marketplace_perikanan.R
import java.io.File

class IkanAdapter(private val ikanList: ArrayList<Ikan>) :
    RecyclerView.Adapter<IkanAdapter.IkanViewHolder>() {

    private lateinit var activity: AppCompatActivity

    class IkanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nik: TextView = itemView.findViewById(R.id.TVLNik)
        val nama: TextView = itemView.findViewById(R.id.TVLNama)
        val jenis_kelamin: TextView = itemView.findViewById(R.id.TVLJenisKelamin)
        val img_ikan: ImageView = itemView.findViewById(R.id.IMLGambarIkan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IkanViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ikan_list_layout, parent, false)
        return IkanViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ikanList.size
    }

    override fun onBindViewHolder(holder: IkanViewHolder, position: Int) {
        val ikan: Ikan = ikanList[position]
        holder.nik.text = ikan.nik
        holder.nama.text = ikan.nama
        holder.jenis_kelamin.text = ikan.jenis_kelamin

        holder.itemView.setOnClickListener {
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, EditIkanActivity::class.java).apply {
                putExtra("nik", ikan.nik.toString())
                putExtra("nama", ikan.nama.toString())
                putExtra("tgl_lahir", ikan.tgl_lahir.toString())
                putExtra("jenis_kelamin", ikan.jenis_kelamin.toString())
                putExtra("penyakit_bawaan", ikan.penyakit_bawaan.toString())
            })
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("img_ikan/${ikan.nik}_${ikan.nama}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img_ikan.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal" )
        }
    }


}