package com.intretech.note

import android.content.Context
import com.activeandroid.app.Application
import com.intretech.note.di.AppComponent
import com.intretech.note.di.DaggerAppComponent
import com.intretech.note.di.NoteModule
import com.intretech.note.utils.initPrefs

class MemobirdApplication : Application() {

    companion object {
        lateinit var graph: AppComponent
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        initPrefs(this)

        context = this
        graph = DaggerAppComponent.builder().noteModule(NoteModule()).build()
    }

}
