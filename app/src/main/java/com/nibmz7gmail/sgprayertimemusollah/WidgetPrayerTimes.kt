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
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.util.TypedValue
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getCurrentDate
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.TIME_OF_DAY
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.toHijriDate
import dagger.android.*
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject




/**
 * Created by USER on 19/12/2017.
 */
class WidgetPrayerTimes : AppWidgetProvider() {

    @Inject
    lateinit var loadTodaysDataUseCase: LoadTodaysDataUseCase

    private val QS_LIGHT = "quicksandlight.ttf"
    private val QS_MEDIUM = "quicksandmedium.ttf"
    private val QS_BOLD = "quicksandbold.ttf"
    private val TIME_OF_DAY_IMG_WHITE = arrayOf(R.drawable.prayer1_white, R.drawable.prayer2_white, R.drawable.prayer3_white, R.drawable.prayer4_white, R.drawable.prayer5_white)
    private val typeIds = arrayOf(R.id.type1, R.id.type2, R.id.type3, R.id.type4, R.id.type5)
    private val timeIds = arrayOf(R.id.time1, R.id.time2, R.id.time3, R.id.time4, R.id.time5)
    private val bgs = arrayOf(R.drawable.abg1, R.drawable.abg2, R.drawable.abg3, R.drawable.abg4, R.drawable.abg5)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        AndroidInjection.inject(this, context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
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
        super.onReceive(context, intent)

        if (ACTION_AUTO_UPDATE_WIDGET == intent.action && ACTION_REFRESH_WIDGET == intent.action) {
            // do something useful here
            // Instruct the widget manager to update the widget
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, WidgetPrayerTimes::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
//            if(ACTION_REFRESH_WIDGET == intent.action) Utils.show(context, "Widget updated!", Toast.LENGTH_SHORT)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                        appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

        val intentSync = Intent(context, WidgetPrayerTimes::class.java)
        intentSync.action = ACTION_REFRESH_WIDGET //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
        val pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT) //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        views.setOnClickPendingIntent(R.id.refreshButton, pendingSync)

        val configIntent = Intent(context, MainActivity::class.java)
        val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
        views.setOnClickPendingIntent(R.id.root, configPendingIntent)

        val cachedExists = loadTodaysDataUseCase.cacheIsUptoDate(true)
        val calendarData = if(cachedExists) loadTodaysDataUseCase.getCachedDate() else null

        if(calendarData == null) {
            loadTodaysDataUseCase(true)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val white = ContextCompat.getColor(context, R.color.white)
        val date = getCurrentDate()

        views.setImageViewBitmap(R.id.header, context.getFontBitmap(13f, white ,date, "quicksandbold.ttf"))



        if(calendarData is Result.Success) {
            views.setViewVisibility(R.id.stub_list, View.VISIBLE)
            for (i in 0..4) {

                context.apply {
                    views.setImageViewBitmap(typeIds[i], getFontBitmap(19f, white, TIME_OF_DAY[i], QS_MEDIUM))
                    views.setImageViewBitmap(timeIds[i], getFontBitmap(19f, white, calendarData.data.prayerTimes[i], QS_LIGHT))
                }
            }

            val hijriDate = calendarData.data.hijriDate
            if (hijriDate.eventH == "nil")
                views.setImageViewBitmap(
                    R.id.sub_header,
                    context.getFontBitmap(11f, white, calendarData.data.toHijriDate(), QS_BOLD)
                )
            else {
                val islamicDate = calendarData.data.toHijriDate() + " (" + hijriDate.eventH + ")"
                views.setImageViewBitmap(
                    R.id.sub_header,
                    context.getFontBitmap(11f, white, islamicDate, QS_BOLD)
                )
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, WidgetPrayerTimes::class.java)
        intent.action = ACTION_AUTO_UPDATE_WIDGET //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    private fun setAlarm(context: Context) {
        val intent = Intent(context, WidgetPrayerTimes::class.java)
        intent.action = ACTION_AUTO_UPDATE_WIDGET //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) //You need to specify a proper flag for the intent. Or else the intent will become deleted.

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 1)

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.setInexactRepeating(AlarmManager.RTC, c.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun Context.getFontBitmap(fontSizeSP: Float, color: Int, text: String,  type: String): Bitmap {
        val fontSizePX = convertDiptoPix(fontSizeSP)
        val pad = fontSizePX / 9.0f
        val paint = Paint()
        val typeface = Typeface.createFromAsset(assets, type)
        paint.isAntiAlias = true
        paint.typeface = typeface
        paint.color = color
        paint.textSize = fontSizePX.toFloat()

        val textWidth = (paint.measureText(text) + pad * 2).toInt()
        val height = (fontSizePX / 0.75).toInt()
        val bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, pad, fontSizePX.toFloat(), paint)
        return bitmap
    }

    private fun Context.convertDiptoPix(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics).toInt()
    }

    companion object {

        var ACTION_AUTO_UPDATE_WIDGET = "com.nibmz7.sgmasjids.ACTION_AUTO_UPDATE_WIDGET"
        var ACTION_REFRESH_WIDGET = "com.nibmz7.sgmasjids.ACTION_REFRESH_WIDGET"

    }



}