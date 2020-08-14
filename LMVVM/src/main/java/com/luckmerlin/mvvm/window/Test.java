package com.luckmerlin.mvvm.window;

import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Test {

    public final boolean bindWindow(Window window){
        WindowManager manager;
        if (null!=window){
            window.setCallback(new Window.Callback() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    return false;
                }

                @Override
                public boolean dispatchKeyShortcutEvent(KeyEvent event) {
                    return false;
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent event) {
                    return false;
                }

                @Override
                public boolean dispatchTrackballEvent(MotionEvent event) {
                    return false;
                }

                @Override
                public boolean dispatchGenericMotionEvent(MotionEvent event) {
                    return false;
                }

                @Override
                public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                    return false;
                }

                @Nullable
                @Override
                public View onCreatePanelView(int featureId) {
                    return null;
                }

                @Override
                public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
                    return false;
                }

                @Override
                public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
                    return false;
                }

                @Override
                public boolean onMenuOpened(int featureId, @NonNull Menu menu) {
                    return false;
                }

                @Override
                public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
                    return false;
                }

                @Override
                public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {

                }

                @Override
                public void onContentChanged() {

                }

                @Override
                public void onWindowFocusChanged(boolean hasFocus) {

                }

                @Override
                public void onAttachedToWindow() {

                }

                @Override
                public void onDetachedFromWindow() {

                }

                @Override
                public void onPanelClosed(int featureId, @NonNull Menu menu) {

                }

                @Override
                public boolean onSearchRequested() {
                    return false;
                }

                @Override
                public boolean onSearchRequested(SearchEvent searchEvent) {
                    return false;
                }

                @Nullable
                @Override
                public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                    return null;
                }

                @Nullable
                @Override
                public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
                    return null;
                }

                @Override
                public void onActionModeStarted(ActionMode mode) {

                }

                @Override
                public void onActionModeFinished(ActionMode mode) {

                }
            });
        }
        return false;
    }
}
