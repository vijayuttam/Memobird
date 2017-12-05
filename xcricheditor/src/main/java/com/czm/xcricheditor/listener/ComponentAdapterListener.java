package com.czm.xcricheditor.listener;

import com.czm.xcricheditor.editor.NoteEditText;

/**
 * Created by deejan on 12/4/17.
 */

public interface ComponentAdapterListener {

  void change(int position, NoteEditText editText);

  void delete(int position);

}
