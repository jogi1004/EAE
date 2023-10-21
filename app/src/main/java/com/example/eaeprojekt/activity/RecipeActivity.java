package com.example.eaeprojekt.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class RecipeActivity extends AppCompatActivity {

    private LinearLayout recipeLayout;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeLayout = findViewById(R.id.recipeLayoutinScrollView);
        db = new DatabaseManager(this);
        db.open();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.recipeListButtonNavBar);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        SwitchCompat mySwitch = findViewById(R.id.switch1);
        updateRecipeList(db.getAllRecipes());
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateRecipeList(db.getFavoritenRezepte());
            } else {
                updateRecipeList(db.getAllRecipes());
            }
        });
    }

    private void updateRecipeList(List<RecipeDTO> recipes) {
        recipeLayout.removeAllViews();

        for (RecipeDTO recipe : recipes) {
            RelativeLayout recipeItem = new RelativeLayout(this);
            recipeItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            /**
             * Creating Backgroundshape with rounded Corners
             */
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(30); // Radius für abgerundete Ecken in Pixeln
            shape.setColor(getResources().getColor(R.color.darkerYellow));

            /**
             * Picture of the Recipe
             */
            ImageView picture = new ImageView(this);
            picture.setImageResource(R.drawable.testbild1);
            picture.setId(View.generateViewId());  // Set a unique ID for the picture

            RelativeLayout.LayoutParams pictureParams = new RelativeLayout.LayoutParams(300, 300);
            pictureParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);  // Align the picture to the left
            pictureParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center the picture vertically
            picture.setLayoutParams(pictureParams);
            picture.setPadding(30, 20, 0, 20);

            LinearLayout dataLayout = new LinearLayout(this);
            dataLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dataParams.addRule(RelativeLayout.RIGHT_OF, picture.getId());  // Position dataLayout to the right of picture
            dataParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center dataLayout vertically
            dataLayout.setLayoutParams(dataParams);

            int marginInDp = (int) getResources().getDimension(R.dimen.margin_70dp);
            dataLayout.setPadding(50, 0, marginInDp, 50);

            TextView recipeName = new TextView(this);
            recipeName.setText(recipe.getTitle());
            recipeName.setTextSize(25);
            recipeName.setTypeface(null, Typeface.BOLD);

            TextView recipeDetails = new TextView(this);
            recipeDetails.setText(String.format(Locale.GERMANY, "%d Portionen %nDauer: %dmin", recipe.getPortions(), recipe.getDuration()));
            recipeDetails.setTextSize(16);

            ImageView favIcon = new ImageView(this);
            favIcon.setImageResource(recipe.getIsFavorite() == 1 ? R.drawable.favorite_on : R.drawable.favorite_off);
            favIcon.setLayoutParams(new RelativeLayout.LayoutParams(140, 140));
            RelativeLayout.LayoutParams favIconParams = new RelativeLayout.LayoutParams(
                    100,
                    100
            );
            favIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);  // Align the favIcon to the right
            favIconParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center the favIcon vertically
            favIcon.setLayoutParams(favIconParams);
            favIcon.setPadding(0, 20, 0, 0);

            favIcon.setOnClickListener(v -> {
                if (recipe.getIsFavorite() == 1) {
                    recipe.setIsFavorite(0);
                    favIcon.setImageResource(R.drawable.favorite_off);
                } else {
                    recipe.setIsFavorite(1);
                    favIcon.setImageResource(R.drawable.favorite_on);
                }
                db.updateRecipe(recipe.getId(), recipe.getTitle(), recipe.getPortions(), recipe.getDuration(), recipe.getIsFavorite() == 1 ? 0 : 1);
            });

            /**
             * Layout for adding Margins between Recipes
             */
            RelativeLayout.LayoutParams margingLayout = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            margingLayout.setMargins(0,0,0,15);
            recipeItem.setLayoutParams(margingLayout);


            recipeItem.setBackground(shape);
            recipeItem.addView(picture);
            dataLayout.addView(recipeName);
            dataLayout.addView(recipeDetails);
            recipeItem.addView(dataLayout);
            recipeItem.addView(favIcon);
            recipeLayout.addView(recipeItem);

        }

    }
    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.shoppingBagButtonNavBar) {
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);
        }
        return false;
    }
}

