plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kinyo.supahwkit"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.kinyo.supahwkit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    /*
     * COMPOSE BUILD FEATURE:
     * Membangun pipeline compile khusus untuk fungsi @Composable.
     * Catatan: viewBinding sengaja DIHAPUS karena seluruh UI kini dibuat
     * dengan Jetpack Compose - tidak ada lagi file layout XML untuk activity.
     */
    buildFeatures {
        compose = true
    }
}

dependencies {
    /*
     * ======================================================================
     * BILL OF MATERIALS (BOM) COMPOSE
     * ======================================================================
     * androidx.compose:compose-bom menyelaraskan versi SEMUA library Compose
     * sekaligus. Keuntungan: kita menulis dependensi tanpa nomor versi dan
     * tidak akan terjadi konflik antar modul compose (foundation vs material3
     * vs ui selalu sepasang yang teruji kompatibel).
     */
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // ===== Core UI toolkit =====
    implementation(libs.androidx.ui)               // komponen dasar: Text, Image, Modifier
    implementation(libs.androidx.ui.graphics)      // rendering layer (canvas, shapes)
    // Tooling = dukungan @Preview di Android Studio (hanya saat develop).
    debugImplementation(libs.androidx.ui.tooling)
    // Implementasi renderer aktual saat runtime aplikasi berjalan.
    implementation(libs.androidx.ui.tooling.preview)

    // ===== Material Design 3 =====
    // Button, Card, Surface, Scaffold, TopAppBar, tema colorScheme, tipografi.
    implementation(libs.androidx.material3)
    // Ikon-ikon siap pakai (ArrowBack, KeyboardArrowRight, dst) sbg ImageVector.
    implementation(libs.androidx.material.icons.extended)

    // ===== Activity Compose =====
    // Menyediakan ComponentActivity.setContent{} -> jembatan Activity->Compose.
    implementation(libs.androidx.activity.compose)

    // ===== AndroidX dasar =====
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Test framework Compose: findNode/findChildByTestId untuk instrumented test UI.
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Manifest tambahan agar komponen test (AndroidComposeTestActivity) terdaftar
    // hanya pada build variant debug.
    debugImplementation(libs.androidx.ui.test.manifest)
}