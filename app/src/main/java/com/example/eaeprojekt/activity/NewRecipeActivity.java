package com.example.eaeprojekt.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.popups.PopupSteps;
import com.example.eaeprojekt.database.DatabaseManager;


public class NewRecipeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    ConstraintLayout button_add_ingredients;
    ConstraintLayout button_add_steps;
    ConstraintLayout button_add_recipe;
    DatabaseManager db;
    EditText title;
    EditText time;
    Spinner spinner_portionsmenge;
    int portionsmenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //initialisierung der View
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_add_ingredients = (ConstraintLayout) findViewById(R.id.button_add_ingredients);
        button_add_ingredients.setOnClickListener(this);

        button_add_steps = (ConstraintLayout) findViewById(R.id.button_add_steps);
        button_add_steps.setOnClickListener(this);

        button_add_recipe = (ConstraintLayout) findViewById(R.id.button_add_recipe);
        button_add_recipe.setOnClickListener(this);

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

        //neuen Eintrag in db (newRecipe)

            //datenbankzugriff
            db = new DatabaseManager(this);
            db.open();

            db.insertRecipe(title.getText().toString(), portionsmenge, time.getText().toString(), 0);
            Log.d("NewRecipe", "Rezept erstellt " + title.getText().toString() + " " +
                    portionsmenge + " " + time.getText().toString());

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