package com.pnj.marketplace_perikanan.users

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
import com.pnj.marketplace_perikanan.ikan.Ikan
import com.pnj.marketplace_perikanan.users.DetailIkanActivity
import com.pnj.marketplace_perikanan.users.RiwayatBelanja
import com.pnj.marketplace_perikanan.users.UsersMainActivity
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class RiwayatBelanjaAdapter(private val ikanList: ArrayList<RiwayatBelanjaLoad>, private val page: String) :
    RecyclerView.Adapter<RiwayatBelanjaAdapter.IkanViewHolder>() {

    private lateinit var activity: AppCompatActivity
    private lateinit var firebaseAuth: FirebaseAuth
    class IkanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.TVLNama)
        val harga: TextView = itemView.findViewById(R.id.TVLHarga)
        val img_ikan: ImageView = itemView.findViewById(R.id.IMLGambarIkan)
        val textStok: TextView = itemView.findViewById(R.id.TVLStok)
        val jumlah: TextView = itemView.findViewById(R.id.TVJumlah)
        val total: TextView = itemView.findViewById(R.id.TVTotal)
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
        if(page == "hasil_belanja"){
            val ikan: RiwayatBelanjaLoad = ikanList.reversed()[position]
            holder.nama.text = ikan.nama_ikan.toString().uppercase()
            holder.harga.text = format_duit(ikan.harga_ikan.toString().toInt())
            holder.jumlah.text = "Pembelian " + ikan.jumlah.toString() + " Ekor"
            holder.total.text = format_duit(ikan.total.toString().toInt())
            holder.textStok.text = ikan.time


            val storageRef = FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${ikan.nama_ikan}.jpg")
            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                holder.img_ikan.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Log.e("foto ?", "gagal" )
            }
        }else if (page == "keranjang"){
            val ikan: RiwayatBelanjaLoad = ikanList[position]
            val nama = ikan.nama_ikan
            val jumlah = ikan.jumlah
            val harga = ikan.harga_ikan
            val total = ikan.total
        }

    }

    private fun format_duit(number: Int): String{
        val localeId = Locale("IND", "ID")
        val numFormat = NumberFormat.getCurrencyInstance(localeId)
        val formatRupiah = numFormat.format(number).toString()
        return formatRupiah
    }
}