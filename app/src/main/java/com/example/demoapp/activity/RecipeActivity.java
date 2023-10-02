package com.example.demoapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.demoapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecipeActivity extends AppCompatActivity {

    FloatingActionButton newRecipe;
    LinearLayout recipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        newRecipe = (FloatingActionButton) findViewById(R.id.newRecipe);
        recipeLayout = (LinearLayout) findViewById(R.id.recipeLayout);
    }
}