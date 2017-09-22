package com.intretech.note.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intretech.note.R
import com.intretech.note.mvp.models.Note
import com.intretech.note.utils.formatDate
import java.util.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private var mNotesList: List<Note> = ArrayList()

    constructor(notesList: List<Note>) {
        mNotesList = notesList
    }

    /**
     * Create a new View and ViewHolder of the list item, which can then be reused
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NotesAdapter.ViewHolder {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.note_item_layout, viewGroup, false)
        return NotesAdapter.ViewHolder(v);
    }

    /**
     * Filling View widgets with data from a list item numbered i
     */
    override
    fun onBindViewHolder(viewHolder: NotesAdapter.ViewHolder, i: Int) {
        var note = mNotesList[i];
        viewHolder.mNoteTitle.text = note.title;
        viewHolder.mNoteDate.text = formatDate(note.changeDate)
    }

    /**
     * Returns the number of elements
     */
    override fun getItemCount(): Int {
        return mNotesList.size
    }

    /**
     * Implementation of the ViewHolder class, which stores links to widgets.
     */
    class ViewHolder : RecyclerView.ViewHolder {

        var mNoteTitle: TextView
        var mNoteDate: TextView

        constructor(itemView: View) : super(itemView) {
            mNoteTitle = itemView.findViewById(R.id.tvItemNoteTitle) as TextView
            mNoteDate = itemView.findViewById(R.id.tvItemNoteDate) as TextView
        }

    }

}
