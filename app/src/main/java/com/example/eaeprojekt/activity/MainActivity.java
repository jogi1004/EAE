package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.RecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout recipes;
    ConstraintLayout shoppingBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Beispieldaten hinzufügen
        DatabaseManager dbMan = new DatabaseManager(this);
        dbMan.open();
        String zutatName1 = "Mehl";
        String zutatName2 = "Milch";
        String zutatEinheit1 = "g";
        String zutatEinheit2 = "ml";
        // Datenbank löschen
        //deleteDatabase(DatabaseManager.DATABASE_NAME);
        List<Long> ids = new ArrayList<>();
        ids.add(dbMan.insertIngredient(zutatName1, zutatEinheit1));
        ids.add(dbMan.insertIngredient(zutatName2, zutatEinheit2));
        List<Long> idds = new ArrayList<>();
        idds.add(dbMan.insertIngredientQuantity(-1, ids.get(0), 201, 1));
        idds.add(dbMan.insertIngredientQuantity(-1, ids.get(1), 350, 1));
        for (long y : idds) {
            Log.d("HSKL", "Neu: " + y);
        }


        // Optional: Schließe die Datenbankverbindung
        dbMan.close();

        recipes = findViewById(R.id.recipes);
        recipes.setOnClickListener(this);

        shoppingBag = findViewById(R.id.shoppingBag);
        shoppingBag.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == recipes){
            Intent recipeIntent = new Intent(this, RecipeActivity.class);
            startActivity(recipeIntent);
        }
        if (view == shoppingBag){
            Intent shoppingBagIntent = new Intent(this, ShoppingBagActivity.class);
            startActivity(shoppingBagIntent);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}