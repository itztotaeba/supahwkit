package com.kinyo.supahwkit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinyo.supahwkit.R
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.data.model.KitCatalog
import com.kinyo.supahwkit.ui.theme.SupaHWKitTheme

/**
 * ============================================================================
 * KitDetailScreen - Halaman demo per-kit (pengganti activity_kit_detail.xml).
 * ============================================================================
 * Struktur:
 *  Scaffold
 *   ├─ TopAppBar  : tombol back + nama kit
 *   └─ konten     : hero ikon kit -> deskripsi -> badge status -> SLOT demo
 *
 * SLOT DEMO (Box containerDemo) adalah tempat nanti kita menaruh implementasi
 * nyata tiap HMS Kit, contoh:
 *  - Map Kit   : AndroidView { MapView(...) }  -> view XML lama dibungkus Compose
 *  - Push Kit  : tombol "Get Token" + hasil token
 *  - Scan Kit  : tombol "Start Scan" -> kamera
 * Jadi kerangka tampilan ini tidak akan berubah; isinya yang bertambah.
 * ============================================================================
 */

// Opt-in needed karena TopAppBar masih berstatus Experimental di M3 stabil awal.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitDetailScreen(
    kit: HmsKit?,                 // nullable: antisipasi extra Intent rusak/hilang
    onNavigateBack: () -> Unit    // callback -> Activity memanggil finish()
) {
    // Guard clause: kalau data kit tidak valid, tampilkan layar error kecil.
    if (kit == null) {
        KitNotFoundScreen(onNavigateBack = onNavigateBack)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = kit.titleRes),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    /*
                     * IconButton = area sentuh bundar 48dp standar Material.
                     * onClick -> naik ke parent (Activity) yang mengeksekusi
                     * finish()/navController.popBackStack().
                     */
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // App bar memakai brand color kit -> identitas halaman langsung terasa
                    containerColor = colorResource(id = kit.colorRes),
                    titleContentColor = colorResource(R.color.white),
                    navigationIconContentColor = colorResource(R.color.white)
                )
            )
        }
    ) { innerPadding ->
        /*
         * verticalScroll(rememberScrollState()) membuat kolom panjang bisa
         * digulir - pengganti ScrollView di dunia XML. rememberScrollState()
         * menyimpan posisi gulir antar recomposition (state di-retain).
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
            // ===== HERO: lingkaran besar berisi ikon kit =====
            val brandColor = colorResource(id = kit.colorRes)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(brandColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = kit.iconRes),
                    contentDescription = null, // sudah diwakili judul app bar
                    tint = brandColor,
                    modifier = Modifier.size(52.dp)
                )
            }

            // ===== Tagline / kalimat pembuka fungsi kit =====
            Text(
                text = stringResource(id = kit.taglineRes),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            // ===== Badge status implementasi =====
            // Pill kecil memberi tahu progress belajar: belum/berjalan/selesai.
            StatusBadge(text = stringResource(R.string.status_not_implemented))

            // ===== SLOT DEMO KIT (nanti diisi MapView, tombol token, dst) =====
            DemoSlot(kit = kit)
        }
    }
}

/**
 * StatusBadge - pill informasi status (reuse untuk semua kit).
 * Menunjukkan pola komponen kecil yang composably dipakai berulang.
 */
@Composable
private fun StatusBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

/**
 * DemoSlot - placeholder area implementasi nyata.
 * Dikasih border putus-putus visual sederhana (surface variant) supaya jelas
 * ini "kotak kosong yang siap diisi". Nantinya isi Box ini diganti per-kit:
 *   when (kit.id) { KitId.MAP -> MapDemo(); KitId.PUSH -> PushDemo(); ... }
 */
@Composable
private fun DemoSlot(kit: HmsKit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.detail_not_implemented,
                stringResource(id = kit.titleRes)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}

/** Layar pengaman bila id kit dari Intent tidak dikenal. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KitNotFoundScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Text(
            text = stringResource(R.string.detail_kit_not_found),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        )
    }
}

/** Preview dengan data katalog asli (Account Kit sbg contoh). */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KitDetailScreenPreview() {
    SupaHWKitTheme {
        KitDetailScreen(kit = KitCatalog.items.first(), onNavigateBack = {})
    }
}
