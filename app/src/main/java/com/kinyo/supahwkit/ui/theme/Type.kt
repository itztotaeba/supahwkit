package com.kinyo.supahwkit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * Type.kt - Skala tipografi aplikasi (Material3 Typography).
 * ============================================================================
 * Di XML dulu, ukuran font ditulis per-TextView lewat android:textSize.
 * Di Compose kita definisikan SEKALI di objek `Typography` ini, lalu setiap
 * Text() tinggal menyebut style (misal headlineSmall) sehingga:
 *  - tampilan konsisten di seluruh aplikasi
 *  - ganti font global cukup edit satu file
 *
 * Struktur nama mengikuti konvensi Material Design 3:
 * display > headline > title > body > label, dengan varian Large/Medium/Small.
 * Aturan praktis pemakaian di proyek ini:
 *  - headlineSmall : judul besar dashboard ("Supa HW Kit")
 *  - titleLarge    : judul halaman detail kit
 *  - titleMedium   : judul di dalam card menu
 *  - bodyMedium    : deskripsi umum
 *  - bodySmall     : sub-teks / caption
 *  - labelLarge    : teks tombol
 * ============================================================================
 */
val Typography = Typography(

    // Judul utama dashboard
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),

    // Judul halaman detail kit
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),

    // Judul pada card menu grid
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),

    // Teks isi/deskripsi standar
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Caption / teks sekunder lebih kecil
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Label tombol & badge status
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
)
