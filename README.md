# Sakana Mawada Warohma | Fish Market
<div align="center">
  <img align="center" src="https://github.com/Bagus324/marketplace_perikanan/blob/master/app/src/main/res/drawable/logo.png"></img>
  </div>

# Tentang Aplikasi

Aplikasi ini merupakan sebuah prototype untuk Penilaian Akhir Semester mata kuliah Pemrograman Bergerak
Tujuan dibuatnya Aplikasi ini adalah untuk memudahkan para penjual ikan dalam melaksanakan proses dagang yang ada

# Anggota Tim dan Pembagian Tugas


| Name                                    | Student-ID  |  Contacts                                                                                                                  |
| :-------------------------------------- | :---------- |  :------------------------------------------------------------------------------------------------------------------------ |
| Akmal Maulana                           | 2007411050  |  [GitHub](https://github.com/akmalm007)                                                                       |
| Bagus Tri Yulianto Darmawan             | 2007411056  |  [GitHub](https://github.com/Bagus324)                                            |
| Muhammad Ilham Fachlevi                  | 2007411048  |  [GitHub](https://github.com/ilhamfachlevi)                                                            |


# Tools

Aplikasi ini dibuat dengan menggunakan Tools [IDE Android Studio](https://developer.android.com/studio) dan menggunakan [Google Cloud Storage (Google Cloud Firestore Platform)](https://firebase.google.com/)
<br><br>
<div align="center">
  <img align="left" src="https://github.com/Bagus324/marketplace_perikanan/blob/master/readme%20assets/android-studio-icon-486x512-zp9um7zl.png" height="180" width="180"></img>
  <img align="right" src="https://github.com/Bagus324/marketplace_perikanan/blob/master/readme%20assets/firestore-logo.png" height="180" width="180"></img>

  </div>
<br><br><br><br><br><br><br><br>

Aplikasi ini juga dibuat menggunakan Bahasa Pemrograman [Kotlin](https://kotlinlang.org/)
<div align="center">
  <img align="center" src="https://github.com/Bagus324/marketplace_perikanan/blob/master/readme%20assets/kotlin_avatar_removebg.png" height="180" width="180"></img>

  </div>

# Fitur

Berikut adalah daftar fitur-fitur yang terdapat pada prototype ini

## Pembeli
- [x] Login
- [x] Register
- [x] Search ikan-ikan yang dijual berdasarkan nama ikan pada Home Page
- [x] Keranjang (tambah ke keranjang, hapus dari keranjang, beli ikan dari keranjang)
- [x] Checkout All (Membeli semua ikan-ikan yang sudah dimasukkan ke keranjang)
- [x] Riwayat perbelanjaan (Diurutkan berdasarkan waktu pembelian)
- [x] Membeli ikan satu persatu (Ikan yang dibeli dipilih dari daftar ikan yang terdapat pada Home Page)
- [x] Mengganti Password
- [x] Chat

      
## Penjual
- [x] Login
- [x] Search ikan-ikan yang dijual berdasarkan nama ikan (sebagai pembeli dan penjual)
- [x] Menambah jenis ikan yang dijual (Penjual meng-input data ikan yang dibutuhkan)*
- [x] Mengganti data ikan (Penjual meng-input data baru untuk ikan yang sudah ada)*
- [x] Menghapus data ikan (Penjual menghapus data ikan yang sudah tidak dijual lagi)*
- [x] Mengganti Password

## Fitur lain
- [x] Sistem pencegahan untuk user (pembeli) memiliki akun admin (penjual)**

## Fitur yang belum terealisasi
- [ ] Pembeli mengganti jumlah ikan yang ingin dibeli pada keranjangnya

# Apk File Download

APK prototype ini dapat di unduh pada link 
<b>[disini](https://s.pnj.ac.id/sakana_mawada_warohma)</b> atau di Release pada Repository ini

# Note
<div align="left">
  <p>* : Data ikan terdiri dari<p>&nbsp;&nbsp;• Nama Ikan</p><p>&nbsp;&nbsp;• Harga Ikan</p><p>&nbsp;&nbsp;• Deskripsi Tentang Ikan</p><p>&nbsp;&nbsp;• Stok Ikan</p></p>
  <p>** : Fitur login pada aplikasi ini menggunakan email*password, admin dan user memiliki domain email yang berbeda, dan jika user mencoba melakukan register menggunakan domail email admin, maka akan dicegah oleh sistem. Maka dari itu juga pendaftaran admin harus dilakukan manual melalui databse oleh orang dengan otoritas lebih tinggi (Super User)</p>
  </div>
<br><br><br><br><br><br>
<div align="right">
  <small>18 Juli 2023 - 03:25 README.md initialized by Bagus Tri Yulianto Darmawan</small>  
</div>
