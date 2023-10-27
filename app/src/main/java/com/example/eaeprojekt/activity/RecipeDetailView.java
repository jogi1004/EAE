package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailView extends AppCompatActivity {

    DatabaseManager db;
    RecipeDTO rDTO;
    BottomNavigationView b;
    String recipeTitle, imagePath;
    int portions, time, isFavorite, recipeid;
    LinearLayout ingredientsLayout;
    GridLayout durationLayout;
    List<IngredientAmountDTO> iADTO;
    List<IngredientDTO> iDTO;
    long idUsed = 0;
    List<IngredientDTO> ingredientsForRecipe = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        Intent receive = getIntent();
        recipeid = receive.getIntExtra("ID", 0);
        Log.d("CookIt", "Id aus Intent geholt: " + recipeid);
        /**
         * Open and close DB for getting Ingredients
         */
        db = new DatabaseManager(this);
        db.open();
        Log.d("CookIt", "DB geöffnet und DBManager erstellt.");
        //Alle Zutaten aus DB lesen
        iDTO = db.getAllIngredients();
        //Alle Zutaten, die für das Rezept benötigt werden (Name und Unit)
        iADTO = db.getIngredientsForRecipe(recipeid);

        for (IngredientDTO ingredient : iDTO) {
            long id = ingredient.getId();
            for (IngredientAmountDTO i : iADTO) {
                idUsed = i.getIngredientId();

                if (id == idUsed) {
                    ingredientsForRecipe.add(ingredient);
                }
            }
        }

        for (IngredientDTO ingredient : ingredientsForRecipe) {
            Log.d("CookIt", "ingredientsForRecipe: " + ingredient.getName() + "\n");
        }
        rDTO = db.getRecipeById(recipeid);
        recipeTitle = rDTO.getTitle();
        time = rDTO.getDuration();
        portions = rDTO.getPortions();
        isFavorite = rDTO.getIsFavorite();
        imagePath = rDTO.getImagePath();
        Log.d("CookIt", "DTO erhalten.");
        //db.close();
        //Log.d("CookIt", "DB geschlossen.");

        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        durationLayout = findViewById(R.id.durationLayout);

        /**
         * LayoutParams for TextViews in IngredientsLayout
         */
        RelativeLayout.LayoutParams textViewParamsName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsName.setMargins(25, 10, 60, 10);

        RelativeLayout.LayoutParams textViewParamsIngredientHeader = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsIngredientHeader.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsIngredientHeader.setMargins(50, 10, 0, 10);

        /**
         * Set Title to Recipe Name
         */
        TextView RecipeTitle = findViewById(R.id.RecipeName);
        RecipeTitle.setText(recipeTitle);
        /**
         * Creating TextViews for Ingredients
         */
        TextView ingredientsHeader = new TextView(this);
        ingredientsHeader.setText("Zutaten");
        ingredientsHeader.setLayoutParams(textViewParamsIngredientHeader);
        ingredientsLayout.addView(ingredientsHeader);

        LinearLayout ingredientsLinearLayout = new LinearLayout(this);
        ingredientsLinearLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientsLayout.addView(ingredientsLinearLayout);

        /**
         * Creating TextViews and Icons for durationView
         */
        TextView durationView = new TextView(this);
        durationView.setLayoutParams(textViewParamsName);
        durationView.setText(time + "min");
        durationView.setTextColor(Color.WHITE);
        TextView portionsView = new TextView(this);
        portionsView.setLayoutParams(textViewParamsName);
        if (portions > 1) {
            portionsView.setText(portions + " Portionen");
        } else {
            portionsView.setText(portions + " Portion");
        }
        portionsView.setTextColor(Color.WHITE);

        ImageView durationIcon = new ImageView(this);
        durationIcon.setImageResource(R.drawable.baseline_timer_24);
        durationIcon.setPadding(20, 0, 0, 0);
        ImageView portionsIcon = new ImageView(this);
        portionsIcon.setImageResource(R.drawable.baseline_person_24);


        /**
         * Adding Views to GridLayout
         */
        durationLayout.addView(durationIcon);
        durationLayout.addView(durationView);
        durationLayout.addView(portionsIcon);
        durationLayout.addView(portionsView);


        for (IngredientDTO ingredient : ingredientsForRecipe) {
            // Erstellen Sie ein horizontales Layout für Name, Einheit und Menge
            LinearLayout ingredientLayout = new LinearLayout(this);
            ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);

            // TextView für den Namen
            TextView ingredientName = new TextView(this);
            ingredientName.setText(ingredient.getName());
            ingredientName.setPadding(75, 10, 0, 10);

            // TextView für die Einheit
            TextView ingredientUnit = new TextView(this);
            ingredientUnit.setText(ingredient.getUnit());
            ingredientUnit.setPadding(20, 0, 0, 0); // Ändern Sie die Padding-Werte nach Bedarf

            ingredientLayout.addView(ingredientName);

            // Durchlaufen Sie iADTO, um die Menge für das aktuelle IngredientDTO zu finden
            for (IngredientAmountDTO i : iADTO) {
                if (i.getIngredientId() == ingredient.getId()) {
                    // TextView für die Menge
                    TextView ingredientAmount = new TextView(this);
                    ingredientAmount.setText(String.valueOf(i.getAmount()));
                    ingredientAmount.setPadding(20, 0, 0, 0); // Ändern Sie die Padding-Werte nach Bedarf

                    // Fügen Sie den TextView für die Menge zum horizontalen Layout hinzu
                    ingredientLayout.addView(ingredientAmount);
                    break;
                }
            }

            // Fügen Sie die TextViews für Name und Einheit zum horizontalen Layout hinzu

            ingredientLayout.addView(ingredientUnit);

            // Fügen Sie das horizontale Layout zum vertikalen Layout hinzu
            ingredientsLinearLayout.addView(ingredientLayout);
        }
    }
        private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            /**
             Creating Intent for starting NewRecipeActivity
             */
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.recipeListButtonNavBar) {
            /**
             Creating Intent for starting RecipeActivity
             */
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
        }
        if(id == R.id.shoppingBagButtonNavBar){
            /**
             * Creating Intent for starting ShoppingBagLayout
             */
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);
        }
        return false;
    }
}