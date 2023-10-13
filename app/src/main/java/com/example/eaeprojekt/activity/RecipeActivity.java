package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.eaeprojekt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecipeActivity extends AppCompatActivity {

    FloatingActionButton newRecipe;
    LinearLayout recipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        newRecipe = findViewById(R.id.newRecipe);
        recipeLayout = findViewById(R.id.recipeLayout);

        newRecipe.setOnClickListener(view -> {
            Intent intent = new Intent(RecipeActivity.this, NewRecipeActivity.class);
            startActivity(intent);
        });
    }
}
