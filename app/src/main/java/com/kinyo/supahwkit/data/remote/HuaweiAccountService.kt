package com.kinyo.supahwkit.data.remote

import android.app.Activity
import android.content.Context
import android.content.Intent

/*
 * ============================================================================
 * IMPORT HUAWEI ACCOUNT KIT (artefak com.huawei.hms:hwid) - PENJELASAN PER KELAS
 * ============================================================================
 * Semua kelas com.huawei.hms.* / com.huawei.hmf.* di bawah baru bisa diimpor
 * setelah dependensi `implementation(libs.huawei.hwid)` ditambahkan di
 * app/build.gradle.kts DAN repo maven Huawei ditambahkan di settings.gradle.kts.
 *
 * 1. HuaweiIdAuthManager      -> "pabrik" resmi Account Kit. Tugasnya:
 *      - getService(ctx, params)        : buat instance service login
 *      - parseAuthResultFromIntent()    : baca hasil dari Intent balikan login
 *      - getAuthResult()                : ambil akun yang tersimpan (cache lokal)
 * 2. HuaweiIdAuthParams       -> objek konfigurasi scope + flag permintaan data.
 * 3. HuaweiIdAuthParamsHelper -> builder untuk menyusun HuaweiIdAuthParams.
 *      Method yang kita pakai: setId(), setProfile(), setEmail(), createParams().
 * 4. HuaweiIdAuthService      -> layanan dengan aksi inti:
 *      signInIntent (properti), silentSignIn(), signOut().
 * 5. AuthHuaweiId             -> representasi akun Huawei ID yang berhasil
 *      diautentikasi: openId, unionId, displayName, avatarUriString, email,
 *      authorizationCode (getter- getter Java terlihat sebagai properti Kotlin).
 * 6. Task<T> (com.huawei.hmf.tasks) -> mekanisme async ala Google Play Services;
 *      kita pasang addOnSuccessListener / addOnFailureListener / onComplete.
 * ============================================================================
 */
import com.huawei.hmf.tasks.Task
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.kinyo.supahwkit.data.model.UserProfile

/**
 * ============================================================================
 * AuthOutcome - tipe hasil seragam untuk SEMUA kejadian autentikasi.
 * ============================================================================
 * Kenapa sealed class (bukan enum)? Setiap cabang bisa membawa payload berbeda:
 *   - Success  : membawa UserProfile hasil ekstraksi SDK
 *   - Failure  : membawa kode + pesan error siap tampilkan
 *   - Canceled : user menekan tombol kembali / belum ada sesi
 *
 * Keunggulan sealed: saat dipakai di `when(outcome) { ... }`, Kotlin memaksa
 * kita menangani semua cabang -> tidak ada state login yang terlewat di UI.
 * ============================================================================
 */
sealed class AuthOutcome {

    /** Login berhasil; profil sudah diekstrak dari hasil SDK. */
    data class Success(val profile: UserProfile) : AuthOutcome()

    /** Login/restore gagal; message = penjelasan manusiawi (bukan kode mentah). */
    data class Failure(val code: Int, val message: String) : AuthOutcome()

    /** Pengguna membatalkan layar login, atau memang belum pernah login. */
    object Canceled : AuthOutcome()
}

/**
 * ============================================================================
 * HuaweiAccountService - Wrapper Account Kit agar API kecil, rapi & mudah diuji.
 * ============================================================================
 * TUGAS UTAMA
 *  Menyembunyikan kerumitan SDK Huawei di belakang empat fungsi sederhana:
 *    1. buildSignInIntent(activity)          : siapkan Intent halaman login
 *    2. parseSignInResult(code, data, cb)    : terjemahkan Intent hasil
 *    3. trySilentSignIn(activity, cb)        : restore sesi tanpa UI
 *    4. signOut(activity, cb)                : akhiri sesi aktif
 *
 * KENAPA DIBUNGKUS?
 *  - Lapisan UI (Compose) hanya mengenal tipe milik kita (UserProfile/AuthOutcome);
 *    impor kelas SDK tidak tersebar ke banyak file.
 *  - Jika versi API Huawei berubah, hanya file ini yang perlu disentuh.
 *  - Memudahkan testing: UI test bisa menyuntik fake service.
 *
 * PENTING - ALUR LOGIN ITU ASINKRON & TERPUTUS (model "Intent"):
 *  Account Kit memakai pola Activity-based auth mirip OAuth authorization code:
 *
 *     [1] service.signInIntent             -> diluncurkan via launcher Compose
 *     [2] user login di halaman Huawei     -> (proses terjadi DI LUAR aplikasi kita)
 *     [3] onActivityResult(code, data)     -> parseAuthResultFromIntent(data)
 *
 *  Tidak ada satu callback langsung; hasil datang sebagai Intent kedua. Karena
 *  itu "buat intent" dan "baca intent" adalah dua langkah terpisah.
 * ============================================================================
 */
