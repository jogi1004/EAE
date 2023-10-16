package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    LinearLayout recipeLayout;
    DatabaseManager db;
    ImageButton recipe;
    ImageButton shopping;
    ImageButton addRecipe;
    BottomNavigationView b;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.recipeListButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        recipeLayout = (LinearLayout) findViewById(R.id.recipeLayoutinScrollView);
        db = new DatabaseManager(this);
        db.open();
        List<RecipeDTO> recipes = db.getAllRecipes();
        db.close();



        for (RecipeDTO recipe : recipes) {
// Erstellen Sie ein neues RelativeLayout für jedes Rezept.
            int backgroundHeight = 100;
            int backgroundWidth = recipeLayout.getWidth() -10;
            RelativeLayout recipeItem = new RelativeLayout(this);
            recipeItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                    ));
            ImageView bg = new ImageView(this);
            bg.setLayoutParams(new ViewGroup.LayoutParams(backgroundWidth, backgroundHeight));
            bg.setId(View.generateViewId());


            //Erstellen der ImageView und hinzufügen zum Layout
            ImageView picture = new ImageView(this);
            picture.setImageResource(R.drawable.add_symbol);
            picture.setLayoutParams(new ViewGroup.LayoutParams(100,100));
            picture.setPadding(30,0,10,40);


            LinearLayout dataLayout = new LinearLayout(this);
            dataLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dataParams.addRule(RelativeLayout.RIGHT_OF, picture.getId()); // Daten rechts vom Bild platzieren
            dataParams.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY, picture.getId()); // Daten oben mit dem Bild ausrichten
            dataLayout.setLayoutParams(dataParams);
            int marginInDp = (int) getResources().getDimension(R.dimen.margin_70dp);
            dataLayout.setPadding(marginInDp, 0, marginInDp, 50); // Rechts und links Abstand von 20dp
            TextView recipeName = new TextView(this);
            recipeName.setText(recipe.getTitle());
            recipeName.setTextSize(25);
            recipeName.setTypeface(null, Typeface.BOLD); // Fettschrift hinzufügen
            TextView recipeDetails = new TextView(this);
            recipeDetails.setText(recipe.getPortions() + "Portionen | Dauer: " + recipe.getDuration() +"min");
            recipeDetails.setTextSize(16);


            //Erstellen des Hintergrunds für die einzelnen Rezepte
           /** int backgroundWidth = recipeLayout.getWidth() -10;
            int backgroundHeight = 100;
            RelativeLayout background = new RelativeLayout(this);
            background.setLayoutParams(new ViewGroup.LayoutParams(backgroundWidth,backgroundHeight));
            background.setBackgroundColor(R.color.buttonGrey);
**/
// Erstellen einer ImageView.
            /**
             * int roundImageViewWidth = 100;
             *             int roundImageViewHeight = 1000;
             *             ImageView imageView = new ImageView(this);
             *             imageView.setImageResource(R.color.backgroundGreen); // Bild für die ImageView
             *             imageView.setId(View.generateViewId()); // ID für die View festlegen
             *             imageView.setLayoutParams(new RelativeLayout.LayoutParams(roundImageViewWidth, roundImageViewHeight));
             *             imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Skalieren
             */


// Erstellen Sie ein ImageView für das Rezeptbild und setzen Sie die Größe.
            /**ImageView recipeImage = new ImageView(this);
            recipeImage.setImageResource(R.drawable.burger_andre);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                    200, // Breite des Bildes
                    RelativeLayout.LayoutParams.MATCH_PARENT // Höhe auf MATCH_PARENT setzen
            );
            imageParams.addRule(RelativeLayout.RIGHT_OF, ovalBackground.getId()); // Das Bild rechts vom Oval platzieren
            recipeImage.setLayoutParams(imageParams);
**/
// Erstellen Sie ein LinearLayout für die Daten.
            /** LinearLayout dataLayout = new LinearLayout(this);
            dataLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dataParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId()); // Daten rechts vom Bild platzieren
            dataParams.addRule(RelativeLayout.ALIGN_TOP, imageView.getId()); // Daten oben mit dem Bild ausrichten
            dataLayout.setLayoutParams(dataParams);
            int marginInDp = (int) getResources().getDimension(R.dimen.margin_20dp);
            dataLayout.setPadding(marginInDp, 0, marginInDp, 0); // Rechts und links Abstand von 20dp

// Erstellen Sie einen Text für den Rezeptnamen.
            TextView recipeName = new TextView(this);
            recipeName.setText(recipe.getTitle());
            recipeName.setTextSize(25);
            recipeName.setTypeface(null, Typeface.BOLD); // Fettschrift hinzufügen

// Erstellen Sie einen Text für Dauer und Anzahl der Personen.
            TextView recipeDetails = new TextView(this);
            recipeDetails.setText("Portionen: " + recipe.getPortions() + " | Dauer: " + recipe.getDuration());
            recipeDetails.setTextSize(16);
             **/

// Die Reihenfolge der Hinzufügung ändern, damit der Hintergrund zuerst angezeigt wird.
            recipeItem.addView(bg);
            recipeItem.addView(picture);
            dataLayout.addView(recipeName);
            dataLayout.addView(recipeDetails);
            recipeItem.addView(dataLayout);
            recipeLayout.addView(recipeItem);
        }
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
