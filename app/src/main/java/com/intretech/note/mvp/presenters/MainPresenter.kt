package com.intretech.note.mvp.presenters

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.intretech.note.MemobirdApplication
import com.intretech.note.bus.NoteDeleteAction
import com.intretech.note.bus.NoteEditAction
import com.intretech.note.mvp.models.Note
import com.intretech.note.mvp.models.NewNote
import com.intretech.note.mvp.views.MainView
import com.intretech.note.ui.activities.NoteActivity
import com.intretech.note.utils.getNotesSortMethodName
import com.intretech.note.utils.setNotesSortMethod
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import javax.inject.Inject

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    enum class SortNotesBy : Comparator<Note> {
        DATE {
            override fun compare(lhs: Note, rhs: Note) = lhs.changeDate!!.compareTo(rhs.changeDate)
        },
        NAME {
            override fun compare(lhs: Note, rhs: Note) = lhs.title!!.compareTo(rhs.title!!)
        },
    }

    @Inject
    lateinit var mNewNote: NewNote
    lateinit var mNotesList: MutableList<Note>

    init {
        MemobirdApplication.graph.inject(this)
        EventBus.getDefault().register(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadAllNotes()
    }

    /**
     * Loads all existing notes and passes them to View
     */
    fun loadAllNotes() {
        mNotesList = mNewNote.loadAllNotes()
        Collections.sort(mNotesList, getCurrentSortMethod())
        viewState.onNotesLoaded(mNotesList)
    }

    /**
     * Removes all existing notes
     */
    fun deleteAllNotes() {
        mNewNote.deleteAllNotes()
        mNotesList.removeAll(mNotesList)
        viewState.onAllNotesDeleted()
    }

    /**
     * Deletes a note by position
     */
    fun deleteNoteByPosition(position: Int) {
        val note = mNotesList[position];
        mNewNote.deleteNote(note)
        mNotesList.remove(note)
        viewState.onNoteDeleted()
    }

    fun openNewNote(activity: Activity) {
        val newNote = mNewNote.createNote()
        mNotesList.add(newNote)
        sortNotesBy(getCurrentSortMethod())
        openNote(activity, mNotesList.indexOf(newNote))
    }

    /**
     * Opens activations with a note by position
     */
    fun openNote(activity: Activity, position: Int) {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_position", position)
        intent.putExtra("note_id", mNotesList[position].id)
        activity.startActivity(intent)
    }

    /**
     * Looks for a note by name
     */
    fun search(query: String) {
        if (query.equals("")) {
            viewState.onSearchResult(mNotesList)
        } else {
            val searchResults = mNotesList.filter { it.title!!.startsWith(query, ignoreCase = true) }
            viewState.onSearchResult(searchResults)
        }
    }

    /**
     * Sorts notes
     */
    fun sortNotesBy(sortMethod: SortNotesBy) {
        mNotesList.sortWith(sortMethod)
        setNotesSortMethod(sortMethod.toString())
        viewState.updateView()
    }

    fun getCurrentSortMethod(): SortNotesBy {
        val defaultSortMethodName = SortNotesBy.DATE.toString()
        val currentSortMethodName = getNotesSortMethodName(defaultSortMethodName)
        return SortNotesBy.valueOf(currentSortMethodName)
    }
    /**
     * It works when you save a note on the editing screen
     */
    @Subscribe
    fun onNoteEdit(action: NoteEditAction) {
        val notePosition = action.position
        mNotesList[notePosition] = mNewNote.getNoteById(mNotesList[notePosition].id) //обновляем заметку по позиции
        sortNotesBy(getCurrentSortMethod())
    }

    /**
     * It works when deleting a note on the editing screen
     */
    @Subscribe
    fun onNoteDelete(action: NoteDeleteAction) {
        mNotesList.removeAt(action.position)
        viewState.updateView()
    }

    /**
     * Displays a shortcut menu for a note
     */
    fun showNoteContextDialog(position: Int) {
        viewState.showNoteContextDialog(position)
    }

    /**
     * Hides the context menu of a note
     */
    fun hideNoteContextDialog() {
        viewState.hideNoteContextDialog()
    }

    /**
     * Displays the delete note dialog
     */
    fun showNoteDeleteDialog(position: Int) {
        viewState.showNoteDeleteDialog(position)
    }

    /**
     * Hides the delete note dialog
     */
    fun hideNoteDeleteDialog() {
        viewState.hideNoteDeleteDialog()
    }

    /**
     * Displays a dialog with note information
     */
    fun showNoteInfo(position: Int) {
        viewState.showNoteInfoDialog(mNotesList[position].getInfo())
    }

    /**
     * Hides the dialog with information about the note
     */
    fun hideNoteInfoDialog() {
        viewState.hideNoteInfoDialog()
    }

}
