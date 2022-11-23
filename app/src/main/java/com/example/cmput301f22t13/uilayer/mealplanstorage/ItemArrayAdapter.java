package com.example.cmput301f22t13.uilayer.mealplanstorage;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301f22t13.R;
import com.example.cmput301f22t13.domainlayer.item.IngredientItem;
import com.example.cmput301f22t13.domainlayer.item.Item;
import com.example.cmput301f22t13.domainlayer.item.RecipeItem;

import java.util.ArrayList;

public class ItemArrayAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> items;

    public ItemArrayAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_item_list, parent, false);
        }

        Item item = items.get(position);

        TextView name = view.findViewById(R.id.name_item_list_textview);
        TextView unit = view.findViewById(R.id.unit_item_list_textview);
        EditText amount = view.findViewById(R.id.amount_item_list_edittext);
        ImageView image = view.findViewById(R.id.picture_item_list_imageview);

        name.setText(item.getName());
        image.setImageURI(Uri.parse(item.getPhoto()));

        if (item instanceof IngredientItem) {
            unit.setText(((IngredientItem)item).getUnit());
            amount.setText(((IngredientItem)item).getAmount().toString());
        }
        else if (item instanceof RecipeItem) {
            amount.setText(String.valueOf(((RecipeItem)item).getServings()));
            unit.setText("serv");
        }

        // using text watcher to update the amount fields when there is a change in text
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (item instanceof IngredientItem) {
                        ((IngredientItem)item).setAmount(Integer.valueOf(charSequence.toString()));
                    }
                    else if (item instanceof RecipeItem) {
                        ((RecipeItem)item).setServings(Integer.valueOf(charSequence.toString()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}