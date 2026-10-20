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

        /*
         * ====================================================================
         * MAPPING PLUGIN ID -> KOORDINAT MAVEN (KRITIS!)
         * ====================================================================
         * Saat kita menulis `id("com.huawei.agconnect") version "1.9.1.300"`
         * di app/build.gradle.kts, Gradle secara default mencari artefak POM
         * "marker" dengan koordinat:
         *     com.huawei.agconnect : com.huawei.agconnect.gradle.plugin : 1.9.1.300
         * FAKTA: Huawei TIDAK mempublikasikan marker POM itu (cek manual ->
         * developer.huawei.com/repo/.../com.huawei.agconnect.gradle.plugin = 404).
         * Yang benar-benar ada hanyalah JAR plugin biasa:
         *     com.huawei.agconnect : agcp : 1.9.1.300
         * Solusi resmi: blok resolutionStrategy di bawah memetakan plugin id langsung ke artefak
         * agcp, sehingga error "Plugin [id: 'com.huawei.agconnect'] was not
         * found" hilang tanpa perlu mengubah cara penulisan di build file.
         * ====================================================================
         */
        resolutionStrategy {
            eachPlugin {
                if (requested.id.id == "com.huawei.agconnect") {
                    useModule("com.huawei.agconnect:agcp:${requested.version}")
                }
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

/*
 * ============================================================================
 * BUILDSCRIPT ROOT - TRICK "com.android.tools.build:gradle is no set"
 * ============================================================================
 * Plugin lama com.huawei.agconnect (agcp) TIDAK memakai mekanisme plugins{}
 * modern; ia mengharapkan AGP klasik tersedia di classpath buildscript-nya.
 * Tanpa ini, apply plugin gagal dengan pesan legendaris Huawei:
 *     "com.android.tools.build:gradle is no set in the build.gradle file"
 * Kita deklarasikan dependensi ke artefak `com.android.tools.build:gradle`
 * (yang implementasinya adalah AGP yang sama dengan plugin id
 * com.android.application) pada root project. Versi diambil dari catalog
 * supaya tetap sinkron dengan libs.versions.toml (single source of truth).
 * ============================================================================
 */
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
    }
}
