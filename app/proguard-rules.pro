# ============================================================================
# proguard-rules.pro - aturan R8 untuk build release (minify).
# Saat ini isMinifyEnabled=false sehingga file ini hanya "siap pakai".
# Aturan Huawei standar: jangan hapus kelas AGConnect & HMS yang dipanggil
# secara refleksi oleh SDK saat runtime.
# ============================================================================
-keep class com.huawei.agconnect.** { *; }
-keep class com.huawei.hms.** { *; }
-dontwarn com.huawei.**
