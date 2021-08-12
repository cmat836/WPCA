package com.cmat.wpca.ui;

import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class SimplifiedPopupWindow extends PopupWindow {
    boolean clickToDismiss = true;

    public SimplifiedPopupWindow(View content, int layout, boolean focusable, boolean clickToDismiss, int backgroundColor) {
        super(content, layout, layout, focusable);
        this.clickToDismiss = clickToDismiss;
        this.setElevation(20);
        this.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        if (!clickToDismiss) {
            this.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return !checkEventWithinView(view, motionEvent);
                }
            });
        }
    }

    public boolean checkEventWithinView(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int vx = loc[0];
        int vy = loc[1];
        int w = v.getWidth();
        int h = v.getHeight();
        if (x > vx && x < vx + w && y > vy && y < vy + h) {
            return true;
        }
        return false;
    }
}
