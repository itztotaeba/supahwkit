package com.kinyo.supahwkit.ui.account

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult // launcher hasil "start for result" ramah Compose
import androidx.activity.result.contract.ActivityResultContracts   // contract StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kinyo.supahwkit.R
import com.kinyo.supahwkit.data.model.UserProfile
import com.kinyo.supahwkit.data.remote.AuthOutcome
import com.kinyo.supahwkit.data.remote.HuaweiAccountService

/**
 * ============================================================================
 * AccountUiState - seluruh kemungkinan kondisi tampilan halaman Account Kit.
 * ============================================================================
 * Pola "state machine" eksplisit mencegah bug UI klasik seperti spinner tampil
 * bersamaan dengan pesan error. Setiap cabang punya representasi data sendiri,
 * sehingga `when(state)` di bawah selalu lengkap dan mudah dibaca.
 * ============================================================================
 */
private sealed interface AccountUiState {
    object Idle : AccountUiState                 // belum login, siap tekan tombol
    object Loading : AccountUiState              // proses silent sign-in berjalan
    data class SignedIn(val profile: UserProfile) : AccountUiState
    data class Error(val message: String) : AccountUiState
}

/**
 * ============================================================================
 * AccountKitScreen - demo interaktif Huawei Account Kit (Jetpack Compose).
 * ============================================================================
 * DIPASANG DI: KitDetailActivity ketika kit.id == KitId.ACCOUNT.
 *
 * ALUR LOGIKA LENGKAP (flow belajar):
 *
 *   [Saat pertama dibuka]
 *     LaunchedEffect(Unit) -> service.trySilentSignIn()
 *        +- sesi lama ditemukan  -> SignedIn(profile)   (auto-login!)
 *        +- tidak ada sesi       -> Idle                (tampil tombol login)
 *
 *   [User menekan "Masuk dengan Huawei ID"]
 *     1. service.buildSignInIntent(activity)            -> Intent halaman login
 *     2. signInLauncher.launch(intent)                  -> sistem buka HMS login
 *     3. user selesai/batal -> callback GetResult(resultCode, data)
 *     4. service.parseSignInResult(code, data) { outcome ->
 *          Success  -> SignedIn(profile) + onLoginSuccess(profil) [navigasi]
 *          Canceled -> Idle       (user batal, bukan error)
 *          Failure  -> Error(pesan bahasa Indonesia)
 *        }
 *
 *   [User menekan "Keluar"]
 *     service.signOut(activity) { ok -> uiState = Idle }
 *
 * KENAPA LAUNCHER DIBUAT DI LEVEL COMPOSABLE (bukan dalam onClick)?
 *  rememberLauncherForActivityResult WAJIB dipanggil saat composition karena
 *  Android harus mendaftarkan request code SEBELUM hasil datang dari activity
 *  login. Jika dibuat di dalam lambda klik, pendaftaran terlalu terlambat dan
 *  aplikasi crash.
 * ============================================================================
 */
