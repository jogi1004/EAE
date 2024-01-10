package com.example.eaeprojekt.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eaeprojekt.R;
import com.example.eaeprojekt.activity.ShoppingBagActivity;
import com.example.eaeprojekt.activity.ShoppingBagUpdateListener;
import com.example.eaeprojekt.database.DatabaseManager;

public class PopupDeleteShoppingBag implements View.OnClickListener {

    DatabaseManager db;
    TextView buttonDeleteAll;
    TextView buttonDeleteChecked;
    TextView buttonBack;
    ShoppingBagActivity mainActivity;
    View view;
    PopupWindow popupWindow;
    FrameLayout frame;
    private final ShoppingBagUpdateListener updateListener;
    private final Context context;

    public PopupDeleteShoppingBag(Context context, ShoppingBagUpdateListener listener) {
        this.context = context;
        this.updateListener = listener;
    }


    public void showPopupWindow(final View view, ShoppingBagActivity shoppingBagActivity) {

        mainActivity = shoppingBagActivity;
        frame = mainActivity.findViewById(R.id.FrameLayoutShoppingBag);
        frame.getForeground().setAlpha(220);

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_shopping_bag_popup, null);
        this.view = popupView;

        //length and width from the Window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        //Create window
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Buttons
        buttonDeleteAll = popupView.findViewById(R.id.deleteAllButton);
        buttonDeleteAll.setOnClickListener(this);

        buttonDeleteChecked = popupView.findViewById(R.id.deleteCheckedButton);
        buttonDeleteChecked.setOnClickListener(this);

        buttonBack = popupView.findViewById(R.id.cancel_button);
        buttonBack.setOnClickListener(this);
        }

    @Override
    public void onClick(View viewClick) {


        if (viewClick == buttonBack) {

            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();

        } else if (viewClick == buttonDeleteChecked) {
            //datenbankzugriff
            db = new DatabaseManager(context);
            db.open();
            int rowCount = db.removeCheckedRecipesFromShoppingBag();
            db.close();
            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();
            if (updateListener != null) {
                updateListener.onUpdateShoppingBag();
            }

            Toast toast = new Toast(context);
            if (rowCount > 1) {
                toast.setText(rowCount + " " + mainActivity.getString(R.string.XEntriesRemoved));
                toast.show();
            } else if (rowCount == 1) {
                toast.setText(rowCount + mainActivity.getString(R.string.entryRemoved));
            }
            else {
                toast.setText(R.string.noEntriesRemoved);
                toast.show();
            }
        } else if (viewClick == buttonDeleteAll) {

            //datenbankzugriff
            db = new DatabaseManager(context);
            db.open();

            db.emptyShoppingList();
            db.close();
            frame.getForeground().setAlpha(0);
            popupWindow.dismiss();
            if (updateListener != null) {
                updateListener.onUpdateShoppingBag();
            }

            Toast toast = new Toast(context);
            toast.setText(R.string.allEntriesRemoved);
            toast.show();
        }
    }
}