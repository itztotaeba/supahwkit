package com.kinyo.supahwkit.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.databinding.ActivityMainBinding
import com.kinyo.supahwkit.ui.adapter.KitMenuAdapter
import com.kinyo.supahwkit.ui.kit.KitDetailActivity

/**
 * ============================================================================
 * MainActivity.kt - HALAMAN UTAMA (Dashboard) aplikasi Supa HW Kit.
 * ============================================================================
 *
 * PERAN DI APLIKASI:
 * Activity ini adalah entry point (launcher). User membuka aplikasi -> melihat
 * grid berisi 8 card HMS Kit -> memilih satu -> dibawa ke KitDetailActivity.
 *
 * ARSITEKTUR SEDERHANA YANG DIPAKAI:
 *   [KitCatalog]  ->  data statis daftar kit        (data layer)
 *   [KitMenuAdapter] -> render list jadi card       (presentation layer)
 *   [MainActivity]   -> wiring: siapkan RecyclerView, dengar klik, navigasi
 *
 * LIFE CYCLE SINGKAT AppCompatActivity (untuk belajar):
 *   onCreate()   : dibuat sekali saat Activity pertama kali "lahir".
 *                  Tempat WAJIB: setContentView + inisialisasi UI.
 *   onStart/onResume : Activity terlihat/dapat interaksi.
 *   onDestroy    : Activity dihancurkan (user tekan back / sistem kill).
 * Untuk aplikasi contoh ini, semua logika kita taruh di onCreate karena
 * datanya statis dan tidak perlu refresh berkala.
 * ============================================================================
 */
class MainActivity : AppCompatActivity() {

    /**
     * Properti binding: akses type-safe ke seluruh view di activity_main.xml.
     * lateinit = "dijamin terisi sebelum dipakai" (di onCreate), supaya Kotlin
     * mengizinkan tipe non-null tanpa harus memberi nilai default dummy.
     */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Panggil implementasi parent class dulu - wajib, agar AppCompat
        // menyiapkan theme/decor view sebelum kita menyentuh layout sendiri.
        super.onCreate(savedInstanceState)

        // ---- 1. Inflate layout dengan ViewBinding -------------------------
        // Alternatif lama: setContentView(R.layout.activity_main).
        // Dengan binding, kita inflate object lalu root view-nya yang dipasang.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---- 2. Siapkan RecyclerView --------------------------------------
        /*
         * GridLayoutManager: mengatur item dalam GRID, bukan list lurus.
         * Argumen spanCount = 2 artinya 2 kolom; tingginya otomatis menyesuaikan
         * jumlah item (8 item -> 4 baris). Ini pola dashboard klasik ala
         * launcher aplikasi Huawei/HMS demo.
         */
        binding.rvKitMenu.layoutManager = GridLayoutManager(this, SPAN_COUNT)

        /*
         * Adapter: kita passing data katalog + lambda onClick.
         * Lambda `{ kit -> openKit(kit) }` akan dipanggil adapter ketika user
         * mengetuk salah satu card. Perhatikan `this` di dalam lambda merujuk
         * ke Activity (karena lambda tidak mengubah receiver).
         */
        val adapter = KitMenuAdapter(KitCatalog.items) { kit ->
            openKitDetail(kit)
        }
        binding.rvKitMenu.adapter = adapter

        // ---- 3. Teks header dashboard -------------------------------------
        binding.tvHeaderSubtitle.text =
            getString(HEADER_SUBTITLE_RES, KitCatalog.items.size)
    }

    /**
     * Navigasi dari dashboard ke halaman demo sebuah kit.
     *
     * MEKANISME INTENT (materi belajar Android):
     * Intent = "pesan" antar component. Kita buat explicit intent (menyebut
     * class tujuan langsung), lalu sisipkan data lewat extras (Bundle key-value).
     * KitDetailActivity nanti membaca extra EXTRA_KIT_ID untuk tahu dia harus
     * menampilkan demo kit apa.
     */
    private fun openKitDetail(kit: HmsKit) {
        val intent = Intent(this, KitDetailActivity::class.java).apply {
            // putExtra: menaruh data kecil (primitif/String) ke dalam Intent.
            // Kita kirim nama enum sebagai String agar aman terhadap refactor
            // (tidak pernah pakai ordinal() — urutan enum bisa berubah!).
            putExtra(KitDetailActivity.EXTRA_KIT_ID, kit.id.name)
        }
        startActivity(intent) // jalankan Activity baru di atas stack navigasi
    }

    /**
     * Companion object = tempat menaruh konstanta "static" di Kotlin.
     * Nilainya milik kelas, bukan instance, jadi bisa diakses tanpa objek
     * MainActivity, namun tetap privat dari dunia luar.
     */
    companion object {
        private const val SPAN_COUNT = 2                 // 2 kolom grid dashboard
        private const val HEADER_SUBTITLE_RES = com.kinyo.supahwkit.R.string.dashboard_subtitle
    }
}
