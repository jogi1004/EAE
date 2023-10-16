package com.example.eaeprojekt.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eaeprojekt.IngredientAmountDTO;
import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ShoppingBagActivity extends AppCompatActivity {

    FloatingActionButton newShoppingBagItem;
    LinearLayout shoppingBagLayout;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        newShoppingBagItem = findViewById(R.id.newShoppingBagEntry);
        shoppingBagLayout = findViewById(R.id.shoppingBagLayout);
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
}
