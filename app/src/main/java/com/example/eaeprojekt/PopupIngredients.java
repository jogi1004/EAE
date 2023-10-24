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

public class PopupIngredients implements View.OnClickListener {

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

    String choosedIngredient;
    String choosedUnit;

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

        ingredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                String selectedItem = ingredientList.get(position);
                String[] separated = selectedItem.split(", ");
                choosedIngredient = separated[0];
                choosedUnit = separated[1];

                Log.d("Selected Ingredient", selectedItem);

                Log.d("Selected Ingredient", choosedIngredient);
                Log.d("Selected Ingredient", choosedUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Wird aufgerufen, wenn nichts ausgewählt ist
            }
        });

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


            IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(choosedIngredient, choosedUnit);
            Log.d("Selected Ingredient", "Ingredient" + ingredientToAdd.getId() + " " + ingredientToAdd.getName()
                    + " " + ingredientToAdd.getUnit());

            EditText amount = popupView.findViewById(R.id.amount);

            // Zutat zum Rezept hinzufügen
            long ingredientId = db.insertIngredientQuantity(NewRecipeActivity.newRecipeId, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()),0);
            /*
            schrittbeschreibung in der view hinzufügen
             */
            ConstraintLayout layout = new ConstraintLayout(mainActivity);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setPadding(20,20,20,20);
            layout.setLayoutParams(layoutParams);
            layoutParams.setMargins(40, 0, 40, 0);

            /*
            Zutat
             */
            TextView ingredientText = new TextView(mainActivity);
            ingredientText.setId(View.generateViewId());
            ingredientText.setText(ingredientToAdd.getName());
            ingredientText.setGravity(Gravity.CENTER);
            ingredientText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams ingredientParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ingredientText.setLayoutParams(ingredientParams);

            layout.addView(ingredientText);

            /*
            Menge
             */
            TextView amountText = new TextView(mainActivity);
            amountText.setId(View.generateViewId());
            amountText.setText(amount.getText().toString());
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams amountParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            amountText.setLayoutParams(amountParams);

            layout.addView(amountText);

            /*
            Einheit
             */
            TextView unitText = new TextView(mainActivity);
            unitText.setId(View.generateViewId());
            unitText.setText(ingredientToAdd.getUnit());
            unitText.setGravity(Gravity.CENTER);
            unitText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            unitText.setLayoutParams(unitParams);

            layout.addView(unitText);

            /*
            Mülleimer
             */
            ImageView trash = new ImageView(mainActivity);
            trash.setImageResource(R.drawable.light_trash_can);
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    50,
                    50
            );
            trash.setLayoutParams(trashParams);
            layout.addView(trash);

            Log.d("inhalt", "" + ingredientToAdd.getName() + " " + amount + " " + ingredientToAdd.getUnit());

            /*
            Constraints
             */
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);

            //Zutat
            constraintSet.connect(ingredientText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(ingredientText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(ingredientText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Menge
            constraintSet.connect(amountText.getId(), ConstraintSet.START, ingredientText.getId(), ConstraintSet.START, 200);
            constraintSet.connect(amountText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(amountText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Einheit
            constraintSet.connect(unitText.getId(), ConstraintSet.START, amountText.getId(), ConstraintSet.START, 50);
            constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Mülleimer
            constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraintSet.applyTo(layout);


            LinearLayout parentLayout = mainActivity.findViewById(R.id.ingredientsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v ->{
                db.deleteIngredientQuantity(ingredientId);
                parentLayout.removeView(layout);
            });


        }
    }
}
