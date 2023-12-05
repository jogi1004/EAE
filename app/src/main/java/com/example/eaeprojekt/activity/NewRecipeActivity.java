package com.example.eaeprojekt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.popups.PopupIngredients;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.popups.PopupSteps;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.DTO.StepDTO;
import com.example.eaeprojekt.database.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class NewRecipeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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

    ConstraintLayout button_add_image;
    ImageView pictureView;
    String imagePath;
    public static long newRecipeId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    //private static final int MY_PERMISSIONS_REQUEST_CAMERA = 11;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        button_add_ingredients = findViewById(R.id.button_add_ingredients);

        //datenbankzugriff
        db = new DatabaseManager(this);
        db.open();

        //Neues Rezept erstellen ohne Inhalte (wird gelöscht, falls Vorgang abgebrochen wird)
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
            newRecipeId = db.insertRecipe(null, 1, 0, -1, null);
        }

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
        pictureView = findViewById(R.id.picture);

        //Layout zum dimmen
        FrameLayout layout_MainMenu = findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);


        //für db eintrag
        title = findViewById(R.id.title_text);
        time = findViewById(R.id.time_text);

        //spinner füllen
        spinner_portionsmenge = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.portionsmenge, android.R.layout.simple_spinner_dropdown_item);
        spinner_portionsmenge.setAdapter(adapter);
        spinner_portionsmenge.setOnItemSelectedListener(this);

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
                                imagePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        }

                    if (imagePath != null) {
                        File imgFile = new File(imagePath);
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
    public void onClick(View view) {

        FrameLayout layout_MainMenu = findViewById( R.id.mainmenu);

        if (view == button_add_steps) {
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
        } else if (view == button_add_image) {
            openImagePicker();
        } else if (view == button_add_recipe) {

            if(title.getText().length() > 0 && time.getText().length() > 0) {
                // datenbankzugriff
                db = new DatabaseManager(this);
                db.open();
                // Rezepteinträge aktualisieren
                db.updateRecipe(newRecipeId, title.getText().toString(), portionsmenge, Integer.parseInt(time.getText().toString()), 0, imagePath);

                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);

                finish();
            }else{
                Toast toast = new Toast(this);
                toast.setText("Fülle bitte zuerst alle Felder aus 😊");
                toast.show();
            }

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

    private void openImagePicker() {
        if (checkPermission(this)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        }
    }

    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                Log.d("HSKL", "Permission not granted");
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Berechtigung notwendig");
                    alertBuilder.setMessage("Bitte erlaube den Zugriff auf die Galerie in den Einstellungen");
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES));
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            }

            /*case MY_PERMISSIONS_REQUEST_CAMERA:
                Log.d("HSKL", "case Camera");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied. Cannot open camera.", Toast.LENGTH_SHORT).show();
                }
                break;*/
        }
    }

/*    private void openCamera() {
        if (checkPermissionForCamera()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.your.package.name.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    imagePickerLauncher.launch(takePictureIntent);
                }
            }
        }
    }*/

/*    private boolean checkPermissionForCamera() {
        int cameraPermissionResult = checkSelfPermission(Manifest.permission.CAMERA);
        if (cameraPermissionResult != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            return true;
        }
    }*/

/*    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/


    //welche portionsmenge ausgewählt wurde
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        portionsmenge = position +1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        portionsmenge = 1;
    }


    public void addIngredients(){
        List<IngredientAmountDTO> ingredientDTOs = db.getIngredientsForRecipe(newRecipeId);


        for(IngredientAmountDTO ingredient : ingredientDTOs){
            IngredientDTO ingredientBare = db.getIngredientById(ingredient.getIngredientId());

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
            amountText.setText((int)ingredient.getAmount());
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

            trash.setOnClickListener(v ->{
                db.deleteIngredientQuantity(ingredient.getId());
                parentLayout.removeView(layout);
            });
        }
    }

    public void addSteps() {
        //schrittbeschreibungen in der view hinzufügen
        List<StepDTO> steps = db.getAllStepsForRecipe((int) newRecipeId);

        for (StepDTO step : steps) {

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
            stepDescriptionText.setTextColor(Color.parseColor("#FFFFFF"));

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
