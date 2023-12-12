package com.example.eaeprojekt.activity;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * MainActivity gets started when the App Icon is pressed. It represents the Home Menu with the Navigation Bar at the Bottom.
 * If the Icons in the NavBar get pressed the Activity behind it gets started.
 */
public class MainActivity extends AppCompatActivity {

    BottomNavigationView b;
    DatabaseManager db;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navbar);

        // Beispieldaten hinzufügen
        //DatabaseManager dbMan = new DatabaseManager(this);
        //dbMan.open();
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);

        // Optional: Schließe die Datenbankverbindung
        //dbMan.close();
        //dbMan.open();

        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        /*DatabaseManager db = new DatabaseManager(this);
        db.open();
        long x = db.insertIngredient("Spargel", "Stangen");
        db.insertIngredientQuantity(-1, x, 300, 1, 0);
        db.insertIngredientQuantity(-1, x, 200, 1, 1);
        db.close();*/

    }

    public void logAllIngredients(DatabaseManager dbMan) {
        List<IngredientDTO> ingredients = dbMan.getAllIngredients();
        for (IngredientDTO ingredient : ingredients) {
            Log.d("HSKL", "Name: " + ingredient.getName() + ", Einheit: " + ingredient.getUnit());
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    /**
     * Method to get the Item in the NavBar and start the Activity.
     * @param item is the Menu Item in which the Buttons for the Bar get declarated
     * @return boolean true or false if pressed or not
     */
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

