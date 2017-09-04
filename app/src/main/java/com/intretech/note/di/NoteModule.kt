package com.intretech.note.di

import com.intretech.note.mvp.models.NewNote
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NoteModule {

    @Provides
    @Singleton
    fun provideNote(): NewNote = NewNote()

}