package com.example.eaeprojekt.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.activity.RecipeEditActivity;
import com.example.eaeprojekt.activity.Shared;
import com.example.eaeprojekt.activity.ShoppingBagActivity;
import com.example.eaeprojekt.activity.ShoppingBagUpdateListener;
import com.example.eaeprojekt.database.DatabaseManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IngredientDialogUtil {

        static DatabaseManager db;

        static Activity mainActivity;
        static View popupView;
        PopupWindow popupWindow;
        //Adapter für Zutaten
        private static CustomAdapter adapter;

        static Spinner ingredients;
        static TextView unitTV;

        static ArrayList<String> ingredientList;


        static ConstraintLayout cancelButtonIngredient;
        static ConstraintLayout addButtonIngredient;

        static EditText nameText;
        static EditText unitText;
        static EditText amountText;

        static ConstraintLayout createIngredient;


        static ImageView addIngredientCross;

        static String chosenIngredient;
        static String chosenUnit;
        static View parentView;
        static Dialog dialog;
        static ConstraintLayout layoutAddIngredient;

        private static ShoppingBagUpdateListener updateListener = null;

        public IngredientDialogUtil() {}
        public IngredientDialogUtil(ShoppingBagUpdateListener updateListener) {
            this.updateListener = updateListener;
        }

        @SuppressLint("ResourceType")
        public void showPopupWindow(final View view, Activity activity) {

            mainActivity = activity;
            dialog = new Dialog(mainActivity);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(true);

            dialog.setContentView(R.layout.add_ingredients_popup);

            dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            layoutAddIngredient = dialog.findViewById(R.id.addIngredientLayout);




            db = new DatabaseManager(mainActivity);
            db.open();

            parentView = view;

            KeyboardUtils.setupUI(dialog.findViewById(R.id.below), mainActivity);



            //Spinner Zutaten
            ingredients = dialog.findViewById(R.id.spinnerIngredient);

            //Spinner mit daten aus db füllen
            ingredientList = new ArrayList<>();

            List<IngredientDTO> allIngredients = db.getAllIngredients();
            for(IngredientDTO newIngredient : allIngredients){
                ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
            }

            adapter = new CustomAdapter(mainActivity, ingredientList);
            ingredients.setAdapter(adapter);

            unitTV = dialog.findViewById(R.id.textViewUnit);

            if(!adapter.isEmpty()) {
                String selectedItem = adapter.getItem(0);
                String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                chosenIngredient = separated[0];
                chosenUnit = separated[1];

                unitTV.setText(chosenUnit);

            }

            addIngredientCross = dialog.findViewById(R.id.addIngredientCross);
            addIngredientCross.setOnClickListener((v)->{
                openDialogNewIngredient(dialog.findViewById(R.id.bbelow));
                    });

            createIngredient = dialog.findViewById(R.id.createIngredient);
            createIngredient.setOnClickListener((v)->{
                nameText = dialog.findViewById(R.id.nameText);
                String nameTextString = nameText.getText().toString();

                unitText = dialog.findViewById(R.id.unitText);
                String unitTextString = unitText.getText().toString();

                createIngredient(nameTextString, unitTextString, dialog.findViewById(R.id.bbelow));
            });


            cancelButtonIngredient = dialog.findViewById(R.id.cancel_button_ingredient);
            cancelButtonIngredient.setOnClickListener((v)->{
                dialog.dismiss();
            });

            addButtonIngredient = dialog.findViewById(R.id.add_button_ingredient);
            addButtonIngredient.setOnClickListener((v)->{
                amountText = dialog.findViewById(R.id.amount);
                addIngredient(amountText);

            });

            dialog.show();
        }

        public static void openDialogNewIngredient(ConstraintLayout bbelow){
            if(layoutAddIngredient.getVisibility() == View.VISIBLE){
                layoutAddIngredient.setVisibility(View.INVISIBLE);
                addIngredientCross.setImageResource(R.drawable.plus_dark);


                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bbelow);

                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.START, R.id.bbelow, ConstraintSet.START, 30);
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.TOP, R.id.chooseAmount, ConstraintSet.BOTTOM, 40); // Hier wird die obere View referenziert
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.BOTTOM, R.id.bbelow, ConstraintSet.BOTTOM, 30);

                constraintSet.applyTo(bbelow);

            }else {
                layoutAddIngredient.setVisibility(View.VISIBLE);
                addIngredientCross.setImageResource(R.drawable.minus_dark);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bbelow);

                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.START, R.id.bbelow, ConstraintSet.START, 30);
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.TOP, R.id.addIngredientLayout, ConstraintSet.BOTTOM, 40); // Hier wird die obere View referenziert
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.BOTTOM, R.id.bbelow, ConstraintSet.BOTTOM, 30);

                constraintSet.applyTo(bbelow);
            }
        }

        public static void createIngredient(String nameTextString, String unitTextString, ConstraintLayout bbelow){
            if(!nameTextString.isEmpty() && !unitTextString.isEmpty()){
                db.insertIngredient(nameTextString, unitTextString);

                ingredientList.add(nameTextString + ", " + unitTextString);
                adapter.notifyDataSetChanged();

                ingredients.setSelection(ingredientList.size() - 1);

                String selectedItem = adapter.getItem(ingredientList.size() - 1);
                String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                chosenIngredient = separated[0];
                chosenUnit = separated[1];

                unitTV.setText(chosenUnit);


                layoutAddIngredient.setVisibility(View.INVISIBLE);
                addIngredientCross.setImageResource(R.drawable.plus_dark);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bbelow);

                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.START, R.id.bbelow, ConstraintSet.START, 30);
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.TOP, R.id.chooseAmount, ConstraintSet.BOTTOM, 40); // Hier wird die obere View referenziert
                constraintSet.connect(R.id.add_button_ingredient, ConstraintSet.BOTTOM, R.id.bbelow, ConstraintSet.BOTTOM, 30);

                constraintSet.applyTo(bbelow);

            }else{
                Toast toast = new Toast(mainActivity);
                toast.setText(R.string.pleaseFillAllFields);
                toast.show();
            }
        }

        public static void addIngredient(EditText amountText){
            if (!amountText.getText().toString().isEmpty()) {

                dialog.dismiss();

                if (mainActivity.getClass() == ShoppingBagActivity.class) {
                    addIngredientToShoppingBag();
                    if (updateListener != null) {
                        updateListener.onUpdateShoppingBag();
                    }
                } else {
                    addIngredientToNewRecipe();
                }
            }
            else {
                Toast toast = new Toast(mainActivity);
                toast.setText(R.string.pleaseFillAllFields);
                toast.show();
            }
        }

    /*
===========================================addIngredientToNewRecipe===============================================================================================
 */

        public static void addIngredientToNewRecipe(){

            IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);

            EditText amount = dialog.findViewById(R.id.amount);

            // Zutat zum Rezept hinzufügen

            long ingredientId;
            if (mainActivity.getClass() == NewRecipeActivity.class) {
                ingredientId = db.insertIngredientQuantity(NewRecipeActivity.newRecipeId, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()), 0, 0);
            }else{
                ingredientId = db.insertIngredientQuantity(RecipeEditActivity.recipeIdEdit, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()), 0, 0);
            }
            /*
            schrittbeschreibung in der view hinzufügen
             */
            ConstraintLayout layout = new ConstraintLayout(mainActivity);

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
            TextView ingredientText = new TextView(mainActivity);
            ingredientText.setId(View.generateViewId());
            ingredientText.setText(ingredientToAdd.getName());
            ingredientText.setGravity(Gravity.CENTER);
            ingredientText.setTextColor(mainActivity.getColor(R.color.white));

            ViewGroup.LayoutParams ingredientParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ingredientText.setLayoutParams(ingredientParams);

            layout.addView(ingredientText);

            /*
            Menge
             */
            TextView amountText = new TextView(mainActivity);
            amountText.setId(View.generateViewId());
            amountText.setText(amount.getText().toString());
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextColor(mainActivity.getColor(R.color.white));

            ViewGroup.LayoutParams amountParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            amountText.setLayoutParams(amountParams);

            layout.addView(amountText);

            /*
            Einheit
             */
            TextView unitText = new TextView(mainActivity);
            unitText.setId(View.generateViewId());
            unitText.setText(ingredientToAdd.getUnit());
            unitText.setGravity(Gravity.CENTER);
            unitText.setTextColor(mainActivity.getColor(R.color.white));

            ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            unitText.setLayoutParams(unitParams);

            layout.addView(unitText);

            /*
            Mülleimer
             */
            ImageView trash = new ImageView(mainActivity);
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


            //LinearLayout parentLayout = NewRecipeActivity.binding.ingredientsLayout;
            LinearLayout parentLayout = mainActivity.findViewById(R.id.ingredientsLayout);
            parentLayout.addView(layout);

            trash.setOnClickListener(v ->{
                db.deleteIngredientQuantity(ingredientId);
                parentLayout.removeView(layout);
            });
        }
        /*
        =============================================addIngredientToShoppingBag=============================================================================================
         */
        public static void addIngredientToShoppingBag()  {

            IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);
            EditText amount = dialog.findViewById(R.id.amount);
            // Zutat in die Einkaufsliste hinzufügen
            db.insertIngredientQuantity(-9, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()),1, 0);
        }


        static class CustomAdapter extends ArrayAdapter<String> {

            public CustomAdapter(Context context, List<String> items) {
                super(context, android.R.layout.simple_spinner_item, items);
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                // Füge den OnLongClickListener zu jedem Element in der Dropdown-Liste hinzu
                view.setOnLongClickListener(v -> {

                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(ingredients);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ConstraintLayout editView = dialog.findViewById(R.id.above);
                    editView.setVisibility(View.VISIBLE);

                    ConstraintLayout belowView = dialog.findViewById(R.id.bbelow);
                    belowView.setVisibility(View.GONE);


                    String selectedItem = getItem(position);
                    String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                    chosenIngredient = separated[0];
                    chosenUnit = separated[1];


                    EditText editName = dialog.findViewById(R.id.nameText2);
                    editName.setText(chosenIngredient);

                    EditText editUnit = dialog.findViewById(R.id.unitText2);
                    editUnit.setText(chosenUnit);

                    IngredientDTO ingredient = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);


                    PopupEditIngredient dialog2 = new PopupEditIngredient();

                    dialog2.insideEdit(view, mainActivity, ingredient.getId(), adapter, ingredientList);

                    return true;
                });

                view.setOnClickListener(view1 -> {
                    String selectedItem = getItem(position);
                    String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                    chosenIngredient = separated[0];
                    chosenUnit = separated[1];

                    unitTV = dialog.findViewById(R.id.textViewUnit);
                    unitTV.setText(chosenUnit);

                    ingredients.setSelection(position);

                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(ingredients);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                return view;
            }
        }


        static class PopupEditIngredient implements View.OnClickListener {


            DatabaseManager db;
            View parentView;
            ConstraintLayout buttonEdit;
            ConstraintLayout buttonBack;
            ConstraintLayout buttonDelete;
            Activity mainActivity;

            long id;

           CustomAdapter ada;

            ArrayList<String> ingredientList;


            public void insideEdit(final View view, Activity activity, long ingredientId, CustomAdapter adapter, ArrayList<String> ingredientList) {

                id = ingredientId;

                parentView = view;

                mainActivity = activity;

                ada = adapter;

                this.ingredientList = ingredientList;


                //Buttons
                buttonEdit = dialog.findViewById(R.id.edit_button);
                buttonEdit.setOnClickListener(this);

                buttonBack = dialog.findViewById(R.id.cancel_edit);
                buttonBack.setOnClickListener(this);

                buttonDelete = dialog.findViewById(R.id.delete_button);
                buttonDelete.setOnClickListener(this);


            }

            @Override
            public void onClick(View viewClick) {

                if(viewClick == buttonBack){
                    dialog.findViewById(R.id.above).setVisibility(View.GONE);
                    dialog.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);
                }else if(viewClick == buttonEdit){
                    //datenbankzugriff
                    db = new DatabaseManager(mainActivity);
                    db.open();

                    EditText name = dialog.findViewById(R.id.nameText2);
                    EditText unit = dialog.findViewById(R.id.unitText2);

                    db.updateIngredient(id, name.getText().toString(), unit.getText().toString());

                    dialog.findViewById(R.id.above).setVisibility(View.GONE);
                    dialog.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

                    ingredientList.clear();

                    List<IngredientDTO> allIngredients = db.getAllIngredients();
                    for(IngredientDTO newIngredient : allIngredients){
                        ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
                    }

                    ada.notifyDataSetChanged();

                    chosenIngredient = name.getText().toString();
                    chosenUnit = unit.getText().toString();

                    unitTV = dialog.findViewById(R.id.textViewUnit);
                    unitTV.setText(chosenUnit);

                    int pos = 0;

                    for(int i = 0; i < ingredientList.size(); i++){
                        if(ingredientList.get(i).equals(chosenIngredient + ", " + chosenUnit)){
                            pos = i;
                            break;
                        }
                    }
                    ingredients.setSelection(pos);

                    LinearLayout ingredientLayout = mainActivity.findViewById(R.id.ingredientsLayout);
                    ingredientLayout.removeAllViews();

                    Shared.addIngredients(db, NewRecipeActivity.newRecipeId, mainActivity, mainActivity.findViewById(android.R.id.content));


                } else if (viewClick == buttonDelete) {
                    db = new DatabaseManager(mainActivity);
                    db.open();

                    boolean isUsed = false;
                    List<IngredientAmountDTO> allIngredientAmounts = db.getAllIngredientAmounts();
                    for (IngredientAmountDTO i : allIngredientAmounts) {
                        if (i.getIngredientId() == id) {
                            isUsed = true;
                            break;
                        }
                    }
                    if (!isUsed)
                        db.deleteIngredient(id);
                    else {
                        Toast toast = new Toast(mainActivity);
                        toast.setText(R.string.ingredientAmountCannotBeDeleted);
                        toast.show();
                    }


                    dialog.findViewById(R.id.above).setVisibility(View.GONE);
                    dialog.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

                    ingredientList.clear();

                    List<IngredientDTO> allIngredients = db.getAllIngredients();
                    for(IngredientDTO newIngredient : allIngredients){
                        ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
                    }

                    ada.notifyDataSetChanged();

                    String selectedItem = ingredientList.get(0);
                    String[] separated = selectedItem.split(", ");
                    chosenIngredient = separated[0];
                    chosenUnit = separated[1];

                    unitTV = dialog.findViewById(R.id.textViewUnit);
                    unitTV.setText(chosenUnit);

                    ingredients.setSelection(0);

                    

                }
            }
        }

    }
