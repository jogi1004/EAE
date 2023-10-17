package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
            }
            else {
                updateRecipeList(db.getAllRecipes());
            }

        });
    }

    private void updateRecipeList(List<RecipeDTO> recipes) {
        Log.d("HSKL", "updateList");
        recipeLayout.removeAllViews();

        for (RecipeDTO recipe : recipes) {
            int backgroundHeight = 100;
            int backgroundWidth = recipeLayout.getWidth() - 10;

            RelativeLayout recipeItem = new RelativeLayout(this);
            recipeItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));

            ImageView bg = new ImageView(this);
            bg.setLayoutParams(new ViewGroup.LayoutParams(backgroundWidth, backgroundHeight));
            bg.setId(View.generateViewId());

            ImageView picture = new ImageView(this);
            picture.setImageResource(R.drawable.add_symbol);
            picture.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
            picture.setPadding(30, 0, 10, 40);

            LinearLayout dataLayout = new LinearLayout(this);
            dataLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dataParams.addRule(RelativeLayout.RIGHT_OF, picture.getId());
            dataParams.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY, picture.getId());
            dataLayout.setLayoutParams(dataParams);

            int marginInDp = (int) getResources().getDimension(R.dimen.margin_70dp);
            dataLayout.setPadding(marginInDp, 0, marginInDp, 50);

            TextView recipeName = new TextView(this);
            recipeName.setText(recipe.getTitle());
            recipeName.setTextSize(25);
            recipeName.setTypeface(null, Typeface.BOLD);

            TextView recipeDetails = new TextView(this);
            recipeDetails.setText(String.format(Locale.GERMANY, "%d Portionen | Dauer: %dmin", recipe.getPortions(), recipe.getDuration()));
            recipeDetails.setTextSize(16);

            recipeItem.addView(bg);
            recipeItem.addView(picture);
            dataLayout.addView(recipeName);
            dataLayout.addView(recipeDetails);
            recipeItem.addView(dataLayout);
            recipeLayout.addView(recipeItem);
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.recipeListButtonNavBar) {
            //Beim andrücken des Buttons öffnen der RecipeActivity
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.shoppingBagButtonNavBar) {
            //Öffnen der Einkaufsliste
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);

        }
        return false;
    }
}
