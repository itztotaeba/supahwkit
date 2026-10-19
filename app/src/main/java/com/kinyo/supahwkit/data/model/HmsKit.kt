package com.kinyo.supahwkit.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kinyo.supahwkit.R

/**
 * ============================================================================
 * KitStatus - Tingkat kesiapan implementasi sebuah kit di aplikasi ini.
 * ============================================================================
 * Dipakai dashboard & halaman detail untuk menampilkan badge progress:
 *  - PLANNED : baru direncanakan (halaman masih kerangka kosong)
 *  - PARTIAL : sebagian fungsi sudah jalan, sisanya menyusul
 *  - DONE    : contoh penggunaan utama sudah terimplementasi penuh
 * Menambah nilai baru di sini tidak merusak kode lain karena pemanggilan
 * selalu lewat when(kit.status) dengan else / semua cabang tercover.
 * ============================================================================
 */
enum class KitStatus {
    PLANNED, PARTIAL, DONE
}

/**
 * ============================================================================
 * KitId - Enum identitas setiap HMS Kit.
 * ============================================================================
 * Kita pakai enum (bukan string bebas) supaya:
 *  - tidak ada typo saat routing (salah ketik = error kompilasi, bukan crash)
 *  - refactor aman: kalau nama kit diganti, semua pemakai ikut terdeteksi IDE
 * valueOf(name) dipakai untuk mengubah string extra Intent kembali ke enum.
 * ============================================================================
 */
enum class KitId {
    ACCOUNT,          // Huawei ID / Account Kit        -> sign-in & profil pengguna
    LOCATION,         // Location Kit                   -> positioning GPS/network/iBeacon
    MAP,              // Map Kit                        -> menampilkan peta & marker
    PUSH,             // Push Kit                       -> notifikasi push via Huawei server
    SCAN,             // Scan Kit                       -> scan barcode / QR code via kamera
    TEXT_RECOGNITION, // Text Recognition Kit (ML)      -> OCR: baca teks dari gambar
    SAFETY_DETECT,    // Safety Detect                  -> deteksi root/emulator/integritas
    FIDO              // FIDO (Biometric) Login Kit     -> autentikasi sidik jari / wajah
}

/**
 * ============================================================================
 * HmsKit - Model data satu menu kit di dashboard.
 * ============================================================================
 * `data class` Kotlin otomatis menyediakan equals()/hashCode()/copy()/toString()
 * yang sangat berguna di Jetpack Compose: ketika objek dianggap "sama" oleh
 * equals(), Compose bisa melewat proses render ulang (smart recomposition),
 * sehingga UI lebih efisien.
 *
 * Semua field memakai resource ID (@StringRes/@DrawableRes/@ColorRes), BUKAN
 * nilai hardcoded, supaya teks mudah dilokalisasi dan tema gampang diubah.
 * ============================================================================
 */
data class HmsKit(
    val id: KitId,                            // kunci navigasi -> halaman detail
    @param:StringRes val titleRes: Int,       // judul kit (dari strings.xml)
    @param:StringRes val descriptionRes: Int, // deskripsi singkat satu baris
    @param:StringRes val taglineRes: Int,     // kalimat pembuka di halaman detail
    @param:DrawableRes val iconRes: Int,      // ikon vector kit
    @param:ColorRes val colorRes: Int,        // warna brand/aksen kit
    /*
     * status: progress implementasi kit ini di aplikasi.
     * Default PLANNED supaya saat menambah kit baru kita tidak wajib
     * menulis field-nya; nilai diubah manual ketika demonya jadi dibuat.
     */
    val status: KitStatus = KitStatus.PLANNED,
)

/**
 * ============================================================================
 * KitCatalog - "Single source of truth" daftar seluruh kit.
 * ============================================================================
 * Tambah kit baru cukup menambah satu item di list ini - dashboard
 * (LazyVerticalGrid) otomatis menyesuaikan karena merender berdasarkan jumlah
 * elemen list, bukan jumlah card yang ditulis manual seperti versi XML dulu.
 *
 * `object` = singleton Kotlin (hanya ada 1 instance seumur proses aplikasi),
 * cocok untuk katalog statis. Akses: KitCatalog.items
 * ============================================================================
 */
