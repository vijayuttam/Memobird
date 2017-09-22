package com.intretech.note.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.czm.xcricheditor.EditItem
import com.czm.xcricheditor.XCRichEditor
import com.facebook.drawee.backends.pipeline.Fresco
import com.intretech.library.bean.PickResult
import com.intretech.library.bundle.PickSetup
import com.intretech.library.dialog.PickImageDialog
import com.intretech.library.listeners.IPickResult
import com.intretech.note.R
import com.intretech.note.mvp.models.Note
import com.intretech.note.mvp.presenters.NotePresenter
import com.intretech.note.mvp.views.NoteView
import com.intretech.note.utils.formatDate
import java.io.File


class NoteActivity : MvpAppCompatActivity(), NoteView, IPickResult {

    @InjectPresenter
    lateinit var mPresenter: NotePresenter
    private var mNoteDeleteDialog: MaterialDialog? = null
    private var mNoteInfoDialog: MaterialDialog? = null
    private var etTitle: EditText? = null
    private var tvNoteDate: TextView? = null
    private var etText: XCRichEditor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        Fresco.initialize(this)
        etTitle = findViewById(R.id.etTitle) as EditText?
        tvNoteDate = findViewById(R.id.tvNoteDate) as TextView?
        etText = findViewById(R.id.richEditor) as XCRichEditor?

        // move the cursor to the end of the input field
        etTitle!!.onFocusChangeListener = View.OnFocusChangeListener() { view, hasFocus ->
            if (hasFocus) {
                var editText = view as EditText
                editText.setSelection((editText.text.length))
            }
        };

        val noteId = intent.extras.getLong("note_id", -1)
        val notePosition = intent.extras.getInt("note_position", -1)
        mPresenter.showNote(noteId, notePosition)
    }

    override fun showNote(note: Note) {
        tvNoteDate!!.text = formatDate(note.changeDate)
        etTitle!!.setText(note.title)
        etText!!.richText = note.text
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

    override fun showNoteDeleteDialog() {
        mNoteDeleteDialog = MaterialDialog.Builder(this)
                .title("Deleting a note")
                .content("Are you sure you want to delete the note")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive { materialDialog, dialogAction ->
                    mPresenter.hideNoteDeleteDialog()
                    mPresenter.deleteNote()
                }
                .onNegative { materialDialog, dialogAction -> mPresenter.hideNoteDeleteDialog() }
                .cancelListener { mPresenter.hideNoteDeleteDialog() }
                .show()
    }


    override fun hideNoteDeleteDialog() {
        mNoteDeleteDialog?.dismiss()
    }

    override fun onNoteSaved() {
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }

    override fun onNoteDeleted() {
        Toast.makeText(this, R.string.note_is_deleted, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun showPickImageDialog() {
        PickImageDialog.build(PickSetup())
                .show(supportFragmentManager)
    }

    override fun onPickResult(pickResult: PickResult?) {
        if (pickResult?.error == null){
            var editItem: EditItem? = EditItem(1, pickResult!!.path, Uri.fromFile(File(pickResult!!.path)))
            etText!!.addImage(editItem)
            Toast.makeText(this, "Picked Image", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, pickResult.error.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSaveNote -> mPresenter.saveNote(etTitle!!.text.toString(), etText!!.richText)

            R.id.menuDeleteNote -> mPresenter.showNoteDeleteDialog()

            R.id.menuNoteInfo -> mPresenter.showNoteInfoDialog()

            R.id.menuPickImage -> mPresenter.showPickImageDialog()
        }
        return super.onOptionsItemSelected(item)
    }


}