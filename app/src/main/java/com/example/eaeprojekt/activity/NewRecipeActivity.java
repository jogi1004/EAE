package com.example.eaeprojekt.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.classes.PopupSteps;


public class NewRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ConstraintLayout button_add_ingredients;
    ConstraintLayout button_add_steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_add_ingredients = (ConstraintLayout) findViewById(R.id.button_add_ingredients);
        button_add_ingredients.setOnClickListener(this);

        button_add_steps = (ConstraintLayout) findViewById(R.id.button_add_steps);
        button_add_steps.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == button_add_ingredients){
            PopupSteps popup = new PopupSteps();
            popup.showPopupWindow(view);
        }else {

        }
    }
}