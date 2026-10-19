# Supa HW Kit 📱

Belajar **Huawei Mobile Services (HMS) Core Kits** lewat satu aplikasi Android all-in-one.
UI dibangun penuh dengan **Jetpack Compose (Material 3)** — tanpa layout XML.

Aplikasi ini adalah dashboard berisi 8 menu; setiap menu menuju halaman demo implementasi salah satu HMS Kit:

| # | Kit | Fungsi | Status |
|---|-----|--------|--------|
| 1 | Account Kit | Sign in dengan Huawei ID | 🔜 Dashboard ready, demo menyusul |
| 2 | Location Kit | Koordinat lokasi perangkat | 🔜 |
| 3 | Map Kit | Peta interaktif & marker | 🔜 |
| 4 | Push Kit | Notifikasi push server → device | 🔜 |
| 5 | Scan Kit | Barcode / QR scanner | 🔜 |
| 6 | Text Recognition Kit | OCR on-device | 🔜 |
| 7 | Safety Detect | Deteksi root/emulator/integritas | 🔜 |
| 8 | FIDO Biometric | Autentikasi sidik jari/wajah | 🔜 |

## Progress

- ✅ **Step 1 — Dashboard 8 menu** (RecyclerView + XML)
- ✅ **Step 1.5 — Full Jetpack Compose refactor** (branch `feature/compose-ui`)
  - `DashboardScreen` : grid adaptif `LazyVerticalGrid` + `TopAppBar` (pengganti RecyclerView/XML)
  - `KitCard` : komponen kartu kit deklaratif (pengganti adapter + item XML)
  - `KitDetailScreen` : shell halaman demo per-kit dengan slot `DemoSlot` siap diisi SDK
  - `ui/theme` : tema Material 3 (Color/Type/Theme.kt), dark mode + opsional dynamic color
  - `MainActivity`/`KitDetailActivity` : kini hanya *host* Compose (`setContent`), navigasi tetap via Intent
  - Katalog data (`KitCatalog`) tidak berubah → tambah kit baru tetap cukup 1 entri data
- ⬜ Step 2 — Setup HMS SDK (AGC config, appid meta-data, dependencies per kit)
- ⬜ Step 3+ — Implementasi demo tiap kit satu per satu di `DemoSlot`

## Arsitektur

```
com.kinyo.supahwkit
├── data/model        → HmsKit.kt (model + enum KitId + KitCatalog)
└── ui
    ├── theme         → Color.kt, Type.kt, Theme.kt (Material 3)
    ├── components    → KitCard.kt (komponen kartu reusable)
    ├── dashboard     → MainActivity (host) + DashboardScreen (grid menu)
    └── kit           → KitDetailActivity (host) + KitDetailScreen (shell demo)
```

Flow logika: **buka app → MainActivity.setContent → DashboardScreen render KitCatalog di LazyVerticalGrid → klik KitCard → callback ke Activity → Intent(EXTRA_KIT_ID) → KitDetailActivity resolve enum → KitDetailScreen tampilkan shell demo kit.**

> Seluruh kode diberi komentar penjelasan berbahasa Indonesia karena proyek ini dibuat untuk belajar — setiap baris penting punya alasan yang tertulis di kodenya.
