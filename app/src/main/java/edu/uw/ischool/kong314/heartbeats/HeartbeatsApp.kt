package edu.uw.ischool.kong314.heartbeats

import android.app.Application

class HeartbeatsApp: Application() {
    lateinit var databaseRepository: DatabaseRepository

    override fun onCreate() {
        super.onCreate()
        databaseRepository = DatabaseRepositoryStorage()
    }
}