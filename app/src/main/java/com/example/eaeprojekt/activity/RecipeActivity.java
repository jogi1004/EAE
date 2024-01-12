package com.example.eaeprojekt.activity;

import static com.example.eaeprojekt.activity.Shared.showImage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.DTO.RecipeDTO;
import com.example.eaeprojekt.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class RecipeActivity extends AppCompatActivity {

    private LinearLayout recipeLayout;
    private DatabaseManager db;
    private SwitchCompat favSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeLayout = findViewById(R.id.recipeLayoutinScrollView);
        db = new DatabaseManager(this);
        db.open();
        updateRecipeList(db.getAllRecipes());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.recipeListButtonNavBar);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        favSwitch = findViewById(R.id.favSwitch);
        favSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateRecipeList(db.getFavoritenRezepte());
            } else {
                updateRecipeList(db.getAllRecipes());
            }
        });
    }

    private void updateRecipeList(List<RecipeDTO> recipes) {
        recipeLayout.removeAllViews();
        for (RecipeDTO recipe : recipes) {
            if (recipe.getIsFavorite() != -1) {
                long recipeid = recipe.getId();
                RelativeLayout recipeItem = new RelativeLayout(this);
                recipeItem.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                /*
                 * Creating Backgroundshape with rounded Corners
                 */
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(30); // Radius für abgerundete Ecken in Pixeln
                shape.setColor(getColor(R.color.darkerYellow));

                /*
                 * Picture of the Recipe
                 */
                de.hdodenhof.circleimageview.CircleImageView picture = new de.hdodenhof.circleimageview.CircleImageView(this);

                if (recipe.getImagePath() != null && Shared.checkPermission(this, false)) {
                    showImage(recipe.getImagePath(), picture);
                } else {
                    picture.setImageResource(R.drawable.camera_small);
                }

                picture.setPadding(15, 15, 15, 15);

                LinearLayout llayout = new LinearLayout(this);
                llayout.setOrientation(LinearLayout.HORIZONTAL);

                RelativeLayout.LayoutParams pictureParams = new RelativeLayout.LayoutParams(200, 200);
                pictureParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);  // Align the picture to the left
                pictureParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center the picture vertically
                picture.setLayoutParams(pictureParams);
                picture.setBorderWidth(5);
                picture.setBorderColor(getColor(R.color.fontColor));

                LinearLayout dataLayout = new LinearLayout(this);
                dataLayout.setOrientation(LinearLayout.VERTICAL);
                RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                dataParams.addRule(RelativeLayout.RIGHT_OF, picture.getId());  // Position dataLayout to the right of picture
                dataParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center dataLayout vertically
                dataLayout.setLayoutParams(dataParams);

                int marginInDp = (int) getResources().getDimension(R.dimen.margin_70dp);
                dataLayout.setPadding(50, 0, marginInDp, 30);

                TextView recipeName = new TextView(this);
                recipeName.setText(recipe.getTitle());
                recipeName.setTextSize(25);
                recipeName.setTypeface(null, Typeface.BOLD);
                TextView recipeDetails = new TextView(this);

                int duration = recipe.getDuration();
                int hours = duration / 60;
                int minutes = duration % 60;

                String durationText;

                if (hours > 0 && minutes > 0) {
                    durationText = String.format(Locale.GERMANY, getString(R.string.hoursAndMinutes), hours, minutes);
                } else if (hours > 0) {
                    durationText = String.format(Locale.GERMANY, getString(R.string.hours), hours);
                } else {
                    durationText = String.format(Locale.GERMANY, getString(R.string.min), minutes);
                }

                int portionen = recipe.getPortions();
                String portion;
                if (portionen > 1) {
                    portion = portionen + " " + getString(R.string.portions);
                } else {
                    portion = getString(R.string.onePortion);
                }

                recipeDetails.setText(String.format(Locale.GERMANY, getString(R.string.duration), portion, durationText));
                recipeDetails.setTextSize(16);

                ImageView favIcon = new ImageView(this);
                favIcon.setImageResource(recipe.getIsFavorite() == 1 ? R.drawable.favoritestar_filled_dark : R.drawable.favoritestar_hollow_dark);
                favIcon.setLayoutParams(new RelativeLayout.LayoutParams(140, 140));
                RelativeLayout.LayoutParams favIconParams = new RelativeLayout.LayoutParams(
                        100,
                        100
                );
                favIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);  // Align the favIcon to the right
                favIconParams.addRule(RelativeLayout.CENTER_VERTICAL);  // Center the favIcon vertically
                favIcon.setLayoutParams(favIconParams);
                favIcon.setPadding(0, 20, 20, 0);

                favIcon.setOnClickListener(v -> {
                    if (recipe.getIsFavorite() == 1) {
                        recipe.setIsFavorite(0);
                        favIcon.setImageResource(R.drawable.favoritestar_hollow_dark);
                    } else {
                        recipe.setIsFavorite(1);
                        favIcon.setImageResource(R.drawable.favoritestar_filled_dark);
                    }
                    db.open();
                    db.updateRecipe(recipe.getId(), recipe.getTitle(), recipe.getPortions(), recipe.getDuration(), recipe.getIsFavorite(), recipe.getImagePath());
                    if (favSwitch.isChecked()) {
                        updateRecipeList(db.getFavoritenRezepte());
                    }
                });

                /*
                 * Layout for adding Margins between Recipes
                 */
                RelativeLayout.LayoutParams marginLayout = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                marginLayout.setMargins(0, 0, 0, 30);
                recipeItem.setLayoutParams(marginLayout);


                recipeItem.setBackground(shape);
                dataLayout.addView(recipeName);
                dataLayout.addView(recipeDetails);
                llayout.addView(picture);
                llayout.addView(dataLayout);
                recipeItem.addView(llayout);
                recipeItem.addView(favIcon);
                /*
                  Use Recipe ID to identify which Layout got which Recipe
                 */
                recipeItem.setId((int) recipeid);
                /*
                 * OnClickListener for opening DetailView of Recipe
                 */
                recipeItem.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent i = new Intent(context, RecipeDetailViewActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.putExtra("ID", recipeItem.getId());
                    startActivity(i);
                });

                recipeItem.setOnLongClickListener(v -> {
                    showDeleteConfirmationDialog(recipe.getId());
                    return true;
                });
                recipeLayout.addView(recipeItem);
            }
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddButtonNavBar) {
            Intent i = new Intent(this, NewRecipeActivity.class);
            startActivity(i);
        }
        if (id == R.id.shoppingBagButtonNavBar) {
            Intent i = new Intent(this, ShoppingBagActivity.class);
            startActivity(i);
        }
        return false;
    }

    private void showDeleteConfirmationDialog(long recipeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.deleteRecipe);
        builder.setMessage(R.string.deleteRecipeCheck);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            for (IngredientAmountDTO ingr : db.getIngredientsForRecipe(recipeId)) {
                db.deleteIngredientQuantity(ingr.getId());
            }
            deleteRecipe(recipeId);
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void deleteRecipe(long recipeId) {
        db.open();
        db.deleteRecipe(recipeId);
        updateRecipeList(db.getAllRecipes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        updateRecipeList(db.getAllRecipes());
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

