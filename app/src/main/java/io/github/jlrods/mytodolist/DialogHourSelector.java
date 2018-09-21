package io.github.jlrods.mytodolist;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by rodjose1 on 06/09/2018.
 */

public class DialogHourSelector extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener listener;
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener escuchador){
        this.listener = escuchador;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        Bundle args = this.getArguments();
        if (args != null) {
            long date = args.getLong("hour");
            calendar.setTimeInMillis(date);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), listener, hour,
                minute, DateFormat.is24HourFormat(getActivity()));
    }
}