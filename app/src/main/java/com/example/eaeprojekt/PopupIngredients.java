package com.example.eaeprojekt;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.IngredientDTO;

import java.util.ArrayList;
import java.util.List;

public class PopupIngredients implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    DatabaseManager db;

    NewRecipeActivity mainActivity;
    View popupView;
    PopupWindow popupWindow;
    //Adapter für Zutaten
    Spinner ingredients;
    ArrayAdapter<String> adapterIngredients;

    FrameLayout frame;

    ArrayList<String> ingredientList;
    
    
    ConstraintLayout cancelButtonIngredient;
    ConstraintLayout addButtonIngredient;

    EditText nameText;
    EditText unitText;
    ConstraintLayout createIngredient;


    ImageView addIngredientCross;

    public void showPopupWindow(final View view, NewRecipeActivity newRecipeActivity) {


        mainActivity = newRecipeActivity;

        db = new DatabaseManager(mainActivity);
        db.open();


        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_ingredients_popup, null);
        this.popupView = popupView;

        //length and width from the Window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        //Create window
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Spinner Zutaten
        ingredients = popupView.findViewById(R.id.spinnerIngredient);

/*
        adapterIngredients = ArrayAdapter.createFromResource(mainActivity, R.array.zutat, android.R.layout.simple_spinner_dropdown_item);
        ingredients.setAdapter(adapterIngredients);
 */

        //Spinner mit daten aus db füllen
        ingredientList = new ArrayList<>();

        List<IngredientDTO> allIngredients = db.getAllIngredients();
        for(IngredientDTO newIngredient : allIngredients){
            Log.d("spinner"," "+ newIngredient.getName() + " " + newIngredient.getUnit());
            ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
            Log.d("spinner"," "+ newIngredient.getName() + " " + newIngredient.getUnit() + " wurde hinzugefügt");
        }

        adapterIngredients = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item, ingredientList);
        ingredients.setAdapter(adapterIngredients);

        //Spinner gefüllt
        ingredients.setOnItemSelectedListener(mainActivity);


        addIngredientCross = popupView.findViewById(R.id.addIngredientCross);
        addIngredientCross.setOnClickListener(this);

        createIngredient = popupView.findViewById(R.id.createIngredient);
        createIngredient.setOnClickListener(this);
        
        
        
        cancelButtonIngredient = popupView.findViewById(R.id.cancel_button_ingredient);
        cancelButtonIngredient.setOnClickListener(this);

        addButtonIngredient = popupView.findViewById(R.id.add_button_ingredient);
        addButtonIngredient.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d("Was", "Hallo? " + adapterView.getItemAtPosition(position));


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        frame = mainActivity.findViewById(R.id.mainmenu);
        ConstraintLayout layoutAddIngredient = popupView.findViewById(R.id.addIngredientLayout);

        nameText = popupView.findViewById(R.id.nameText);
        String nameTextString = nameText.getText().toString();

        unitText = popupView.findViewById(R.id.unitText);
        String unitTextString = unitText.getText().toString();

        if (view == addIngredientCross){

            if(layoutAddIngredient.getVisibility() == View.VISIBLE){
                layoutAddIngredient.setVisibility(View.INVISIBLE);
            }else {
                layoutAddIngredient.setVisibility(View.VISIBLE);
            }
        }else if(view == createIngredient){

            if(!nameTextString.isEmpty() && !unitTextString.isEmpty()){
                db.insertIngredient(nameTextString, unitTextString);

                ingredientList.add(nameTextString + ", " + unitTextString);
                adapterIngredients.notifyDataSetChanged();

                layoutAddIngredient.setVisibility(View.INVISIBLE);


            }else{
                Toast toast = new Toast(mainActivity);
                toast.setText("Füllen sie zuerst die Felder aus");
                toast.show();
            }

        }else if (view == cancelButtonIngredient) {
            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();
        } else if (view == addButtonIngredient) {

            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();

            //TODO

            long ingredientId = db.insertIngredientQuantity(NewRecipeActivity.newRecipeId, );

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
            stepDescriptionText.setText(nameTextString);
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
                db.deleteStep(ingredientId);
                parentLayout.removeView(layout);
            });

        }
    }
}
