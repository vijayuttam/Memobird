package com.intretech.note.ui.activities

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.intretech.note.R
import com.intretech.note.mvp.models.Note
import com.intretech.note.mvp.presenters.MainPresenter
import com.intretech.note.mvp.views.MainView
import com.intretech.note.ui.commons.ItemClickSupport
import com.pawegio.kandroid.onQueryChange
import com.intretech.note.ui.adapters.NotesAdapter
import com.melnykov.fab.FloatingActionButton

class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var mPresenter: MainPresenter
    private var mNoteContextDialog: MaterialDialog? = null
    private var mNoteDeleteDialog: MaterialDialog? = null
    private var mNoteInfoDialog: MaterialDialog? = null

    private var rvNotesList: RecyclerView? = null
    private var fabButton: FloatingActionButton? = null
    private var tvNotesIsEmpty: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvNotesList = findViewById(R.id.rvNotesList)
        fabButton = findViewById(R.id.fabButton)
        tvNotesIsEmpty = findViewById(R.id.tvNotesIsEmpty)

        with(ItemClickSupport.addTo(rvNotesList!!)) {
            setOnItemClickListener { recyclerView, position, v -> mPresenter.openNote(this@MainActivity, position) }
            setOnItemLongClickListener { recyclerView, position, v -> mPresenter.showNoteContextDialog(position); true }
        }

        fabButton!!.attachToRecyclerView(rvNotesList!!)
        fabButton!!.setOnClickListener { mPresenter.openNewNote(this) }
    }

    override fun onNotesLoaded(notes: List<Note>) {
        rvNotesList!!.adapter = NotesAdapter(notes)
        updateView()
    }

    override fun updateView() {
        rvNotesList!!.adapter.notifyDataSetChanged()
        if (rvNotesList!!.adapter.itemCount == 0) {
            rvNotesList!!.visibility = View.GONE
            tvNotesIsEmpty!!.visibility = View.VISIBLE
        } else {
            rvNotesList!!.visibility = View.VISIBLE
            tvNotesIsEmpty!!.visibility = View.GONE
        }
    }

    override fun onNoteDeleted() {
        updateView()
        Toast.makeText(this, R.string.note_is_deleted, Toast.LENGTH_SHORT).show()
    }

    override fun onAllNotesDeleted() {
        updateView()
        Toast.makeText(this, R.string.all_notes_is_deleted, Toast.LENGTH_SHORT).show()
    }

    override fun onSearchResult(notes: List<Note>) {
        rvNotesList!!.adapter = NotesAdapter(notes)
    }

    override fun showNoteContextDialog(notePosition: Int) {
        mNoteContextDialog = MaterialDialog.Builder(this)
                .items(R.array.main_note_context)
                .itemsCallback { dialog, view, position, text ->
                    onContextDialogItemClick(position, notePosition)
                    mPresenter.hideNoteContextDialog()
                }
                .cancelListener { mPresenter.hideNoteContextDialog() }
                .show();
    }

    override fun hideNoteContextDialog() {
        mNoteContextDialog?.dismiss()
    }

    override fun showNoteDeleteDialog(notePosition: Int) {
        mNoteDeleteDialog = MaterialDialog.Builder(this)
                .title("Deleting a note")
                .content("Are you sure you want to delete the note")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive { materialDialog, dialogAction ->
                    mPresenter.deleteNoteByPosition(notePosition)
                    mNoteInfoDialog?.dismiss()
                }
                .onNegative { materialDialog, dialogAction -> mPresenter.hideNoteDeleteDialog() }
                .cancelListener { mPresenter.hideNoteDeleteDialog() }
                .show()
    }

    override fun hideNoteDeleteDialog() {
        mNoteDeleteDialog?.dismiss()
    }

    override fun showNoteInfoDialog(noteInfo: String) {
        mNoteInfoDialog = MaterialDialog.Builder(this)
                .title("Information about the note")
                .positiveText("OK")
                .content(noteInfo)
                .onPositive { materialDialog, dialogAction -> mPresenter.hideNoteInfoDialog() }
                .cancelListener { mPresenter.hideNoteInfoDialog() }
                .show()
    }

    override fun hideNoteInfoDialog() {
        mNoteInfoDialog?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        initSearchView(menu)
        return true
    }

    private fun initSearchView(menu: Menu) {
        var searchViewMenuItem = menu.findItem(R.id.action_search);
        var searchView = searchViewMenuItem.actionView as SearchView;
        searchView.onQueryChange { query -> mPresenter.search(query) }
        searchView.setOnCloseListener { mPresenter.search(""); false }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuDeleteAllNotes -> mPresenter.deleteAllNotes()
            R.id.menuSortByName -> mPresenter.sortNotesBy(MainPresenter.SortNotesBy.NAME)
            R.id.menuSortByDate -> mPresenter.sortNotesBy(MainPresenter.SortNotesBy.DATE)
        }
        return super.onOptionsItemSelected(item)
    }

    fun onContextDialogItemClick(contextItemPosition: Int, notePosition: Int) {
        when (contextItemPosition) {
            0 -> mPresenter.openNote(this, notePosition)
            1 -> mPresenter.showNoteInfo(notePosition)
            2 -> mPresenter.showNoteDeleteDialog(notePosition)
        }
    }

}
