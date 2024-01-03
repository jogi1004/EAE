package com.example.eaeprojekt.popups;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.MainActivity;
import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.activity.ShoppingBagActivity;
import com.example.eaeprojekt.database.DatabaseManager;
import com.example.eaeprojekt.DTO.IngredientDTO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


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

    EditText nameText;
    EditText unitText;
    ConstraintLayout createIngredient;


    ImageView addIngredientCross;

    String choosedIngredient;
    String choosedUnit;

    View parentView;

    public void showPopupWindow(final View view, Activity activity) {


        mainActivity = activity;

        db = new DatabaseManager(mainActivity);
        db.open();

        parentView = view;


        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_ingredients_popup, null);
        this.popupView = popupView;

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
            String[] separated = selectedItem.split(", ");
            choosedIngredient = separated[0];
            choosedUnit = separated[1];

            unitTV.setText(choosedUnit.toString());
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
                String[] separated = selectedItem.split(", ");
                choosedIngredient = separated[0];
                choosedUnit = separated[1];

                unitTV.setText(choosedUnit.toString());


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
                toast.setText("Füllen sie zuerst die Felder aus");
                toast.show();
            }

        }else if (view == cancelButtonIngredient) {
            frame.getForeground().setAlpha(0);
            frame.setElevation(0);
            popupWindow.dismiss();
            frame.performClick();
        } else if (view == addButtonIngredient) {

            frame.getForeground().setAlpha(0);
            frame.setElevation(0);
            popupWindow.dismiss();
            frame.performClick();

            if(mainActivity.getClass() == ShoppingBagActivity.class){
                addIngredientToShoppingBag();
            }else {
                addIngredientToNewRecipe();
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

        IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(choosedIngredient, choosedUnit);

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
        TextView amountText = new TextView(mainActivity);
        amountText.setId(View.generateViewId());
        amountText.setText(amount.getText().toString());
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
        TextView unitText = new TextView(mainActivity);
        unitText.setId(View.generateViewId());
        unitText.setText(ingredientToAdd.getUnit());
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
        ImageView trash = new ImageView(mainActivity);
        trash.setImageResource(R.drawable.trashcan_light);
        trash.setId(View.generateViewId());

        ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                50,
                50
        );
        trash.setLayoutParams(trashParams);
        layout.addView(trash);

        Log.d("inhalt", "" + ingredientToAdd.getName() + " " + amount + " " + ingredientToAdd.getUnit());

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
    public void addIngredientToShoppingBag(){

        IngredientDTO ingredientToAdd = db.getIngredientByNameAndUnit(choosedIngredient, choosedUnit);

        EditText amount = popupView.findViewById(R.id.amount);

        LinearLayout parentLayout = mainActivity.findViewById(R.id.ingredientWithoutRecipe);
        ConstraintLayout layout = new ConstraintLayout(mainActivity);

        List<IngredientAmountDTO> list = db.getIngredientsForRecipe(-9);
        long i = 0;
        if(list.isEmpty()) {

            TextView titleOther = new TextView(mainActivity);
            titleOther.setId(View.generateViewId());
            titleOther.setText("Andere Zutaten");
            parentLayout.addView(titleOther);

            View line = new View(mainActivity);
            line.setId(View.generateViewId());
            line.setBackgroundColor(Color.parseColor("#844D29"));
            ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
            );
            line.setLayoutParams(viewParams);
            Log.d("abc", "vorView");
            parentLayout.addView(line);
            Log.d("abc", "nachView");

            i = -9;
        }

        // Zutat in die Einkaufsliste hinzufügen
        long ingredientId = db.insertIngredientQuantity(-9, ingredientToAdd.getId(), Double.parseDouble(amount.getText().toString()),1, 0);

        Log.d("abc", "nachInsert");


            /*
            Zutaten in der view hinzufügen
             */

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setPadding(20,20,20,20);
        layout.setLayoutParams(layoutParams);
        layoutParams.setMargins(40, 0, 40, 0);
        Log.d("abc", "nachLayout");
            /*
            Zutat
             */
        TextView ingredientText = new TextView(mainActivity);
        ingredientText.setId(View.generateViewId());
        ingredientText.setText(ingredientToAdd.getName());
        ingredientText.setGravity(Gravity.CENTER);
        ingredientText.setTextColor(Color.parseColor("#844D29"));

        ViewGroup.LayoutParams ingredientParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ingredientText.setLayoutParams(ingredientParams);

        layout.addView(ingredientText);
        Log.d("abc", "nachText");

            /*
            Menge
             */
        TextView amountText = new TextView(mainActivity);
        amountText.setId(View.generateViewId());
        amountText.setText(amount.getText().toString());
        amountText.setGravity(Gravity.CENTER);
        amountText.setTextColor(Color.parseColor("#844D29"));

        ViewGroup.LayoutParams amountParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        amountText.setLayoutParams(amountParams);

        layout.addView(amountText);
        Log.d("abc", "nachAmount");

            /*
            Einheit
             */
        TextView unitText = new TextView(mainActivity);
        unitText.setId(View.generateViewId());
        unitText.setText(ingredientToAdd.getUnit());
        unitText.setGravity(Gravity.CENTER);
        unitText.setTextColor(Color.parseColor("#844D29"));

        ViewGroup.LayoutParams unitParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        unitText.setLayoutParams(unitParams);

        layout.addView(unitText);
        Log.d("abc", "nachUnit");

            /*
            Checkbox
            */
        CheckBox checkBox = new CheckBox(mainActivity);
        checkBox.setId(View.generateViewId());

        ViewGroup.LayoutParams checkBoxParams = new ViewGroup.LayoutParams(
                50,
                50
        );
        checkBox.setLayoutParams(checkBoxParams);
        layout.addView(checkBox);
        Log.d("abc", "nachBox");

            /*
            Mülleimer
             */
        ImageView trash = new ImageView(mainActivity);
        trash.setImageResource(R.drawable.trashcan_dark);
        trash.setId(View.generateViewId());

        ViewGroup.LayoutParams trashParams = new ViewGroup.LayoutParams(
                50,
                50
        );
        trash.setLayoutParams(trashParams);
        layout.addView(trash);
        Log.d("abc", "nachMüll");

        Log.d("inhalt", "" + ingredientToAdd.getName() + " " + amount + " " + ingredientToAdd.getUnit());

            /*
            Constraints
             */
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        Log.d("abc", "nachClone");

        //Zutat
        constraintSet.connect(ingredientText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(ingredientText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(ingredientText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        //Menge
        constraintSet.connect(amountText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 250);
        constraintSet.connect(amountText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(amountText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        //Einheit
        constraintSet.connect(unitText.getId(), ConstraintSet.START, amountText.getId(), ConstraintSet.END, 20);
        constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        //Checkbox
        constraintSet.connect(checkBox.getId(), ConstraintSet.END, trash.getId(), ConstraintSet.START, 20);
        constraintSet.connect(unitText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(unitText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        //Mülleimer
        constraintSet.connect(trash.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(trash.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(trash.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        constraintSet.applyTo(layout);
        Log.d("abc", "nachApply");


        parentLayout.addView(layout);

        Log.d("abc", "nachAllem");

        /*
        noch bearbeiten
         */
        trash.setOnClickListener(v ->{
            db.deleteIngredientQuantity(ingredientId);
            List<IngredientAmountDTO> li = db.getIngredientsForRecipe(-9);
            parentLayout.removeView(layout);
            if(li.isEmpty()){
                parentLayout.removeAllViews();
            }
        });

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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(ingredients);
                        Log.d("eehm", "Hat geklappt");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ConstraintLayout editView = popupView.findViewById(R.id.above);
                    editView.setVisibility(View.VISIBLE);

                    ConstraintLayout belowView = popupView.findViewById(R.id.bbelow);
                    belowView.setVisibility(View.GONE);


                    String selectedItem = getItem(position);
                    String[] separated = selectedItem.split(", ");
                    choosedIngredient = separated[0];
                    choosedUnit = separated[1];


                    EditText editName = popupView.findViewById(R.id.nameText2);
                    editName.setText(choosedIngredient);

                    EditText editUnit = popupView.findViewById(R.id.unitText2);
                    editUnit.setText(choosedUnit);

                    IngredientDTO ingredient = db.getIngredientByNameAndUnit(choosedIngredient, choosedUnit);

                    PopupEditIngredient popup = new PopupEditIngredient();

                    popup.insideEdit(popupView, mainActivity, ingredient.getId(), adapter, ingredientList);



                    return true;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedItem = getItem(position);
                    String[] separated = selectedItem.split(", ");
                    choosedIngredient = separated[0];
                    choosedUnit = separated[1];

                    unitTV = popupView.findViewById(R.id.textViewUnit);
                    unitTV.setText(choosedUnit.toString());

                    ingredients.setSelection(position);

                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(ingredients);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return view;
        }
    }

    public CustomAdapter getAdapter() {
        return adapter;
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

                EditText name = (EditText) parentView.findViewById(R.id.nameText2);
                EditText unit = (EditText) parentView.findViewById(R.id.unitText2);

                db.updateIngredient(id, name.getText().toString(), unit.getText().toString());

                parentView.findViewById(R.id.above).setVisibility(View.GONE);
                parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

                ingredientList.clear();

                List<IngredientDTO> allIngredients = db.getAllIngredients();
                for(IngredientDTO newIngredient : allIngredients){
                    ingredientList.add(newIngredient.getName() + ", " + newIngredient.getUnit());
                }

                ada.notifyDataSetChanged();

                //String selectedItem = ingredientList.get(0);
                //String[] separated = selectedItem.split(", ");
                choosedIngredient = name.getText().toString();
                choosedUnit = unit.getText().toString();

                unitTV = popupView.findViewById(R.id.textViewUnit);
                unitTV.setText(choosedUnit);

                int pos = 0;

                for(int i = 0; i < ingredientList.size(); i++){
                    if(ingredientList.get(i).equals(choosedIngredient + ", " + choosedUnit)){
                        pos = i;
                        break;
                    }
                    Log.d("void", ingredientList.get(i) + "____" +  choosedIngredient + ", " + choosedUnit);
                }
                ingredients.setSelection(pos);


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
                choosedIngredient = separated[0];
                choosedUnit = separated[1];

                unitTV = popupView.findViewById(R.id.textViewUnit);
                unitTV.setText(choosedUnit.toString());

                ingredients.setSelection(0);

            }
        }
    }

}
