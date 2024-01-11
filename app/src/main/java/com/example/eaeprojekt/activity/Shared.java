package com.example.eaeprojekt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Shared {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    //private static final int MY_PERMISSIONS_REQUEST_CAMERA = 11;
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 12;


    public static boolean checkPermission(final Context context, boolean reminder) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowGalleryAccess);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                            // ask user to grant permission
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES)
                    );
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else if (reminder) {
                    // ask the user to allow the permission in the settings
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowGalleryAccessInSettings);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                return false;
            } else {
                // Permission already granted
                return true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowStorageAccess);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                            // ask user to grant permission
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    );
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    // ask the user to allow the permission in the settings
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowStorageAccessInSettings);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                return false;
            } else {
                // Permission already granted
                return true;
            }
        }
    }

    public static int dpToPx(int dp, float density) {
        return Math.round(dp * density);
    }

    public static void addIngredients(DatabaseManager db, long recipeId, Context context, View view) {
        List<IngredientAmountDTO> ingredientDTOs = db.getIngredientsForRecipe(recipeId);

        for (IngredientAmountDTO ingredient : ingredientDTOs) {

            IngredientDTO ingredientBare = db.getIngredientById(ingredient.getIngredientId());

            ConstraintLayout layout = new ConstraintLayout(context);

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
            TextView ingredientText = new TextView(context);
            ingredientText.setId(View.generateViewId());
            ingredientText.setText(ingredientBare.getName());
            ingredientText.setGravity(Gravity.CENTER);
            ingredientText.setTextColor(ContextCompat.getColor(context, R.color.white));

            ViewGroup.LayoutParams ingredientParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ingredientText.setLayoutParams(ingredientParams);

            layout.addView(ingredientText);

            /*
            Menge
             */
            TextView amountText = new TextView(context);
            amountText.setId(View.generateViewId());
            amountText.setText(String.valueOf((int) ingredient.getAmount()));
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextColor(ContextCompat.getColor(context, R.color.white));

            ViewGroup.LayoutParams amountParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            amountText.setLayoutParams(amountParams);

            layout.addView(amountText);

            /*
            Einheit
             */
            TextView unitText = new TextView(context);
            unitText.setId(View.generateViewId());
            unitText.setText(ingredientBare.getUnit());
            unitText.setGravity(Gravity.CENTER);
            unitText.setTextColor(ContextCompat.getColor(context, R.color.white));

            ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            unitText.setLayoutParams(unitParams);

            layout.addView(unitText);

            /*
            Mülleimer
             */
            ImageView trash = new ImageView(context);
            trash.setImageResource(R.drawable.trashcan_light);
            trash.setId(View.generateViewId());

            ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                    45,
                    45
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
            constraintSet.connect(amountText.getId(), ConstraintSet.START, ingredientText.getId(), ConstraintSet.START, 250);
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

            LinearLayout parentLayout = view.findViewById(R.id.ingredientsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v -> {
                db.deleteIngredientQuantity(ingredient.getId());
                parentLayout.removeView(layout);
            });
        }
    }

    public static void showImage(String imagePath, ImageView pictureView) {
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
