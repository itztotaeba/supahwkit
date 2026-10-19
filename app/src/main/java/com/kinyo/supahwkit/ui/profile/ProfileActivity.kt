package com.kinyo.supahwkit.ui.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kinyo.supahwkit.data.model.UserProfile
import com.kinyo.supahwkit.data.remote.HuaweiAccountService
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * ProfileActivity - Halaman profil sederhana hasil login Account Kit.
 * ============================================================================
 * PERAN
 *  Membuktikan secara visual bahwa implementasi Huawei Account Kit BERHASIL:
 *  begitu login selesai, halaman ini menampilkan data asli yang dikembalikan
 *  Huawei (nama, openID, avatar, email). Kalau semua field terisi = kit jalan.
 *
 * CARA DATA MASUK KE HALAMAN INI (dua jalur):
 *  1. EKSTRA INTENT : AccountKitScreen mengirim UserProfile (Parcelable) lewat
 *     Intent saat login interaktif baru saja sukses -> paling akurat.
 *  2. CACHE SDK     : bila user membuka halaman profil tanpa lewat alur login
 *     (misal dari notifikasi), kita ambil sesi tersimpan via
 *     HuaweiIdAuthManager.getAuthResult() di service.cachedProfile().
 *
 * Pola Activity-nya sama dengan MainActivity/KitDetailActivity: Activity hanya
 * host Compose; seluruh tampilan dideklarasikan di ProfileScreen.
 * ============================================================================
 */
class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Prioritas 1: baca Parcelable dari extra Intent.
         * getParcelableExtra deprecated di API 33+ tapi tetap cara paling
         * kompatibel minSdk 24; dipisah ke helper kecil di bawah.
         */
        val fromIntent: UserProfile? = readProfileExtra()

        /*
         * Prioritas 2: fallback ke cache sesi Account Kit (silent).
         * cachedProfile() tidak menyentuh jaringan, hanya membaca state lokal.
         */
        val profile = fromIntent ?: HuaweiAccountService(this).cachedProfile()

        setContent {
            SupaHWKitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileScreen(
                        profile = profile,          // boleh null -> tampilkan empty state
                        onNavigateBack = { finish() } // panah back -> kembali ke halaman kit
                    )
                }
            }
        }
    }

    /**
     * Ekstraksi aman extra profil dari Intent.
     *
     * Catatan kompatibilitas:
     *  - API < 33 : intent.getParcelableExtra(key, Class) belum ada, pakai yang lama.
     *  - API >= 33 : gunakan overload bertipe agar tidak memicu warning.
     * runCatching melindungi dari BadParcelableException bila format extra berubah.
     */
    private fun readProfileExtra(): UserProfile? = runCatching {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_PROFILE, UserProfile::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PROFILE)
        }
    }.getOrNull()

    companion object {
        /** Key extra Intent pembawa objek profil (harus sama dgn pengirim). */
        const val EXTRA_PROFILE = "extra_user_profile"
    }
}
