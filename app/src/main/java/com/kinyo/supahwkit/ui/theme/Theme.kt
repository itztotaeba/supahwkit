package com.kinyo.supahwkit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * ============================================================================
 * Theme.kt - Tema Material 3 versi Jetpack Compose.
 * ============================================================================
 * PERBEDAAN PENTING dengan XML:
 *  - Dulu : tema didefinisikan di res/values/themes.xml (XML style) dan
 *           dipasang lewat AndroidManifest (android:theme).
 *  - Sekarang : tema adalah FUNGSI COMPOSABLE. Seluruh UI dibungkus
 *           SupaHWKitTheme { ... } -> semua komponen anak otomatis membaca
 *           warna/font dari MaterialTheme.colorScheme / typography di sini.
 *
 * @Composable = penanda "fungsi ini menggambar UI". Aturan utamanya: hanya
 * boleh dipanggil dari fungsi @Composable lain (karena Compose membangun
 * tree UI secara deklaratif, bukan imperatif seperti findViewById).
 * ============================================================================
 */

// ===== ColorScheme LIGHT mode =====
// colorScheme = "pasangan warna" yang saling kontras & lolos uji aksesibilitas.
// primary   : warna brand utama (merah Huawei) -> tombol, header, indikator
// onPrimary : warna teks/ikon DI ATAS primary (putih agar terbaca)
// surface   : latar card/komponen
// onSurface : teks utama; onSurfaceVariant: teks sekunder (muted)
private val LightColors = lightColorScheme(
    primary = HuaweiRed,
    onPrimary = SurfaceLight,
    primaryContainer = SoftRed,
    onPrimaryContainer = HuaweiRedDark,
    secondary = Teal,
    onSecondary = SurfaceLight,
    tertiary = Amber,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = MutedLight
)

// ===== ColorScheme DARK mode =====
// Di dark theme, warna brand dibuat lebih terang agar kontras di latar gelap.
private val DarkColors = darkColorScheme(
    primary = SoftRed,
    onPrimary = HuaweiRedDark,
    primaryContainer = HuaweiRedDark,
    onPrimaryContainer = SoftRed,
    secondary = Teal,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = MutedDark
)

/**
 * Fungsi tema utama aplikasi.
 *
 * @param darkTheme  : default ikut setting HP user (isSystemInDarkTheme()).
 *                    MainActivity bisa override bila ingin memaksa mode tertentu.
 * @param dynamicColor: "Material You" (Android 12+) - warna diambil dari
 *                    wallpaper HP user. Kita set false secara default karena
 *                    aplikasi edukasi ini ingin identitas brand Huawei konsisten.
 *                    Parameter tetap disediakan supaya user bisa mencoba.
 */
@Composable
fun SupaHWKitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit          // slot: seluruh UI anak tema
) {
    // Pilih skema warna final:
    val colorScheme = when {
        // Dynamic color butuh API 31+; cek Build.VERSION agar tidak crash
        // di HP lama. LocalContext = cara Compose mengakses Context Android.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    // Buat status bar transparan agar warna latar Compose menyatu dengannya.
    // Side effect seperti ini wajar di komposisi tema (bukan gambar UI murni).
    val view = LocalContext.current as? Activity
    view?.window?.statusBarColor = android.graphics.Color.TRANSPARENT

    // MaterialTheme = "provider" tema. Semua komposisi di dalam content()
    // akan membaca colorScheme + typography yang kita pass ke sini.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
