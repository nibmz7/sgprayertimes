package com.nibmz7gmail.sgprayertimemusollah.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils
import com.nibmz7gmail.sgprayertimemusollah.core.util.getFontBitmap
import dagger.android.AndroidInjection
import timber.log.Timber


/**
 * Created by USER on 19/12/2017.
 */
class WidgetPrayerTimesSmall : BaseWidget() {
    override fun injectWidget(baseWidget: BaseWidget, context: Context) {
        AndroidInjection.inject(this, context)
    }

    override fun getWidgetClass(): Class<out BaseWidget> {
        return this.javaClass
    }

    override fun updateWidget(context: Context, result: Result<CalendarData>?) {
        updateSmallWidget(context, result)
    }

    companion object {
        fun update(context: Context, result: Result<CalendarData>?) {
            updateSmallWidget(context, result)
        }
    }

}

private fun updateSmallWidget(
    context: Context,
    result: Result<CalendarData>?
) {
    val views = RemoteViews(context.packageName, R.layout.widget_prayer_times_small)

    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
    val thisWidget = ComponentName(context.applicationContext, WidgetPrayerTimesSmall::class.java)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
    if (appWidgetIds != null && appWidgetIds.isNotEmpty()){
        createPrayerWidget(
            context,
            views,
            result,
            WidgetPrayerTimesSmall::class.java,
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
            R.layout.viewstub_prayertimes_small
        )
        context.apply {
            prayerTimesView.setImageViewBitmap(
                R.id.prayerType, getFontBitmap(17f, white,
                    QS_MEDIUM, PrayerTimesUtils.TIME_OF_DAY[i]))
            prayerTimesView.setImageViewBitmap(
                R.id.prayerTime, getFontBitmap(19f, white,
                    QS_LIGHT, result.data.prayerTimes[i]))
            prayerTimesView.setImageViewResource(R.id.timeIcon, TIME_ICONS[i])
        }
        views.addView(R.id.prayer_list, prayerTimesView)
    }
}

