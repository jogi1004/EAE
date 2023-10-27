package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eaeprojekt.IngredientAmountDTO;
import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.List;

public class RecipeDetailView extends AppCompatActivity {

    DatabaseManager db;
    RecipeDTO rDTO;
    BottomNavigationView b;
    String recipeTitle, imagePath;
    int portions, time, isFavorite, recipeid;
    LinearLayout ingredientsLayout;
    GridLayout durationLayout;
    List<IngredientAmountDTO> iADTO;
    List<IngredientDTO> iDTO;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        Intent receive = getIntent();
        recipeid = receive.getIntExtra("ID",0);
        Log.d("CookIt", "Id aus Intent geholt: "+ recipeid);
        /**
         * Open and close DB for getting Ingredients
         */
        db = new DatabaseManager(this);
        db.open();
        Log.d("CookIt", "DB geöffnet und DBManager erstellt.");
        iADTO = db.getIngredientsForRecipe(recipeid);
        iDTO = db.getAllIngredients();
        rDTO = db.getRecipeById(recipeid);
        recipeTitle = rDTO.getTitle();
        time = rDTO.getDuration();
        portions = rDTO.getPortions();
        isFavorite = rDTO.getIsFavorite();
        imagePath = rDTO.getImagePath();
        Log.d("CookIt", "DTO erhalten.");
        //db.close();
        //Log.d("CookIt", "DB geschlossen.");

        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        durationLayout = findViewById(R.id.durationLayout);

        /**
         * LayoutParams for TextViews in IngredientsLayout
         */
        RelativeLayout.LayoutParams textViewParamsName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsName.setMargins(25, 10, 0, 10);

        RelativeLayout.LayoutParams textViewParamsIngredientHeader = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsIngredientHeader.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsIngredientHeader.setMargins(50, 10, 0, 10);
        
        /**
         * Set Title to Recipe Name
         */
        TextView RecipeTitle = findViewById(R.id.RecipeName);
        RecipeTitle.setText(recipeTitle);
        /**
         * Creating TextViews for Ingredients
         */
        TextView ingredientsHeader = new TextView(this);
        ingredientsHeader.setText("Zutaten");
        ingredientsHeader.setLayoutParams(textViewParamsIngredientHeader);
        ingredientsLayout.addView(ingredientsHeader);

        LinearLayout ingredientsLinearLayout = new LinearLayout(this);
        ingredientsLinearLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientsLayout.addView(ingredientsLinearLayout);



        /**
         * Creating TextViews and Icons for durationView
         */
        TextView durationView = new TextView(this);
        durationView.setLayoutParams(textViewParamsName);
        durationView.setText(time + "min");
        durationView.setTextColor(Color.WHITE);
        TextView portionsView = new TextView(this);
        portionsView.setLayoutParams(textViewParamsName);
        if(portions > 1) {
            portionsView.setText(portions + " Portionen");
        } else{
            portionsView.setText(portions + " Portion");
        }
        portionsView.setTextColor(Color.WHITE);

        ImageView durationIcon = new ImageView(this);
        durationIcon.setImageResource(R.drawable.baseline_timer_24);
        ImageView portionsIcon = new ImageView(this);
        portionsIcon.setImageResource(R.drawable.baseline_person_24);


        /**
         * Adding Views to GridLayout
         */
        durationLayout.addView(durationIcon);
        durationLayout.addView(durationView);
        durationLayout.addView(portionsIcon);
        durationLayout.addView(portionsView);


        for (IngredientAmountDTO ingredient : iADTO) {
            long ingredientid = ingredient.getIngredientId();
            for (IngredientDTO i : iDTO) {
                TextView t = new TextView(this);
                t.setText(i.getName());
                t.setId(View.generateViewId());
                t.setPadding(75, 10, 0, 10); // Hier können Sie die gewünschten Abstände anpassen
                /**
                 * Adding TextViews to vertical LinearLayout and then the LinearLayout to the ingredientLayout
                 */
                ingredientsLinearLayout.addView(t);
            }
        }
    }
    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            /**
             Creating Intent for starting NewRecipeActivity
             */
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.recipeListButtonNavBar) {
            /**
             Creating Intent for starting RecipeActivity
             */
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
        }
        if(id == R.id.shoppingBagButtonNavBar){
            /**
             * Creating Intent for starting ShoppingBagLayout
             */
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);
        }
        return false;
    }
}