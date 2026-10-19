pluginManagement {
    repositories {
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
