package com.kinyo.supahwkit.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinyo.supahwkit.R
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.ui.components.KitCard
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * DashboardScreen - Halaman utama berisi grid 8 menu HMS Kit.
 * (Pengganti activity_main.xml + RecyclerView + GridLayoutManager versi View)
 * ============================================================================
 * Arsitektur screen ini:
 *  Scaffold ── TopAppBar ("Supa HW Kit")
 *          └─ LazyVerticalGrid (2 kolom)
 *               └─ KitCard x N   <- komponen dari ui/components/KitCard.kt
 *
 * Prinsip "stateless composable": screen ini TIDAK melakukan navigasi sendiri;
 * ia hanya menerima callback onKitSelected dari Activity. Activity-lah yang
 * tahu cara membuat Intent. Hasilnya: screen bisa dipakai ulang & di-preview.
 * ============================================================================
 */
@Composable
fun DashboardScreen(
    onKitSelected: (HmsKit) -> Unit     // event naik ke parent (MainActivity)
) {
    /*
     * Scaffold = kerangka halaman Material3: mengatur area app bar, konten,
     * FAB, snackbar dalam satu komponen. innerPadding yang diberikan ke
     * konten memastikan grid tidak tertutup app bar.
     */
    Scaffold(
        topBar = {
            /*
             * TopAppBar = app bar versi Compose (Material3).
             * centerAligned=false -> judul rata kiri seperti dashboard modern.
             * containerColor ikut warna primary tema (merah Huawei), teks putih.
             */
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)   // hormati jarak dari app bar
        ) {
            // ===== Sub-judul informatif + counter jumlah menu =====
            Text(
                text = stringResource(
                    id = R.string.dashboard_subtitle,
                    KitCatalog.items.size      // argumen %1$d format string
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            /*
             * LazyVerticalGrid = pengganti RecyclerView + GridLayoutManager.
             * "Lazy" artinya item hanya dibuat (composed) saat akan tampil di
             * layar - sama seperti mekanisme recycling pada RecyclerView, tapi
             * deklaratif: kita tidak menulis adapter sama sekali.
             *
             *  - GridCells.Adaptive(minSize = 160.dp):
             *      jumlah kolom dihitung otomatis dari lebar layar sehingga
             *      tiap kartu minimal 160dp. HP portrait -> 2 kolom, tablet ->
             *      3-4 kolom. Ini keunggulan dibanding fixed 2 kolom.
             *  - contentPadding + spacedBy -> gutter antar kartu.
             *  - items(list) -> iterasi katalog; tiap elemen jadi satu sel grid.
             */
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ekstensi items() dari paket lazy.grid: ubah List -> sel grid
                items(items = KitCatalog.items) { kit ->
                    KitCard(
                        kit = kit,
                        onClick = onKitSelected // tekan kartu -> panggil callback
                    )
                }
            }
        }
    }
}

/** Preview dashboard langsung di Android Studio (tanpa emulator). */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreview() {
    SupaHWKitTheme {
        DashboardScreen(onKitSelected = {})
    }
}
