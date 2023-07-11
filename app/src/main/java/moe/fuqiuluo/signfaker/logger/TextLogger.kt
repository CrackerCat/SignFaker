package moe.fuqiuluo.signfaker.logger

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Message
import java.util.Date

object TextLogger {
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("HH:mm:ss")
    const val WHAT_INFO = 0

    lateinit var updateTextHandler: Handler

    fun log(str: String) {
        updateTextHandler.sendMessage(Message.obtain(updateTextHandler, WHAT_INFO, "[" + format.format(Date()) + "]  " + str))
    }

    fun input(str: String) {
        updateTextHandler.sendMessage(Message.obtain(updateTextHandler, WHAT_INFO, str))
    }
}