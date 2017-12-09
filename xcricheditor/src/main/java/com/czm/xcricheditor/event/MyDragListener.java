package com.czm.xcricheditor.event;

import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by deejan on 12/7/17.
 */

public class MyDragListener implements View.OnDragListener {
  android.widget.RelativeLayout.LayoutParams layoutParams;

  @Override public boolean onDrag(View v, DragEvent event) {
    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        break;

      case DragEvent.ACTION_DRAG_ENTERED:
        int x_cord = (int) event.getX();
        int y_cord = (int) event.getY();
        break;

      case DragEvent.ACTION_DRAG_EXITED:
        x_cord = (int) event.getX();
        y_cord = (int) event.getY();
        layoutParams.leftMargin = x_cord;
        layoutParams.rightMargin = y_cord;
        v.setLayoutParams(layoutParams);
        break;

      case DragEvent.ACTION_DRAG_LOCATION:
        x_cord = (int) event.getX();
        y_cord = (int) event.getY();
        break;

      default:
        break;
    }
    return true;
  }
}
