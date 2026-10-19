plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kinyo.supahwkit"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.kinyo.supahwkit"
        minSdk = 24
        targetSdk = 37
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
     * VIEW BINDING:
     * Ketika enable, build system otomatis men-generate satu class Binding per
     * file layout XML (activity_main.xml -> ActivityMainBinding, dst).
     * Class ini menyediakan properti type-safe ke setiap view ber-id, sehingga
     * kita tidak perlu findViewById() yang rawan typo & null.
     */
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    // RecyclerView: widget daftar/grid super efisien dengan mekanisme view recycling.
    // Dashboard kit memakai GridLayoutManager dari library ini.
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}