class HuaweiAccountService(private val context: Context) {

    /**
     * Konfigurasi scope = daftar data yang kita minta kepada Huawei ID.
     *
     * Penjelasan tiap flag builder:
     *  - setId()      : minta openID (+unionID bila tersedia). Wajib untuk login.
     *  - setProfile() : minta display name + foto avatar.
     *  - setEmail()   : minta alamat email (opsional; pengguna boleh menolak).
     *
     * Prinsip "least privilege": minta sekecil mungkin. Menambah scope membuat
     * layar consent lebih panjang dan proses review di AppGallery Connect lebih
     * ketat (Huawei memeriksa urgensi tiap scope).
     *
     * `by lazy` = properti baru diinisialisasi saat pertama diakses, sehingga
     * startup aplikasi tidak membayar biaya pembuatan objek yang belum tentu dipakai.
     */
    private val authParams: HuaweiIdAuthParams by lazy {
        /*
         * HuaweiIdAuthParamsHelper dengan DEFAULT_AUTH_REQUEST_PARAM = mulai dari param default;
         * kita susun manual lewat helper. createParams() membekukan hasil builder menjadi objek param.
         */
        HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setId()
            .setProfile()
            .setEmail()
            .createParams()
    }

    /**
     * Buat instance service auth yang menempel pada Activity pemanggil.
     *
     * Kenapa harus Activity (bukan Context biasa)? Halaman login Huawei ID
     * dijalankan sebagai anak window dari activity tersebut supaya sistem bisa
     * mengembalikan Intent hasil ke pemanggil awal.
     */
    private fun authService(activity: Activity): HuaweiIdAuthService =
        HuaweiIdAuthManager.getService(activity, authParams)

    /**
     * KONVERSI: objek SDK (AuthHuaweiId) -> model bersih aplikasi (UserProfile).
     *
     * Fungsi ekstensi (extension function) Kotlin: menambah "method" ke kelas
     * pihak ketiga tanpa subclassing. Getter Java otomatis terlihat seperti
     * properti: java account.getOpenId() == kotlin account.openId.
     */
    private fun AuthHuaweiId.toUserProfile(): UserProfile = UserProfile(
        openId = openId,                                             // id utama utk app ini
        unionId = unionId,                                           // id lintas-app satu developer
        displayName = displayName,                                   // nama tampilan
        avatarUri = avatarUriString?.takeIf { it.isNotBlank() },     // foto profil (String URL)
        email = email,                                               // null jika scope ditolak user
        authorizationCode = authorizationCode,                       // utk verifikasi di server nanti
    )

    /**
     * LANGKAH [1]: Siapkan Intent menuju halaman login Huawei ID.
     *
     * Pemanggil (Compose screen) meneruskan intent ini ke ActivityResultLauncher.
     * Kita sengaja TIDAK memanggil startActivity di sini karena launcher wajib
     * dikelola oleh komposisi UI (lifecycle-aware), bukan oleh service.
     */
    fun buildSignInIntent(activity: Activity): Intent =
        authService(activity).signInIntent

