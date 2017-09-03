package com.intretech.note.di

import com.intretech.note.mvp.models.NoteDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NoteDaoModule {

    @Provides
    @Singleton
    fun provideNoteDao(): NoteDao = NoteDao()

}