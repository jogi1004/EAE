package com.example.eaeprojekt.popups;

import android.graphics.Color;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

            buttonBack = popupView.findViewById(R.id.cancel_button_ingredient);
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
            db = new DatabaseManager(mainActivity);
            db.open();

            EditText stepDescription = (EditText) view.findViewById(R.id.step_description);

            long stepId = db.insertStep(NewRecipeActivity.newRecipeId, stepDescription.getText().toString());

            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();


            //schrittbeschreibung in der view hinzufügen

            ConstraintLayout layout = new ConstraintLayout(mainActivity);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setBackgroundResource(R.drawable.background_with_rounded_corners_green);
            layout.setPadding(20,20,20,20);
            layout.setLayoutParams(layoutParams);
            layoutParams.setMargins(40, 10, 40, 10);

            // Text der Schrittbeschreibung
            TextView stepDescriptionText = new TextView(mainActivity);
            stepDescriptionText.setId(View.generateViewId());
            stepDescriptionText.setText(stepDescription.getText().toString());
            stepDescriptionText.setGravity(Gravity.CENTER);
            stepDescriptionText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            stepDescriptionText.setLayoutParams(textViewParams);

            layout.addView(stepDescriptionText);

            //Mülleimer
            ImageView trash = new ImageView(mainActivity);
            trash.setImageResource(R.drawable.light_trash_can);
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    70,
                    70
            );
            trash.setLayoutParams(trashParams);
            layout.addView(trash);


            //Constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);

            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.END, trash.getId(), ConstraintSet.START);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);


            constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraintSet.applyTo(layout);


            LinearLayout parentLayout = mainActivity.findViewById(R.id.stepsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v ->{
                db.deleteStep(stepId);
                parentLayout.removeView(layout);
            });

        }
    }
}
