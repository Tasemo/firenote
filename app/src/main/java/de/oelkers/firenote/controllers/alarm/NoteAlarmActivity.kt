package de.oelkers.firenote.controllers.alarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import de.oelkers.firenote.R
import de.oelkers.firenote.util.AppBarActivity
import java.text.SimpleDateFormat
import java.util.*

const val RECEIVER_ARG = "ALARM_RECEIVER"

class NoteAlarmActivity : AppBarActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var pendingIntent: PendingIntent? = null
    private val alarmDateTime: Calendar = Calendar.getInstance()
    private lateinit var alarmButton: Button
    private lateinit var initialAlarmText: CharSequence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_alarm)
        alarmButton = findViewById(R.id.timerButton)
        val cancelButton = findViewById<Button>(R.id.clearButton)
        initialAlarmText = alarmButton.text
        alarmButton.setOnClickListener { onAlarmClick() }
        cancelButton.setOnClickListener { onCancelClick() }
        registerReceiver(baseContext, AlarmReceiver(), IntentFilter(RECEIVER_ARG), ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        alarmDateTime.set(Calendar.YEAR, year)
        alarmDateTime.set(Calendar.MONTH, month)
        alarmDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        TimePickerFragment(this).show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        alarmDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        alarmDateTime.set(Calendar.MINUTE, minute)
        scheduleAlarm()
        alarmButton.text = SimpleDateFormat.getDateTimeInstance().format(alarmDateTime.time)
        Toast.makeText(baseContext, "Alarm successfully set to: \n${alarmButton.text}", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleAlarm() {
        val alarmManager = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        pendingIntent = PendingIntent.getBroadcast(baseContext, 0, Intent(RECEIVER_ARG), flags)
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmDateTime.timeInMillis, pendingIntent!!)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmDateTime.timeInMillis, pendingIntent!!)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmDateTime.timeInMillis, pendingIntent!!)
        }
    }

    private fun onAlarmClick() {
        DatePickerFragment(this).show(supportFragmentManager, "datePicker")
    }

    private fun onCancelClick() {
        pendingIntent?.cancel()
        alarmButton.text = initialAlarmText
        Toast.makeText(baseContext, "Alarm successfully canceled", Toast.LENGTH_SHORT).show()
    }

    inner class AlarmReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(context, notification).play()
            alarmButton.text = initialAlarmText
        }
    }
}
