package com.intretech.note.di

import com.intretech.note.mvp.presenters.MainPresenter
import com.intretech.note.mvp.presenters.NotePresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NoteDaoModule::class))
interface AppComponent {
    fun inject(mainPresenter: MainPresenter)

    fun inject(notePresenter: NotePresenter)
}