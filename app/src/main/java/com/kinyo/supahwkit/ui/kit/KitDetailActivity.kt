package com.kinyo.supahwkit.ui.kit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.data.model.KitId
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * KitDetailActivity - Host KitDetailScreen untuk satu kit terpilih.
 * ============================================================================
 * Sama seperti MainActivity: Activity hanya menjadi jembatan Android <->
 * Compose. Tanggung jawabnya:
 *  1. Membaca extra Intent (nama enum) -> konversi aman ke model HmsKit
 *  2. Menyiapkan tema & merender KitDetailScreen
 *  3. Menyediakan aksi "back" (finish()) saat panah app bar ditekan
 * ============================================================================
 */
class KitDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * resolveKit(): ubah string extra -> enum -> model katalog.
         * runCatching dipakai agar nilai string tak dikenal (misal app dipanggil
         * dengan extra rusak) tidak melempar IllegalArgumentException yang
         * membuat halaman crash; hasilnya null dan UI menampilkan layar
         * "kit not found". Ini pola defensive programming sederhana.
         */
        val kit: HmsKit? = resolveKit()

        setContent {
            SupaHWKitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KitDetailScreen(
                        kit = kit,
                        // finish() menutup Activity ini -> user kembali ke dashboard
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }

    /** Ekstraksi & validasi data navigasi dari Intent. */
    private fun resolveKit(): HmsKit? {
        val rawId = intent.getStringExtra(KitCatalog.EXTRA_KIT_ID) ?: return null
        val kitId = runCatching { KitId.valueOf(rawId) }.getOrNull() ?: return null
        return KitCatalog.find(kitId)
    }
}
