package com.example.eaeprojekt.activity;

import static com.example.eaeprojekt.R.id.AddButtonNavBar;
import static com.example.eaeprojekt.R.id.recipeListButtonNavBar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton recipe;
    ImageButton shopping;
    ImageButton addRecipe;
    BottomNavigationView b;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navbar);

        // Beispieldaten hinzufügen
        DatabaseManager dbMan = new DatabaseManager(this);
        dbMan.open();
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);
        String zutatName1 = "Mehl";
        String zutatName2 = "Milch";
        String zutatEinheit1 = "g";
        String zutatEinheit2 = "ml";
        dbMan.insertIngredient(zutatName1, zutatEinheit1);
        dbMan.insertIngredient(zutatName2, zutatEinheit2);
        dbMan.insertRecipe("Pizza", 4, "30", 1);
        dbMan.insertRecipe("Pasta", 2, "60", 0);
        logAllIngredients(dbMan);


        // Optional: Schließe die Datenbankverbindung
        dbMan.close();

        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        //Verknüpfen der ImageButtons die in der XML eine ID erhalten haben
        //recipe = findViewById(R.id.recipeListButtonNavBar);
        //shopping = findViewById(R.id.shoppingBagButtonNavBar);
        //addRecipe = findViewById(R.id.AddButtonNavBar);
        //On Click Listener Hinzufügen um das antippen erkennen zu könnens
        //recipe.setOnClickListener(this);
        //shopping.setOnClickListener(this);
        //addRecipe.setOnClickListener(this);

    }

    public void logAllIngredients(DatabaseManager dbMan) {
        List<IngredientDTO> ingredients = dbMan.getAllIngredients();
        for (IngredientDTO ingredient : ingredients) {
            Log.d("HSKL", "Name: " + ingredient.getName() + ", Einheit: " + ingredient.getUnit());
        }

    }

    @Override
    public void onClick(View view) {
        if (view == recipe) {
            Intent recipeIntent = new Intent(this, RecipeActivity.class);
            startActivity(recipeIntent);
            finish();
        }
        if (view == shopping) {
            //Öffnen der Einkaufsliste
        }
        if (view == addRecipe) {
            //Öffnen der Hinzufügen Activity
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            //Öffne ADDActivity
        }
        if (id == R.id.recipeListButtonNavBar) {
            /**
             Erstellen eines Intents zum Öffnen der RecipeActivity
            sobald in der Navbar der entsprechende Button gedrückt wurde
             **/
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.shoppingBagButtonNavBar) {
            //Öffnen der Einkaufsliste

        }
        return false;
    }
}

