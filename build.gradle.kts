// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Deklarasikan plugin Kotlin & Compose di root agar versi terpusat
    // di libs.versions.toml (module app tinggal memakai `alias(...)`).
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}