package com.kinyo.supahwkit.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ============================================================================
 * Color.kt - Palet warna versi Jetpack Compose.
 * ============================================================================
 * Di Compose, warna TIDAK lagi didefinisikan lewat res/values/colors.xml untuk
 * kebutuhan tema, melainkan sebagai konstanta `Color(r, g, b)` di kode Kotlin.
 * Format: Color(red, green, blue) dengan nilai 0..255 per kanal.
 *
 * PALET: mengikuti identitas brand Huawei (merah #C7000B sebagai aksen utama)
 * dipadukan netral abu terang untuk latar dashboard.
 *
 * Kenapa hex 8 digit? Format ARGB: AA=alpha(opasitas), RR=GGBB warna.
 * FF di depan = penuh/tidak transparan.
 * ============================================================================
 */

// ===== Warna primer (brand Huawei merah) =====
val HuaweiRed = Color(0xFFC7000B)        // primary light
val HuaweiRedDark = Color(0xFF8E0008)    // varian gelap (pressed/dark theme)
val SoftRed = Color(0xFFFFB4AB)          // aksen terang di dark theme

// ===== Warna sekunder & tertiary (pendamping agar UI tidak satu warna) =====
val Teal = Color(0xFF00897B)             // secondary: kesan teknis/developer
val Amber = Color(0xFFF5A623)            // tertiary: highlight status/badge

// ===== Warna struktural light mode =====
val BackgroundLight = Color(0xFFF6F5F8)  // latar halaman dashboard
val SurfaceLight = Color(0xFFFFFFFF)     // latar card / sheet
val OnSurfaceLight = Color(0xFF1B1B1F)   // teks utama di atas surface
val MutedLight = Color(0xFF6C6C75)       // teks sekunder (description)

// ===== Warna struktural dark mode =====
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E22)
val OnSurfaceDark = Color(0xFFE4E4E9)
val MutedDark = Color(0xFFA9A9B2)

/*
 * ===== BRAND COLOR PER KIT =====
 * Dipakai oleh KitCard/KitDetailScreen lewat painterResource/colorResource
 * dari res/values/colors.xml (tetap dipakai karena disimpan di model HmsKit
 * sebagai Int resource ID). Fungsi kitBrandColor() di Color.kt lain file
 * memetakan resource tsb -> objek Compose Color bila dibutuhkan.
 */
val KitAccountColor = Color(0xFF4A90D9)  // biru   : identitas/akun
val KitLocationColor = Color(0xFFE94E45) // merah  : pin lokasi
val KitMapColor = Color(0xFF34A853)      // hijau  : peta
val KitPushColor = Color(0xFFF5A623)     // oranye : notifikasi
val KitScanColor = Color(0xFF7B61FF)     // ungu   : scanner
val KitTextColor = Color(0xFF00B8A9)     // teal   : OCR
val KitSafetyColor = Color(0xFFD35400)   // jingga : keamanan
val KitFidoColor = Color(0xFF2C3E50)     // navy   : biometrik
