package com.example.eaeprojekt.popups;


import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.NewRecipeActivity;
import com.example.eaeprojekt.database.DatabaseManager;

public class PopupEditIngredient implements View.OnClickListener {


    DatabaseManager db;
    View parentView;
    ConstraintLayout buttonEdit;
    ConstraintLayout buttonBack;
    ConstraintLayout buttonDelete;
    FrameLayout frame;
    Activity mainActivity;

    long id;


    public void insideEdit(final View view, Activity activity, long ingredientId) {

        id = ingredientId;

        parentView = view;

        mainActivity = activity;


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



        } else if (viewClick == buttonDelete) {
            db = new DatabaseManager(mainActivity);
            db.open();

            db.deleteIngredient(id);

            parentView.findViewById(R.id.above).setVisibility(View.GONE);
            parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

        }
    }
}
