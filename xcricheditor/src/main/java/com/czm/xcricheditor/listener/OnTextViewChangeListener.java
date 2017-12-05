package com.czm.xcricheditor.listener;

import android.view.KeyEvent;

/**
 * Created by deejan on 12/4/17.
 */

public interface OnTextViewChangeListener {

  /**
   * Delete current edit text when {@link KeyEvent#KEYCODE_DEL} happens
   * and the text is null
   */
  void onEditTextDelete(int index, String text);

  /**
   * Add edit text after current edit text when {@link KeyEvent#KEYCODE_ENTER}
   * happen
   */
  void onEditTextEnter(int index, String text);

  /**
   * Hide or show item option when text change
   */
  void onTextChange(int index, boolean hasText);

}
