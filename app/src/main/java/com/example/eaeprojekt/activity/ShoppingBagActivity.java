package com.example.eaeprojekt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class ShoppingBagActivity extends AppCompatActivity {

    FloatingActionButton newShoppingBagItem;
    LinearLayout shoppingBagLayout;
    DatabaseManager db;
    BottomNavigationView b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        newShoppingBagItem = findViewById(R.id.newShoppingBagEntry);
        shoppingBagLayout = findViewById(R.id.shoppingBagLayout);
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        db = new DatabaseManager(this);
        db.open();
        List<IngredientAmountDTO> ingredientsOnShoppingList = db.getIngredientsOnShoppingList();


        for (IngredientAmountDTO ingredientAmount : ingredientsOnShoppingList) {

            RelativeLayout ingredientAmountItem = new RelativeLayout(this);
            ingredientAmountItem.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));

            long ingredientId = ingredientAmount.getIngredientId();
            IngredientDTO ingredient = db.getIngredientById((int) ingredientId);
            String ingredientName = ingredient.getName();
            String ingredientUnit = ingredient.getUnit();
            double ingredientAmountAmount = ingredientAmount.getAmount();

            // Erstellen und Konfigurieren Sie das Anzeige-TextView für das Zutatennamen.
            TextView ingredientNameTextView = new TextView(this);
            ingredientNameTextView.setText(ingredientName + ": "+ ingredientAmountAmount + ingredientUnit);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            ingredientAmountItem.addView(ingredientNameTextView, textViewParams);

            // Hinzufügen des RelativeLayout zur Layoutansicht.
            shoppingBagLayout.addView(ingredientAmountItem);
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
