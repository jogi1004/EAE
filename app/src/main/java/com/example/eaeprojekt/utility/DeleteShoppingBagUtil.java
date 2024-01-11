package com.example.eaeprojekt.utility;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.ShoppingBagActivity;
import com.example.eaeprojekt.activity.ShoppingBagUpdateListener;
import com.example.eaeprojekt.database.DatabaseManager;

public class DeleteShoppingBagUtil {

    DatabaseManager db;
    TextView buttonDeleteAll;
    TextView buttonDeleteChecked;
    TextView buttonBack;
    ShoppingBagActivity mainActivity;
    private final ShoppingBagUpdateListener updateListener;
    private final Context context;
    Dialog dialog;

    public DeleteShoppingBagUtil(Context context, ShoppingBagUpdateListener listener) {
        this.context = context;
        this.updateListener = listener;
    }


    public void showPopupWindow(ShoppingBagActivity shoppingBagActivity) {

        mainActivity = shoppingBagActivity;

        dialog = new Dialog(mainActivity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        dialog.setContentView(R.layout.delete_shopping_bag_popup);

        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        //Buttons
        buttonDeleteAll = dialog.findViewById(R.id.deleteAllButton);
        buttonDeleteAll.setOnClickListener((v)-> {
            //datenbankzugriff
            db = new DatabaseManager(context);
            db.open();
            db.emptyShoppingList();
            db.close();

            dialog.dismiss();
            if (updateListener != null) {
                updateListener.onUpdateShoppingBag();
            }

            Toast toast = new Toast(context);
            toast.setText(R.string.allEntriesRemoved);
            toast.show();
        });

        buttonDeleteChecked = dialog.findViewById(R.id.deleteCheckedButton);
        buttonDeleteChecked.setOnClickListener((v)->{

            //datenbankzugriff
            db = new DatabaseManager(context);
            db.open();
            int rowCount = db.removeCheckedRecipesFromShoppingBag();
            db.close();
            dialog.dismiss();
            if (updateListener != null) {
                updateListener.onUpdateShoppingBag();
            }

            Toast toast = new Toast(context);
            if (rowCount > 0) {
                toast.setText(rowCount + " " + mainActivity.getString(R.string.XEntriesRemoved));
                toast.show();
            } else {
                toast.setText(R.string.noEntriesRemoved);
                toast.show();
            }
        });

        buttonBack = dialog.findViewById(R.id.cancel_button);
        buttonBack.setOnClickListener((v)-> dialog.dismiss());

        dialog.show();
    }

}
