package com.example.android.letspark.addremovecar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letspark.R;
import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddRemoveCarFragment extends Fragment implements AddRemoveCarContract.View {

    private View root;

    private TextInputEditText text_input_car_number_plate;

    private TextView text_no_vehicle_added_yet;

    private CarsAdapter carsAdapter;

    private ProgressBar progressBar;

    private AddRemoveCarContract.Presenter addRemoveCarPresenter;

    public AddRemoveCarFragment() {
        // Require empty constructor so it can be instantiated when restoring Activity's state.
    }

    public static AddRemoveCarFragment newInstance() {
        return new AddRemoveCarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carsAdapter = new CarsAdapter(new ArrayList<Car>(0));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_remove_car, container, false);

        // Set up cars view.
        ListView listView = root.findViewById(R.id.list_cars);
        listView.setAdapter(carsAdapter);

        // Set up no car view.
        text_no_vehicle_added_yet = root.findViewById(R.id.text_no_vehicle_added_yet);

        // Set up horizontal progressBar.
        progressBar = root.findViewById(R.id.progressBar);

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_car_plate_number);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCarAlertDialog();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addRemoveCarPresenter.start();
    }

    @Override
    public void setPresenter(AddRemoveCarContract.Presenter presenter) {
        addRemoveCarPresenter = checkNotNull(presenter);
    }

    @Override
    public void showCarsAfterAddingOrRemoving(List<Car> cars) {
        carsAdapter.replaceData(cars);
    }

    @Override
    public void showAddCarAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because its going in the dialog layout.
        View dialogView = inflater.inflate(R.layout.dialog_add_car_plate_number, null);

        text_input_car_number_plate = dialogView
                .findViewById(R.id.text_input_car_number_plate);

        builder.setView(dialogView)
                // Add action buttons
                .setTitle("Add Car")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String carNumberPlate = text_input_car_number_plate.getText().toString();
                        addRemoveCarPresenter.addCar(carNumberPlate, carsAdapter.cars);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        builder.show();
    }

    @Override
    public void showEmptyCarErr() {
        showMessage(getString(R.string.error_enter_car_number_plate));
    }

    @Override
    public void showRemoteDbErrMsg(String errMsg) {
        showMessage(errMsg);
    }

    @Override
    public void showSuccessfullySavedCarMsg() {
        showMessage(getString(R.string.msg_saved_car));
    }

    @Override
    public void showNoCarsView(boolean show) {
        if (show) {
            text_no_vehicle_added_yet.setVisibility(View.VISIBLE);
        } else {
            text_no_vehicle_added_yet.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCarExistErrMsg() {
        showMessage(getString(R.string.error_car_number_plate_already_linked_with_your_account));
    }

    @Override
    public void showSuccessfullyDeletedCarMsg() {
        showMessage(getString(R.string.msg_deleted_car));
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyParkingBaysUi(String carNumberPlate) {
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.EXTRA_CAR_NUMBER_PLATE, carNumberPlate);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    private class CarsAdapter extends BaseAdapter {

        private List<Car> cars;

        private CarsAdapter(List<Car> cars) {
            setList(cars);
        }

        private void replaceData(List<Car> cars) {
            this.cars.clear();
            setList(cars);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cars.size();
        }

        @Override
        public Object getItem(int position) {
            return cars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if the existing view is being reused, otherwise inflate the view
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.car_item, parent, false);
            }

            // Get the car object located at this position in the list.
            final Car currentCar = (Car) getItem(position);

            TextView text_number_plate = rowView.findViewById(R.id.text_number_plate);
            text_number_plate.setText(currentCar.getCarNumberPlate());

            ImageView image_delete = rowView.findViewById(R.id.image_delete);
            image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRemoveCarPresenter.removeCar(currentCar);
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRemoveCarPresenter.selectCar(currentCar.getCarNumberPlate());
                }
            });

            return rowView;
        }

        private void setList(List<Car> cars) {
            this.cars = checkNotNull(cars);
        }
    }
}
