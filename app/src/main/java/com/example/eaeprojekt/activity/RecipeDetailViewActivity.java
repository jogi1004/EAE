package com.example.eaeprojekt.activity;

import static com.example.eaeprojekt.R.id.menu_edit;
import static com.example.eaeprojekt.activity.Shared.roundDouble;
import static com.example.eaeprojekt.activity.Shared.showImage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;


import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecipeDetail View provides all functional Methods for showing
 * Image, Ingredients and Steps of your recipe
 */
public class RecipeDetailViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {

    private DatabaseManager db;
    RecipeDTO rDTO;
    BottomNavigationView b;
    String recipeTitle, imagePath, time;
    int portions, isFavorite, recipeid;
    LinearLayout ingredientsLayout, stepsLayout, ingredientsLinearLayout;
    GridLayout durationLayout;
    ImageView circleViewImage;
    List<IngredientAmountDTO> iADTO;
    List<StepDTO> steps;
    ImageButton favoriteStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);
        /*
         * Setting up BottomNavigationBar Buttons
         */
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.recipeListButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);
        /*
         * Receive Intent from RecipeActivity and extracting ID
         */
        Intent receive = getIntent();
        recipeid = receive.getIntExtra("ID", 0);
        circleViewImage = findViewById(R.id.circleViewRecipe);
        /*
         * Open DB for getting Ingredients
         */
        db = new DatabaseManager(this);
        db.open();
        iADTO = db.getIngredientsForRecipe(recipeid);

        //Bild aus DB holen
        RecipeDTO recipe = db.getRecipeById(recipeid);
        updateImage(recipe.getImagePath());

        rDTO = db.getRecipeById(recipeid);
        recipeTitle = rDTO.getTitle();
        steps = db.getAllStepsForRecipe(recipeid);

        int duration = recipe.getDuration();
        int hours = duration / 60;
        int minutes = duration % 60;

        if (hours > 0 && minutes > 0) {
            time = String.format(Locale.GERMAN, getString(R.string.hoursAndMinutes), hours, minutes);
        } else if (hours > 0) {
            time = String.format(Locale.GERMAN, getString(R.string.hours), hours);
        } else {
            time = String.format(Locale.GERMAN, getString(R.string.min), minutes);
        }
        portions = rDTO.getPortions();
        isFavorite = rDTO.getIsFavorite();
        imagePath = rDTO.getImagePath();

        // ingredientshape for rounded Corners
        GradientDrawable ishape = new GradientDrawable();
        ishape.setShape(GradientDrawable.RECTANGLE);
        ishape.setCornerRadius(30); // Radius für abgerundete Ecken in Pixeln
        ishape.setColor(getColor(R.color.darkerYellow));

        GradientDrawable gshape = new GradientDrawable();
        gshape.setShape(GradientDrawable.RECTANGLE);
        gshape.setCornerRadius(30);

        int darkmode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(darkmode == Configuration.UI_MODE_NIGHT_YES){
            gshape.setColor(getColor(R.color.fontColor));
        } else{
            gshape.setColor(getColor(R.color.backgroundGreen));
        }


        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        ingredientsLayout.setBackground(ishape);
        durationLayout = findViewById(R.id.durationLayout);

        //Favoriten Stern anpassen je nach Einstellung des Smartphones (darkmode/non-darkmode)
        favoriteStar = findViewById(R.id.favoriteStar);
        if(isFavorite == 1) {
            if (darkmode == Configuration.UI_MODE_NIGHT_YES) {
                favoriteStar.setImageResource(R.drawable.favoritestar_filled_light);
            } else {
                favoriteStar.setImageResource(R.drawable.favoritestar_filled_dark);
            }
        }else {
            if (darkmode == Configuration.UI_MODE_NIGHT_YES) {
                favoriteStar.setImageResource(R.drawable.favoritestar_hollow_light);
            } else {
                favoriteStar.setImageResource(R.drawable.favoritestar_hollow_dark);
            }
        }

        //Erstellen eines OnClickListener für den Favoritenstern
        favoriteStar.setOnClickListener(v -> {
                if(isFavorite == 1){
                    isFavorite = 0;
                    if(darkmode == Configuration.UI_MODE_NIGHT_YES){
                        favoriteStar.setImageResource(R.drawable.favoritestar_hollow_light);
                    } else {
                        favoriteStar.setImageResource(R.drawable.favoritestar_hollow_dark);
                    }
                    db.updateRecipe(recipeid,recipeTitle,portions, duration, isFavorite,imagePath);
                } else {
                    isFavorite = 1;
                    if(darkmode == Configuration.UI_MODE_NIGHT_YES){
                        favoriteStar.setImageResource(R.drawable.favoritestar_filled_light);
                    } else {
                        favoriteStar.setImageResource(R.drawable.favoritestar_filled_dark);
                    }
                    db.updateRecipe(recipeid,recipeTitle,portions, duration, isFavorite,imagePath);
                }
                });

        /*
         * LayoutParams for TextViews in IngredientsLayout
         */

        RelativeLayout.LayoutParams textViewParamsName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsName.setMargins(25, 0, 60, 0);


        /*
         * Set Title to Recipe Name
         */
        TextView RecipeTitle = findViewById(R.id.RecipeName);
        RecipeTitle.setText(recipeTitle);
        /*
         * Creating TextViews for Ingredients
         */
        RelativeLayout.LayoutParams textViewParamsIngredientHeader = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParamsIngredientHeader.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textViewParamsIngredientHeader.setMargins(50, 10, 0, 10);


        if(!iADTO.isEmpty()) {

            TextView ingredientsHeader = new TextView(this);
            ingredientsHeader.setText(getText(R.string.ingredients));
            ingredientsHeader.setLayoutParams(textViewParamsIngredientHeader);
            ingredientsHeader.setPaintFlags(ingredientsHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            //Hinzufügen des "Zutaten" Headers auf das Layout für die Zutaten
            ingredientsLayout.addView(ingredientsHeader);

            //Layout das den Inhalt wie Name, Menge usw enthält
            ingredientsLinearLayout = new LinearLayout(this);
            ingredientsLinearLayout.setOrientation(LinearLayout.VERTICAL);
            ingredientsLinearLayout.setPadding(10, 10, 10, 10);
        }

         //Creating TextViews and Icons for durationView

         TextView durationView = new TextView(this);
         durationView.setLayoutParams(textViewParamsName);
         durationView.setText(time);
         durationView.setTextColor(Color.WHITE);

         ImageView durationIcon = new ImageView(this);
         durationIcon.setImageResource(R.drawable.baseline_timer_24);
         durationIcon.setPadding(20, 0, 0, 0);
         ImageView portionsIcon = new ImageView(this);
         portionsIcon.setImageResource(R.drawable.baseline_person_24);


         /*
         * Adding Views to GridLayout
         */
         durationLayout.addView(durationIcon);
         durationLayout.addView(durationView);
         durationLayout.addView(portionsIcon);

        TextView portionsDescription = new TextView(this);
        portionsDescription.setPadding(10, 10, 0, 0);
        portionsDescription.setTextColor(getColor(R.color.white));
        if (portions > 1) {
            portionsDescription.setText(getString(R.string.portions));
        } else if (portions == 1) {
            portionsDescription.setText(getString(R.string.onePortion));
        }

        if (iADTO.isEmpty()) {
            if (portions > 1) {
                TextView portionsView = new TextView(this);
                portionsView.setTextColor(Color.WHITE);
                portionsView.setText(String.valueOf(portions));
                portionsView.setPadding(10, 0, 0, 0);
                durationLayout.addView(portionsView);
            }
        }
        else {
            Spinner convertSpinner = new Spinner(this);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.portionsmenge, android.R.layout.simple_spinner_dropdown_item);
            convertSpinner.setAdapter(adapter);
            convertSpinner.setOnItemSelectedListener(this);
            convertSpinner.setSelection(portions - 1);
            durationLayout.addView(convertSpinner);
        }
        durationLayout.addView(portionsDescription);


        /*
         * BEGIN OF STEPS
         */

        stepsLayout = findViewById(R.id.stepsLayout);

        if (!steps.isEmpty()) {
            TextView stepsHeader = new TextView(this);
            stepsHeader.setText(getText(R.string.steps));
            stepsHeader.setLayoutParams(textViewParamsIngredientHeader);
            stepsHeader.setTextColor(Color.WHITE);
            stepsHeader.setPaintFlags(stepsHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            stepsLayout.addView(stepsHeader);


            int counter = 0;
            for (StepDTO s : steps) {
                LinearLayout singleStepLayout = new LinearLayout(this);

                LinearLayout.LayoutParams layoutStepsParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutStepsParams.setMargins(10, 10, 10, 10);

                singleStepLayout.setLayoutParams(layoutStepsParams);
                singleStepLayout.setOrientation(LinearLayout.VERTICAL);
                TextView h = new TextView(this);
                h.setText(String.format(Locale.GERMAN, "%d. Schritt:", ++counter));
                h.setTextColor(Color.WHITE);
                h.setPadding(40, 10, 0, 10);
                h.setPaintFlags(h.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                singleStepLayout.addView(h);
                TextView t = new TextView(this);
                t.setText(s.getText());
                t.setTextColor(Color.WHITE);
                t.setPadding(40, 0, 0, 10);
                singleStepLayout.addView(t);
                stepsLayout.addView(singleStepLayout);
            }
        }
        stepsLayout.setBackground(gshape);

    }


        private void updateImage(String path) {
        if (path != null && Shared.checkPermission(this, false)) {
            showImage(path, circleViewImage);
        }
        else {
            circleViewImage.setImageResource(R.drawable.camera_small);
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            /*
             Creating Intent for starting NewRecipeActivity
             */
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.recipeListButtonNavBar) {
            /*
             Creating Intent for starting RecipeActivity
             */
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
        }
        if(id == R.id.shoppingBagButtonNavBar){
            /*
             * Creating Intent for starting ShoppingBagLayout
             */
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);
        }
        return false;
    }

