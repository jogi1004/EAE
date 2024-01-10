package com.example.eaeprojekt.utility;

import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class StepDialogUtil{


        static DatabaseManager db;
        static Dialog dialog;
        static ConstraintLayout buttonAdd;
        static ConstraintLayout buttonBack;
        static NewRecipeActivity mainActivity;

        public static void showPopupWindow(NewRecipeActivity newRecipeActivity) {

            mainActivity = newRecipeActivity;

            dialog = new Dialog(mainActivity);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(true);

            dialog.setContentView(R.layout.add_steps_popup);

            dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            //Buttons
            buttonAdd = dialog.findViewById(R.id.add_button);
            buttonAdd.setOnClickListener((v)->{
                addStep();
            });

            buttonBack = dialog.findViewById(R.id.cancel_button_ingredient);
            buttonBack.setOnClickListener((v)-> {
                dialog.dismiss();
            });

            dialog.show();
        }

        public static void addStep(){


                //datenbankzugriff
                db = new DatabaseManager(mainActivity);
                db.open();

                EditText stepDescription = (EditText) dialog.findViewById(R.id.step_description);

                long stepId = db.insertStep(NewRecipeActivity.newRecipeId, stepDescription.getText().toString());

                dialog.dismiss();


                //schrittbeschreibung in der view hinzufügen

                ConstraintLayout layout = new ConstraintLayout(mainActivity);

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                layout.setBackgroundResource(R.drawable.background_with_rounded_corners_green);
                layout.setPadding(20,20,20,20);
                layout.setLayoutParams(layoutParams);
                layoutParams.setMargins(20, 10, 20, 10);

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
                trash.setImageResource(R.drawable.trashcan_light);
                trash.setId(View.generateViewId());

                ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                        45,
                        45
                );
                trash.setLayoutParams(trashParams);
                layout.addView(trash);


                //Constraints
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20);
                constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.END, trash.getId(), ConstraintSet.START, 20);
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

