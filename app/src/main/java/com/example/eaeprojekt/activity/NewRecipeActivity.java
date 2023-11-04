package com.example.eaeprojekt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.PopupIngredients;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.popups.PopupSteps;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.database.DatabaseManager;

import java.util.List;


public class NewRecipeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    ImageView backButton;
    ConstraintLayout button_add_ingredients;
    ConstraintLayout button_add_steps;
    ConstraintLayout button_add_recipe;
    ConstraintLayout button_cancel;
    DatabaseManager db;
    EditText title;
    EditText time;
    Spinner spinner_portionsmenge;
    int portionsmenge;

    public static long newRecipeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //datenbankzugriff
        db = new DatabaseManager(this);
        db.open();

        //Neues Rezept erstellen mit keinen Inhalten (wird gelöscht, falls vorgang abgebrochen wird)
        List<RecipeDTO> alleRezepte = db.getAllRecipes();

        boolean foundRecipe = false;
        for(RecipeDTO recipe : alleRezepte) {
            if (recipe.getIsFavorite() == -1) {
                foundRecipe = true;
                newRecipeId = recipe.getId();
                break;
            }
        }

        if(!foundRecipe) {
            newRecipeId = db.insertRecipe("", 1, "", -1, "-1");
        }

        //ZurückButton behandeln
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);


        //Buttons zum Rezept behandeln
        button_add_ingredients = (ConstraintLayout) findViewById(R.id.button_add_ingredients);
        button_add_ingredients.setOnClickListener(this);

        button_add_steps = (ConstraintLayout) findViewById(R.id.button_add_steps);
        button_add_steps.setOnClickListener(this);


        button_add_recipe = (ConstraintLayout) findViewById(R.id.button_add_recipe);
        button_add_recipe.setOnClickListener(this);

        button_cancel = (ConstraintLayout) findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(this);

        //Layout zum dimmen
        FrameLayout layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);


        //für db eintrag
        title = (EditText) findViewById(R.id.title_text);
        time = (EditText) findViewById(R.id.time_text);

        //spinner füllen
        spinner_portionsmenge = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.portionsmenge, android.R.layout.simple_spinner_dropdown_item);
        spinner_portionsmenge.setAdapter(adapter);
        spinner_portionsmenge.setOnItemSelectedListener(this);


        addIngredients();

        addSteps();

    }

    @Override
    public void onClick(View view) {

        FrameLayout layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);

        if(view == button_add_steps){
            PopupSteps popup = new PopupSteps();
            popup.showPopupWindow(view, this);


            //background-dimming
            layout_MainMenu.getForeground().setAlpha(220);
            layout_MainMenu.setElevation(1);

        } else if (view == button_add_ingredients) {
            PopupIngredients popup = new PopupIngredients();
            popup.showPopupWindow(view, this);

            //background-dimming
            layout_MainMenu.getForeground().setAlpha(220);
            layout_MainMenu.setElevation(1);

        } else if (view == button_add_recipe) {

            if(title.getText().length() > 0 && time.getText().length() > 0) {
                //datenbankzugriff
                db = new DatabaseManager(this);
                db.open();
                //Rezepteinträge aktuallisieren
                db.updateRecipe(newRecipeId, title.getText().toString(), portionsmenge, Integer.parseInt(time.getText().toString()), 0, "-1");

                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);

                finish();
            }else{
                Toast toast = new Toast(this);
                toast.setText("Fülle bitte zuerst alle Felder aus 😊");
                toast.show();
            }

        }/*else if (view == spinner_portionsmenge){
            hideKeyboard(this);

        }
        */
        else if (view == backButton || view == button_cancel) {
            Intent intent = new Intent(this, RecipeActivity.class);
            startActivity(intent);

            db = new DatabaseManager(this);
            db.open();

            db.deleteRecipe(newRecipeId);

            db.close();
            finish();
        }

    }

    //welche portionsmenge ausgewählt wurde
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        portionsmenge = position +1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        portionsmenge = 1;
    }
/*

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

 */


    public void addIngredients(){
        List<IngredientAmountDTO> ingredientDTOs = db.getIngredientsForRecipe(newRecipeId);

        for(IngredientAmountDTO ingredient : ingredientDTOs){
            IngredientDTO ingredientBare = db.getIngredientById(ingredient.getRecipeId());

            /*
            schrittbeschreibung in der view hinzufügen
             */
            ConstraintLayout layout = new ConstraintLayout(this);

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
            ingredientText.setText(ingredientBare.getName());
            ingredientText.setGravity(Gravity.CENTER);
            ingredientText.setTextColor(Color.parseColor("#FFFFFF"));

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
            amountText.setText("" + ingredient.getIngredientId());
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextColor(Color.parseColor("#FFFFFF"));

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
            unitText.setText(ingredientBare.getUnit());
            unitText.setGravity(Gravity.CENTER);
            unitText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            unitText.setLayoutParams(unitParams);

            layout.addView(unitText);

            /*
            Mülleimer
             */
            ImageView trash = new ImageView(this);
            trash.setImageResource(R.drawable.light_trash_can);
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    50,
                    50
            );
            trash.setLayoutParams(trashParams);
            layout.addView(trash);
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
            constraintSet.connect(amountText.getId(), ConstraintSet.START, ingredientText.getId(), ConstraintSet.START, 200);
            constraintSet.connect(amountText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(amountText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Einheit
            constraintSet.connect(unitText.getId(), ConstraintSet.START, amountText.getId(), ConstraintSet.END, 20);
            constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            //Mülleimer
            constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraintSet.applyTo(layout);


            LinearLayout parentLayout = findViewById(R.id.ingredientsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v ->{
                db.deleteStep(ingredient.getId());
                parentLayout.removeView(layout);
            });

        }

    }

    public void addSteps(){
        //schrittbeschreibungen in der view hinzufügen
        List<StepDTO> stepss = db.getAllStepsForRecipe((int) newRecipeId);

        for(StepDTO step: stepss) {

            //schrittbeschreibung in der view hinzufügen

            ConstraintLayout layout = new ConstraintLayout(this);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setBackgroundResource(R.drawable.background_with_rounded_corners_green);
            layout.setPadding(20,20,20,20);
            layout.setLayoutParams(layoutParams);
            layoutParams.setMargins(40, 10, 40, 10);

            // Text der Schrittbeschreibung
            TextView stepDescriptionText = new TextView(this);
            stepDescriptionText.setId(View.generateViewId());
            stepDescriptionText.setText(step.getText());
            stepDescriptionText.setGravity(Gravity.CENTER);
            stepDescriptionText.setTextColor(Color.parseColor("#FFFFFF"));

            ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            stepDescriptionText.setLayoutParams(textViewParams);

            layout.addView(stepDescriptionText);

            //Mülleimer
            ImageView trash = new ImageView(this);
            trash.setImageResource(R.drawable.light_trash_can);
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    70,
                    70
            );
            trash.setLayoutParams(trashParams);
            layout.addView(trash);


            //Constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);

            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.END, trash.getId(), ConstraintSet.START);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(stepDescriptionText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);


            constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraintSet.applyTo(layout);


            LinearLayout parentLayout = findViewById(R.id.stepsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v ->{
                db.deleteStep(step.getId());
                parentLayout.removeView(layout);
            });

        }
    }
}
