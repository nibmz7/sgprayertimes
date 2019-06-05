package com.nibmz7gmail.sgprayertimemusollah.ui.widgets

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils
import com.nibmz7gmail.sgprayertimemusollah.core.util.getFontBitmap
import dagger.android.AndroidInjection


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

    override fun getRootView(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_prayer_times_small)
    }

    override fun showPrayerTimes(
        context: Context,
        views: RemoteViews,
        result: Result.Success<CalendarData>
    ) {

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
}

