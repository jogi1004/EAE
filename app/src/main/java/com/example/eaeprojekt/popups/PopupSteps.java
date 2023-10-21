package com.example.eaeprojekt.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;

public class PopupSteps implements View.OnClickListener {

    DatabaseManager db;

    ConstraintLayout buttonAdd;
    ConstraintLayout buttonBack;
    NewRecipeActivity mainActivity;
    View view;
    PopupWindow popupWindow;
    FrameLayout frame;

    private Context context;

    public PopupSteps(Context context) {
        this.context = context;
    }


    public void showPopupWindow(final View view, NewRecipeActivity newRecipeActivity) {

        mainActivity = newRecipeActivity;

            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.add_steps_popup, null);
            this.view = popupView;

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
    public void onClick(View viewClick) {

        frame = mainActivity.findViewById(R.id.mainmenu);

        if(viewClick == buttonBack){

            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();

        } else if (viewClick == buttonAdd) {

            //datenbankzugriff
            db = new DatabaseManager(context);
            db.open();

            EditText stepDescription = (EditText) view.findViewById(R.id.step_description);

            db.insertStep(-1, stepDescription.getText().toString());

            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();



            //schrittbeschreibung in der view hinzufügen

            RelativeLayout layout = new RelativeLayout(context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            layout.setLayoutParams(layoutParams);

            TextView stepDiscriptionText = new TextView(context);
            stepDiscriptionText.setText(stepDescription.getText().toString());

            layout.addView(stepDiscriptionText);

            LinearLayout parentLayout = mainActivity.findViewById(R.id.stepsLayout);
            parentLayout.addView(layout);

        }
    }
}