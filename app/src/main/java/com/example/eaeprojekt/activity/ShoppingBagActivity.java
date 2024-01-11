package com.example.eaeprojekt.activity;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.popups.PopupDeleteShoppingBag;
import com.example.eaeprojekt.popups.PopupIngredients;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Activity for showing the ShoppingList with ingredients in it, also has a navigation bar for navigation
 */
public class ShoppingBagActivity extends AppCompatActivity implements View.OnClickListener,ShoppingBagUpdateListener {

    ImageButton addIngredient, deleteAllIcon;
    PopupDeleteShoppingBag deleteShoppingBagPopup;
    LinearLayout shoppingLayout, ingredientWithoutRecipe;
    DatabaseManager db;
    BottomNavigationView b;
    FrameLayout dimmableLayoutShoppingBag;
    TextView helperTextView;
    int darkmode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        //Abfragen des Darkmodes
        darkmode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        ingredientWithoutRecipe = findViewById(R.id.ingredientWithoutRecipe);
        shoppingLayout = findViewById(R.id.shoppingLayoutinScrollView);
        deleteAllIcon = findViewById(R.id.deleteAllIcon);
        deleteAllIcon.setOnClickListener(this);
        deleteShoppingBagPopup = new PopupDeleteShoppingBag(this, this);
        dimmableLayoutShoppingBag = findViewById(R.id.FrameLayoutShoppingBag);
        dimmableLayoutShoppingBag.getForeground().setAlpha(0);
        helperTextView = findViewById(R.id.helperTextBox);
        b = findViewById(R.id.bottomNavView);
        b.setSelectedItemId(R.id.shoppingBagButtonNavBar);
        b.setOnItemSelectedListener(this::onNavigationItemSelected);

        addIngredient = findViewById(R.id.addButtonShoppingBag);
        addIngredient.setOnClickListener(this);

