package com.pnj.marketplace_perikanan.keranjang

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.pnj.marketplace_perikanan.R
import com.pnj.marketplace_perikanan.ikan.EditIkanActivity
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.users.*
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class KeranjangAdapter(private val riwayatList: ArrayList<RiwayatBelanjaLoad>, private val ikanList: ArrayList<Ikan>, private val page: String) :
    RecyclerView.Adapter<KeranjangAdapter.IkanViewHolder>() {

    private lateinit var activity: AppCompatActivity
    private lateinit var firebaseAuth: FirebaseAuth



    class IkanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.TVLNama)
        val harga: TextView = itemView.findViewById(R.id.TVLHarga)
        val img_ikan: ImageView = itemView.findViewById(R.id.IMLGambarIkan)
        val textStok: TextView = itemView.findViewById(R.id.TVLStok)
        val jumlah: TextView = itemView.findViewById(R.id.TVJumlah)
        val total: TextView = itemView.findViewById(R.id.TVTotal)
        val time: TextView = itemView.findViewById(R.id.TVLStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IkanViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ikan_list_layout, parent, false)
        return IkanViewHolder(itemView)

    }

    override fun getItemCount(): Int {

        return riwayatList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: IkanViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()
        if(page == "hasil_belanja"){
            holder.textStok.visibility = View.GONE
        }

        var stok: String? = null
        var desk: String? = null
        var ikan2 = ikanList
        val ikan: RiwayatBelanjaLoad = riwayatList[position]
        for (ikanikan in ikan2){
            if (ikanikan.nama_ikan==ikan.nama_ikan){
                stok = ikanikan.stok_ikan
                desk = ikanikan.deskripsi_ikan

               break
            }
        }
        holder.nama.text = ikan.nama_ikan.toString().uppercase()
        holder.harga.text = format_duit(ikan.harga_ikan.toString().toInt())
        holder.jumlah.text = "Pembelian " + ikan.jumlah.toString() + " Ekor"
        holder.total.text = format_duit(ikan.total.toString().toInt())
        holder.time.text = ikan.time
        var emailCurr = firebaseAuth.currentUser!!.email.toString()
        firebaseAuth = FirebaseAuth.getInstance()
        holder.itemView.setOnClickListener {
            activity = it.context as AppCompatActivity
                activity.startActivity(Intent(activity, DetailIkanKeranjangActivity::class.java).apply {
                    putExtra("nama_ikan", ikan.nama_ikan.toString())
                    putExtra("deskripsi_ikan", desk.toString())
                    putExtra("stok_ikan", stok.toString())
                    putExtra("harga_ikan", ikan.harga_ikan.toString())
                    putExtra("jumlah", ikan.jumlah.toString())
                    putExtra("total", ikan.total.toString())
                    putExtra("time", ikan.time.toString())

                })


        }

        val storageRef = FirebaseStorage.getInstance().reference.child("foto_ikan_riwayat/foto_ikan_riwayat_${ikan.nama_ikan}_${firebaseAuth.currentUser!!.email.toString()}_${ikan.time}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img_ikan.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal" )
        }
    }
    private fun format_duit(number: Int): String{
        val localeId = Locale("IND", "ID")
        val numFormat = NumberFormat.getCurrencyInstance(localeId)
        val formatRupiah = numFormat.format(number).toString()
        return formatRupiah
    }

}