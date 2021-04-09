package com.luckmerlin.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import com.luckmerlin.databinding.ViewBindingBinder;
import com.luckmerlin.databinding.dialog.PopupWindow;
import com.luckmerlin.databinding.view.Touch;

public class MainActivity extends Activity  {

    PopupWindow popupWindow=new PopupWindow(true);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
//        FrameLayout frameLayout=new FrameLayout(this);
//        Button button=new Button(this);
//        button.setText("牛不");
//        new ViewBindingBinder().bind(button,Touch.dispatch(Touch.CLICK));
//        frameLayout.addView(button,new FrameLayout.LayoutParams
//                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        setContentView(frameLayout);
//        button.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ImageButton imageButton=new ImageButton(MainActivity.this);
//                imageButton.setImageResource(R.drawable.ic_menu_normal);
//                popupWindow.setContentView(imageButton);
////                new Dialog(MainActivity.this).setContentView(imageButton).show();
////
//                new ViewBindingBinder().bind(imageButton,Touch.dispatch(Touch.CLICK));
////                //
//                popupWindow.showAsDropDown(button,0,0,PopupWindow.DISMISS_INNER_MASK);
//            }
//        },5000);
    }

}