        updateShoppingBag();
    }

    public void updateShoppingBag() {
        shoppingLayout.removeAllViews();
        ingredientWithoutRecipe.removeAllViews();
        db = new DatabaseManager(this);
        db.open();
        List<IngredientAmountDTO> ingredientsOnShoppingList = db.getIngredientsOnShoppingList();
        /*
         * Show helper-text while list is empty
         */

        helperTextView.setVisibility(ingredientsOnShoppingList.isEmpty()? View.VISIBLE : View.INVISIBLE);

        /*
         * Using DTO to get all Ingredients from the DB
         */

        long i = 0;
        for (IngredientAmountDTO ingredientAmount : ingredientsOnShoppingList) {

            IngredientDTO ingredient = db.getIngredientById(ingredientAmount.getIngredientId());

            LinearLayout parentLayout1 = this.findViewById(R.id.ingredientWithoutRecipe);

            LinearLayout parentLayout2 = new LinearLayout(this);
            ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            parentLayout2.setOrientation(LinearLayout.VERTICAL);
            parentLayout2.setLayoutParams(pp);


            if((int) ingredientAmount.getRecipeId() == -9){ // using -9 as id for "no recipe"

                if(i != ingredientAmount.getRecipeId()) {
                    TextView titleOther = new TextView(this);
                    titleOther.setText(R.string.otherIngredients);

                    parentLayout1.addView(titleOther);

                    View view = new View(this);
                    view.setBackgroundColor(getColor(R.color.fontColor));
                    ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dpToPx(1)
                    );
                    view.setLayoutParams(viewParams);
                    parentLayout1.addView(view);

                    i = ingredientAmount.getRecipeId();
                }
            }else{

                if(i != ingredientAmount.getRecipeId()) {
                    TextView titleRecipes = new TextView(this);
                    long id = ingredientAmount.getRecipeId();
                    RecipeDTO recipe = db.getRecipeById(id);
                    String titleRecipe = recipe.getTitle();

                    titleRecipes.setText(titleRecipe);
                    if(!db.getIngredientsForRecipe(-9).isEmpty()) {
                        titleRecipes.setPadding(0, 60, 0, 0);
                    }else if(parentLayout2.getChildCount() > 1){
                        titleRecipes.setPadding(0, 60, 0, 0);
                    }
                    parentLayout2.addView(titleRecipes);

                    View line = new View(this);
                    line.setBackgroundColor(getColor(R.color.fontColor));
                    ViewGroup.LayoutParams lineParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dpToPx(1)
                    );
                    line.setLayoutParams(lineParams);
                    parentLayout2.addView(line);

                    i = ingredientAmount.getRecipeId();
                }
            }
            /*
            Zutaten in der view hinzufügen
             */
            ConstraintLayout layout = new ConstraintLayout(this);
            // Checkbox für Einkaufsliste
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(View.generateViewId());


            //Ändern der Farben wenn Darkmode aktiviert ist
            if(Configuration.UI_MODE_NIGHT_YES == darkmode){
                parentLayout1.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                parentLayout2.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                layout.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.background)));
            }

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setPadding(20,20,20,20);
            layout.setLayoutParams(layoutParams);
            layoutParams.setMargins(40, 0, 40, 0);

            /*
            Zutat
             */
            TextView ingredientText = new TextView(this);
            ingredientText.setId(View.generateViewId());
            ingredientText.setText(ingredient.getName());
            ingredientText.setGravity(Gravity.CENTER);
            ingredientText.setTextAppearance(R.style.TextViewShopping);

            ViewGroup.LayoutParams ingredientParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT

            );
            ingredientText.setLayoutParams(ingredientParams);
            layout.addView(ingredientText);

            /*
            Menge
             */
            TextView amountText = new TextView(this);
            amountText.setId(View.generateViewId());
            amountText.setText(String.valueOf((int) ingredientAmount.getAmount()));
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextAppearance(R.style.TextViewShopping);

            ViewGroup.LayoutParams amountParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            amountText.setLayoutParams(amountParams);

            layout.addView(amountText);

            /*
            Einheit
             */
            TextView unitText = new TextView(this);
            unitText.setId(View.generateViewId());
            unitText.setText(ingredient.getUnit());
            unitText.setGravity(Gravity.CENTER);
            unitText.setTextAppearance(R.style.TextViewShopping);

            ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            unitText.setLayoutParams(unitParams);

            layout.addView(unitText);

            /*
            Checkboxparameter
            */
            ViewGroup.LayoutParams checkBoxParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            checkBox.setLayoutParams(checkBoxParams);
            if(ingredientAmount.getIsChecked() == 1){
                checkBox.setChecked(true);
            }
            layout.addView(checkBox);

            /*
            Mülleimer
             */
            ImageView trash = new ImageView(this);
            if(darkmode == Configuration.UI_MODE_NIGHT_YES){
             trash.setImageResource(R.drawable.trashcan_light);
            } else {
                trash.setImageResource(R.drawable.trashcan_dark);
            }
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    50,
                    50
            );
            trash.setLayoutParams(trashParams);
            layout.addView(trash);

            checkBox.setOnClickListener(v -> {
                handleCheckboxClick(ingredientAmount);
                updateShoppingBag();
            });


            /*
            Constraints
             */
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);

            //Zutat
            constraintSet.connect(ingredientText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(ingredientText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(ingredientText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Menge
            constraintSet.connect(amountText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 250);
            constraintSet.connect(amountText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(amountText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Einheit
            constraintSet.connect(unitText.getId(), ConstraintSet.START, amountText.getId(), ConstraintSet.END, 20);
            constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Checkbox
            constraintSet.connect(checkBox.getId(), ConstraintSet.END, trash.getId(), ConstraintSet.START, dpToPx(8));
            constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Mülleimer
            constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraintSet.applyTo(layout);

            LinearLayout parentparent2 = this.findViewById(R.id.shoppingLayoutinScrollView);

            if(ingredientAmount.getRecipeId() == -9){
                parentLayout1.addView(layout);
            }else {
                parentLayout2.addView(layout);
                parentparent2.addView(parentLayout2);
            }

            trash.setOnClickListener(v ->{
                if(ingredientAmount.getRecipeId() == -9) {

                    db.deleteIngredientQuantity(ingredientAmount.getId());
                    List<IngredientAmountDTO> li = db.getIngredientsForRecipe(-9);
                    parentLayout1.removeView(layout);
                    if(li.isEmpty()){
                        parentLayout1.removeAllViews();
                    }
                }else{

                    db.updateIngredientQuantity(ingredientAmount.getId(),ingredientAmount.getIngredientId(),ingredientAmount.getAmount(),0,0);
                    List<IngredientAmountDTO> li = db.getIngredientsForRecipe(ingredientAmount.getRecipeId());
                    boolean empty = true;
                    for(IngredientAmountDTO a: li){
                        if(a.getOnShoppingList() == 1){
                            empty = false;
                            break;
                        }
                    }

                    if(empty)
                        parentLayout2.removeAllViews();
                    else
                        parentLayout2.removeView(layout);
                }
                helperTextView.setVisibility(ingredientsOnShoppingList.isEmpty()? View.VISIBLE : View.INVISIBLE);
                updateShoppingBag();
            });
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            /*
             Creating Intent for starting NewRecipeActivity
             **/

            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.recipeListButtonNavBar) {
            /*
             Creating Intent for starting RecipeActivity
             **/
            Intent i = new Intent(this, RecipeActivity.class);
            startActivity(i);
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        FrameLayout layout_MainMenu = findViewById( R.id.FrameLayoutShoppingBag);

        if (view == deleteAllIcon) {
            deleteShoppingBagPopup.showPopupWindow(view, this);

            dimmableLayoutShoppingBag.getForeground().setAlpha(220);
            dimmableLayoutShoppingBag.setElevation(1);
        } else if (view == addIngredient) {
            PopupIngredients popup = new PopupIngredients(this);
            popup.showPopupWindow(view, this);

            //background-dimminge
            dimmableLayoutShoppingBag.getForeground().setAlpha(220);
            dimmableLayoutShoppingBag.setElevation(1);
        }
    }

    @Override
    public void onUpdateShoppingBag() { // used in Popups
        updateShoppingBag();
    }

    private void handleCheckboxClick(IngredientAmountDTO ingredientAmount) {
        db.open();
        db.updateIngredientQuantity(ingredientAmount.getId(), ingredientAmount.getIngredientId(), ingredientAmount.getAmount(), ingredientAmount.getOnShoppingList(), ingredientAmount.getIsChecked()==0? 1:0 );
        db.close();
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