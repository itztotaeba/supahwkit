package com.kinyo.supahwkit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kinyo.supahwkit.data.model.HmsKit
import com.kinyo.supahwkit.databinding.ItemKitMenuBinding

/**
 * ============================================================================
 * KitMenuAdapter.kt - Adapter RecyclerView untuk merender daftar card HMS Kit.
 * ============================================================================
 *
 * APA ITU ADAPTER?
 * Dalam pola RecyclerView Android, Adapter adalah "penerjemah" antara data
 * (List<HmsKit>) dengan tampilan. Tugasnya:
 *  1. onCreateViewHolder : membuat "wadah" view (ViewHolder) dari layout XML,
 *                          sekali per slot layar yang terlihat + sedikit buffer.
 *  2. onBindViewHolder   : mengisi wadah tersebut dengan data pada posisi ke-i.
 *                          Dipanggil ulang setiap kali user scrolling dan item
 *                          baru masuk layar (view lama dipakai ulang = hemat memori).
 *  3. itemCount          : memberi tahu RecyclerView total data yang ada.
 *
 * VIEW BINDING:
 * ItemKitMenuBinding adalah class yang AUTO-GENERATED oleh build system AGP dari
 * file res/layout/item_kit_menu.xml (fitur "viewBinding" di build.gradle.kts).
 * Daripada findViewById(R.id.tvTitle) yang rawan typo & lambat, binding
 * menyediakan properti langsung: binding.tvKitTitle, binding.ivKitIcon, dst.
 * ============================================================================
 */
class KitMenuAdapter(
    // Data yang akan ditampilkan. Default emptyList agar adapter bisa dibuat
    // sebelum data tersedia (misal di onCreate sebelum load data).
    private var kits: List<HmsKit> = emptyList(),

    /*
     * CALLBACK NAVIGASI (pola dependency injection sederhana):
     * Adapter TAHU kapan item diklik, tapi TIDAK TAHU harus buka halaman apa -
     * itu urusan Activity. Karena itu kita terima lambda onItemClick yang akan
     * dipanggil dengan membawa HmsKit yang diklik. Pemisahan tanggung jawab ini
     * membuat adapter tetap reusable & mudah di-test.
     */
    private val onItemClick: (HmsKit) -> Unit
) : RecyclerView.Adapter<KitMenuAdapter.KitViewHolder>() {

    /**
     * ViewHolder = "kantong" yang memegang referensi view-view di dalam satu card.
     * Dengan menyimpan binding, kita tidak perlu findViewById berulang kali
     * ketika onBindViewHolder dipanggil - ini optimasi standar RecyclerView.
     */
    class KitViewHolder(val binding: ItemKitMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Dipanggil saat RecyclerView butuh ViewHolder baru (pool kosong).
     * Di sini kita inflate layout item_kit_menu.xml menjadi object binding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitViewHolder {
        // LayoutInflater: mesin yang mengubah XML layout menjadi view sungguhan.
        val binding = ItemKitMenuBinding.inflate(
            LayoutInflater.from(parent.context), // context si induk RecyclerView
            parent,                              // parent = area item RecyclerView
            false                                // false: JANGAN attach dulu,
                                                 // RecyclerView akan attach sendiri
        )
        return KitViewHolder(binding)
    }

    /**
     * Dipanggil untuk menempatkan data posisi [position] ke dalam holder.
     * Prinsipnya: bind harus CEPAT & BEBAS SIDE EFFECT, karena bisa dipanggil
     * puluhan kali saat user scrolling.
     */
    override fun onBindViewHolder(holder: KitViewHolder, position: Int) {
        val kit = kits[position] // ambil data pada indeks ke-position

        with(holder.binding) {
            // 'with' = scope function Kotlin; di dalam blok ini kita bisa akses
            // properti binding langsung tanpa menulis "holder.binding.xxx" terus.

            tvKitTitle.text = kit.title
            tvKitDescription.text = kit.description

            // Set ikon kit. Context diambil dari itemView (setiap View punya context).
            ivKitIcon.setImageResource(kit.iconRes)

            // Warnai latar ikon sesuai identitas warna kit, supaya tiap menu
            // punya "brand color" sendiri di dashboard.
            val accentColor = root.context.getColor(kit.colorRes)
            ivKitIcon.setBackgroundColor(accentColor)

            // Klik seluruh card -> panggil callback yang diberikan oleh Activity.
            root.setOnClickListener { onItemClick(kit) }
        }
    }

    /** Total item. RecyclerView pakai angka ini untuk scrollbar & prefetch logic. */
    override fun getItemCount(): Int = kits.size

    /**
     * Ganti seluruh isi list (dipakai saat data berubah, misal setelah filter).
     * notifyDataSetChanged() memberitahu adapter: "semua view usang, re-bind!".
     * Untuk list kecil seperti ini (8 item) cara simpel ini sudah efisien.
     */
    fun submitList(newKits: List<HmsKit>) {
        kits = newKits
        notifyDataSetChanged()
    }
}
