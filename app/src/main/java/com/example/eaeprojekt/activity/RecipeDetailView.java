package com.example.eaeprojekt.activity;

import static com.example.eaeprojekt.R.id.menu_edit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.LineNumberInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecipeDetail View provides all functional Methods for showing
 * Image, Ingredients and Steps of your recipe
 */
public class RecipeDetailView extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private DatabaseManager db;
    RecipeDTO rDTO;
    BottomNavigationView b;
    String recipeTitle, imagePath, time;
    int portions, isFavorite, recipeid;
    LinearLayout ingredientsLayout, stepsLayout;
    GridLayout durationLayout;
    ImageView circleViewImage;
    List<IngredientAmountDTO> iADTO;
    List<IngredientDTO> iDTO;
    long idUsed = 0;
    List<IngredientDTO> ingredientsForRecipe = new ArrayList<>();

    List<StepDTO> steps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);
        /**
         * Setting up BottomNavigationBar Buttons
         */
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.AddButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        /**
         * Receive Intent from RecipeActivity and extracting ID
         */
        Intent receive = getIntent();
        recipeid = receive.getIntExtra("ID", 0);
        circleViewImage = findViewById(R.id.circleViewRecipe);
        Log.d("CookIt", "Id aus Intent geholt: " + recipeid);
        /**
         * Open and close DB for getting Ingredients
         */
        db = new DatabaseManager(this);
        db.open();
        Log.d("CookIt", "DB geöffnet und DBManager erstellt.");
        //Alle Zutaten aus DB lesen
        iDTO = db.getAllIngredients();
        //Alle Zutaten, die für das Rezept benötigt werden (Name und Unit)
        iADTO = db.getIngredientsForRecipe(recipeid);
        //Bild aus DB holen
        RecipeDTO recipe = db.getRecipeById(recipeid);
        if (recipe.getImagePath()!= null) {
            File imgFile = new File(recipe.getImagePath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                circleViewImage.setImageBitmap(myBitmap);
            }
        }
        else {
            circleViewImage.setImageResource(R.drawable.camera);
        }


        /**
         * Getting all Ingredients for RecipeID
         */
        for (IngredientDTO ingredient : iDTO) {
            long id = ingredient.getId();
            for (IngredientAmountDTO i : iADTO) {
                idUsed = i.getIngredientId();

                if (id == idUsed) {
                    ingredientsForRecipe.add(ingredient);
                }
            }
        }

        for (IngredientDTO ingredient : ingredientsForRecipe) {
            Log.d("CookIt", "ingredientsForRecipe: " + ingredient.getName() + "\n");
        }
        rDTO = db.getRecipeById(recipeid);
        recipeTitle = rDTO.getTitle();
        steps = db.getAllStepsForRecipe(recipeid);

        int duration = recipe.getDuration();
        int hours = duration / 60;
        int minutes = duration % 60;

        if (hours > 0 && minutes > 0) {
            time = String.format(Locale.GERMANY, "%d Std %d Min", hours, minutes);
        } else if (hours > 0) {
            time = String.format(Locale.GERMANY, "%d Std", hours);
        } else {
            time = String.format(Locale.GERMANY, "%d Min", minutes);
        }
        portions = rDTO.getPortions();
        isFavorite = rDTO.getIsFavorite();
        imagePath = rDTO.getImagePath();
        Log.d("CookIt", "DTO erhalten.");

        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        durationLayout = findViewById(R.id.durationLayout);

        /**
         * LayoutParams for TextViews in IngredientsLayout
         *
         *
         * BEGIN OF INGREDIENTS
         */
        RelativeLayout.LayoutParams textViewParamsName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsName.setMargins(25, 10, 60, 10);

        RelativeLayout.LayoutParams textViewParamsIngredientHeader = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsIngredientHeader.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsIngredientHeader.setMargins(50, 10, 0, 10);

        RelativeLayout.LayoutParams ingredientUnitLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        ingredientUnitLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

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
        durationView.setText(time);
        durationView.setTextColor(Color.WHITE);
        TextView portionsView = new TextView(this);
        portionsView.setLayoutParams(textViewParamsName);
        if (portions > 1) {
            String portionen = portions + " Portionen";
            portionsView.setText(portionen);
        } else {
            String portion = "Eine Portion";
            portionsView.setText(portion);
        }
        portionsView.setTextColor(Color.WHITE);

        ImageView durationIcon = new ImageView(this);
        durationIcon.setImageResource(R.drawable.baseline_timer_24);
        durationIcon.setPadding(20, 0, 0, 0);
        ImageView portionsIcon = new ImageView(this);
        portionsIcon.setImageResource(R.drawable.baseline_person_24);


        /**
         * Adding Views to GridLayout
         */
        durationLayout.addView(durationIcon);
        durationLayout.addView(durationView);
        durationLayout.addView(portionsIcon);
        durationLayout.addView(portionsView);


        for (IngredientDTO ingredient : ingredientsForRecipe) {
            // Erstellen Sie ein horizontales Layout für Name, Einheit und Menge
            LinearLayout ingredientLayout = new LinearLayout(this);
            ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);

            // TextView für den Namen
            TextView ingredientName = new TextView(this);
            ingredientName.setText(ingredient.getName());
            ingredientName.setPadding(75, 10, 0, 10);

            // TextView für die Einheit
            TextView ingredientUnit = new TextView(this);
            ingredientUnit.setText(ingredient.getUnit());
            ingredientUnit.setPadding(20, 0, 0, 0);
            ingredientUnit.setLayoutParams(ingredientUnitLayoutParams);

            ingredientLayout.addView(ingredientName);

            // Iterate above iADTO to get ID of Ingredient
            for (IngredientAmountDTO i : iADTO) {
                if (i.getIngredientId() == ingredient.getId()) {
                    // TextView für die Menge
                    TextView ingredientAmount = new TextView(this);
                    ingredientAmount.setText(String.valueOf((int)i.getAmount()));
                    ingredientAmount.setPadding(20, 0, 0, 0); // Ändern Sie die Padding-Werte nach Bedarf

                    // Fügen Sie den TextView für die Menge zum horizontalen Layout hinzu
                    ingredientLayout.addView(ingredientAmount);
                    break;
                }
            }

            // Fügen Sie die TextViews für Name und Einheit zum horizontalen Layout hinzu

            ingredientLayout.addView(ingredientUnit);

            // Fügen Sie das horizontale Layout zum vertikalen Layout hinzu
            ingredientsLinearLayout.addView(ingredientLayout);
        }

        /**
         * BEGIN OF STEPS
         */

        stepsLayout = findViewById(R.id.stepsLayout);

        TextView StepsHeader= new TextView(this);
        StepsHeader.setText("Zubereitungsschritte");
        StepsHeader.setLayoutParams(textViewParamsIngredientHeader);
        StepsHeader.setTextColor(Color.WHITE);
        stepsLayout.addView(StepsHeader);

        for(StepDTO s : steps){
            LinearLayout singleStepLayout = new LinearLayout(this);

            LinearLayout.LayoutParams layoutStepsParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutStepsParams.setMargins(10,10,10,10);

            singleStepLayout.setLayoutParams(layoutStepsParams);
            TextView t = new TextView(this);
            t.setText(s.getText());
            t.setTextColor(Color.WHITE);
            t.setPadding(40,10,0,10);
            singleStepLayout.addView(t);
            stepsLayout.addView(singleStepLayout);
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
/**
    @Override
    public void onClick(View v) {
        if(delete == v){
            db.open();
            db.deleteRecipe(recipeid);
            Log.d("CookIt", "Rezept gelöscht");
            db.close();
            Intent back = new Intent(this, RecipeActivity.class);
            Toast toast = new Toast(this);
            toast.setText("Rezept gelöscht");
            toast.show();
            startActivity(back);
        } else if (favorite == v) {
            if(isFavorite == 1){
                isFavorite = 0;
                favorite.setImageResource(R.drawable.favorite_off);
            } else{
                isFavorite = 1;
                favorite.setImageResource(R.drawable.favorite_on);
            }
            db.updateRecipe(recipeid,recipeTitle,portions,time,isFavorite,imagePath);

        }
    }
**//*
@RequiresApi(api = Build.VERSION_CODES.Q)
@SuppressLint({"RestrictedApi", "NonConstantResourceId"})*/
public void showMenu(View v) {
    PopupMenu popupMenu = new PopupMenu(this, v);
    MenuInflater inflater = popupMenu.getMenuInflater();
    inflater.inflate(R.menu.burger_menu, popupMenu.getMenu());

    popupMenu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == menu_edit) {
            Toast.makeText(this, "Bearbeiten", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menu_delete) {
            db.open();
            db.deleteRecipe(recipeid);
            Log.d("CookIt", "Rezept gelöscht");
            db.close();
            Intent back = new Intent(this, RecipeActivity.class);
            Toast toast = new Toast(this);
            toast.setText("Rezept gelöscht");
            toast.show();
            startActivity(back);
        }
        return false;
    });

    popupMenu.show();
}

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}