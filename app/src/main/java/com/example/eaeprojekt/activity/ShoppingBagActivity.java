package com.example.eaeprojekt.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eaeprojekt.IngredientAmountDTO;
import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Activity for Showing the ShoppingList with Ingriedients in it, has also an NavBar for Navigation
 */
public class ShoppingBagActivity extends AppCompatActivity {

    FloatingActionButton newShoppingBagItem;
    LinearLayout shoppingLayout;
    DatabaseManager db;
    BottomNavigationView b;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        shoppingLayout = findViewById(R.id.shoppingLayoutinScrollView);
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        db = new DatabaseManager(this);
        db.open();
        List<IngredientAmountDTO> ingredientsOnShoppingList = db.getIngredientsOnShoppingList();


        /**
         * Using DTO to get all Ingredients from the DB
         */
        for (IngredientAmountDTO ingredientAmount : ingredientsOnShoppingList) {

            /**
             * Creating Rectangle Shape with rounded Corners as background
             */
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(30); // Radius für abgerundete Ecken in Pixeln
            shape.setColor(getResources().getColor(R.color.darkerYellow));

            //Creating Layout for single Ingredient
            RelativeLayout ingredientAmountItem = new RelativeLayout(this);
            ingredientAmountItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            ingredientAmountItem.setBackground(shape);
            ingredientAmountItem.setPadding(0,20,0,30);
            RelativeLayout.LayoutParams MarginBetween = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            MarginBetween.setMargins(0,20,0,0);
            ingredientAmountItem.setLayoutParams(MarginBetween);

            long ingredientId = ingredientAmount.getIngredientId();
            IngredientDTO ingredient = db.getIngredientById((int) ingredientId);

            // Saving name, unit and amount of ingredient
            String ingredientName = ingredient.getName();
            String ingredientUnit = ingredient.getUnit();
            double ingredientAmountAmount = ingredientAmount.getAmount();

            /**
             * Creating Layout and Views for Ingredients from DB
             */
            TextView ingNameTextView = new TextView(this);
            ingNameTextView.setText(ingredientName);
            ingNameTextView.setTextSize(20);
            ingNameTextView.setTextColor(getResources().getColor(R.color.fontColor));
            ingNameTextView.setId(View.generateViewId());
            TextView ingAmountTextView = new TextView(this);
            String amountTXT = String.valueOf(ingredientAmountAmount);
            ingAmountTextView.setText(amountTXT);
            ingAmountTextView.setTextSize(20);
            ingAmountTextView.setTextColor(getResources().getColor(R.color.fontColor));
            ingAmountTextView.setId(View.generateViewId());
            TextView ingUnitTextView = new TextView(this);
            ingUnitTextView.setText(ingredientUnit);
            ingUnitTextView.setTextSize(20);
            ingUnitTextView.setTextColor(getResources().getColor(R.color.fontColor));
            ingUnitTextView.setId(View.generateViewId());
            ImageView trashCanIconImageView = new ImageView(this);
            trashCanIconImageView.setImageResource(R.drawable.trash_can);

            /**
             * Changing Layout with params
             */

            RelativeLayout.LayoutParams textViewParamsName = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            textViewParamsName.setMargins(25,20,0,10);

            RelativeLayout.LayoutParams textViewParamsAmount = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParamsAmount.addRule(RelativeLayout.RIGHT_OF, ingNameTextView.getId());
            textViewParamsAmount.setMargins(50,20,0,10);

            RelativeLayout.LayoutParams textViewParamsUnit = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParamsUnit.addRule(RelativeLayout.RIGHT_OF, ingAmountTextView.getId());
            textViewParamsUnit.setMargins(30,20,0,10);

            RelativeLayout.LayoutParams trashCanParams = new RelativeLayout.LayoutParams(
                    100,
                    100
            );
            trashCanParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            trashCanParams.setMargins(0,0,20,0);

            /**
             * Adding LayoutParams to TextViews
             */
            ingNameTextView.setLayoutParams(textViewParamsName);
            ingAmountTextView.setLayoutParams(textViewParamsAmount);
            ingUnitTextView.setLayoutParams(textViewParamsUnit);
            trashCanIconImageView.setLayoutParams(trashCanParams);
            /**
             * Adding TextViews to Layout
             */
            ingredientAmountItem.addView(ingNameTextView);
            ingredientAmountItem.addView(ingAmountTextView);
            ingredientAmountItem.addView(ingUnitTextView);
            ingredientAmountItem.addView(trashCanIconImageView);
            /**
             * Adding Layout to ScrollView
             */
            shoppingLayout.addView(ingredientAmountItem);
        }
        db.close();
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
