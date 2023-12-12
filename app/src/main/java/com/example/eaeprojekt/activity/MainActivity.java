package com.example.eaeprojekt.activity;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
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
        //Abfrage ob auf dem Gerät der Darkmode aktiviert ist
        int darkmode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(darkmode == Configuration.UI_MODE_NIGHT_YES){
            getWindow().setStatusBarColor(Color.rgb(52,73,42));
        } else{
            //Ändert immer die Farbe der StatusBarColor auf die BackgroundFarbe
            getWindow().setStatusBarColor(Color.rgb(255,236,175));
        }

        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);

        //Abfragen der Displaygröße um die Positionierung der Item u.ä. prozentual anzuordnen
        DisplayMetrics dM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dM);
        int height = dM.heightPixels;
        int width = dM.widthPixels;
        Toast.makeText(this, "Höhe des Bildschirms: " + height + " Breite des Bildschirms: " + width, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Abfrage ob auf dem Gerät der Darkmode aktiviert ist
        int darkmode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(darkmode == Configuration.UI_MODE_NIGHT_YES){
            getWindow().setStatusBarColor(Color.rgb(52,73,42));
        } else{
            //Ändert immer die Farbe der StatusBarColor auf die BackgroundFarbe
            getWindow().setStatusBarColor(Color.rgb(255,236,175));
        }
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
            Log.d("CooktIt", "Öffnen der NewRecipeActivity");
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

