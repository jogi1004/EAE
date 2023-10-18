package com.example.eaeprojekt.activity;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity gets started when the App Icon is pressed. It represents the Home Menu with the Navigation Bar at the Bottom.
 * If the Icons in the NavBar get pressed the Activity behind it gets started.
 */
public class MainActivity extends AppCompatActivity {

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

