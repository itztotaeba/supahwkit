package com.kinyo.supahwkit.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.kinyo.supahwkit.R

/**
 * ============================================================================
 * HmsKit.kt - Model data untuk merepresentasikan satu Huawei Mobile Services (HMS) Kit.
 * ============================================================================
 *
 * KONSEP:
 * Kelas ini adalah "data class", yaitu kelas Kotlin yang fokus menyimpan data.
 * Kita membuatnya `data class` agar otomatis mendapat method berguna seperti:
 *  - equals()/hashCode()  : membandingkan isi dua objek
 *  - copy()               : menyalin objek dengan modifikasi sebagian
 *  - toString()           : tampilan teks otomatis untuk debugging
 *
 * Setiap item di dashboard (Account, Location, Map, dst) akan dibuat sebagai
 * satu objek HmsKit. Jadi dashboard pada dasarnya hanya menampilkan List<HmsKit>.
 *
 * PENJELASAN PARAMETER KONSTRUKTOR:
 *  - id          : Penanda unik kit, dipakai untuk routing (menentukan Activity
 *                  mana yang dibuka saat card diklik). Pakai enum supaya tidak
 *                  ada typo string dan refactor lebih aman.
 *  - title       : Nama kit yang tampil besar di card (contoh: "Map Kit").
 *  - description : Penjelasan singkat fungsi kit, tampil di bawah title.
 *  - iconRes     : Resource ID ikon (vector drawable) untuk sisi kiri card.
 *                  @DrawableRes memastikan yang kita passing hanyalah resource
 *                  gambar - jika salah ketik, compiler langsung memberi error.
 *  - colorRes    : Resource ID warna aksen untuk ikon/card tiap kit, supaya
 *                  setiap menu punya identitas visual berbeda.
 * ============================================================================
 */
enum class KitId {
    ACCOUNT,          // Huawei ID / Account Kit        -> sign-in & profil pengguna
    LOCATION,         // Location Kit                   -> perangkat positioning (GPS/network)
    MAP,              // Map Kit                        -> Menampilkan peta & marker
    PUSH,             // Push Kit                       -> notifikasi push dari server Huawei
    SCAN,             // Scan Kit                       -> scan barcode / QR code via kamera
    TEXT_RECOGNITION, // Text Recognition Kit (ML)      -> OCR: baca teks dari gambar
    SAFETY_DETECT,    // Safety Detect                  -> deteksi risiko perangkat/root/emulator
    FIDO              // FIDO (Biometric) Login Kit     -> autentikasi sidik jari / face unlock
}

data class HmsKit(
    val id: KitId,                       // identitas kit untuk keperluan navigasi
    val title: String,                   // judul yang ditampilkan pada card
    val description: String,             // sub-judul / penjelasan singkat
    @param:DrawableRes val iconRes: Int, // resource ikon (misal R.drawable.ic_map)
    @param:ColorRes val colorRes: Int    // resource warna aksen kit tersebut
)

/**
 * ============================================================================
 * KIT_CATALOG - Daftar (katalog) seluruh HMS Kit yang akan ditampilkan dashboard.
 * ============================================================================
 *
 * Ini adalah "single source of truth": satu tempat yang berisi definisi semua
 * menu aplikasi. Jika nanti ingin menambah kit baru (misal ML Kit atau IAP),
 * cukup tambahkan satu baris di list ini - UI tidak perlu diubah sama sekali
 * karena RecyclerView merender berdasarkan jumlah item di list ini.
 *
 * `object` di Kotlin = singleton (hanya ada 1 instance seumur proses aplikasi),
 * cocok untuk katalog statis seperti ini. Aksesnya: KitCatalog.items
 *
 * Urutan item di list = urutan tampilan card di dashboard.
 * ============================================================================
 */
object KitCatalog {

    val items: List<HmsKit> = listOf(

        // 1. ACCOUNT KIT - login memakai akun Huawei (Huawei ID).
        HmsKit(
            id = KitId.ACCOUNT,
            title = "Account Kit",
            description = "Sign in dengan Huawei ID & ambil profil pengguna",
            iconRes = R.drawable.ic_account,
            colorRes = R.color.kit_account
        ),

        // 2. LOCATION KIT - posisi perangkat gabungan GPS + jaringan + iBeacon.
        HmsKit(
            id = KitId.LOCATION,
            title = "Location Kit",
            description = "Peroleh koordinat lokasi perangkat (GPS & network)",
            iconRes = R.drawable.ic_location,
            colorRes = R.color.kit_location
        ),

        // 3. MAP KIT - rendering peta Huawei (HMS Map Service).
        HmsKit(
            id = KitId.MAP,
            title = "Map Kit",
            description = "Tampilkan peta interaktif, marker, dan camera",
            iconRes = R.drawable.ic_map,
            colorRes = R.color.kit_map
        ),

        // 4. PUSH KIT - kirim pesan/notifikasi remote lewat Huawei Push Server.
        HmsKit(
            id = KitId.PUSH,
            title = "Push Kit",
            description = "Token device & terima notifikasi push dari server",
            iconRes = R.drawable.ic_push,
            colorRes = R.color.kit_push
        ),

        // 5. SCAN KIT - scanner barcode/QR default maupun custom view.
        HmsKit(
            id = KitId.SCAN,
            title = "Scan Kit",
            description = "Scan barcode / QR code melalui kamera",
            iconRes = R.drawable.ic_scan,
            colorRes = R.color.kit_scan
        ),

        // 6. TEXT RECOGNITION KIT - machine learning OCR on-device.
        HmsKit(
            id = KitId.TEXT_RECOGNITION,
            title = "Text Recognition Kit",
            description = "Kenali teks dari foto/gambar secara on-device (OCR)",
            iconRes = R.drawable.ic_text,
            colorRes = R.color.kit_text
        ),

        // 7. SAFETY DETECT - deteksi ancaman keamanan perangkat.
        HmsKit(
            id = KitId.SAFETY_DETECT,
            title = "Safety Detect",
            description = "Deteksi root, emulator, & integritas aplikasi",
            iconRes = R.drawable.ic_safety,
            colorRes = R.color.kit_safety
        ),

        // 8. FIDO - Fast Identity Online: biometric login (sidik jari/wajah).
        HmsKit(
            id = KitId.FIDO,
            title = "FIDO Biometric",
            description = "Autentikasi biometrik sidik jari / wajah",
            iconRes = R.drawable.ic_fido,
            colorRes = R.color.kit_fido
        )
    )
}