    /**
     * LANGKAH [3]: Terjemahkan resultCode+data Intent menjadi AuthOutcome.
     *
     * Alur internal:
     *  1. Kalau Android melaporkan bukan RESULT_OK -> anggap Canceled.
     *  2. HuaweiIdAuthManager.parseAuthResultFromIntent(data) mengekstrak
     *     payload HMS dari extras Intent; mengembalikan Task<AuthHuaweiId>.
     *  3. onSuccess -> konversi ke UserProfile -> Success.
     *     onFailure -> petakan kode error -> Failure(pesan Indonesia).
     *
     * Listener Task dipakai (bukan blocking await) agar tetap ringan di main
     * thread; SDK menjamin callback dikirim ke thread utama caller.
     */
    fun parseSignInResult(
        resultCode: Int,
        data: Intent?,
        onResult: (AuthOutcome) -> Unit,
    ) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            onResult(AuthOutcome.Canceled)
            return
        }

        val task: Task<AuthHuaweiId> = HuaweiIdAuthManager.parseAuthResultFromIntent(data)

        task.addOnSuccessListener { account ->
            /*
             * Kadang task sukses tapi profil tidak lengkap (openID kosong).
             * Null-check defensif -> ubah jadi Failure informatif, bukan crash.
             */
            if (account == null || account.openId.isNullOrEmpty()) {
                onResult(
                    AuthOutcome.Failure(
                        code = AUTH_ERR_EMPTY_ACCOUNT,
                        message = describeError(AUTH_ERR_EMPTY_ACCOUNT, null)
                    )
                )
            } else {
                onResult(AuthOutcome.Success(account.toUserProfile()))
            }
        }.addOnFailureListener { e ->
            /*
             * Throwable dari SDK biasanya bertipe ApiException dengan method
             * getStatusCode(). Refleksi aman dibungkus runCatching supaya
             * struktur exception tak terduga tetap menghasilkan kode fallback.
             */
            val code = runCatching {
                e.javaClass.getMethod("getStatusCode").invoke(e) as Int
            }.getOrDefault(AUTH_ERR_UNKNOWN)
            onResult(AuthOutcome.Failure(code, describeError(code, e.message)))
        }
    }

    /**
     * SILENT SIGN-IN (auto-login): coba restore sesi sebelumnya TANPA UI.
     *
     * Berguna bila user pernah login dan belum sign-out: aplikasi langsung
     * menampilkan profil tanpa bertanya lagi. Bila tidak ada sesi, hasilnya
     * Canceled -> UI menampilkan tombol "Sign in with Huawei".
     */
    fun trySilentSignIn(activity: Activity, onResult: (AuthOutcome) -> Unit) {
        authService(activity)
            .silentSignIn() // Task<AuthHuaweiId>
            .addOnSuccessListener { account ->
                if (account == null || account.openId.isNullOrEmpty()) {
                    // Tidak ada sesi tersimpan: bukan error fatal.
                    onResult(AuthOutcome.Canceled)
                } else {
                    onResult(AuthOutcome.Success(account.toUserProfile()))
                }
            }
            .addOnFailureListener { e ->
                val code = runCatching {
                    e.javaClass.getMethod("getStatusCode").invoke(e) as Int
                }.getOrDefault(AUTH_ERR_UNKNOWN)
                // Silent sign-in gagal itu NORMAL (belum pernah login) -> Canceled.
                onResult(
                    if (code == AUTH_ERR_NO_ACTIVE_SESSION) AuthOutcome.Canceled
                    else AuthOutcome.Failure(code, describeError(code, e.message))
                )
            }
    }

    /**
     * SIGN OUT: cabut sesi aktif pada service.
     *
     * signOut() mengembalikan Task<Void>; onComplete selalu dipanggil baik
     * sukses maupun gagal, jadi cocok untuk membersihkan state UI di satu tempat.
     */
    fun signOut(activity: Activity, onDone: (Boolean) -> Unit) {
        authService(activity)
            .signOut()
            .addOnCompleteListener { _ ->
                // Apa pun hasilnya, UI wajib kembali ke mode "belum login".
                onDone(true)
            }
    }

    /**
     * Cek cepat keberadaan sesi tersimpan TANPA memanggil network.
     * getAuthResult() membaca cache lokal Account Kit; null = belum login.
     * Dipakai ProfileActivity saat dibuka langsung dari dashboard.
     */
    fun cachedProfile(): UserProfile? =
        HuaweiIdAuthManager.getAuthResult()?.toUserProfile()

    /**
     * Petakan kode status HMS menjadi pesan bahasa Indonesia yang ramah user.
     * Nilai konstanta mengikuti AuthErrorCode dokumentasi resmi HMS Core.
     */
    private fun describeError(code: Int, raw: String?): String = when (code) {
        AUTH_ERR_NO_ACTIVE_SESSION -> "Belum ada sesi Huawei ID aktif di perangkat ini."
        AUTH_ERR_CANCELED -> "Anda membatalkan proses login."
        AUTH_ERR_APP_NOT_REGISTERED -> "Aplikasi belum terdaftar benar di AppGallery Connect (cek appId & agconnect-services.json)."
        AUTH_ERR_SERVICE_UNAVAILABLE -> "Layanan HMS tidak tersedia; pastikan HMS Core terpasang & diperbarui."
        AUTH_ERR_EMPTY_ACCOUNT -> "Login sukses tetapi profil kosong (openID tidak dikembalikan)."
        AUTH_ERR_NETWORK -> "Kesalahan jaringan; periksa koneksi lalu coba lagi."
        else -> raw ?: "Autentikasi gagal (kode $code)."
    }

    companion object {
        /** Request code untuk launcher login; angka bebas asal unik dalam activity. */
        const val REQUEST_CODE_SIGN_IN = 1001

        // ---- Kode error yang sering muncul (subset AuthErrorCode HMS) ----
        const val AUTH_ERR_UNKNOWN = -1                  // fallback umum
        const val AUTH_ERR_NO_ACTIVE_SESSION = 10010000  // tidak ada sesi tersimpan
        const val AUTH_ERR_CANCELED = 10010001           // user batal
        const val AUTH_ERR_APP_NOT_REGISTERED = 10010003 // appId AGC salah
        const val AUTH_ERR_SERVICE_UNAVAILABLE = 10010009// HMS Core tak tersedia
        const val AUTH_ERR_EMPTY_ACCOUNT = 10010100      // sukses tapi profil kosong
        const val AUTH_ERR_NETWORK = 6002                // masalah transport
    }
}