@Composable
fun AccountKitScreen(
    activity: Activity,                       // host untuk API berbasis Activity
    onLoginSuccess: (UserProfile) -> Unit,    // callback navigasi ke halaman profil
) {
    // ===== State halaman. mutableStateOf = "observable" versi Compose =====
    // Initial value Loading: begitu layar muncul kita langsung cek sesi tersimpan.
    var uiState by remember { mutableStateOf<AccountUiState>(AccountUiState.Loading) }

    // ===== Service pembungkus Account Kit (dibuat sekali per composition) =====
    val service = remember { HuaweiAccountService(activity) }

    /*
     * Launcher hasil login. Contract StartActivityForResult dipakai karena
     * halaman Huawei ID mengembalikan Intent biasa (bukan permission dialog).
     * Lambda di bawah dipanggil SISTEM setelah activity login finish().
     */
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Parsing didelegasikan ke service; hasilnya dipetakan ke state UI.
        service.parseSignInResult(result.resultCode, result.data) { outcome ->
            when (outcome) {
                is AuthOutcome.Success -> {
                    uiState = AccountUiState.SignedIn(outcome.profile)
                    // Langsung buka halaman profil sebagai bukti login berhasil.
                    onLoginSuccess(outcome.profile)
                }

                is AuthOutcome.Canceled -> {
                    // Pembatalan sadar user: kembali idle tanpa pesan error.
                    uiState = AccountUiState.Idle
                }

                is AuthOutcome.Failure -> {
                    uiState = AccountUiState.Error(outcome.message)
                }
            }
        }
    }

    /*
     * LaunchedEffect(Unit) = "jalankan SEKALI saat composable masuk komposisi".
     * Kunci Unit berarti efek tidak diulang pada recomposition biasa.
     * Di sinilah silent sign-in dicoba otomatis agar user lama tidak perlu
     * mengetik password dua kali (UX auto-login).
     */
    LaunchedEffect(Unit) {
        service.trySilentSignIn(activity) { outcome ->
            uiState = when (outcome) {
                is AuthOutcome.Success -> AccountUiState.SignedIn(outcome.profile)
                is AuthOutcome.Canceled -> AccountUiState.Idle
                is AuthOutcome.Failure -> AccountUiState.Error(outcome.message)
            }
        }
    }

    // ===== Render sesuai state =====
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val state = uiState) {

            AccountUiState.Loading -> {
                /*
                 * Progress tak berputar penuh (indeterminate) sederhana:
                 * spinner + teks "memeriksa sesi...".
                 */
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
                Text(
                    text = stringResource(R.string.account_checking_session),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AccountUiState.Idle -> NotSignedInCard(
                onSignInClick = {
                    // LANGKAH [1]-[2]: buat intent login lalu luncurkan.
                    val intent = service.buildSignInIntent(activity)
                    signInLauncher.launch(intent)
                }
            )

            is AccountUiState.SignedIn -> SignedInCard(
                profile = state.profile,
                onOpenProfile = { onLoginSuccess(state.profile) },
                onSignOut = {
                    service.signOut(activity) { _ ->
                        uiState = AccountUiState.Idle
                    }
                }
            )

            is AccountUiState.Error -> ErrorCard(
                message = state.message,
                onRetry = {
                    // Ulangi dari awal: tampilkan loading lalu silent sign-in lagi.
                    uiState = AccountUiState.Loading
                    service.trySilentSignIn(activity) { outcome ->
                        uiState = when (outcome) {
                            is AuthOutcome.Success -> AccountUiState.SignedIn(outcome.profile)
                            is AuthOutcome.Canceled -> AccountUiState.Idle
                            is AuthOutcome.Failure -> AccountUiState.Error(outcome.message)
                        }
                    }
                }
            )
        }
    }
}

/**
 * Kartu kondisi BELUM login: penjelasan singkat + tombol besar sign-in.
 * Dipisah jadi composable kecil agar mudah dibaca & dipakai di preview.
 */
@Composable
private fun NotSignedInCard(onSignInClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.account_not_signed_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.account_not_signed_desc),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAddAlt1,
                    contentDescription = null // sudah dijelaskan teks tombol
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.account_btn_sign_in))
            }
        }
    }
}

/**
 * Kartu kondisi SUDAH login: ringkasan identitas + aksi lanjut.
 * Avatar memakai placeholder inisial (profile.initials) supaya tidak perlu
 * library gambar eksternal; upgrade ke Coil AsyncImage bisa ditambahkan nanti.
 */
@Composable
private fun SignedInCard(
    profile: UserProfile,
    onOpenProfile: () -> Unit,
    onSignOut: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Lingkaran avatar berinisial (contoh Box + clip(CircleShape)).
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.initials,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = profile.displayName ?: stringResource(R.string.account_no_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        // openID disingkat (12 char + ellipsis) agar baris rapi.
                        text = profile.openId?.take(12)?.plus("…")
                            ?: stringResource(R.string.account_no_openid),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Dua aksi berdampingan: detail profil & keluar. weight(1f) bagi rata.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.account_btn_view_profile)) }

                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.account_btn_sign_out))
                }
            }
        }
    }
}

/** Kartu pesan error + tombol coba lagi (mengulang silent sign-in). */
@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            OutlinedButton(onClick = onRetry) {
                Text(stringResource(R.string.account_btn_retry))
            }
        }
    }
}
