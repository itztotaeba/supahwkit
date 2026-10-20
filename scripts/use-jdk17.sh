#!/usr/bin/env bash
# ============================================================================
# use-jdk17.sh - HELPER KONFIGURASI LINGKUNGAN (bukan bagian aplikasi Android)
# ----------------------------------------------------------------------------
# FUNGSI:
#   Mencari JDK 17 yang terpasang di mesin ini, lalu menulis path-nya ke
#   `gradle/gradle-jdk.local.properties`. File itu dibaca gradle.properties
#   dan dipakai sebagai org.gradle.java.home -> Gradle daemon dipaksa jalan
#   di Java 17 MESKIPUN Android Studio mengatur "Gradle JDK" = Java 25.
#
# CARA PAKAI:
#   ./scripts/use-jdk17.sh                # deteksi otomatis & tulis config
#   ./scripts/use-jdk17.sh /path/jdk17    # pakai path spesifik
#
# KENAPA TIDAK HARDCODE SAJA DI gradle.properties?
#   Karena path JDK berbeda tiap mesin (Linux/macOS/Windows). File hasil
#   generate ini di-gitignore supaya tidak merusak mesin orang lain.
# ============================================================================
set -euo pipefail

# Pindah ke root repo (folder induk dari ./scripts) supaya path relatif aman
cd "$(dirname "$0")/.."

REQ_MAJOR=17 # Gradle 8.9 + AGP 8.7 + plugin AGConnect paling stabil di 17

# ---------------------------------------------------------------------------
# Fungsi: mengambil versi MAYOR dari sebuah java executable
# Contoh: "/usr/lib/jvm/java-17-openjdk-amd64/bin/java" -> "17"
# ---------------------------------------------------------------------------
java_major() {
  local bin="$1"
  [ -x "$bin" ] || return 1
  # java -version mencetak ke STDERR; handle pola Java 8 lama ("1.8.0_x")
  local v major minor
  v=$("$bin" -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+)(\.([0-9]+))?.*/\1 \3/')
  read -r major minor <<< "$v"
  if [ "$major" = "1" ]; then major="$minor"; fi
  echo "$major"
}

CANDIDATES=()
if [ $# -ge 1 ]; then
  # Argumen manual punya prioritas tertinggi
  CANDIDATES+=("$1")
else
  # Kandidat umum per platform:
  #  - Linux : /usr/lib/jvm/*            (openjdk, temurin, zulu)
  #  - macOS : /Library/Java/JavaVirtualMachines/*/Contents/Home, homebrew
  #  - SDKMAN: ~/.sdkman/candidates/java/*
  for d in /usr/lib/jvm/* \
           /Library/Java/JavaVirtualMachines/*/Contents/Home \
           "$HOME"/.sdkman/candidates/java/* \
           /opt/homebrew/opt/openjdk@17 \
           "${JAVA_HOME:-}"; do
    [ -n "$d" ] && [ -d "$d" ] && CANDIDATES+=("$d")
  done
fi

FOUND=""
for c in "${CANDIDATES[@]}"; do
  m=$(java_major "$c/bin/java" 2>/dev/null || true)
  if [ "$m" = "$REQ_MAJOR" ]; then FOUND="$c"; break; fi
done

if [ -z "$FOUND" ]; then
  echo "ERROR: JDK $REQ_MAJOR tidak ditemukan." >&2
  echo "Install dulu, misal: sudo apt install openjdk-17-jdk" >&2
  echo "atau download Temurin 17: https://adoptium.net" >&2
  exit 1
fi

mkdir -p gradle
{
  echo "# Di-generate oleh scripts/use-jdk17.sh - JANGAN di-commit (gitignored)."
  echo "# File ini mengunci Gradle daemon agar memakai JDK 17, memperbaiki error:"
  echo "#   'Incompatible Gradle JVM version' saat Android Studio memakai JBR 25."
  echo "org.gradle.java.home=$FOUND"
} > gradle/gradle-jdk.local.properties

echo "OK: Gradle akan memakai JDK $REQ_MAJOR -> $FOUND"
echo "Config ditulis ke: gradle/gradle-jdk.local.properties"
