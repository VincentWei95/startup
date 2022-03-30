package com.example.startup.launch.task

import android.content.Context
import android.util.Log
import com.example.startup.launch.AndroidStartUp

class Task1 : AndroidStartUp<Unit>() {
    override fun create(context: Context) {
        Log.i("===test===", "学习java")
        Thread.sleep(1000)
        Log.i("====test===", "掌握java")
    }
}