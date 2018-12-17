package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nibmz7gmail.sgprayertimemusollah.MainActivity
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import kotlinx.android.synthetic.main.item_mosque_data.view.*
import java.text.DecimalFormat

class NearbyListAdapter : ListAdapter<Mosque, NearbyListAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mosque_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NearbyListAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Mosque) = with(itemView) {
            this.mosque_name.text = item.name
            this.address.text = item.address
            val dist = DecimalFormat("#.##").format(item.distance/1000)
            this.distance.text = "$dist km"

            setOnClickListener {
                val detailsFrag = DetailsDialogFragment.newInstance(item)
                detailsFrag.show((itemView.context as MainActivity).supportFragmentManager, "details_dialog")
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Mosque>() {
    override fun areItemsTheSame(oldItem: Mosque, newItem: Mosque): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Mosque, newItem: Mosque): Boolean {
        return oldItem == newItem
    }
}