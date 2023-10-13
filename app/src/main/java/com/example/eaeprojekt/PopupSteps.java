package com.example.eaeprojekt;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PopupSteps implements View.OnClickListener {


    ConstraintLayout buttonAdd;
    ConstraintLayout buttonBack;

    com.example.eaeprojekt.NewRecipeActivity mainActivity;

    PopupWindow popupWindow;

    public void showPopupWindow(final View view, com.example.eaeprojekt.NewRecipeActivity newRecipeActivity) {

        mainActivity = newRecipeActivity;

            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.add_steps_popup, null);

            //length and width from the Window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;

            //Create window
            popupWindow = new PopupWindow(popupView, width, height, focusable);

            //Set the location of the window on the screen
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            //Buttons
            buttonAdd = popupView.findViewById(R.id.add_button);
            buttonAdd.setOnClickListener(this);

            buttonBack = popupView.findViewById(R.id.cancel_button);
            buttonBack.setOnClickListener(this);

        }

    @Override
    public void onClick(View view) {

        if(view == buttonBack){
            FrameLayout frame = mainActivity.findViewById(R.id.mainmenu);
            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();
        }
    }
}