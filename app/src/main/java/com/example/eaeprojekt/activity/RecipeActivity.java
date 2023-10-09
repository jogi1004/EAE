package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    FloatingActionButton newRecipe;
    LinearLayout recipeLayout;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        newRecipe = (FloatingActionButton) findViewById(R.id.newRecipe);
        recipeLayout = (LinearLayout) findViewById(R.id.recipeLayout);
        db = new DatabaseManager(this);
        db.open();
        List<RecipeDTO> recipes = db.getAllRecipes();
        db.close();



        for (RecipeDTO recipe : recipes) {
// Erstellen Sie ein neues RelativeLayout für jedes Rezept.
            RelativeLayout recipeItem = new RelativeLayout(this);
            recipeItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));

// Erstellen Sie das Hintergrund-Oval und setzen Sie die Hintergrundfarbe.
            int ovalSizeWidth = getResources().getDimensionPixelSize(R.dimen.oval_size_width);
            int ovalSizeHeight = getResources().getDimensionPixelSize(R.dimen.oval_size_height);
            ImageView ovalBackground = new ImageView(this);
            ovalBackground.setImageResource(R.drawable.oval_background); // Bild für das Hintergrund-Oval
            ovalBackground.setId(View.generateViewId()); // ID für das Oval festlegen
            ovalBackground.setLayoutParams(new RelativeLayout.LayoutParams(ovalSizeWidth, ovalSizeHeight));
            ovalBackground.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Skalieren, um das Oval zu füllen

// Erstellen Sie ein ImageView für das Rezeptbild und setzen Sie die Größe.
            ImageView recipeImage = new ImageView(this);
            recipeImage.setImageResource(R.drawable.burger_andre);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                    200, // Breite des Bildes
                    RelativeLayout.LayoutParams.MATCH_PARENT // Höhe auf MATCH_PARENT setzen
            );
            imageParams.addRule(RelativeLayout.RIGHT_OF, ovalBackground.getId()); // Das Bild rechts vom Oval platzieren
            recipeImage.setLayoutParams(imageParams);

// Erstellen Sie ein LinearLayout für die Daten.
            LinearLayout dataLayout = new LinearLayout(this);
            dataLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dataParams.addRule(RelativeLayout.RIGHT_OF, recipeImage.getId()); // Daten rechts vom Bild platzieren
            dataParams.addRule(RelativeLayout.ALIGN_TOP, recipeImage.getId()); // Daten oben mit dem Bild ausrichten
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

// Die Reihenfolge der Hinzufügung ändern, damit der Hintergrund zuerst angezeigt wird.
            recipeItem.addView(ovalBackground);
            recipeItem.addView(recipeImage);
            dataLayout.addView(recipeName);
            dataLayout.addView(recipeDetails);
            recipeItem.addView(dataLayout);
            recipeLayout.addView(recipeItem);
        }
    }
}