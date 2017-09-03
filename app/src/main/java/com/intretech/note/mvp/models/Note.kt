package com.intretech.note.mvp.models

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.intretech.note.utils.formatDate
import java.util.*

@Table(name = "Notes")
class Note : Model {

    @Column(name = "title")
    var title: String? = null
    @Column(name = "text")
    var text: String? = null
    @Column(name = "create_date")
    var createDate: Date? = null
    @Column(name = "change_date")
    var changeDate: Date? = null

    constructor(title: String, createDate: Date) {
        this.title = title
        this.createDate = createDate
        this.changeDate = createDate
    }

    constructor()

    fun getInfo(): String = "Name:\n$title\n" +
            "Time of creation:\n${formatDate(createDate)}\n" +
            "Change Date:\n${formatDate(changeDate)}";
}