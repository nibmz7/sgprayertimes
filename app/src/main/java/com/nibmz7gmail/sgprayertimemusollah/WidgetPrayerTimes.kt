package com.nibmz7gmail.sgprayertimemusollah

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.*
import android.util.TypedValue
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.TIME_OF_DAY
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getTodaysDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toHijriDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toBeautifiedDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.convertDiptoPix
import com.nibmz7gmail.sgprayertimemusollah.core.util.getFontBitmap
import com.nibmz7gmail.sgprayertimemusollah.domain.ErrorTypes
import dagger.android.*
import timber.log.Timber
import javax.inject.Inject




/**
 * Created by USER on 19/12/2017.
 */
class WidgetPrayerTimes : AppWidgetProvider() {

    @Inject
    lateinit var loadTodaysDataUseCase: LoadTodaysDataUseCase

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Timber.i("OnUpdate Called")
        val cacheExists = loadTodaysDataUseCase.cacheIsUptoDate()

        val result = if(cacheExists) {
            loadTodaysDataUseCase.getCachedDate()
        } else null

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, result)
        }

        if(result == null) loadTodaysDataUseCase(true)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        setAlarm(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        cancelAlarm(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if(ACTION_REFRESH_WIDGET == intent.action) {
            cancelAlarm(context)
            setAlarm(context)
            loadTodaysDataUseCase.setCacheToNull()
        }

        if (ACTION_REFRESH_WIDGET == intent.action || ACTION_AUTO_UPDATE == intent.action) {
            Timber.i("Refresh Widget called")
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, WidgetPrayerTimes::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, WidgetPrayerTimes::class.java)
        intent.action = ACTION_AUTO_UPDATE
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    private fun setAlarm(context: Context) {
        Timber.i("Alarm set")
        val intent = Intent(context, WidgetPrayerTimes::class.java)
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

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.nibmz7.sgmasjids.ACTION_REFRESH_WIDGET"
        const val ACTION_AUTO_UPDATE = "com.nibmz7.sgmasjids.AUTO_UPDATE_WIDGET"
        private val QS_LIGHT = R.font.quicksand_light
        private val QS_MEDIUM = R.font.quicksand_medium
        private val QS_BOLD = R.font.quicksand_bold
        private val typeIds = arrayOf(R.id.type1, R.id.type2, R.id.type3, R.id.type4, R.id.type5)
        private val timeIds = arrayOf(R.id.time1, R.id.time2, R.id.time3, R.id.time4, R.id.time5)

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                            appWidgetId: Int, result: Result<CalendarData>?) {
            val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

            Timber.e("Updating widget ---> $result")

            views.setViewVisibility(R.id.appBar, View.GONE)

            val configIntent = Intent(context, LauncherActivity::class.java)
            val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
            views.setOnClickPendingIntent(R.id.root, configPendingIntent)

            val intentSync = Intent(context, WidgetPrayerTimes::class.java)
            intentSync.action = ACTION_REFRESH_WIDGET //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
            val pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

            val grey = ContextCompat.getColor(context, R.color.grey)
            val white = ContextCompat.getColor(context, R.color.white)

            if(result == null) {
                fetchNewData(context, views, appWidgetManager, appWidgetId)
                return
            }

            if(result is Result.Success) {

                views.setViewVisibility(R.id.root_loading, View.GONE)
                views.setViewVisibility(R.id.root_list, View.VISIBLE)

                val date = result.data.toBeautifiedDate()
                views.setImageViewBitmap(R.id.header, context.getFontBitmap(13f, white, QS_BOLD ,date))

                for (i in 0..4) {

                    context.apply {
                        views.setImageViewBitmap(typeIds[i], getFontBitmap(24f, white, QS_MEDIUM, TIME_OF_DAY[i]))
                        views.setImageViewBitmap(timeIds[i], getFontBitmap(20f, white, QS_LIGHT, result.data.prayerTimes[i]))
                    }
                }

                val hijriDate = result.data.hijriDate
                if (hijriDate.eventH == "")
                    views.setImageViewBitmap(
                        R.id.sub_header,
                        context.getFontBitmap(11f, white, QS_BOLD, result.data.toHijriDate())
                    )
                else {
                    val islamicDate = result.data.toHijriDate() + " (" + hijriDate.eventH + ")"
                    views.setImageViewBitmap(
                        R.id.sub_header,
                        context.getFontBitmap(11f, white, QS_BOLD, islamicDate)
                    )
                }

                views.setViewVisibility(R.id.appBar, View.VISIBLE)
                views.setOnClickPendingIntent(R.id.refreshButton, pendingSync)
            }
            else if(result is Result.Error) {
                views.setViewVisibility(R.id.root_list, View.GONE)
                views.setViewVisibility(R.id.appBar, View.GONE)
                val msg = if(result.errorType == ErrorTypes.NETWORK) R.string.error_network
                else R.string.error_date

                views.setViewVisibility(R.id.root_loading, View.VISIBLE)
                views.setViewVisibility(R.id.retryButton, View.VISIBLE)
                context.apply {
                    val text = getString(msg).split("\n")
                    views.setImageViewBitmap(
                        R.id.info_message,
                        getFontBitmap(15f, grey, QS_MEDIUM, text[0], text[1])
                    )
                    views.setOnClickPendingIntent(R.id.retryButton, pendingSync)
                }

            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


        private fun fetchNewData(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val grey = ContextCompat.getColor(context, R.color.grey)
            views.setViewVisibility(R.id.root_list, View.GONE)
            views.setViewVisibility(R.id.retryButton, View.GONE)
            views.setViewVisibility(R.id.appBar, View.GONE)
            views.setViewVisibility(R.id.root_loading, View.VISIBLE)
            views.setImageViewBitmap(
                R.id.info_message,
                context.getFontBitmap(15f, grey, QS_MEDIUM, "Fetching data...")
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateWidget(context: Context, result: Result<CalendarData>?) {
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, WidgetPrayerTimes::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, result)
                }
            }
        }

    }



}