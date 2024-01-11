package com.example.eaeprojekt.activity;



import static com.example.eaeprojekt.activity.Shared.showImage;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.List;
import java.util.Random;


/**
 * MainActivity gets started when the App Icon is pressed. It represents the Home Menu with the Navigation Bar at the Bottom.
 * If the Icons in the NavBar get pressed the Activity behind it gets started.
 */
public class MainActivity extends AppCompatActivity {

    BottomNavigationView b;
    DatabaseManager db;
    ImageView imageView;
    TextView titleView;
    LinearLayout randomRecipeHeading, randomRecipeLayout;
    Button reloadButton;
    int oldRandomNumber;

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

        randomRecipeHeading = findViewById(R.id.randomRecipeHeading);
        randomRecipeLayout = findViewById(R.id.randomRecipeLayout);

        imageView = findViewById(R.id.circleViewRecipe);
        titleView = findViewById(R.id.randomRecipeTitle);
        reloadButton = findViewById(R.id.reloadButton);

        reloadButton.setOnClickListener(v -> {
            showRandomRecipe();
                }
        );

        showRandomRecipe();

        //Abfragen der Displaygröße um die Positionierung der Item u.ä. prozentual anzuordnen
        /* DisplayMetrics dM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dM);
        int height = dM.heightPixels;
        int width = dM.widthPixels;
        Toast.makeText(this, "Höhe des Bildschirms: " + height + " Breite des Bildschirms: " + width, Toast.LENGTH_LONG).show();
        */
    }

    public void showRandomRecipe() {
        db = new DatabaseManager(this);
        db.open();
        List<RecipeDTO> allRecipes = db.getAllRecipes();
        db.close();
        allRecipes.removeIf(r -> r.getIsFavorite() == -1);
        allRecipes.removeIf(r -> r.getTitle() == null);

        int maxNumber = allRecipes.size();
        if (maxNumber > 0) {
            reloadButton.setVisibility(View.VISIBLE);
            if (maxNumber == 1) {
                reloadButton.setVisibility(View.INVISIBLE);
            }
            Random rand = new Random();
            int randomNumber;
            do {
                randomNumber = rand.nextInt(maxNumber); // 0 inclusive, maxNumber exclusive
            } while (randomNumber == oldRandomNumber && maxNumber != 1);
            oldRandomNumber = randomNumber;

            // Title
            titleView.setText(allRecipes.get(randomNumber).getTitle());

            // Image
            String path = allRecipes.get(randomNumber).getImagePath();
            imageView.setRotation(0); // reset rotation
            if (path != null && Shared.checkPermission(this, false)) {
                showImage(path, imageView);
            }
        else {
                imageView.setImageResource(R.drawable.camera_small);
            }

            int id = (int) allRecipes.get(randomNumber).getId();
            randomRecipeLayout.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, RecipeDetailViewActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.putExtra("ID", id);
                startActivity(i);
            });

        }
        else {
            randomRecipeLayout.removeAllViews();
        }
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
            /*
             Erstellen eines Intents zum Öffnen der RecipeActivity
            sobald in der Navbar der entsprechende Button gedrückt wurde
             */
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

