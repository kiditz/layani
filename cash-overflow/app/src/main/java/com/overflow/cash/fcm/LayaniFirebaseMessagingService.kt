package com.overflow.cash.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.overflow.cash.R
import com.overflow.cash.mvp.menu.FirebaseTokenContract
import com.overflow.cash.mvp.menu.FirebaseTokenPresenter
import com.overflow.libs.core.Data
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class LayaniFirebaseMessagingService : FirebaseMessagingService(), FirebaseTokenContract.View {
    @Inject
    lateinit var presenter: FirebaseTokenPresenter
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        this.presenter.attach(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        val notificationId = Random().nextInt(60000)
        Timber.i("Message:  %s", remoteMessage!!.data.toString())
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setTicker(remoteMessage.notification?.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = getString(R.string.notification_channel)
        val adminChannelDescription = getString(R.string.notification_channel_description)

        val adminChannel = NotificationChannel(getString(R.string.notification_channel), adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        adminChannel.setSound(defaultSoundUri, attributes)
        notificationManager?.createNotificationChannel(adminChannel)
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        s?.let {
            this.presenter.saveToken(it)
        }
    }

    override fun onTokenSaved(data: Data) {
        Timber.i("TOKEN DATA : %s", data.toString())
    }

    override fun showError(error: Throwable) {
    }

    override fun showNoOk(res: String) {
    }

    override fun showEmpty() {
    }

    override fun showNotConnected(res: String) {
    }

}