object KitCatalog {

    /** Extra key yang dipakai MainActivity -> KitDetailActivity lewat Intent. */
    const val EXTRA_KIT_ID = "extra_kit_id"

    val items: List<HmsKit> = listOf(

        // 1. ACCOUNT KIT - login memakai akun Huawei (Huawei ID).
        //    Status DONE: sign-in/silent-sign-in/sign-out + halaman profil
        //    sudah terimplementasi (lihat ui/account & ui/profile).
        HmsKit(
            id = KitId.ACCOUNT,
            titleRes = R.string.kit_account_title,
            descriptionRes = R.string.kit_account_desc,
            taglineRes = R.string.kit_account_tagline,
            iconRes = R.drawable.ic_account,
            colorRes = R.color.kit_account,
            status = KitStatus.DONE
        ),

        // 2. LOCATION KIT - posisi perangkat gabungan GPS + jaringan + iBeacon.
        HmsKit(
            id = KitId.LOCATION,
            titleRes = R.string.kit_location_title,
            descriptionRes = R.string.kit_location_desc,
            taglineRes = R.string.kit_location_tagline,
            iconRes = R.drawable.ic_location,
            colorRes = R.color.kit_location
        ),

        // 3. MAP KIT - rendering peta Huawei (HMS Map Service).
        HmsKit(
            id = KitId.MAP,
            titleRes = R.string.kit_map_title,
            descriptionRes = R.string.kit_map_desc,
            taglineRes = R.string.kit_map_tagline,
            iconRes = R.drawable.ic_map,
            colorRes = R.color.kit_map
        ),

        // 4. PUSH KIT - kirim pesan/notifikasi remote lewat Huawei Push Server.
        HmsKit(
            id = KitId.PUSH,
            titleRes = R.string.kit_push_title,
            descriptionRes = R.string.kit_push_desc,
            taglineRes = R.string.kit_push_tagline,
            iconRes = R.drawable.ic_push,
            colorRes = R.color.kit_push
        ),

        // 5. SCAN KIT - scanner barcode/QR default maupun custom view.
        HmsKit(
            id = KitId.SCAN,
            titleRes = R.string.kit_scan_title,
            descriptionRes = R.string.kit_scan_desc,
            taglineRes = R.string.kit_scan_tagline,
            iconRes = R.drawable.ic_scan,
            colorRes = R.color.kit_scan
        ),

        // 6. TEXT RECOGNITION KIT - machine learning OCR on-device.
        HmsKit(
            id = KitId.TEXT_RECOGNITION,
            titleRes = R.string.kit_text_title,
            descriptionRes = R.string.kit_text_desc,
            taglineRes = R.string.kit_text_tagline,
            iconRes = R.drawable.ic_text,
            colorRes = R.color.kit_text
        ),

        // 7. SAFETY DETECT - deteksi ancaman keamanan perangkat.
        HmsKit(
            id = KitId.SAFETY_DETECT,
            titleRes = R.string.kit_safety_title,
            descriptionRes = R.string.kit_safety_desc,
            taglineRes = R.string.kit_safety_tagline,
            iconRes = R.drawable.ic_safety,
            colorRes = R.color.kit_safety
        ),

        // 8. FIDO - Fast Identity Online: biometric login (sidik jari/wajah).
        HmsKit(
            id = KitId.FIDO,
            titleRes = R.string.kit_fido_title,
            descriptionRes = R.string.kit_fido_desc,
            taglineRes = R.string.kit_fido_tagline,
            iconRes = R.drawable.ic_fido,
            colorRes = R.color.kit_fido
        )
    )

    /**
     * Ambil model kit berdasarkan enum id.
     * firstOrNull dipakai agar tidak crash walau suatu saat ada enum tanpa data.
     */
    fun find(id: KitId): HmsKit? = items.firstOrNull { it.id == id }
}
