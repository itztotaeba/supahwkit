package com.kinyo.supahwkit.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity      // Activity modern: dasar setContent() Compose
import androidx.activity.compose.setContent     // jembatan Activity -> dunia Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.ui.kit.KitDetailActivity
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * MainActivity - Pintu masuk aplikasi (launcher) = host DashboardScreen.
 * ============================================================================
 * PERAN ACTIVITY DI DUNIA COMPOSE berubah dibanding XML:
 *  - Dulu : Activity inflate layout XML, findViewById, pasang adapter.
 *  - Sekarang : Activity HANYA jadi "jangkar" (host). Tugasnya:
 *      1. onCreate -> panggil setContent { ... } untuk memulai tree Compose
 *      2. menyediakan Context Android (untuk Intent/navigasi antar halaman)
 *    Selebihnya murni deklaratif di fungsi @Composable.
 *
 * ComponentActivity dipilih menggantikan AppCompatActivity karena:
 *  - lebih ringan (bagian dari androidx.activity)
 *  - punya ekstensi setContent{} resmi untuk Compose
 *  - kita tidak lagi butuh fitur AppCompat (tema ditangani Material3).
 *
 * NAVIGASI: sengaja masih pakai Intent eksplisit (bukan Navigation-Compose)
 * supaya konsep mudah dipahami pemula dan bisa dicampur dengan HMS SDK yang
 * kadang meminta Activity/Fragment biasa. Bila proyek membesar, migrasi ke
 * single-activity + NavHost cukup mengganti bagian onKitSelected saja.
 * ============================================================================
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * setContent = titik masuk Compose. Polanya selalu:
         *   Theme { Screen }  -> tema dibungkus dulu, baru konten halaman.
         * Surface warna background dipakai sebagai "kanvas" penuh layar agar
         * latar konsisten walau activity berpindah (menghindari flash putih).
         */
        setContent {
            SupaHWKitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*
                     * DashboardScreen stateless: kita inject lambda navigasi.
                     * `this` di dalam lambda = MainActivity (Context tersedia),
                     * jadi pembuatan Intent dilakukan di sini, bukan di screen.
                     */
                    DashboardScreen(
                        onKitSelected = { kit -> openKitDetail(kit) }
                    )
                }
            }
        }
    }

    /**
     * Buka halaman demo kit terpilih lewat Intent eksplisit.
     *
     * Alur:
     *  1. Intent(this, KitDetailActivity::class.java) -> target class jelas.
     *  2. putExtra membawa identifier kit (nama enum -> String, karena
     *     Bundle paling aman menyimpan tipe primitif/String; di tujuan kita
     *     konversi balik dengan KitId.valueOf()).
     *  3. startActivity menjalankan Activity baru di atas stack navigasi;
     *     tombol back sistem otomatis kembali ke dashboard.
     */
    private fun openKitDetail(kit: HmsKit) {
        val intent = Intent(this, KitDetailActivity::class.java).apply {
            putExtra(KitCatalog.EXTRA_KIT_ID, kit.id.name)
        }
        startActivity(intent)
    }
}
