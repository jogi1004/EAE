package com.example.demoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.activity.RecipeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout recipes;
    ConstraintLayout shoppingBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipes = findViewById(R.id.recipes);
        recipes.setOnClickListener(this);

        shoppingBag = findViewById(R.id.shoppingBag);
        shoppingBag.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == recipes){
            Intent recipeIntent = new Intent(this, RecipeActivity.class);
            startActivity(recipeIntent);
        }
        if (view == shoppingBag){

        }

    }
}