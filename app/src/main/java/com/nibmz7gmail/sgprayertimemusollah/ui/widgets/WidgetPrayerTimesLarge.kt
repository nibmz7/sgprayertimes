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

    override fun updateWidget(context: Context, result: Result<CalendarData>?) {
        updateLargeWidget(context, result)
    }

    companion object {
        fun update(context: Context, result: Result<CalendarData>?) {
            updateLargeWidget(context, result)
        }
    }

}

private fun updateLargeWidget(
    context: Context,
    result: Result<CalendarData>?
) {
    val views = RemoteViews(context.packageName, R.layout.widget_prayer_times_large)

    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
    val thisWidget = ComponentName(context.applicationContext, WidgetPrayerTimesLarge::class.java)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
    if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
        createPrayerWidget(
            context,
            views,
            result,
            WidgetPrayerTimesLarge::class.java,
            showPrayerTimes
        )
    } else return

    for (appWidgetId in appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

private val showPrayerTimes = {
    context: Context,
    views: RemoteViews,
    result: Result.Success<CalendarData> ->

    views.removeAllViews(R.id.prayer_list)

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