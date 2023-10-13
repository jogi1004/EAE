package com.example.eaeprojekt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.PopupSteps;


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

        FrameLayout layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha( 0);
    }

    @Override
    public void onClick(View view) {
        if(view == button_add_ingredients){
            PopupSteps popup = new PopupSteps();
            popup.showPopupWindow(view, this);

            //background-dimming
            FrameLayout layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);
            layout_MainMenu.getForeground().setAlpha( 220);
        }else {

        }
    }
}