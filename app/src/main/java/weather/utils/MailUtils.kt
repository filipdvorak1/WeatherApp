package com.simple.weather.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


class MailUtils(val contact : Context) {



     fun mail() {
         val uriText = "mailto:contact@example.com" +
                 "?subject=" + "your subject here" +
                 "&body=" + ""
         val uri = Uri.parse(uriText)
         val sendIntent = Intent(Intent.ACTION_SENDTO)
         sendIntent.data = uri
         contact.startActivity(Intent.createChooser(sendIntent, "Send Email").addFlags(FLAG_ACTIVITY_NEW_TASK))
     }
}