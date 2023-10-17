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
import com.example.eaeprojekt.activity.RecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton recipe;
    ImageButton shoppingBag;
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
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);
        List<Long> ids = new ArrayList<>();
        ids.add(dbMan.insertIngredient(zutatName1, zutatEinheit1));
        ids.add(dbMan.insertIngredient(zutatName2, zutatEinheit2));
        List<Long> idds = new ArrayList<>();
        idds.add(dbMan.insertIngredientQuantity(-1, ids.get(0), 201, 1));
        idds.add(dbMan.insertIngredientQuantity(-1, ids.get(1), 350, 1));

        // Optional: Schließe die Datenbankverbindung
        dbMan.close();

        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);

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
        if (view == addRecipe) {
            //Öffnen der Hinzufügen Activity
        }
        if (view == shoppingBag) {
            Intent shoppingBagIntent = new Intent(this, ShoppingBagActivity.class);
            startActivity(shoppingBagIntent);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
            //Öffne ADDActivity
        }
        if (id == R.id.recipeListButtonNavBar) {
            /**
             Erstellen eines Intents zum Öffnen der RecipeActivity
            sobald in der Navbar der entsprechende Button gedrückt wurde
             **/
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.shoppingBagButtonNavBar) {
            //Öffnen der Einkaufsliste
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);

        }
        return false;
    }
}

