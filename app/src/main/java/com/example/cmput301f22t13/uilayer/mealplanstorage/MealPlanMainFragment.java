package com.example.cmput301f22t13.uilayer.mealplanstorage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cmput301f22t13.R;
import com.example.cmput301f22t13.databinding.FragmentMealPlanMainBinding;
import com.example.cmput301f22t13.datalayer.MealPlanDL;
import com.example.cmput301f22t13.domainlayer.item.IngredientItem;
import com.example.cmput301f22t13.domainlayer.item.Item;
import com.example.cmput301f22t13.domainlayer.item.MealPlan;
import com.example.cmput301f22t13.uilayer.userlogin.Login;
import com.example.cmput301f22t13.uilayer.userlogin.ResultListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * {@link Fragment} for listing all of the meal plans. User can add and delete a meal plan as well
 * as click on one to view more information about it.
 *
 * @author Logan Thimer
 */
public class MealPlanMainFragment extends Fragment {

    private FragmentMealPlanMainBinding binding;

    private ArrayAdapter<MealPlan> mealPlanAdapter;
    private ArrayList<MealPlan> mealPlans;
    private MealPlanDL mealPlanDL = MealPlanDL.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealPlans = mealPlanDL.getStorage();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMealPlanMainBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mealPlanAdapter = new MealPlanArrayAdapter(getActivity(), mealPlans);
        binding.mealPlanListView.setAdapter(mealPlanAdapter);
        binding.mealPlanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MealPlan item = (MealPlan) adapterView.getItemAtPosition(i);

                Bundle bundle = new Bundle();
                bundle.putSerializable(MealPlanViewFragment.ARG_MEAL_PLAN_ITEM, item);
                NavHostFragment.findNavController(MealPlanMainFragment.this)
                        .navigate(R.id.action_MealPlanMainFragment_to_mealPlanViewFragment, bundle);
            }
        });

        mealPlanDL.listener = new ResultListener() {
            @Override
            public void onSuccess() {
                mealPlanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        };

        /**
         * Idea and implementation for the material date picker was adapted from a web reference
         * Name: Material Design Date Picker in Android
         * Publisher: https://auth.geeksforgeeks.org/user/adityamshidlyali/articles
         * URL: https://www.geeksforgeeks.org/material-design-date-picker-in-android/
         * License: https://www.geeksforgeeks.org/copyright-information/
         */
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        binding.addMealPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair<Long, Long> dates = (Pair<Long, Long>) materialDatePicker.getSelection();

                GregorianCalendar start = new GregorianCalendar();
                start.setTimeInMillis(dates.first);
                start.add(Calendar.DATE, 1); // for some reason the date always comes offset by 1

                GregorianCalendar end = new GregorianCalendar();
                end.setTimeInMillis(dates.second);
                end.add(Calendar.DATE, 1); // for some reason the date always comes offset by 1

//                mealPlans.add(new MealPlan(start, end));
                mealPlanDL.firebaseAddEdit(new MealPlan(start, end));
                mealPlanAdapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}