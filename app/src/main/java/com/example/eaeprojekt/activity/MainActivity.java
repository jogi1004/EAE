package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout recipes;
    ConstraintLayout shoppingBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Beispieldaten hinzufügen
        DatabaseManager dbMan = new DatabaseManager(this);
        dbMan.open();
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);
        String zutatName1 = "Mehl";
        String zutatName2 = "Milch";
        String zutatEinheit1 = "g";
        String zutatEinheit2 = "ml";
        //dbMan.insertIngredient(zutatName1, zutatEinheit1);
        //dbMan.insertIngredient(zutatName2, zutatEinheit2);
        //dbMan.insertRecipe("Pizza", 4, "30", 1);
        //dbMan.insertRecipe("Pasta", 2, "60", 0);
        logAllIngredients(dbMan);


        // Optional: Schließe die Datenbankverbindung
        dbMan.close();

        recipes = findViewById(R.id.recipes);
        recipes.setOnClickListener(this);

        shoppingBag = findViewById(R.id.shoppingBag);
        shoppingBag.setOnClickListener(this);
    }

    public void logAllIngredients(DatabaseManager dbMan) {
     List<IngredientDTO> ingredients = dbMan.getAllIngredients();
     for (IngredientDTO ingredient : ingredients) {
         Log.d("HSKL", "Name: " + ingredient.getName() + ", Einheit: " + ingredient.getUnit());
     }

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
