package com.kinyo.supahwkit.data.model

// android.os.Parcelable: kontrak serialisasi objek antar komponen Android.
import android.os.Parcelable
// kotlinx.parcelize.Parcelize: anotasi @Parcelize dari plugin kotlin-parcelize.
import kotlinx.parcelize.Parcelize

/*
 * ============================================================================
 * UserProfile - Model "bersih" profil pengguna Huawei ID.
 * ============================================================================
 * KENAPA ADA MODEL SENDIRI (tidak langsung memakai HuaweiIdAuthResult)?
 *  1. Lapisan UI (Compose) tidak boleh bergantung pada kelas SDK pihak ketiga.
 *     Kalau nanti versi SDK Huawei berubah / kita ganti provider login lain,
 *     cukup ubah kode KONVERSI di service - screen & composable tetap utuh.
 *  2. Kelas hasil SDK bersifat Parcelable dan hanya hidup saat Intent hasil
 *     login tiba; menyimpannya lebih lama rawan bocor memori / tidak bisa
 *     dipersistensi. Data class biasa jauh lebih mudah di-cache & diuji.
 *  3. Memudahkan unit test: kita bisa membuat instance palsu tanpa SDK.
 *
 * Semua field nullable karena kelengkapan data tergantung scope yang disetujui
 * pengguna saat consent (misal avatarUri kosong bila pengguna tak punya foto).
 * ----------------------------------------------------------------------------
 * @Parcelize + Parcelable (PENAMBAHAN PR ACCOUNT KIT):
 *  Android mengirim objek antar-Activity lewat Intent dengan cara SERIALISASI:
 *  objek diubah jadi byte stream (Parcel), lalu dibalik lagi jadi objek di sisi
 *  penerima. Interface `Parcelable` adalah kontrak serialisasi versi Google
 *  (jauh lebih cepat dari Serializable Java).
 *
 *  Zaman dulu kita harus menulis writeToParcel()/CREATOR manual. Dengan plugin
 *  Gradle `kotlin-parcelize` (lihat app/build.gradle.kts), anotasi @Parcelize
 *  membuat compiler men-generate seluruh boilerplate itu otomatis - ASALKAN
 *  semua properti ikut parcelable (String? aman, begitu juga Int?, Long?, dst).
 *
 *  Efeknya: ProfileActivity bisa membaca objek utuh dari
 *  intent.getParcelableExtra(EXTRA_PROFILE, UserProfile::class.java).
 * ============================================================================
 */
@Parcelize // <- generate implementasi Parcelable otomatis via plugin kotlin-parcelize
data class UserProfile(
    /**
     * openID: identitas unik pengguna HANYA di aplikasi ini.
     * Aplikasi lain dari developer yang sama TIDAK melihat nilai yang sama.
     * Ini id utama yang biasanya dipakai sebagai kunci akun di server kita.
     */
    val openId: String?,

    /**
     * unionID: identitas yang konsisten untuk semua aplikasi milik satu
     * developer/akun Huawei. Hanya terisi jika kita memintanya via scope
     * dan aplikasi terhubung ke satu akun developer di AGC. Boleh null.
     */
    val unionId: String?,

    /** Nama tampilan (display name) yang dipilih pengguna di Huawei ID. */
    val displayName: String?,

    /**
     * URI foto profil (avatar). Bentuknya string URL https://... sehingga
     * bisa langsung diberikan ke komponen AsyncImage di Compose.
     */
    val avatarUri: String?,

    /**
     * Email - HANYA tersedia jika scope "email" diminta DAN pengguna menyetujui.
     * Sengaja opsional demi privasi (prinsip data minimization).
     */
    val email: String?,

    /**
     * Authorization code untuk pertukaran token di sisi SERVER (alur
     * server-side sign-in). Untuk demo on-device ini kita simpan tapi tidak
     * pakai; dokumentasinya ada di halaman "Account Kit -> Server-side".
     */
    val authorizationCode: String? = null,
) : Parcelable { // <- wajib: @Parcelize hanya bekerja pada kelas yang mendeklarasikan Parcelable
    /**
     * Inisial nama untuk placeholder avatar (misal "Budi Santoso" -> "BS").
     * Kalau nama kosong, fallback ke tanda "?" agar UI tetap punya teks.
     *
     * Penjelasan singkat sintaks Kotlin di bawah:
     *  - split(" ")  : pecah string per spasi -> List<String>
     *  - mapNotNull  : ambil tiap elemen, buang yang null setelah transform
     *  - take(2)     : batasi maksimal 2 kata (depan + belakang saja)
     *  - joinToString("") : gabung huruf jadi satu string ("BS")
     */
    val initials: String
        get() = displayName
            ?.split(" ")                 // ["Budi", "Santoso"]
            ?.mapNotNull { it.firstOrNull()?.uppercase()?.toString() } // ["B","S"]
            ?.take(2)                    // maksimal 2 inisial
            ?.joinToString("")           // "BS"
            ?.takeIf { it.isNotBlank() } // kalau hasil "" dianggap tidak valid
            ?: "?"                       // fallback final
}
