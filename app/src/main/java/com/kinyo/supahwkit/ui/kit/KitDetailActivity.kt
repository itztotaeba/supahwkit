package com.kinyo.supahwkit.ui.kit

import android.app.Activity // dibutuhkan AccountKitScreen (API HMS berbasis Activity)
import android.content.Intent
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
import com.kinyo.supahwkit.ui.profile.ProfileActivity
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
                        onNavigateBack = { finish() },

                        /*
                         * ===== ROUTING DEMO PER-KIT (PENAMBAHAN ACCOUNT KIT) =====
                         * KitDetailScreen menerima parameter `demoContent` berupa
                         * lambda @Composable. Di sini kita isi sesuai id kit:
                         *   - ACCOUNT -> AccountKitScreen (login Huawei ID nyata)
                         *   - lainnya -> null (tampilan placeholder "belum dibuat")
                         * Pola ini membuat halaman detail TIDAK perlu tahu isi
                         * tiap kit; cukup daftarkan composable-nya di when().
                         */
                        demoContent = if (kit?.id == KitId.ACCOUNT) {
                            {
                                /*
                                 * `this@KitDetailActivity` = referensi objek Activity
                                 * host. AccountKitScreen butuh Activity (bukan Context
                                 * biasa) karena API HMS seperti signInIntent/silentSignIn
                                 * mengembalikan hasil lewat mekanisme activity-result.
                                 */
                                com.kinyo.supahwkit.ui.account.AccountKitScreen(
                                    activity = this@KitDetailActivity,
                                    /*
                                     * Callback saat login SUKSES -> buka ProfileActivity
                                     * dan bawa objek profil (Parcelable via @Parcelize).
                                     * Inilah "halaman profil sederhana" bukti Account
                                     * Kit bekerja: data asli Huawei ID tampil di sana.
                                     */
                                    onLoginSuccess = { profile ->
                                        openProfile(profile)
                                    }
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }

    /**
     * openProfile - helper navigasi ke halaman profil.
     *
     * Alur Intent:
     *  1. Intent(context, target::class.java) -> deklarasikan tujuan.
     *  2. putExtra(key, parcelable)           -> titipkan objek UserProfile.
     *     Karena UserProfile kini Parcelable (@Parcelize), sistem bisa
     *     men-serialisasi seluruh field ke Parcel antar-proses.
     *  3. startActivity                      -> ProfileActivity onCreate akan
     *     membaca extra yang sama (lihat ProfileActivity.readProfileExtra()).
     */
    private fun openProfile(profile: com.kinyo.supahwkit.data.model.UserProfile) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(ProfileActivity.EXTRA_PROFILE, profile)
        }
        startActivity(intent)
    }

    /** Ekstraksi & validasi data navigasi dari Intent. */
    private fun resolveKit(): HmsKit? {
        val rawId = intent.getStringExtra(KitCatalog.EXTRA_KIT_ID) ?: return null
        val kitId = runCatching { KitId.valueOf(rawId) }.getOrNull() ?: return null
        return KitCatalog.find(kitId)
    }
}
