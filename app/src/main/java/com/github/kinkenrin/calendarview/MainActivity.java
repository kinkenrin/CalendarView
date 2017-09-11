package com.github.kinkenrin.calendarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class MainActivity extends AppCompatActivity {
    CalendarPickerView calendar;
    TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_date = (TextView) findViewById(R.id.tv_date);

        final Calendar startYear = Calendar.getInstance();
        startYear.set(Calendar.YEAR, 2013);
        startYear.set(Calendar.MONTH, 7);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, 1);
        nextYear.set(HOUR_OF_DAY, 0);
        nextYear.set(MINUTE, 0);
        nextYear.set(SECOND, 0);
        nextYear.set(MILLISECOND, 0);

        final Calendar today = Calendar.getInstance();

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter() {
            @Override
            public boolean isDateSelectable(Date date) {
                if (date.compareTo(today.getTime()) < 0) {
                    return true;
                }
                return false;
            }
        });

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                tv_date.setText(calendar.getCalendarDateStr());
            }

            @Override
            public void onDateUnselected(Date date) {
                tv_date.setText(calendar.getCalendarDateStr());
            }
        });
        calendar.init(startYear.getTime(), nextYear.getTime())
                .withSelectedDate(new Date());

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Date> selectedDates = calendar.getSelectedDates();
                for (int i = 0; i < selectedDates.size(); i++) {
                    Toast.makeText(MainActivity.this, selectedDates.get(i).getTime() + "", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
