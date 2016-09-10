package com.ztech.fakecallm7;


import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTimeFragment extends Fragment {

    private EditText timeInput;

    private Calendar calendar = null;

    private Activity activity;

    public SelectTimeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_select_time, container, false);

        timeInput = (EditText)view.findViewById(R.id.scheduleTimePicker);

        activity = getActivity();

        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    calendar = Calendar.getInstance();

                    int cHour = calendar.get(Calendar.HOUR_OF_DAY);

                    int cMinute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

                            calendar.set(Calendar.MINUTE, minute);

                            calendar.set(Calendar.SECOND, 0);

                            Date scheduleTime = calendar.getTime();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);

                            timeInput.setText(dateFormat.format(scheduleTime));

                            ((IEventListener)activity).sendTime(calendar);

                        }
                    }, cHour, cMinute, false);

                    timePickerDialog.show();

                }
        });

        return view;

    }

    interface IEventListener {
        public void sendTime(Calendar calendar);
    }

}
