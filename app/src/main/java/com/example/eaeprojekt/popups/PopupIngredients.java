package com.example.eaeprojekt.popups;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.activity.ShoppingBagActivity;
import com.example.eaeprojekt.activity.ShoppingBagUpdateListener;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.DTO.IngredientDTO;
import com.example.eaeprojekt.utility.KeyboardUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class PopupIngredients implements View.OnClickListener {

    DatabaseManager db;

    Activity mainActivity;
    View popupView;
    PopupWindow popupWindow;
    //Adapter für Zutaten
    private CustomAdapter adapter;
    Spinner ingredients;
    TextView unitTV;

    FrameLayout frame;

    ArrayList<String> ingredientList;
    
    
    ConstraintLayout cancelButtonIngredient;
    ConstraintLayout addButtonIngredient;

    EditText nameText, unitText, amountText;
    ConstraintLayout createIngredient;


    ImageView addIngredientCross;

    String chosenIngredient;
    String chosenUnit;
    View parentView;

    private ShoppingBagUpdateListener updateListener = null;

    public PopupIngredients() {}
    public PopupIngredients(ShoppingBagUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void showPopupWindow(final View view, Activity activity) {

        mainActivity = activity;


        db = new DatabaseManager(mainActivity);
        db.open();

        parentView = view;


        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_ingredients_popup, null);
        this.popupView = popupView;

        KeyboardUtils.setupUI(popupView.findViewById(R.id.below), mainActivity);

        //length and width from the Window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        //Create window
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Spinner Zutaten
        ingredients = popupView.findViewById(R.id.spinnerIngredient);

        //Spinner mit daten aus db füllen
        ingredientList = new ArrayList<>();

        List<IngredientDTO> allIngredients = db.getAllIngredients();
        for(IngredientDTO newIngredient : allIngredients){
            ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
        }

        adapter = new CustomAdapter(mainActivity, ingredientList);
        ingredients.setAdapter(adapter);

        unitTV = popupView.findViewById(R.id.textViewUnit);

        if(!adapter.isEmpty()) {
            String selectedItem = adapter.getItem(0);
            String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
            chosenIngredient = separated[0];
            chosenUnit = separated[1];

            unitTV.setText(chosenUnit);

        }

        addIngredientCross = popupView.findViewById(R.id.addIngredientCross);
        addIngredientCross.setOnClickListener(this);

        createIngredient = popupView.findViewById(R.id.createIngredient);
        createIngredient.setOnClickListener(this);
        
        
        cancelButtonIngredient = popupView.findViewById(R.id.cancel_button_ingredient);
        cancelButtonIngredient.setOnClickListener(this);

        addButtonIngredient = popupView.findViewById(R.id.add_button_ingredient);
        addButtonIngredient.setOnClickListener(this);

        if(mainActivity.getClass() == NewRecipeActivity.class) {
            frame = mainActivity.findViewById(R.id.mainmenu);
        }else{
            frame = mainActivity.findViewById(R.id.FrameLayoutShoppingBag);
        }
    }


    @Override
    public void onClick(View view) {

        ConstraintLayout layoutAddIngredient = popupView.findViewById(R.id.addIngredientLayout);

        nameText = popupView.findViewById(R.id.nameText);
        String nameTextString = nameText.getText().toString();

        unitText = popupView.findViewById(R.id.unitText);
        String unitTextString = unitText.getText().toString();

        amountText = popupView.findViewById(R.id.amount);

        ConstraintLayout bbelow = popupView.findViewById(R.id.bbelow);

        if (view == addIngredientCross){

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
        }else if(view == createIngredient){

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
        }else if (view == cancelButtonIngredient) {
            frame.getForeground().setAlpha(0);
            frame.setElevation(0);
            popupWindow.dismiss();
            frame.performClick();
        } else if (view == addButtonIngredient) {

            if (!amountText.getText().toString().isEmpty()) {

                frame.getForeground().setAlpha(0);
                frame.setElevation(0);
                popupWindow.dismiss();
                frame.performClick();

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

        }else if (view == frame) {
            frame.getForeground().setAlpha(0);
            frame.setElevation(0);
        }
    }

    /*
===========================================addIngredientToNewRecipe===============================================================================================
 */

    public void addIngredientToNewRecipe(){

        IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);

        EditText amount = popupView.findViewById(R.id.amount);

        // Zutat zum Rezept hinzufügen
        long ingredientId = db.insertIngredientQuantity(NewRecipeActivity.newRecipeId, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()),0, 0);
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
    public void addIngredientToShoppingBag()  {

        IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);
        EditText amount = popupView.findViewById(R.id.amount);
        // Zutat in die Einkaufsliste hinzufügen
        db.insertIngredientQuantity(-9, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()),1, 0);
    }

    class CustomAdapter extends ArrayAdapter<String> {

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
                ConstraintLayout editView = popupView.findViewById(R.id.above);
                editView.setVisibility(View.VISIBLE);

                ConstraintLayout belowView = popupView.findViewById(R.id.bbelow);
                belowView.setVisibility(View.GONE);


                String selectedItem = getItem(position);
                String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                chosenIngredient = separated[0];
                chosenUnit = separated[1];


                EditText editName = popupView.findViewById(R.id.nameText2);
                editName.setText(chosenIngredient);

                EditText editUnit = popupView.findViewById(R.id.unitText2);
                editUnit.setText(chosenUnit);

                IngredientDTO ingredient = db.getIngredientByNameAndUnit(chosenIngredient, chosenUnit);


                PopupEditIngredient popup = new PopupEditIngredient();

                popup.insideEdit(popupView, mainActivity, ingredient.getId(), adapter, ingredientList);

                return true;
            });

            view.setOnClickListener(view1 -> {
                String selectedItem = getItem(position);
                String[] separated = selectedItem != null ? selectedItem.split(", ") : new String[0];
                chosenIngredient = separated[0];
                chosenUnit = separated[1];

                unitTV = popupView.findViewById(R.id.textViewUnit);
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


    class PopupEditIngredient implements View.OnClickListener {


        DatabaseManager db;
        View parentView;
        ConstraintLayout buttonEdit;
        ConstraintLayout buttonBack;
        ConstraintLayout buttonDelete;
        Activity mainActivity;

        long id;

        PopupIngredients.CustomAdapter ada;

        ArrayList<String> ingredientList;


        public void insideEdit(final View view, Activity activity, long ingredientId, PopupIngredients.CustomAdapter adapter, ArrayList<String> ingredientList) {

            id = ingredientId;

            parentView = view;

            mainActivity = activity;

            ada = adapter;

            this.ingredientList = ingredientList;


            //Buttons
            buttonEdit = view.findViewById(R.id.edit_button);
            buttonEdit.setOnClickListener(this);

            buttonBack = view.findViewById(R.id.cancel_edit);
            buttonBack.setOnClickListener(this);

            buttonDelete = view.findViewById(R.id.delete_button);
            buttonDelete.setOnClickListener(this);


        }

        @Override
        public void onClick(View viewClick) {

            if(viewClick == buttonBack){
                parentView.findViewById(R.id.above).setVisibility(View.GONE);
                parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);
            }else if(viewClick == buttonEdit){
                //datenbankzugriff
                db = new DatabaseManager(mainActivity);
                db.open();

                EditText name = parentView.findViewById(R.id.nameText2);
                EditText unit = parentView.findViewById(R.id.unitText2);

                db.updateIngredient(id, name.getText().toString(), unit.getText().toString());

                parentView.findViewById(R.id.above).setVisibility(View.GONE);
                parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

                ingredientList.clear();

                List<IngredientDTO> allIngredients = db.getAllIngredients();
                for(IngredientDTO newIngredient : allIngredients){
                    ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
                }

                ada.notifyDataSetChanged();

                chosenIngredient = name.getText().toString();
                chosenUnit = unit.getText().toString();

                unitTV = popupView.findViewById(R.id.textViewUnit);
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
                NewRecipeActivity.addIngredients(db, NewRecipeActivity.newRecipeId, mainActivity, mainActivity.findViewById(android.R.id.content));


            } else if (viewClick == buttonDelete) {
                db = new DatabaseManager(mainActivity);
                db.open();

                db.deleteIngredient(id);

                parentView.findViewById(R.id.above).setVisibility(View.GONE);
                parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

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

                unitTV = popupView.findViewById(R.id.textViewUnit);
                unitTV.setText(chosenUnit);

                ingredients.setSelection(0);

                LinearLayout ingredientLayout = mainActivity.findViewById(R.id.ingredientsLayout);
                ingredientLayout.removeAllViews();
                NewRecipeActivity.addIngredients(db, NewRecipeActivity.newRecipeId, mainActivity, mainActivity.findViewById(android.R.id.content));


            }
        }
    }

}
