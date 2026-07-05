# Aplikasi Pemesanan Tiket Transportasi (UAS)

Aplikasi ini adalah sistem pemesanan tiket perjalanan (Pesawat, Kapal, Kereta) yang dibangun secara penuh (Full Native) menggunakan **Android Jetpack Compose** dan **Kotlin**. Aplikasi ini juga terintegrasi langsung dengan ekosistem **Google Firebase** untuk memastikan data pengguna tersimpan secara permanen dan dijamin keamanannya menggunakan biometrik (*fingerprint*).

## ✨ Fitur Utama

- **Modern & Responsive UI**: Seluruh antarmuka dirancang dengan *Material 3 Jetpack Compose* yang memberikan kesan premium, modern (Indigo Theme), dan nyaman digunakan.
- **Firebase Authentication**: Proses Daftar (*Register*) dan Masuk (*Login*) yang aman menggunakan infrastruktur *backend* milik Firebase. Penggantian kata sandi dapat dilakukan langsung dari dalam aplikasi.
- **Firebase Realtime Database (RTDB)**: Data pribadi dan rekam jejak pesanan pengguna disimpan terpisah per-akun, sehingga riwayat pesanan (History) Anda tidak akan tercampur dengan pengguna lain.

## 🛡️ Keamanan yang Diterapkan

Aplikasi ini telah dirancang dengan memperhatikan privasi dan keamanan pengguna secara menyeluruh. Fitur keamanan yang sudah ada di dalam aplikasi meliputi:

1. **Autentikasi Biometrik (Fingerprint)**:
   - Apabila Anda sudah dalam status login dan keluar sementara dari aplikasi, aplikasi akan langsung menahan layar dengan *Biometric Prompt* saat dibuka kembali. Ini mencegah pencurian tiket (atau pembajakan sesi) dari orang yang memegang HP Anda.
2. **Kriptografi & Hashing oleh Firebase Auth**:
   - Seluruh aliran data kata sandi tidak pernah disimpan secara lokal. 
   - Firebase menggunakan standar algoritma Scrypt termodifikasi di sisi server yang sangat tangguh untuk mem-bypass upaya *brute-force* atau serangan *rainbow table*. Data sensitif terenkripsi sepenuhnya.
3. **Pencegahan Perekaman dan Tangkapan Layar (FLAG_SECURE)**:
   - Data diri dan daftar pesanan Anda adalah rahasia. Aplikasi ini disematkan `FLAG_SECURE` di level OS Android, sehingga fitur rekam layar pihak ketiga atau tombol *Screenshot* HP tidak akan bisa menangkap antarmuka aplikasi ini (layar akan dihitamkan atau diblokir).
4. **Validasi Input Klien yang Ketat (Input Sanitization)**:
   - **Pola Kata Sandi**: Pengguna dipaksa menggunakan standar *Strong Password* (minimum 8 karakter dengan kombinasi angka dan huruf). 
   - **Validasi Format**: Pengecekan otomatis format *Email* dan keabsahan panjang digit Nomor Telepon menggunakan standar *Regex*. Mencegah input injeksi sampah atau akun *bot*.
5. **Re-Autentikasi Kritis (Ubah Password)**:
   - Jika pengguna ingin mengubah *password* lama dengan yang baru di halaman Profil, aplikasi secara cerdas akan memaksa validasi ulang akun tersebut demi memastikan sang pengubah benar-benar pemilik sah akun tersebut.

## 📱 Cara Penggunaan Aplikasi

1. **Mendaftar Akun Baru**
   Saat pertama kali membuka aplikasi, ketuk tautan "Daftar Sekarang" di bawah tombol Masuk. Lengkapi data (Nama, Email, No. HP, Password min. 8 karakter gabungan huruf & angka). Jika pendaftaran berhasil, Anda siap untuk *Login*.

2. **Memasuki Aplikasi (Login & Fingerprint)**
   Setelah memiliki akun, masuk ke aplikasi. Sesi Anda akan otomatis disimpan. 
   Jika Anda keluar dari aplikasi lalu masuk kembali beberapa saat kemudian, Anda tidak perlu mengetik password ulang, melainkan Anda cukup meletakkan jari di pemindai *Fingerprint*!

3. **Membuat Pesanan Tiket**
   Di halaman utama (Dashboard), Anda dapat:
   - Mengisikan Nama Penumpang.
   - Memilih jenis Transportasi (Pesawat / Kapal / Kereta).
   - Memasukkan asal dan tujuan (tombol tukar tersedia di tengah).
   - Menentukan jadwal keberangkatan, kelas (Ekonomi/Eksekutif), dan jumlah penumpang.
   Sistem akan secara langsung memperbarui *Total Estimasi Harga* secara langsung (*Real-time*). Ketuk **Pesan Sekarang**.

4. **Melihat Riwayat**
   Setiap tiket yang berhasil dipesan akan muncul di *tab* **Riwayat**. Tiket diurutkan dan dikaitkan secara eksklusif hanya pada akun Anda.

5. **Manajemen Profil & Ganti Password**
   Dari menu *tab* **Profil**, Anda bisa melihat informasi diri Anda, mengubahnya (Nama & No HP), hingga mengganti kata sandi dengan verifikasi *re-authenticate* demi keamanan tambahan.

## 🛠 Instalasi dan Konfigurasi untuk Developer

Untuk menjalankan aplikasi ini dari kode sumber (Source Code):

1. **Clone & Buka**
   Buka *project* ini di perangkat dengan *Android Studio (Koala/Ladybug)* atau versi di atasnya.
2. **Koneksi Firebase**
   - Aplikasi ini membutuhkan file `google-services.json` yang harus Anda masukkan di direktori `app/`.
   - Pastikan Firebase Project Anda telah mengaktifkan **Authentication (Email/Password)** dan **Realtime Database**.
   - Setel Rules Realtime Database agar bisa dibaca/ditulis, atau gunakan Test Mode `(".read": "now < [tanggal]", ".write": "now < [tanggal]")`.
3. **Build & Run**
   Hubungkan emulator atau perangkat Android asli (pastikan Anda sudah menyetel sidik jari/PIN/Pola di HP Anda untuk mencoba fitur Biometric). Klik Run (`Shift+F10`).

---
Dibuat menggunakan Kotlin & Jetpack Compose.
