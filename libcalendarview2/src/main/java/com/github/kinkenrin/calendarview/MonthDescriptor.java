package com.github.kinkenrin.calendarview;

import java.util.Date;

/**
 * Created by jinxl on 2017/8/25.
 */

class MonthDescriptor {
    private final int month;
    private final int year;
    private final Date date;
    private String label;

    MonthDescriptor(int month, int year, Date date, String label) {
        this.month = month;
        this.year = year;
        this.date = date;
        this.label = label;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Date getDate() {
        return date;
    }

    public String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "MonthDescriptor{"
                + "label='"
                + label
                + '\''
                + ", month="
                + month
                + ", year="
                + year
                + '}';
    }
}
