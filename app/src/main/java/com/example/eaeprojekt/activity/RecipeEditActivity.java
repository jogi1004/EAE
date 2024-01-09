package com.example.eaeprojekt.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.exifinterface.media.ExifInterface;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.popups.PopupIngredientsEdit;
import com.example.eaeprojekt.popups.PopupStepsEdit;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecipeEditActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ImageView backButton;

    ConstraintLayout button_add_image;
    ImageView pictureView;
    ConstraintLayout button_add_ingredients;
    ConstraintLayout button_add_steps;
    ConstraintLayout button_add_recipe;
    ConstraintLayout button_cancel;
    DatabaseManager db;
    EditText time, title;
    Spinner spinner_portionsmenge;
    String recipeTitle, image;
    private int isFavorite, portions;
    public static long recipeIDEdit;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);

        Intent receive = getIntent();
        recipeIDEdit = receive.getIntExtra("ID", 0);

        //datenbankzugriff
        db = new DatabaseManager(this);
        db.open();

        RecipeDTO recipe = db.getRecipeById(recipeIDEdit);
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
            File imgFile = new File(image);
            if(imgFile.exists()){
                try {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    int rotate = 0;
                    ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                    pictureView.setImageBitmap(myBitmap);
                    pictureView.setRotation(rotate);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
                        File imgFile = new File(image);
                        if(imgFile.exists()){
                            try {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                int rotate = 0;
                                ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotate = 270;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotate = 180;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotate = 90;
                                        break;
                                }
                                pictureView.setImageBitmap(myBitmap);
                                pictureView.setRotation(rotate);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });

            addIngredients();

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
                    db.updateRecipe(recipeIDEdit, title.getText().toString(), portions, Integer.parseInt(time.getText().toString()), isFavorite, image);

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


        public void addIngredients () {
            List<IngredientAmountDTO> ingredientDTOs = db.getIngredientsForRecipe(recipeIDEdit);


            for (IngredientAmountDTO ingredient : ingredientDTOs) {
                IngredientDTO ingredientBare = db.getIngredientById(ingredient.getIngredientId());

                ConstraintLayout layout = new ConstraintLayout(this);

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                layout.setPadding(20, 20, 20, 20);
                layout.setLayoutParams(layoutParams);
                layoutParams.setMargins(40, 0, 40, 0);

            /*
            Zutat
             */
                TextView ingredientText = new TextView(this);
                ingredientText.setId(View.generateViewId());
                ingredientText.setText(ingredientBare.getName());
                ingredientText.setGravity(Gravity.CENTER);
                ingredientText.setTextColor(getColor(R.color.white));

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
                amountText.setText(String.valueOf((int) ingredient.getAmount()));
                amountText.setGravity(Gravity.CENTER);
                amountText.setTextColor(getColor(R.color.white));

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
                unitText.setTextColor(getColor(R.color.white));

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
                trash.setImageResource(R.drawable.trashcan_light);
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

                trash.setOnClickListener(v -> {
                    db.deleteIngredientQuantity(ingredient.getId());
                    parentLayout.removeView(layout);
                });

            }

        }

        public void addSteps () {
            //schrittbeschreibungen in der view hinzufügen
            List<StepDTO> stepss = db.getAllStepsForRecipe((int) recipeIDEdit);

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
                layoutParams.setMargins(40, 10, 40, 10);

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


