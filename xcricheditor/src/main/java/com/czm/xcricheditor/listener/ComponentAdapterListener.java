package com.czm.xcricheditor.listener;

import android.widget.EditText;

/**
 * Created by deejan on 12/4/17.
 */

public interface ComponentAdapterListener {

  void change(int position, EditText editText);

  void enter(int position, String text);

  void delete(int position);

}
