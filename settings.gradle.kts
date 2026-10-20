pluginManagement {
    repositories {
        /*
         * ====================================================================
         * REPO PLUGIN HUAWEI (AGCONNECT) - PENAMBAHAN PR INI
         * ====================================================================
         * Plugin "com.huawei.agconnect" TIDAK dipublikasikan di Gradle Plugin
         * Portal maupun google(). Sumber resminya hanya Maven repository
         * Huawei. Tanpa baris ini, blok plugins{} di app/build.gradle.kts akan
         * gagal dengan "Plugin [id: 'com.huawei.agconnect'] was not found".
         * includeGroupByRegex membatasi resolusi HANYA untuk grup com.huawei.*
         * supaya artefak lain tetap diambil dari sumber resmi AndroidX/Google
         * (lebih cepat & aman dari dependency confusion).
         * ====================================================================
         */
        maven("https://developer.huawei.com/repo/") {
            content {
                includeGroupByRegex("com\\.huawei.*")
            }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        /*
         * ====================================================================
         * REPO RESMI HUAWEI MOBILE SERVICES
         * ====================================================================
         * Seluruh artefak HMS (com.huawei.hms:*) TIDAK dipublikasikan ke
         * mavenCentral maupun google(). Satu-satunya sumber resmi adalah Maven
         * repository Huawei di alamat di bawah. Tanpa baris ini, dependensi
         * Account Kit (hwid) tidak akan pernah selesai di-resolve oleh Gradle.
         *
         * Catatan: repo ini hanya perlu ditambahkan SEKALI di level settings;
         * semua modul (termasuk :app) otomatis mewarisinya karena mode
         * FAIL_ON_PROJECT_REPOS melarang tiap modul mendeklarasikan repo sendiri.
         * ====================================================================
         */
        maven("https://developer.huawei.com/repo/")
    }
}

rootProject.name = "Supa HW Kit"
include(":app")
