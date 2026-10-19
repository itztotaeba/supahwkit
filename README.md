# Supa HW Kit 🇨🇳➡️📱

Belajar **Huawei Mobile Services (HMS) Core Kits** lewat satu aplikasi Android all-in-one.

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

- ✅ **Step 1 — Dashboard 8 menu** (branch `feature/dashboard-hms-menus`)
  - `MainActivity` : grid 2 kolom RecyclerView + MaterialCardView
  - `KitMenuAdapter` : render card dari katalog kit
  - `KitCatalog` : single source of truth daftar kit (tambah kit baru = tambah 1 baris data)
  - `KitDetailActivity` : kerangka halaman demo per-kit (routing via Intent extra `EXTRA_KIT_ID`)
- ⬜ Step 2 — Setup HMS SDK (AGC config, appid meta-data, dependencies per kit)
- ⬜ Step 3+ — Implementasi demo tiap kit satu per satu

## Arsitektur

```
com.kinyo.supahwkit
├── data/model        → HmsKit.kt (model + KitCatalog)
└── ui
    ├── dashboard     → MainActivity (launcher/grid menu)
    ├── adapter       → KitMenuAdapter (RecyclerView adapter)
    └── kit           → KitDetailActivity (shell halaman demo)
```

Flow logika: **buka app → MainActivity render KitCatalog → klik card → Intent(EXTRA_KIT_ID) → KitDetailActivity resolve enum → tampilkan shell demo kit.**

> Seluruh kode diberi komentar penjelasan berbahasa Indonesia karena proyek ini memang dibuat untuk belajar — setiap baris penting punya alasan yang tertulis di kodenya.
