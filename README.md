# Supa HW Kit 📱

Belajar **Huawei Mobile Services (HMS) Core Kits** lewat satu aplikasi Android all-in-one.
UI dibangun penuh dengan **Jetpack Compose (Material 3)** — tanpa layout XML.

Aplikasi ini adalah dashboard berisi 8 menu; setiap menu menuju halaman demo implementasi salah satu HMS Kit:

| # | Kit | Fungsi | Status |
|---|-----|--------|--------|
| 1 | Account Kit | Sign in dengan Huawei ID | ✅ Login + halaman profil (PR ini) |
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
- ✅ **Step 3 — Huawei Account Kit** (PR ini)
  - Dependensi `com.huawei.hms:hwid` + repo maven Huawei (`settings.gradle.kts`)
  - `HuaweiAccountService` : pembungkus API SDK (signIn intent, silent sign-in,
    parse hasil, sign out, cache sesi) — UI tidak menyentuh SDK langsung
  - `AccountKitScreen` : state machine login (Idle/Loading/SignedIn/Error) dengan
    `rememberLauncherForActivityResult` ala activity-result modern
  - `UserProfile` kini `@Parcelize` (plugin `kotlin-parcelize`) → dikirim utuh
    antar Activity lewat Intent extra
  - **Halaman Profil** (`ProfileActivity` + `ProfileScreen`) = bukti visual login:
    menampilkan displayName, OpenID, UnionID, email & avatar URI asli dari
    Huawei ID; empty state aman bila dibuka tanpa sesi
  - Routing slot demo: `KitDetailScreen(demoContent = ...)` diisi per-kit —
    ACCOUNT → AccountKitScreen, kit lain tetap placeholder
  - Badge status halaman detail kini dinamis dari `kit.status` (DONE/PARTIAL/PLANNED)
  - ⚠️ Saat runtime: ganti/fill `agconnect-services.json` + `appid` dari proyek
    AGC milikmu, dan aktifkan *Account Kit* di konsol, agar login nyata muncul
- ⬜ Step 4+ — Implementasi demo kit berikutnya satu per satu di slot `demoContent`

## Arsitektur

```
com.kinyo.supahwkit
├── data
│   ├── model     → HmsKit.kt (model + enum KitId + KitCatalog), UserProfile.kt (@Parcelize)
│   └── remote    → HuaweiAccountService.kt (pembungkus HMS hwid: scopes, Task->outcome)
└── ui
    ├── theme      → Color.kt, Type.kt, Theme.kt (Material 3)
    ├── components → KitCard.kt (komponen kartu reusable)
    ├── dashboard  → MainActivity (host) + DashboardScreen (grid menu)
    ├── kit        → KitDetailActivity (host+routing) + KitDetailScreen (shell demo)
    ├── account    → AccountKitScreen.kt (demo login Huawei ID)
    └── profile    → ProfileActivity (host) + ProfileScreen (bukti hasil login)
```

Flow logika Account Kit: **Dashboard → klik "Account Kit" → KitDetailActivity mendeteksi `KitId.ACCOUNT` → render `AccountKitScreen` di slot demo → tombol masuk → `service.buildSignInIntent()` → launcher halaman Huawei ID → hasil di-`parse` service → `AuthOutcome.Success(profile)` → `onLoginSuccess` → `ProfileActivity` (Intent Parcelable) → `ProfileScreen` tampilkan identitas asli pengguna.**

> Seluruh kode diberi komentar penjelasan berbahasa Indonesia karena proyek ini dibuat untuk belajar — setiap baris penting punya alasan yang tertulis di kodenya.
