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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.pnj.marketplace_perikanan.R
import com.pnj.marketplace_perikanan.users.DetailIkanActivity
import com.pnj.marketplace_perikanan.users.RiwayatBelanja
import com.pnj.marketplace_perikanan.users.UsersMainActivity
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class IkanAdapter(private val ikanList: ArrayList<Ikan>, private val page: String) :
    RecyclerView.Adapter<IkanAdapter.IkanViewHolder>() {

    private lateinit var activity: AppCompatActivity
    private lateinit var firebaseAuth: FirebaseAuth
    class IkanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.TVLNama)
        val stok: TextView = itemView.findViewById(R.id.TVLStok)
        val harga: TextView = itemView.findViewById(R.id.TVLHarga)
        val img_ikan: ImageView = itemView.findViewById(R.id.IMLGambarIkan)
        val textJumlah: TextView = itemView.findViewById(R.id.TVJumlah)
        val textTotal: TextView = itemView.findViewById(R.id.TVTotal)
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
        if (page == "main") {
            holder.textJumlah.visibility = View.GONE
            holder.textTotal.visibility = View.GONE
            val ikan: Ikan = ikanList[position]
            holder.nama.text = ikan.nama_ikan.toString().uppercase()
            holder.stok.text = ikan.stok_ikan + " Ekor"
            holder.harga.text = format_duit(ikan.harga_ikan.toString().toInt())

            firebaseAuth = FirebaseAuth.getInstance()
            holder.itemView.setOnClickListener {
                activity = it.context as AppCompatActivity
                var emailCurr = firebaseAuth.currentUser!!.email.toString()
                var emailListed = emailCurr.toList()
                var idx = 0
                for (x in emailListed) {
//                        Log.e("index @ ", x.toString())
                    if (x.toString() == "@") {
                        break
                    }
                    idx += 1
                }
                var domainEmail = emailCurr.substring(idx)
                Log.e("domain", domainEmail)
                if (domainEmail == "@admin.com") {
                    activity.startActivity(Intent(activity, EditIkanActivity::class.java).apply {
                        putExtra("nama_ikan", ikan.nama_ikan.toString())
                        putExtra("deskripsi_ikan", ikan.deskripsi_ikan.toString())
                        putExtra("stok_ikan", ikan.stok_ikan.toString())
                        putExtra("harga_ikan", ikan.harga_ikan.toString())
                    })
                } else {
                    activity.startActivity(Intent(activity, DetailIkanActivity::class.java).apply {
                        putExtra("nama_ikan", ikan.nama_ikan.toString())
                        putExtra("deskripsi_ikan", ikan.deskripsi_ikan.toString())
                        putExtra("stok_ikan", ikan.stok_ikan.toString())
                        putExtra("harga_ikan", ikan.harga_ikan.toString())
                    })
                }

            }

            val storageRef =
                FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${ikan.nama_ikan}.jpg")
            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                holder.img_ikan.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Log.e("foto ?", "gagal")
            }
        } else if (page == "keranjang") {
            val ikan: Ikan = ikanList[position]
            val nama = ikan.nama_ikan
            val stok = ikan.stok_ikan
            val harga = ikan.harga_ikan
            val deskripsi = ikan.deskripsi_ikan
        } else {
            val ikan: Ikan = ikanList[position]
            holder.nama.text = ikan.nama_ikan.toString().uppercase()
            holder.stok.text = ikan.stok_ikan + " Ekor"
            holder.harga.text = format_duit(ikan.harga_ikan.toString().toInt())

            firebaseAuth = FirebaseAuth.getInstance()
            holder.itemView.setOnClickListener {
                activity = it.context as AppCompatActivity
                var emailCurr = firebaseAuth.currentUser!!.email.toString()
                var emailListed = emailCurr.toList()
                var idx = 0
                for (x in emailListed) {
//                        Log.e("index @ ", x.toString())
                    if (x.toString() == "@") {
                        break
                    }
                    idx += 1
                }
                var domainEmail = emailCurr.substring(idx)
                Log.e("domain", domainEmail)
                if (domainEmail == "@admin.com") {
                    activity.startActivity(Intent(activity, EditIkanActivity::class.java).apply {
                        putExtra("nama_ikan", ikan.nama_ikan.toString())
                        putExtra("deskripsi_ikan", ikan.deskripsi_ikan.toString())
                        putExtra("stok_ikan", ikan.stok_ikan.toString())
                        putExtra("harga_ikan", ikan.harga_ikan.toString())
                    })
                } else {
                    activity.startActivity(Intent(activity, DetailIkanActivity::class.java).apply {
                        putExtra("nama_ikan", ikan.nama_ikan.toString())
                        putExtra("deskripsi_ikan", ikan.deskripsi_ikan.toString())
                        putExtra("stok_ikan", ikan.stok_ikan.toString())
                        putExtra("harga_ikan", ikan.harga_ikan.toString())
                    })
                }

            }

            val storageRef =
                FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${ikan.nama_ikan}.jpg")
            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                holder.img_ikan.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Log.e("foto ?", "gagal")
            }
        }
    }

    private fun format_duit(number: Int): String{
        val localeId = Locale("IND", "ID")
        val numFormat = NumberFormat.getCurrencyInstance(localeId)
        val formatRupiah = numFormat.format(number).toString()
        return formatRupiah
    }
}