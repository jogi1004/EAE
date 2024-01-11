package com.example.eaeprojekt.activity;

import static com.example.eaeprojekt.activity.Shared.addIngredients;
import static com.example.eaeprojekt.activity.Shared.showImage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.popups.PopupIngredientsEdit;
import com.example.eaeprojekt.popups.PopupStepsEdit;
import com.example.eaeprojekt.utility.KeyboardUtils;
import java.util.List;

public class RecipeEditActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ImageView backButton, pictureView;
    ConstraintLayout button_cancel, button_add_ingredients, button_add_recipe, button_add_steps, button_add_image;
    DatabaseManager db;
    EditText time, title;
    Spinner spinner_portionsmenge;
    String recipeTitle, image;
    private int isFavorite, portions;
    public static long recipeIdEdit;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);

        // Erhalte die Root-Ansicht der Activity
        View rootView = findViewById(android.R.id.content);

        KeyboardUtils.setupUI(findViewById(R.id.layout_edit_recipe), this);

        Intent receive = getIntent();
        recipeIdEdit = receive.getIntExtra("ID", 0);

        //datenbankzugriff
        db = new DatabaseManager(this);
        db.open();

        RecipeDTO recipe = db.getRecipeById(recipeIdEdit);
        recipeTitle = recipe.getTitle();
        int duration = recipe.getDuration();
        portions = recipe.getPortions();
        image = recipe.getImagePath();
        isFavorite = recipe.getIsFavorite();


        //ZurückButton behandeln
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);


        //Buttons zum Rezept behandeln
        button_add_ingredients = findViewById(R.id.button_add_ingredients);
        button_add_ingredients.setOnClickListener(this);

        button_add_steps = findViewById(R.id.button_add_steps);
        button_add_steps.setOnClickListener(this);


        button_add_recipe = findViewById(R.id.button_add_recipe);
        button_add_recipe.setOnClickListener(this);

        button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(this);

        button_add_image = findViewById(R.id.picture_layout);
        button_add_image.setOnClickListener(this);

        //Layout zum dimmen
        FrameLayout layout_MainMenu = findViewById(R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);


        //für db eintrag
        title = findViewById(R.id.title_text);
        title.setText(recipeTitle);
        time = findViewById(R.id.time_text);
        time.setText(String.valueOf(duration));

        //spinner füllen
        spinner_portionsmenge = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.portionsmenge, android.R.layout.simple_spinner_dropdown_item);
        spinner_portionsmenge.setAdapter(adapter);
        // Spinner auf die in der DB gespeicherte Portionsanzahl setzen
        spinner_portionsmenge.setSelection(portions -1);
        spinner_portionsmenge.setOnItemSelectedListener(this);

        pictureView = findViewById(R.id.picture);

        if (image != null && Shared.checkPermission(this, true)) {
            showImage(image, pictureView);
        }
        else {
            pictureView.setImageResource(R.drawable.camera_small);
        }

        // initialisiere den ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    grantUriPermission(
                            getPackageName(), selectedImageUri,
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(
                            selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    String wholeID = DocumentsContract.getDocumentId(data.getData());
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    column, sel, new String[]{id}, null);
                    if (cursor != null) {
                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            image = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    }

                    if (image != null) {
                        showImage(image, pictureView);
                    }
                }
            }
        });

            addIngredients(db, recipeIdEdit, this, rootView);

            addSteps();

        }

        @Override
        public void onClick (View view){

            FrameLayout layout_MainMenu = findViewById(R.id.mainmenu);

            if (view == button_add_steps) {
                PopupStepsEdit popup = new PopupStepsEdit();
                popup.showPopupWindow(view, this);


                //background-dimming
                layout_MainMenu.getForeground().setAlpha(220);
                layout_MainMenu.setElevation(1);

            } else if (view == button_add_ingredients) {
                PopupIngredientsEdit popup = new PopupIngredientsEdit();
                popup.showPopupWindow(view, this);

                //background-dimming
                layout_MainMenu.getForeground().setAlpha(220);
                layout_MainMenu.setElevation(1);

            } else if (view == button_add_recipe) {

                if (title.getText().length() > 0 && time.getText().length() > 0) {
                    //Rezepteinträge aktualisieren
                    db.updateRecipe(recipeIdEdit, title.getText().toString(), portions, Integer.parseInt(time.getText().toString()), isFavorite, image);

                    finish();
                } else {
                    Toast toast = new Toast(this);
                    toast.setText(getString(R.string.pleaseFillAllFields));
                    toast.show();
                }

            } else if (view == backButton || view == button_cancel) {
                finish();
            } else if (view == button_add_image) {
                openImagePicker();
            }

        }

    private void openImagePicker() {
        if (Shared.checkPermission(this, true)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        }
    }

        //welche portionsmenge ausgewählt wurde
        @Override
        public void onItemSelected (AdapterView < ? > adapterView, View view,int position, long l){
            portions = position + 1;
        }

        @Override
        public void onNothingSelected (AdapterView < ? > adapterView){
            portions = 1;
        }


        public void addSteps () {
            //schrittbeschreibungen in der view hinzufügen
            List<StepDTO> stepss = db.getAllStepsForRecipe((int) recipeIdEdit);

            for (StepDTO step : stepss) {

                //schrittbeschreibung in der view hinzufügen

                ConstraintLayout layout = new ConstraintLayout(this);

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                layout.setBackgroundResource(R.drawable.background_with_rounded_corners_green);
                layout.setPadding(20, 20, 20, 20);
                layout.setLayoutParams(layoutParams);
                layoutParams.setMargins(20, 10, 20, 10);

                // Text der Schrittbeschreibung
                TextView stepDescriptionText = new TextView(this);
                stepDescriptionText.setId(View.generateViewId());
                stepDescriptionText.setText(step.getText());
                stepDescriptionText.setGravity(Gravity.CENTER);
                stepDescriptionText.setTextColor(getColor(R.color.white));

                ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                stepDescriptionText.setLayoutParams(textViewParams);

                layout.addView(stepDescriptionText);

                //Mülleimer
                ImageView trash = new ImageView(this);
                trash.setImageResource(R.drawable.trashcan_light);
                trash.setId(View.generateViewId());

                ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                        45,
                        45
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

                trash.setOnClickListener(v -> {
                    db.deleteStep(step.getId());
                    parentLayout.removeView(layout);
                });

            }
        }

        @Override
        public void onPointerCaptureChanged ( boolean hasCapture){
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


