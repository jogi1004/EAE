package com.example.eaeprojekt.popups;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.DTO.IngredientAmountDTO;
import com.example.eaeprojekt.R;
import com.example.eaeprojekt.database.DatabaseManager;

import java.util.List;

public class PopupEditIngredient implements View.OnClickListener {


    DatabaseManager db;
    View parentView;
    ConstraintLayout buttonEdit;
    ConstraintLayout buttonBack;
    ConstraintLayout buttonDelete;
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

            EditText name = parentView.findViewById(R.id.nameText2);
            EditText unit = parentView.findViewById(R.id.unitText2);

            db.updateIngredient(id, name.getText().toString(), unit.getText().toString());

            parentView.findViewById(R.id.above).setVisibility(View.GONE);
            parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);



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

            parentView.findViewById(R.id.above).setVisibility(View.GONE);
            parentView.findViewById(R.id.bbelow).setVisibility(View.VISIBLE);

        }
    }
}