@RequiresApi(api = Build.VERSION_CODES.Q)
public void showMenu(View v) {
    PopupMenu popupMenu = new PopupMenu(this, v);
    MenuInflater inflater = popupMenu.getMenuInflater();
    inflater.inflate(R.menu.burger_menu, popupMenu.getMenu());

    popupMenu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == menu_edit) {
            Intent i = new Intent(this, RecipeEditActivity.class);
            i.putExtra("ID", recipeid);
            startActivity(i);
        } else if (item.getItemId() == R.id.menu_delete) {
            // remove all ingredientAmounts
            for (IngredientAmountDTO ingr : db.getIngredientsForRecipe(recipeid)) {
                db.deleteIngredientQuantity(ingr.getId());
            }
            db.deleteRecipe(recipeid);
            Intent back = new Intent(this, RecipeActivity.class);
            Toast toast = new Toast(this);
            toast.setText(getString(R.string.recipeDeleted));
            toast.show();
            startActivity(back);
        }
        return false;
    });

    popupMenu.show();
}

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        convertPortions(portions, position+1);
    }

    private void convertPortions(int oldPortions, int newPortions) {
            // Berechne den Umrechnungsfaktor
            float factor = (float) newPortions / oldPortions;
            List<String> newAmounts = new ArrayList<>(iADTO.size());

            // Iteriere durch alle Zutatenmengen
            for (IngredientAmountDTO ingredient : iADTO) {
                // Passe die Menge entsprechend dem Umrechnungsfaktor an
                newAmounts.add(roundDouble((ingredient.getAmount() * factor )));
            }
            updateIngredientTextViews(newAmounts);
    }

    private void updateIngredientTextViews(List<String> newPortions) {
        if (ingredientsLinearLayout != null && !iADTO.isEmpty()) {
        //Entfernen aller bereits bestehenden Views und ersetzen durch die Korrekten
        ingredientsLinearLayout.removeAllViews();
        ingredientsLayout.removeAllViews();
        int counter = 0;
    for (IngredientAmountDTO ingredient : iADTO) {

        //Alle Zutaten die benötigt werden aus der DB auslesen und in currIngredient speichern
        IngredientDTO currIngredient = db.getIngredientById(ingredient.getIngredientId());

        // Erstellen eines horizontalen Layouts für Name, Einheit und Menge
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
        ingredientLayout.setGravity(Gravity.CENTER_VERTICAL);
        ingredientLayout.setPadding(10,10,10,10);

        //layout zum Anpassen der Zeilenbreite und -höhe
        LinearLayout.LayoutParams ingLayoutParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ingredientLayout.setLayoutParams(ingLayoutParams);

        // TextView für den Namen
        TextView ingredientName = new TextView(this);
        ingredientName.setText(currIngredient.getName());
        ingredientName.setPadding(15, 0, 20, 0);

        // TextView für die Einheit
        RelativeLayout.LayoutParams ingredientUnitLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        ingredientUnitLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        TextView ingredientUnit = new TextView(this);
        ingredientUnit.setText(currIngredient.getUnit());
        ingredientUnit.setPadding(15, 0, 0, 0);
        ingredientUnit.setLayoutParams(ingredientUnitLayoutParams);


        TextView ingredientAmount = new TextView(this);
        //Counter macht keine Probleme bei mehreren Rezepten
        ingredientAmount.setText(String.valueOf(newPortions.get(counter++)));
        ingredientAmount.setPadding(50,0,0,0);

        RelativeLayout shoppingBaglayout = new RelativeLayout(this);
        shoppingBaglayout.setLayoutParams(new RelativeLayout.LayoutParams (
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        ImageView shoppingBag = new ImageView(this);
        if(ingredient.getOnShoppingList() == 0){
            shoppingBag.setImageResource(R.drawable.shoppingbag_dark_hollow);
        } else {
            shoppingBag.setImageResource(R.drawable.shoppingbag_dark_filled);
        }
        RelativeLayout.LayoutParams shoppingBagParams = new RelativeLayout.LayoutParams(
                55,
                55
        );
        shoppingBagParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        shoppingBag.setLayoutParams(shoppingBagParams);
        shoppingBaglayout.addView(shoppingBag);

        // OnClick Listener zur Shopping Bag hinzufügen
        shoppingBag.setOnClickListener(v -> {
            if (ingredient.getOnShoppingList() == 0) {
                ingredient.setOnShoppingList(1);
                shoppingBag.setImageResource(R.drawable.shoppingbag_dark_filled);
                Toast toast = new Toast(this);
                toast.setText(R.string.ingredientAddedToShoppingBag);
                toast.show();
            } else {
                ingredient.setOnShoppingList(0);
                shoppingBag.setImageResource(R.drawable.shoppingbag_dark_hollow);
                Toast toast = new Toast(this);
                toast.setText(R.string.ingredientRemovedFromShoppingBag);
                toast.show();
            }
            db.updateIngredientQuantity(ingredient.getId(), ingredient.getIngredientId(), ingredient.getAmount(), ingredient.getOnShoppingList(), 0);
        });

        // Einfügen der Text und ImageViews
        ingredientLayout.addView(ingredientAmount);
        ingredientLayout.addView(ingredientUnit);
        ingredientLayout.addView(ingredientName);
        ingredientLayout.addView(shoppingBaglayout);

        // Fügen Sie das horizontale Layout zum vertikalen Layout hinzu
        ingredientsLinearLayout.addView(ingredientLayout);
        ingredientsLayout.removeAllViews();
        ingredientsLayout.addView(ingredientsLinearLayout);
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        portions = 1;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
    }
}