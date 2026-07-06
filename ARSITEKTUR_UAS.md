# Penjelasan Arsitektur Aplikasi (Persiapan UAS)

Aplikasi ini dibangun menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan Jetpack Compose. Arsitektur ini dipilih karena standar industri Android modern yang memisahkan antara *tampilan UI*, *logika aplikasi*, dan *sumber data*. Pemisahan ini membuat kode lebih mudah dibaca, dikelola, dan dikembangkan.

## 1. Konsep Arsitektur MVVM

Aplikasi ini dibagi menjadi tiga lapisan utama:
- **View (Tampilan/UI)**: Bertugas menampilkan data kepada pengguna dan merespons interaksi (klik tombol, input teks). *View tidak memiliki logika bisnis atau pemrosesan data secara langsung.*
- **ViewModel (Logika Bisnis)**: Bertugas sebagai jembatan antara View dan Model. Mengambil data dari Repository, memprosesnya, dan menyediakannya dalam bentuk yang mudah dikonsumsi UI (menggunakan `StateFlow`).
- **Model & Repository (Manajemen Data)**: Bertugas berinteraksi dengan sumber data yang sesungguhnya (dalam hal ini adalah Firebase Auth dan Firebase Realtime Database).

---

## 2. Struktur Folder & File

Semua kode utama Kotlin berada di direktori: `app/src/main/java/com/example/uas/`

### 🎨 A. Tampilan (View / UI)
Semua tampilan dibangun dengan **Jetpack Compose** (Deklaratif UI). File-file ini berada di folder `screens/`.

* **`LoginScreen.kt`**: Layar awal bagi pengguna. Menerima input email dan password. 
* **`RegisterScreen.kt`**: Layar untuk pendaftaran akun baru. Memiliki validasi input seperti pengecekan format email dan pencocokan password.
* **`BookingScreen.kt`**: Layar utama tempat pengguna melakukan pemesanan (pesawat, kapal, kereta). Data yang di-input akan dikirim ke ViewModel.
* **`HistoryScreen.kt`**: Layar yang bertugas membaca arus data pemesanan secara *real-time* (mengobservasi `StateFlow` dari ViewModel).
* **`ProfileScreen.kt`**: Layar untuk mengubah data diri dan kata sandi pengguna yang sedang login.

### 🧠 B. Logika Aplikasi & State Management (ViewModel)
Berada di folder `viewmodel/`. Bertindak sebagai "otak" sementara untuk layar yang sedang aktif.

* **`AuthViewModel.kt`**: Mengatur sesi login. File ini memantau siapa yang sedang login (`currentUser`) dan menyediakan fungsi seperti `login()`, `register()`, `logout()`, dan `changePassword()`. Jika ada layar yang butuh mengetahui siapa yang login, mereka cukup memantau variabel dari file ini.
* **`BookingViewModel.kt`**: Bertugas mengambil dan menyimpan daftar tiket. File ini menarik riwayat tiket dari *Repository*, lalu mengonversinya menjadi *StateFlow* agar `HistoryScreen` dan `BookingScreen` bisa langsung merespons jika ada data tiket baru yang masuk secara reaktif tanpa perlu me-*refresh* halaman.

### 🗄 C. Pengelolaan Data (Repository & Model)
Berada di folder `repository/` dan `data/`. Bertugas "berbicara" langsung dengan server/database.

* **`AuthRepository.kt`**: Menjalankan fungsi otentikasi Firebase langsung. Di sinilah eksekusi murni ke Firebase terjadi (misal memanggil `FirebaseAuth.getInstance().signInWithEmailAndPassword()`).
* **`BookingRepository.kt`**: Berinteraksi dengan Firebase Realtime Database. Menulis tiket baru dengan metode `.push().setValue()` dan menggunakan `ValueEventListener` untuk membaca data tiket secara *real-time*.
* **`AppData.kt` (Folder data/)**: Murni berisi `Data Class` / cetakan data. Contohnya `User` (menyimpan detail nama, email, no HP) dan `BookingData` (menyimpan rute, jenis kelas, dsb).

### 🛣 D. Navigasi
* **`navigation/NavGraph.kt`**: Mengatur perpindahan antar layar. File ini adalah "peta" dari aplikasi. Di sini juga tempat di mana kita **menyuntikkan (inject)** `ViewModel` ke berbagai `Screen`. Misalnya, memberikan `AuthViewModel` yang sama kepada `LoginScreen` dan `ProfileScreen`.

---

## 3. Fitur Keamanan (Security)

Keamanan aplikasi menjadi nilai jual utama dan poin penting saat sidang/presentasi UAS. Semua celah telah ditutup melalui beberapa lapis:

1. **Autentikasi & Database Terenkripsi (Firebase)**
   * Lokasi: `AuthRepository.kt`
   * Penjelasan: Menggunakan Firebase Auth memastikan bahwa kata sandi pengguna telah di-hash secara otomatis dan dienkripsi oleh Google. Realtime database menggunakan arsitektur rules yang aman (`.read: "auth != null"`).

2. **Isolasi Data (Data Privacy)**
   * Lokasi: `BookingRepository.kt`
   * Penjelasan: Saat memanggil dan menyimpan ke database, aplikasi selalu merujuk ke `.child(uid)`. Ini memastikan setiap pengguna **hanya bisa mengakses tiket mereka sendiri** dan tiketnya tidak akan tertukar dengan tiket orang lain.

3. **Autentikasi Biometrik (Sidik Jari / Wajah)**
   * Lokasi: `utils/BiometricHelper.kt` dan dipanggil di `LoginScreen.kt`
   * Penjelasan: Menggunakan *Android Biometric API*. Ketika user sudah pernah login (Sesi masih hidup), aplikasi tidak akan langsung membuka halaman utama, namun menagih sidik jari pengguna terlebih dahulu untuk menjamin bahwa yang memegang HP adalah pemilik asli akun.

4. **Proteksi Anti-Tangkapan Layar (Screenshot Protection)**
   * Lokasi: `MainActivity.kt`
   * Penjelasan: Kita menyisipkan `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, ...)` pada fungsi `onCreate`. Ini menginstruksikan sistem Android untuk memblokir siapapun yang mencoba melakukan *Screenshot* atau merekam layar (hasilnya akan blank hitam) di dalam aplikasi. Ini krusial karena aplikasi ini menampilkan tiket pesawat berbayar.

5. **Validasi Input Klien**
   * Lokasi: `LoginScreen.kt` dan `RegisterScreen.kt`
   * Penjelasan: Mencegah error dan spamming dengan memvalidasi input *sebelum* memanggil database. Contoh: Format email di-tes menggunakan `android.util.Patterns.EMAIL_ADDRESS.matcher()`. Kata sandi baru dikonfirmasi kesamaannya sebelum diizinkan ganti sandi.
