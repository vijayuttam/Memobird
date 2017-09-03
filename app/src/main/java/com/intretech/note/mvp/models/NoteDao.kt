package com.intretech.note.mvp.models

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import java.util.*

class NoteDao {

    /**
     * It creates a new note
     */
    fun createNote(): Note {
        var note = Note("New note", Date())
        note.save()
        return note
    }

    /**
     * Saves a note to the database
     */
    fun saveNote(note: Note) = note.save()

    /**
     * Loads all existing notes and passes them to View
     */
    fun loadAllNotes() = Select().from(Note::class.java).execute<Note>()

    /**
     * Looks for a note on id and returns it
     */
    fun getNoteById(noteId: Long) = Select().from(Note::class.java).where("id = ?", noteId).executeSingle<Note>()

    /**
     * Removes all existing notes
     */
    fun deleteAllNotes() {
        Delete().from(Note::class.java).execute<Note>();
    }

    /**
     * Removes a note by id
     */
    fun deleteNote(note: Note) {
        note.delete()
    }

}
