package com.example.eaeprojekt;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;

import java.util.List;

public class PopupIngredients implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    DatabaseManager db;

    NewRecipeActivity mainActivity;
    View popupView;
    PopupWindow popupWindow;
    //Adapter für Zutaten
    ArrayAdapter<CharSequence> adapterIngredients;

    FrameLayout frame;
    
    
    ConstraintLayout cancelButtonIngredient;

    ImageView addIngredientCross;

    public void showPopupWindow(final View view, NewRecipeActivity newRecipeActivity) {

        mainActivity = newRecipeActivity;

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
        Spinner ingredients = popupView.findViewById(R.id.spinnerIngredient);
        adapterIngredients = ArrayAdapter.createFromResource(mainActivity, R.array.zutat, android.R.layout.simple_spinner_dropdown_item);
        ingredients.setAdapter(adapterIngredients);
        ingredients.setOnItemSelectedListener(mainActivity);

        addIngredientCross = popupView.findViewById(R.id.addIngredientCross);
        addIngredientCross.setOnClickListener(this);
        
        
        
        cancelButtonIngredient = popupView.findViewById(R.id.cancel_button_ingredient);
        cancelButtonIngredient.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        db = new DatabaseManager(mainActivity);
        db.open();
        if(adapterView.getItemAtPosition(position) == "neue Zutat erstellen"){

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        frame = mainActivity.findViewById(R.id.mainmenu);

        if (view == addIngredientCross){
            ConstraintLayout layout = popupView.findViewById(R.id.addIngredientLayout);

            if(layout.getVisibility() == View.VISIBLE){
                layout.setVisibility(View.INVISIBLE);
            }else {
                layout.setVisibility(View.VISIBLE);
            }
        } else if (view == cancelButtonIngredient) {
            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();
        }
    }
}
