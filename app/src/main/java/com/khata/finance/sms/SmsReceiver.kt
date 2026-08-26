package com.khata.finance.sms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.khata.finance.KhataApp
import com.khata.finance.MainActivity
import com.khata.finance.data.Transaction
import com.khata.finance.data.TxnSource
import com.khata.finance.data.UnparsedSms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fires every time a new SMS arrives. If it looks like it's from Meezan Bank
 * (see SmsParser.KNOWN_SENDERS), we try to parse it into a transaction and save
 * it automatically. Everything happens on-device — the SMS body is never sent
 * anywhere off the phone.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val app = context.applicationContext as KhataApp

        for (msg in messages) {
            val sender = msg.originatingAddress ?: continue
            val body = msg.messageBody ?: continue

            if (!SmsParser.isFromBank(sender)) continue

            val parsed = SmsParser.parse(body)

            CoroutineScope(Dispatchers.IO).launch {
                if (parsed != null) {
                    app.repository.addTransaction(
                        Transaction(
                            amount = parsed.amount,
                            type = parsed.type,
                            categoryId = null,
                            note = parsed.merchant ?: "Auto-detected from SMS",
                            merchant = parsed.merchant,
                            dateMillis = System.currentTimeMillis(),
                            source = TxnSource.SMS,
                            rawSms = body
                        )
                    )
                    notify(
                        context,
                        "Transaction detected",
                        "Rs. ${parsed.amount} ${if (parsed.type.name == "INCOME") "received" else "spent"} — tap to categorize"
                    )
                } else {
                    app.repository.addUnparsedSms(
                        UnparsedSms(sender = sender, body = body, receivedMillis = System.currentTimeMillis())
                    )
                    notify(context, "Bank SMS needs review", "Couldn't auto-read this one — tap to add it in a few seconds")
                }
            }
        }
    }

    private fun notify(context: Context, title: String, text: String) {
        val channelId = "khata_sms_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Transaction Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        } catch (_: SecurityException) {
            // Notification permission not granted — transaction was still saved.
        }
    }
}
