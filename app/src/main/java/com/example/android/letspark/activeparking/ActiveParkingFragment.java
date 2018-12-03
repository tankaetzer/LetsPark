package com.example.android.letspark.activeparking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letspark.R;

import java.util.Map;

import static android.graphics.Color.argb;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_HOUR;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_MINUTE;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_SECOND;
import static com.google.common.base.Preconditions.checkNotNull;

public class ActiveParkingFragment extends Fragment implements ActiveParkingContract.View {

    public static final int TYPE_EXPIRING = 0;
    public static final int TYPE_EXPIRED = 1;
    private View root;
    private ProgressBar progressBar;
    private TextView text_hours;
    private TextView text_minutes;
    private TextView text_seconds;
    private TextView text_period;
    private TextView text_location;
    private TextView text_car_number_plate;
    private TextView text_no_active_parking;
    private ActiveParkingContract.Presenter activeParkingPresenter;
    private View constraintLayout_active_parking;
    private Context context;

    public ActiveParkingFragment() {
        // Require empty constructor so it can be instantiated when restoring Activity's state.
    }

    public static ActiveParkingFragment newInstance() {
        return new ActiveParkingFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_active_parking, container, false);

        // Set up horizontal progressBar.
        progressBar = root.findViewById(R.id.progressBar);

        // Set up TextView
        text_hours = root.findViewById(R.id.text_hours);
        text_minutes = root.findViewById(R.id.text_minutes);
        text_seconds = root.findViewById(R.id.text_seconds);
        text_period = root.findViewById(R.id.text_period);
        text_location = root.findViewById(R.id.text_location);
        text_car_number_plate = root.findViewById(R.id.text_car_number_plate);
        text_no_active_parking = root.findViewById(R.id.text_no_active_parking);

        // Set up constraintLayout_active_parking for grouping all active parking widget.
        constraintLayout_active_parking = root.findViewById(R.id.constraintLayout_active_parking);

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_extend);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        activeParkingPresenter.checkActiveParkingExist();
    }

    @Override
    public void onStop() {
        super.onStop();
        activeParkingPresenter.stop();
    }

    @Override
    public void setPresenter(ActiveParkingContract.Presenter presenter) {
        activeParkingPresenter = checkNotNull(presenter);
    }

    @Override
    public void showCountDownTime(Map<String, Object> time) {
        long hour = (long) time.get(MAP_HOUR);
        long minute = (long) time.get(MAP_MINUTE);
        long second = (long) time.get(MAP_SECOND);
        text_hours.setText(String.valueOf(hour));
        text_minutes.setText(String.valueOf(minute));
        text_seconds.setText(String.valueOf(second));
    }

    @Override
    public void showDbErrMsg(String errMsg) {
        showMessage(errMsg);
    }

    @Override
    public void showActiveParkingDetail(String period, String location, String carNumberPlate) {
        text_period.setText(period);
        text_location.setText(location);
        text_car_number_plate.setText(carNumberPlate);
    }

    @Override
    public void showNoActiveParkingView(boolean show) {
        if (show) {
            text_no_active_parking.setVisibility(View.VISIBLE);
            constraintLayout_active_parking.setVisibility(View.GONE);
        } else {
            text_no_active_parking.setVisibility(View.GONE);
            constraintLayout_active_parking.setVisibility(View.VISIBLE);
        }
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
    public void showExpiringOrExpiredNotification(String carNumberPlate, int notificationType) {
        String CHANNEL_ID = getString(R.string.package_id);
        long[] vibrationPattern = {0, 250, 250, 250};
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this.context, ActiveParkingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this.context, 0, intent, 0);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrationPattern);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.context.
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder expiringBuilder
                = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_white)
                .setContentTitle(carNumberPlate)
                .setContentText(getString(R.string.active_parking_parking_expiring))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.active_parking_parking_expiring)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                // color primary in argb
                .setLights(argb(255, 63, 81, 181),
                        getResources().getInteger(R.integer.config_defaultNotificationLedOn),
                        getResources().getInteger(R.integer.config_defaultNotificationLedOff))
                .setVibrate(vibrationPattern)
                .setSound(uri);

        NotificationCompat.Builder expiredBuilder
                = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_white)
                .setContentTitle(carNumberPlate)
                .setContentText(getString(R.string.active_parking_parking_expired))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.active_parking_parking_expired)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                // color primary in argb
                .setLights(argb(255, 63, 81, 181),
                        getResources().getInteger(R.integer.config_defaultNotificationLedOn),
                        getResources().getInteger(R.integer.config_defaultNotificationLedOff))
                .setVibrate(vibrationPattern)
                .setSound(uri);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

        // notificationId is a unique int for each notification that you must define
        if (notificationType == TYPE_EXPIRING) {
            notificationManager.notify(0, expiringBuilder.build());
        } else if (notificationType == TYPE_EXPIRED) {
            notificationManager.notify(1, expiredBuilder.build());
        }
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
