package com.kinyo.supahwkit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * KitCard - Satu "kartu menu" kit pada grid dashboard.
 * (Pengganti item_kit_menu.xml + KitMenuAdapter versi RecyclerView)
 * ============================================================================
 * KONSEP UTAMA COMPOSE:
 *  - Tidak ada ViewHolder / adapter / XML layout lagi. UI = fungsi yang
 *    menerima DATA (kit) sebagai parameter. Ganti data -> UI ikut berubah.
 *  - Modifier = "chaining properti" tampilan (padding, size, background...).
 *    Urutan modifier PENTING: `padding` sebelum `size` berarti jarak dihitung
 *    dari tepi luar; setelah `size` berarti jarak dari tepi dalam objek.
 *  - onClick dikirim dari parent (DashboardScreen), tidak di-hardcode di sini.
 *    Pola ini namanya "event up": komponen anak tidak tahu soal navigasi,
 *    parent yang memutuskan -> komponen jadi reusable & mudah di-test.
 * ============================================================================
 */
@Composable
fun KitCard(
    kit: HmsKit,                    // data satu kit yang akan dirender
    modifier: Modifier = Modifier,  // default: biarkan parent mengatur modifier
    onClick: (HmsKit) -> Unit       // callback saat card ditekan
) {
    /*
     * Surface = komponen Material3 yang sudah paham tema (warna container,
     * elevation/bayangan, bentuk sudut). Versi Surface yang menerima onClick
     * otomatis memberi efek ripple ketika ditekan.
     */
    Surface(
        modifier = modifier
            .fillMaxWidth(),                 // lebar penuh kolom grid
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,               // sedikit tint elevasi Material3
        shadowElevation = 3.dp,              // bayangan halus -> kesan kartu
        shape = MaterialTheme.shapes.large,  // radius besar khas M3
        onClick = { onClick(kit) }           // diteruskan ke parent
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)              // inner padding konten kartu
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp) // jarak antar anak
        ) {
            // ===== Baris atas: ikon bulat berwarna + panah kanan =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // ikon kiri, panah kanan
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*
                 * Brand color kit dibaca dari res/colors.xml lewat
                 * colorResource() lalu dijadikan latar Box bundar (CircleShape).
                 * copy(alpha = 0.12f) = versi transparan 12% agar ikon tetap fokus.
                 */
                val brandColor = colorResource(id = kit.colorRes)

                Box(
                    modifier = Modifier
                        .size(48.dp)                       // lingkaran 48dp
                        .background(
                            color = brandColor.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center    // ikon di tengah box
                ) {
                    /*
                     * painterResource(): memuat vector drawable lama (ic_map dll)
                     * ke dunia Compose -> aset XML tetap dipakai, tidak dibuang.
                     */
                    Icon(
                        painter = painterResource(id = kit.iconRes),
                        contentDescription = stringResource(id = kit.titleRes),
                        // contentDescription utk aksesibilitas (screen reader):
                        // TalkBack akan membacakan nama kit
                        tint = brandColor,                 // ikon ikut warna brand
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Panah kecil "->" menandakan kartu bisa ditekan/lanjut
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,  // null = dekoratif, abaikan screen reader
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ===== Judul kit =====
            Text(
                text = stringResource(id = kit.titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis  // potong dengan "..." bila panjang
            )

            // ===== Deskripsi singkat =====
            Text(
                text = stringResource(id = kit.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Preview = render langsung di panel Android Studio tanpa perlu emulator.
 * @Preview menjalankan fungsi ini di environment preview; karena parameter
 * hanya boleh nilai konstan, kita ambil contoh 2 kit dari KitCatalog.
 */
@Preview(showBackground = true)
@Composable
private fun KitCardPreview() {
    SupaHWKitTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KitCard(kit = KitCatalog.items.first(), onClick = {})   // Account
            KitCard(kit = KitCatalog.items[2], onClick = {})        // Map
        }
    }
}
