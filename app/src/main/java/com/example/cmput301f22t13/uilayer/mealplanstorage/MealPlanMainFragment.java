package com.example.cmput301f22t13.uilayer.mealplanstorage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cmput301f22t13.R;
import com.example.cmput301f22t13.databinding.FragmentMealPlanMainBinding;
import com.example.cmput301f22t13.domainlayer.item.MealPlan;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MealPlanMainFragment extends Fragment {

    private FragmentMealPlanMainBinding binding;

    private ArrayAdapter<MealPlan> mealPlanAdapter;
    private ArrayList<MealPlan> mealPlans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealPlans = new ArrayList<>();
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

        // https://www.geeksforgeeks.org/material-design-date-picker-in-android/
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
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair<Long, Long> dates = (Pair<Long, Long>) materialDatePicker.getSelection();
                GregorianCalendar start = new GregorianCalendar();
                start.setTimeInMillis(dates.first);
                GregorianCalendar end = new GregorianCalendar();
                end.setTimeInMillis(dates.second);
                mealPlans.add(new MealPlan(start, end));
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