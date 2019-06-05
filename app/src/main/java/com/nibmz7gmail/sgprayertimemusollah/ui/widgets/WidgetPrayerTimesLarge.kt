package com.nibmz7gmail.sgprayertimemusollah.ui.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.*
import android.view.View
import androidx.core.content.ContextCompat
import com.nibmz7gmail.sgprayertimemusollah.LauncherActivity
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.TIME_OF_DAY
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toHijriDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toBeautifiedDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.getFontBitmap
import com.nibmz7gmail.sgprayertimemusollah.domain.ErrorTypes
import dagger.android.*
import timber.log.Timber
import javax.inject.Inject




/**
 * Created by USER on 19/12/2017.
 */
class WidgetPrayerTimesLarge : BaseWidget() {

    override fun injectWidget(baseWidget: BaseWidget, context: Context) {
        AndroidInjection.inject(this, context)
    }

    override fun getWidgetClass(): Class<out BaseWidget> {
        return this.javaClass
    }

    override fun getRootView(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_prayer_times_large)
    }

    override fun showPrayerTimes(context: Context, views: RemoteViews, result: Result.Success<CalendarData>) {
        for (i in 0..4) {
            val prayerTimesView = RemoteViews(context.packageName,
                R.layout.viewstub_prayertimes_large
            )
            context.apply {
                prayerTimesView.setImageViewBitmap(
                    R.id.type, getFontBitmap(24f, white,
                        QS_MEDIUM, TIME_OF_DAY[i]))
                prayerTimesView.setImageViewBitmap(
                    R.id.time, getFontBitmap(20f, white,
                        QS_LIGHT, result.data.prayerTimes[i]))
                prayerTimesView.setImageViewResource(R.id.time_icon, TIME_ICONS[i])
                prayerTimesView.setInt(
                    R.id.time_color, "setBackgroundResource",
                    TIME_COLOUR[i])
            }
            views.addView(R.id.prayer_list, prayerTimesView)
        }
    }
}