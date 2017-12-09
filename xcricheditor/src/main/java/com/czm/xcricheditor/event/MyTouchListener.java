package com.czm.xcricheditor.event;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by deejan on 12/7/17.
 */

public class MyTouchListener implements View.OnTouchListener {

  @Override public boolean onTouch(View v, MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      ClipData data = ClipData.newPlainText("", "");
      View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
      v.startDrag(data, shadowBuilder, v, 0);
      v.setVisibility(View.INVISIBLE);
      return true;
    } else {
      return false;
    }
  }
}
