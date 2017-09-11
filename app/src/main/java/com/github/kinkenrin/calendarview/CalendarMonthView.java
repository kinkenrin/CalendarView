package com.github.kinkenrin.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by jinxl on 2017/8/25.
 */

public class CalendarMonthView extends ViewGroup implements View.OnClickListener {
    private CalendarMonthView.Listener listener = null;
    private Locale locale;
    private TimeZone timeZone;
    private DateFormat monthNameFormat;
    private boolean isHasHeader = false;
    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    protected List<Integer> mLineWidth = new ArrayList<Integer>();
    private List<View> lineViews = new ArrayList<>();
    private int oneDp = 1;
    private int mCellWidth = 38 * oneDp;
    private MonthDescriptor mMonth;

    public CalendarMonthView(Context context) {
        this(context, null);
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
//        mGravity = ta.getInt(R.styleable.TagFlowLayout_gravity,LEFT);
//        ta.recycle();
        oneDp = (int) DensityUtil.dip2px(getContext(), 1);
        timeZone = TimeZone.getDefault();
        locale = Locale.getDefault();
        monthNameFormat = new SimpleDateFormat("M", locale);
        monthNameFormat.setTimeZone(timeZone);
    }

    public static CalendarMonthView create(Context context, ViewGroup parent, LayoutInflater inflater, int dayBackgroundResId) {
        final CalendarMonthView view = (CalendarMonthView) inflater.inflate(R.layout.layout_month, parent, false);
        view.addTitleView(getMonthTitleView(context));

        for (int i = 0; i < 42; i++) {
            CalendarCellView cellView = getDayView(context);
            setUpCellView(context, cellView, dayBackgroundResId);
            view.addView(cellView);
        }
        return view;
    }

    private static void setUpCellView(Context context, CalendarCellView cellView, int resId) {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_day_cell, null);
        if (resId != 0) {
            cellView.setBackgroundResource(resId);
        }
        cellView.addView(layout);
        cellView.setDayOfMonthTextView((TextView) layout.findViewById(R.id.day_view));
    }

    private static View getMonthTitleView(Context context) {

        return View.inflate(context, R.layout.layout_month_title, null);
    }

    private static CalendarCellView getDayView(Context context) {
        return new CalendarCellView(context);
    }

    public void bindData(MonthDescriptor month, List<List<MonthCellDescriptor>> cells, Listener listener) {
        this.mMonth = month;
        this.listener = listener;

        View titleContainer = getChildAt(0);
        TextView titleView = (TextView) titleContainer.findViewById(R.id.month_title);
        titleView.setText(month.getLabel());

        final int numRows = cells.size();
        for (int i = 0; i < 6; i++) {
            if (i < numRows) {
                List<MonthCellDescriptor> week = cells.get(i);
                for (int j = 0; j < week.size(); j++) {
                    MonthCellDescriptor cell = week.get(j);
                    CalendarCellView cellView = (CalendarCellView) getChildAt(i * week.size() + j + 1);
                    if (cell.isCurrentMonth()) {
                        cellView.setOnClickListener(this);
                        cellView.setVisibility(View.VISIBLE);
                        cellView.getDayOfMonthTextView().setText("" + cell.getValue());

                        if (!cellView.getDayOfMonthTextView().getText().equals(cell.getValue() + "")) {
                            cellView.getDayOfMonthTextView().setText(cell.getValue() + "");
                        }
                        cellView.setEnabled(cell.isSelectable());
                        cellView.setClickable(true);


                        cellView.setSelectable(cell.isSelectable());
                        cellView.setSelected(cell.isSelected());
//                        cellView.setCurrentMonth(cell.isCurrentMonth());
                        cellView.setToday(cell.isToday());
                        cellView.setRangeState(cell.getRangeState());
                        cellView.setHighlighted(cell.isHighlighted());
                        if (cell.isSelectable()) {
                            cellView.getDayOfMonthTextView().setTextColor(Color.rgb(61, 73, 102));
                        } else {
                            cellView.getDayOfMonthTextView().setTextColor(Color.rgb(189, 195, 209));
                        }
                        cellView.setTag(cell);
                    } else {
                        cellView.setVisibility(i == 0 ? View.INVISIBLE : View.GONE);
                    }
                }
            } else {
                for (int j = 0; j < 7; j++) {
                    CalendarCellView cellView = (CalendarCellView) getChildAt(i * 7 + j + 1);
                    cellView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        //计算每个块的大小
        mCellWidth = ((sizeWidth - oneDp * 20) - (15 * oneDp * 6)) / 7;

        int cCount = getChildCount();
        int cellWidthSpec = makeMeasureSpec(mCellWidth, MeasureSpec.EXACTLY);
        int cellHeightSpec = makeMeasureSpec(mCellWidth, MeasureSpec.EXACTLY);

        int titleWidthSpec = makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY);
        int titleHeightSpec = makeMeasureSpec(oneDp * 55, MeasureSpec.EXACTLY);

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }

            if (i == 0) {
                child.measure(titleWidthSpec, titleHeightSpec);
            } else {
                child.measure(cellWidthSpec, cellHeightSpec);
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            if (i == 0) {
//                lp.bottomMargin = 8 * oneDp;
//                lp.leftMargin = 10 * oneDp;
            } else {
                lp.bottomMargin = 15 * oneDp;
                if (i % 7 == 1) {
                    //第一列
                    lp.leftMargin = 10 * oneDp;
                    lp.rightMargin = 15 * oneDp;
                } else if (i % 7 == 0) {
                    //第7列
                    lp.rightMargin = 10 * oneDp;
                } else {
                    lp.rightMargin = 15 * oneDp;
                }
            }
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(lineWidth);

                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);

        }
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);


        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            left = getPaddingLeft();

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            top += lineHeight;
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.handleClick((MonthCellDescriptor) v.getTag());
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }


    public void addTitleView(View child) {
        isHasHeader = true;
        super.addView(child);
    }

    public interface Listener {
        void handleClick(MonthCellDescriptor cell);
    }
}
