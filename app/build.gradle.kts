/*
 * ============================================================================
 * plugins {} - blok paling atas build file: mendaftarkan "otak" pembangun modul.
 * ============================================================================
 * android-application : plugin resmi Gradle untuk modul aplikasi Android (APK).
 * kotlin-compose      : plugin Compose Compiler bawaan Kotlin 2.x.
 * kotlin-parcelize    : menggenerasi kode Parcelable otomatis dari @Parcelize.
 *      Cukup beri anotasi @Parcelize pada data class, plugin membuat isi
 *      CREATOR/writeToParcel() untuk kita (tanpa menulis boilerplate manual).
 * com.huawei.agconnect: NEW di PR ini -> plugin AppGallery Connect (agcp).
 *      Tugasnya saat build: membaca app/src/main/agconnect-services.json,
 *      menyuntikkannya ke resource APK, dan menuliskan meta-data
 *      com.huawei.hms.client.appid ke manifest hasil merge. TANPA plugin ini
 *      SDK HMS tidak menemukan App ID -> login gagal dengan error logcat:
 *      "Get client/app_id failed: FileNotFoundException agconnect-services.json".
 *      Letak: setelah android-application (plugin ini menempelkan task-nya
 *      ke pipeline AGP yang sudah terbentuk).
 * ============================================================================
 */
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.agcp) // com.huawei.agconnect - baca catatan di atas
}

android {
    namespace = "com.kinyo.supahwkit"
    // compileSdk 35: diturunkan dari API 37 karena AGP 8.7.3 belum mengenal
    // API level baru (lihat alasan lengkap di gradle/libs.versions.toml).
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kinyo.supahwkit"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // AGP 8.x memakai nama properti klasik: isMinifyEnabled/isShrinkResources.
            // (AGP 9 memakai DSL baru `optimization{}` - tidak tersedia di 8.7.)
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../src/main/keepRules/rules.keep"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

    /*
     * ======================================================================
     * HUAWEI ACCOUNT KIT (artefak: com.huawei.hms:hwid)
     * ======================================================================
     * Memberi akses ke HuaweiIdAuthService untuk:
     *   - signIn()      : membuka halaman login Huawei ID (consent screen)
     *   - silentSignIn(): mencoba restore sesi lama tanpa UI (auto-login)
     *   - signOut()     : mengakhiri sesi
     * Hasilnya berupa HuaweiIdAuthResult yang memuat profil pengguna
     * (openID, display name, avatar URI, dan unionID bila diminta).
     */
    implementation(libs.huawei.hwid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Test framework Compose: findNode/findChildByTestId untuk instrumented test UI.
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Manifest tambahan agar komponen test (AndroidComposeTestActivity) terdaftar
    // hanya pada build variant debug.
    debugImplementation(libs.androidx.ui.test.manifest)
}