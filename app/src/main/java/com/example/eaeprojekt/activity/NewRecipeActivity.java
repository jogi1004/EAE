package com.example.eaeprojekt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.PopupSteps;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.StepDTO;
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
            newRecipeId = db.insertRecipe("", 1, "", -1);
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
        layout_MainMenu.getForeground().setAlpha( 0);


        //für db eintrag
        title = (EditText) findViewById(R.id.title_text);
        time = (EditText) findViewById(R.id.time_text);

        //spinner füllen
        spinner_portionsmenge = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.portionsmenge, android.R.layout.simple_spinner_dropdown_item);
        spinner_portionsmenge.setAdapter(adapter);
        spinner_portionsmenge.setOnItemSelectedListener(this);




        //schrittbeschreibungen in der view hinzufügen
        Log.d("recreate", "das kann ja nicht sein" + (int) newRecipeId);
        List<StepDTO> stepss = db.getAllStepsForRecipe((int) newRecipeId);

        for(StepDTO step: stepss) {

            Log.d("recreate", "" + step.getText());

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
                Log.d("delete","here normal");
                db.deleteStep(step.getId());
                parentLayout.removeView(layout);
            });

        }


    }

    @Override
    public void onClick(View view) {
        if(view == button_add_steps){
            PopupSteps popup = new PopupSteps(this);
            popup.showPopupWindow(view, this);

            //background-dimming
            FrameLayout layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);
            layout_MainMenu.getForeground().setAlpha(220);

        } else if (view == button_add_recipe) {

            //datenbankzugriff
            db = new DatabaseManager(this);
            db.open();
            //Rezepteinträge aktuallisieren
            db.updateRecipe(newRecipeId, title.getText().toString(), portionsmenge, Integer.parseInt(time.getText().toString()), 0);

            Intent intent = new Intent(this, RecipeActivity.class);
            startActivity(intent);

            finish();

        } else if (view == backButton || view == button_cancel) {
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
        switch (spinner_portionsmenge.getItemAtPosition(position).toString()){
            case ("1"):
                Log.d("NewRecipe", "1");
                portionsmenge = 1;
            break;
            case ("2"):
                Log.d("NewRecipe", "2");
                portionsmenge = 2;
                break;
            case ("3"):
                Log.d("NewRecipe", "3");
                portionsmenge = 3;
                break;
            case ("4"):
                Log.d("NewRecipe", "4");
                portionsmenge = 4;
                break;
            case ("5"):
                Log.d("NewRecipe", "5");
                portionsmenge = 5;
                break;
            case ("6"):
                Log.d("NewRecipe", "6");
                portionsmenge = 6;
                break;
            case ("7"):
                Log.d("NewRecipe", "7");
                portionsmenge = 7;
                break;
            case ("8"):
                Log.d("NewRecipe", "8");
                portionsmenge = 8;
                break;
            case ("9"):
                Log.d("NewRecipe", "9");
                portionsmenge = 9;
                break;
            case ("10"):
                Log.d("NewRecipe", "10");
                portionsmenge = 10;
                break;
            case ("11"):
                Log.d("NewRecipe", "11");
                portionsmenge = 11;
                break;
            case ("12"):
                Log.d("NewRecipe", "12");
                portionsmenge = 12;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        portionsmenge = 1;
    }
}