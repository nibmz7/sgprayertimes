package com.nibmz7gmail.sgprayertimemusollah.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getTodaysDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toHijriDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.dpToPixels
import kotlinx.android.synthetic.main.item_calendar_data.view.*
import timber.log.Timber

//https://android.jlelse.eu/recylerview-list-adapter-template-in-kotlin-6b9814201458
class CalendarAdapter : ListAdapter<CalendarData, CalendarAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calendar_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val days = arrayOf("Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun")
        private val ids = arrayOf(R.id.timeTxt1, R.id.timeTxt3, R.id.timeTxt4, R.id.timeTxt5, R.id.timeTxt6, R.id.timeTxt2)
        private val currentDate = getTodaysDate()

        fun bind(item: CalendarData) = with(itemView) {
            this.hijriDate.text = item.toHijriDate()
            this.date.text = item.date
            this.day.text = days[item.day]
            this.event.text = if(item.hijriDate.eventH == "") "nil" else item.hijriDate.eventH

            for(i in 0..5) {
                this.findViewById<TextView>(ids[i]).text = item.prayerTimes[i]
            }

            if(item.date == currentDate) {
                this.root.elevation = 8f
            } else this.elevation = this.context.dpToPixels(0f)

            setOnClickListener {
                Timber.i(item.toString())
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<CalendarData>() {
    override fun areItemsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
        return oldItem == newItem
    }
}