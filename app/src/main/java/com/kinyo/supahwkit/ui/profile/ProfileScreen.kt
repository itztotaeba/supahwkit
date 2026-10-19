package com.kinyo.supahwkit.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonOff // ikon "akun belum login"
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinyo.supahwkit.R
import com.kinyo.supahwkit.data.model.UserProfile
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * ProfileScreen - Tampilan halaman profil hasil login Huawei Account Kit.
 * ============================================================================
 * PERAN BELAJAR:
 *  Halaman ini adalah "bukti visual" implementasi Account Kit: bila login
 *  sukses, data asli Huawei ID (nama, openID, avatar, email) tampil di sini.
 *  profile == null berarti tidak ada sesi -> tampilkan empty state.
 *
 * STRUKTUR KOMPOSISI (pola dekomposisi ala dokumentasi resmi Compose):
 *  Scaffold
 *   ├─ TopAppBar : judul + tombol back (callback ke Activity -> finish())
 *   └─ konten    : when(profile != null)
 *        ├─ ProfileContent(profile)  -> AvatarHeader + InfoCard rows
 *        └─ EmptyProfileState()      -> ilustrasi + pesan "belum login"
 *
 * KENAPA COMPOSABLE INI "STATELESS"?
 *  Tidak ada mutableStateOf di dalam; semua data masuk lewat parameter dan
 *  semua aksi keluar lewat callback. Efeknya: mudah di-preview, mudah dites,
 *  dan source of truth tetap di Activity/ViewModel.
 * ============================================================================
 */

// Opt-in: TopAppBar Material3 masih berstatus eksperimen API-nya.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile?,      // boleh null = belum ada sesi login
    onNavigateBack: () -> Unit, // aksi panah back -> Activity finish()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    /*
                     * IconButton = area sentuh bundar standar Material 48dp.
                     * onClick langsung meneruskan ke callback parent; screen
                     * ini tidak tahu-menahu soal finish()/navController -
                     * itulah pemisahan tanggung jawab UI vs navigasi.
                     */
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        /*
         * Modifier chain dibaca dari luar ke dalam:
         *  fillMaxSize() -> pakai seluruh layar
         *  .padding(innerPadding) -> sisakan ruang untuk app bar (dari Scaffold)
         * Kolom juga dibuat scrollable agar di HP kecil konten panjang tetap
         * bisa diakses (pengganti NestedScrollView di dunia XML).
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Percabangan utama: ada profil vs kosong.
            if (profile != null) {
                ProfileContent(profile = profile)
            } else {
                EmptyProfileState()
            }
        }
    }
}

/**
 * ProfileContent - daftar field identitas pengguna.
 * Dimulai dengan header avatar besar, lalu kartu berisi baris label-value.
 */
@Composable
private fun ProfileContent(profile: UserProfile) {
    // ===== AVATAR HEADER =====
    /*
     * Ujung-ujung sintaks yang perlu dicatat pemula:
     *  Box(...){ ... }  -> region komposisi; anak digambar bertumpuk di tengah.
     *  clip(CircleShape)-> memotong bentuk view jadi lingkaran.
     * Jika avatarUri tersedia kita TAMPILKAN indikator teks "Foto" + inisial
     * (demo ini sengaja tanpa library gambar eksternal seperti Coil agar fokus
     * ke Account Kit; upgrade ke AsyncImage dicatat sebagai TODO di README).
     */
    Box(
        modifier = Modifier
            .size(112.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = profile.initials, // turunan nama, misal "Budi Santoso" -> "BS"
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    // Nama lengkap + keterangan status (teks sukses hijau-style via tertiary).
    Text(
        text = profile.displayName ?: stringResource(R.string.account_no_name),
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = stringResource(R.string.profile_login_success),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(4.dp))

    // ===== KARTU DETAIL IDENTITAS =====
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            /*
             * Setiap InfoRow = satu baris "Label : nilai".
             * Field opsional (email/unionId) dilewati bila null memakai `?.let`
             * -> idiom Kotlin: jalankan blok hanya jika receiver bukan null.
             */
            InfoRow(
                label = stringResource(R.string.profile_label_openid),
                value = profile.openId ?: "-"
            )
            profile.unionId?.let { union ->
                InfoRow(
                    label = stringResource(R.string.profile_label_unionid),
                    value = union
                )
            }
            profile.email?.let { email ->
                InfoRow(
                    label = stringResource(R.string.profile_label_email),
                    value = email
                )
            }
            profile.avatarUri?.let { uri ->
                InfoRow(
                    label = stringResource(R.string.profile_label_avatar),
                    value = uri
                )
            }
        }
    }

    // Catatan edukatif di kaki halaman: apa arti openID vs unionID.
    Text(
        text = stringResource(R.string.profile_footer_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * InfoRow - satu baris label/value dalam kartu detail.
 * Contoh komponen kecil reusable (prinsip: komposisi > pewarisan).
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Label punya lebar tetap (weight) agar kolom nilai rapi sejajar.
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.32f)
        )
        /*
         * Nilai openID/unionID panjang -> font monospace supaya mudah dibaca
         * & terlihat "teknis". softWrap default true sudah memecah kata panjang.
         */
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.weight(0.68f)
        )
    }
    // Garis tipis pemisah antar baris (Divider di M3 = HorizontalDivider,
    // tapi cukup Spacer 1dp berwarna outline varian agar hemat dependensi).
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

/**
 * EmptyProfileState - tampil saat profile == null (tidak ada sesi valid).
 * Penting untuk membuktikan deteksi kegagalan: halaman tetap aman dibuka
 * tanpa crash, hanya menampilkan pesan "belum terdeteksi akun login".
 */
@Composable
private fun EmptyProfileState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PersonOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = stringResource(R.string.profile_empty_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.profile_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/*
 * ============================================================================
 * @Preview - pratinjau Android Studio (panel Design) tanpa menjalankan app.
 * Dua preview disiapkan: kondisi ADA profil & kondisi KOSONG, supaya kedua
 * cabang logika `if (profile != null)` bisa dicek secara visual saat coding.
 * Preview hanya dikompilasi pada build debug (ui-tooling), tidak masuk APK.
 * ============================================================================
 */
@Preview(showBackground = true, name = "Profile - Terisi")
@Composable
private fun ProfileScreenFilledPreview() {
    SupaHWKitTheme {
        ProfileScreen(
            profile = UserProfile(
                openId = "JD7YcA9zK1mVx0eQ-randomstring-demo",
                unionId = "CN_aVeryLongUnionIdForDemoPurposes",
                displayName = "Budi Santoso",
                avatarUri = "https://example.com/avatar.png",
                email = "budi@example.com"
            ),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile - Kosong")
@Composable
private fun ProfileScreenEmptyPreview() {
    SupaHWKitTheme {
        ProfileScreen(profile = null, onNavigateBack = {})
    }
}
