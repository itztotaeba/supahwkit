package com.kinyo.supahwkit.ui.kit

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.kinyo.supahwkit.R
import com.kinyo.supahwkit.data.model.KitId
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.databinding.ActivityKitDetailBinding

/**
 * ============================================================================
 * KitDetailActivity.kt - Halaman demo untuk SATU HMS Kit tertentu.
 * ============================================================================
 *
 * FASE SAAT INI (Step 1 sesuai rencana proyek):
 * Dashboard 8 menu sudah berfungsi penuh. Halaman detail ini masih berupa
 * KERANGKA (shell) yang menampilkan info kit + penanda "implementasi belum
 * dibuat". Pada langkah berikutnya, tiap KitId akan punya fragment/layout
 * demo sendiri (misal MapView untuk MAP, tombol getToken untuk PUSH, dst).
 *
 * CARA KERJA ROUTING:
 * 1. MainActivity mengirim Intent dengan extra bertipe String:
 *      EXTRA_KIT_ID = KitId.ACCOUNT.name  -> "ACCOUNT"
 * 2. Di sini kita baca extra tersebut lalu ubah balik ke enum:
 *      KitId.valueOf("ACCOUNT") -> KitId.ACCOUNT
 *    valueOf() melempar IllegalArgumentException jika string tak dikenal,
 *    makanya dibungkus runCatching agar aplikasi tidak crash.
 * 3. Cari metadata kit di KitCatalog untuk mengisi judul & deskripsi.
 * 4. when(kitId) menentukan teks status implementasi per kit.
 * ============================================================================
 */
class KitDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKitDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---- Aktifkan tombol back (panah kiri) di ActionBar ---------------
        // getSupportActionBar() bisa null tergantung theme; ?. amankan call.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ---- 1. Baca kit mana yang diminta lewat Intent --------------------
        val kitIdName = intent.getStringExtra(EXTRA_KIT_ID)
        // runCatching: eksekusi blok, kalau exception tangkap sebagai Result.
        // getOrNull(): hasil sukses atau null. fold(): pilih di antara dua nilai.
        val kitId: KitId = runCatching { KitId.valueOf(kitIdName ?: "") }
            .getOrNull()
            ?: KitId.ACCOUNT // fallback aman bila extra hilang/rusak

        // ---- 2. Ambil metadata kit dari katalog (firstOrNull = safe search)
        val kit = KitCatalog.items.firstOrNull { it.id == kitId }

        // ---- 3. Isi UI dengan metadata -------------------------------------
        supportActionBar?.title = kit?.title ?: getString(R.string.app_name)
        binding.tvKitDetailTitle.text = kit?.title.orEmpty()
        binding.tvKitDetailDescription.text = kit?.description.orEmpty()
        binding.ivKitDetailIcon.setImageResource(
            kit?.iconRes ?: R.drawable.ic_account
        )
        kit?.let { binding.ivKitDetailIcon.setBackgroundColor(getColor(it.colorRes)) }

        // ---- 4. Tentukan status implementasi per kit -----------------------
        /*
         * 'when' = switch-nya Kotlin; lebih kuat karena bisa return nilai,
         * tidak butuh break, dan compiler mengingatkan jika ada case hilang.
         * Saat ini semua branch menunjukkan "belum diimplementasikan" —
         * nanti, ketika misal Map Kit selesai, branch MAP diganti dengan
         * memanggil setupMapDemo() yang menambahkan MapView secara dinamis.
         */
        binding.tvKitStatus.text = when (kitId) {
            KitId.ACCOUNT,
            KitId.LOCATION,
            KitId.MAP,
            KitId.PUSH,
            KitId.SCAN,
            KitId.TEXT_RECOGNITION,
            KitId.SAFETY_DETECT,
            KitId.FIDO -> getString(R.string.detail_not_implemented, kit?.title ?: kitId.name)
        }
    }

    /**
     * Tangani klik item pada ActionBar (termasuk tombol Up/back panah kiri).
     * android.R.id.home adalah ID khusus yang diberikan sistem untuk tombol Up.
     * finish() = tutup Activity ini, user kembali ke dashboard.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true // beri tahu sistem: event sudah ditangani
            }
            else -> super.onOptionsItemSelected(item) // serahkan ke parent jika bukan
        }
    }

    companion object {
        /**
         * Kunci ekstra Intent. Dibuat PUBLIC agar MainActivity bisa memakai
         * konstanta yang sama (single source of truth - mencegah typo key).
         */
        const val EXTRA_KIT_ID = "extra_kit_id"
    }
}
