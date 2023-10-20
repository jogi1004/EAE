package com.example.eaeprojekt;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

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

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getItemAtPosition(position) == "neue Zutat erstellen"){

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        frame = mainActivity.findViewById(R.id.mainmenu);
        ConstraintLayout layoutAddIngredient = popupView.findViewById(R.id.addIngredientLayout);

        if (view == addIngredientCross){

            if(layoutAddIngredient.getVisibility() == View.VISIBLE){
                layoutAddIngredient.setVisibility(View.INVISIBLE);
            }else {
                layoutAddIngredient.setVisibility(View.VISIBLE);
            }
        }else if(view == createIngredient){
            nameText = popupView.findViewById(R.id.nameText);
            String nameTextString = nameText.getText().toString();

            unitText = popupView.findViewById(R.id.unitText);
            String unitTextString = unitText.getText().toString();

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
        }
    }
}
