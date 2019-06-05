package com.nibmz7gmail.sgprayertimemusollah.ui.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.nibmz7gmail.sgprayertimemusollah.LauncherActivity
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toBeautifiedDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toHijriDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.getFontBitmap
import com.nibmz7gmail.sgprayertimemusollah.domain.ErrorTypes
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import timber.log.Timber
import java.util.*
import javax.inject.Inject

abstract class BaseWidget: AppWidgetProvider()  {

    @Inject
    lateinit var loadTodaysDataUseCase: LoadTodaysDataUseCase


    abstract fun injectWidget(
        baseWidget: BaseWidget,
        context: Context
    )

    abstract fun showPrayerTimes(context: Context, views: RemoteViews, result: Result.Success<CalendarData>)

    abstract fun getWidgetClass(): Class<out BaseWidget>

    abstract fun getRootView(context: Context): RemoteViews

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Timber.i("OnUpdate Called")
        val cacheExists = loadTodaysDataUseCase.cacheIsUptoDate()

        val result = if(cacheExists) {
            loadTodaysDataUseCase.getCachedDate()
        } else null

        for (appWidgetId in appWidgetIds) {
            updatePrayerWidget(context, appWidgetManager, appWidgetId, result)
        }

        if(result == null) loadTodaysDataUseCase(true)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        setAlarm(context)
    }

    override fun onDisabled(context: Context) {
        cancelAlarm(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        injectWidget(this, context)
        super.onReceive(context, intent)

        if(ACTION_REFRESH_WIDGET == intent.action) {
            cancelAlarm(context)
            setAlarm(context)
            loadTodaysDataUseCase.setCacheToNull()
        }

        if (ACTION_REFRESH_WIDGET == intent.action || ACTION_AUTO_UPDATE == intent.action) {
            Timber.i("Refresh Widget called")
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, getWidgetClass())
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, getWidgetClass())
        intent.action = ACTION_AUTO_UPDATE
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    private fun setAlarm(context: Context) {
        Timber.i("Alarm set")
        val intent = Intent(context, getWidgetClass())
        intent.action = ACTION_AUTO_UPDATE
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 1)

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.setInexactRepeating(AlarmManager.RTC, c.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun updatePrayerWidget(context: Context, appWidgetManager: AppWidgetManager,
                                   appWidgetId: Int, result: Result<CalendarData>?) {
        val views = getRootView(context)

        Timber.e("Updating widget ---> $result")

        views.setViewVisibility(R.id.appBar, View.GONE)

        val configIntent = Intent(context, LauncherActivity::class.java)
        val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
        views.setOnClickPendingIntent(R.id.root, configPendingIntent)

        val intentSync = Intent(context, getWidgetClass())
        intentSync.action =
            ACTION_REFRESH_WIDGET
        val pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

        if(result == null) {
            fetchNewData(
                context,
                views,
                appWidgetManager,
                appWidgetId
            )
            return
        }

        if(result is Result.Success) {

            val date = result.data.toBeautifiedDate()
            views.setImageViewBitmap(R.id.header, context.getFontBitmap(13f, white,
                QS_BOLD,date))
            val hijriDate = result.data.hijriDate
            if (hijriDate.eventH == "")
                views.setImageViewBitmap(
                    R.id.sub_header,
                    context.getFontBitmap(11f, white,
                        QS_BOLD, result.data.toHijriDate())
                )
            else {
                val islamicDate = result.data.toHijriDate() + " (" + hijriDate.eventH + ")"
                views.setImageViewBitmap(
                    R.id.sub_header,
                    context.getFontBitmap(11f, white,
                        QS_BOLD, islamicDate)
                )
            }

            showPrayerTimes(context, views, result)

            views.setViewVisibility(R.id.root_loading, View.GONE)
            views.setViewVisibility(R.id.appBar, View.VISIBLE)
            views.setViewVisibility(R.id.prayer_list, View.VISIBLE)
            views.setOnClickPendingIntent(R.id.refreshButton, pendingSync)
        }
        else if(result is Result.Error) {
            showErrorView(context, views, pendingSync, result)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun showErrorView(context: Context, views: RemoteViews, pendingSync: PendingIntent, result: Result.Error) {
        views.setViewVisibility(R.id.prayer_list, View.GONE)
        views.setViewVisibility(R.id.appBar, View.GONE)
        val msg = if(result.errorType == ErrorTypes.NETWORK) R.string.error_network
        else R.string.error_date

        views.setViewVisibility(R.id.root_loading, View.VISIBLE)
        views.setViewVisibility(R.id.retryButton, View.VISIBLE)
        context.apply {
            val text = getString(msg).split("\n")
            views.setImageViewBitmap(
                R.id.info_message,
                getFontBitmap(15f, grey,
                    QS_MEDIUM, text[0], text[1])
            )
            views.setOnClickPendingIntent(R.id.retryButton, pendingSync)
        }
    }

    private fun fetchNewData(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val grey = ContextCompat.getColor(context, R.color.grey)
        views.setViewVisibility(R.id.prayer_list, View.GONE)
        views.setViewVisibility(R.id.retryButton, View.GONE)
        views.setViewVisibility(R.id.appBar, View.GONE)
        views.setViewVisibility(R.id.root_loading, View.VISIBLE)
        views.setImageViewBitmap(
            R.id.info_message,
            context.getFontBitmap(15f, grey,
                QS_MEDIUM, "Fetching data...")
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}

const val ACTION_REFRESH_WIDGET = "com.nibmz7.sgmasjids.ACTION_REFRESH_WIDGET"
const val ACTION_AUTO_UPDATE = "com.nibmz7.sgmasjids.AUTO_UPDATE_WIDGET"
const val QS_LIGHT = R.font.quicksand_light
const val QS_MEDIUM = R.font.quicksand_medium
const val QS_BOLD = R.font.quicksand_bold
val grey = Color.parseColor("#ff303030")
val white = Color.parseColor("#FFFFFF")
val TIME_ICONS = arrayOf(
    R.drawable.prayer1_white,
    R.drawable.prayer2_white,
    R.drawable.prayer3_white,
    R.drawable.prayer4_white,
    R.drawable.prayer5_white
)
val TIME_COLOUR = arrayOf(
    R.drawable.abg1,
    R.drawable.abg2,
    R.drawable.abg3,
    R.drawable.abg4,
    R.drawable.abg5
)