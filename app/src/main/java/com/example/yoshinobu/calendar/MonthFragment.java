package com.example.yoshinobu.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.yoshinobu.calendar.R.string.format_month_year;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {
    private static final String TAG = MonthFragment.class.getSimpleName();

    private static final int WEEKDAYS = 7;
    private static final int MAX_WEEK = 6;

    // 週の始まりの曜日を保持する
    private static final int BIGINNING_DAY_OF_WEEK = Calendar.SUNDAY;
    // 今日のフォント色
    private static final int TODAY_COLOR = Color.RED;
    // 通常のフォント色
    private static final int DEFAULT_COLOR = Color.DKGRAY;
    // 今週の背景色
    private static final int TODAY_BACKGROUND_COLOR = Color.LTGRAY;
    // 通常の背景色
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    private Calendar targetCalendar;

    private ArrayList<LinearLayout> mDays = new ArrayList<LinearLayout>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int year;
    private int month;

    private OnFragmentInteractionListener mListener;

    public MonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthFragment newInstance(int param1, int param2) {
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getInt(ARG_PARAM1);
            month = getArguments().getInt(ARG_PARAM2);
        }
        Calendar todayCalendar = Calendar.getInstance();
        if (year == 0) {
            year = todayCalendar.get(Calendar.YEAR);
            month = todayCalendar.get(Calendar.MONTH);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_month, container, false);
        Button frontButton = (Button) rootView.findViewById(R.id.front);
        frontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.CALENDAR_ID, 2);
                    values.put(CalendarContract.Events.TITLE, "テストイベント2");
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                    values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis() + 1000 * 60 * 60);
                    values.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 1000 * 60 * 60 * 2);
                    Uri uri = resolver.insert(CalendarContract.Events.CONTENT_URI, values);
                    Long eventID = Long.parseLong(uri.getLastPathSegment());
                    Log.i(TAG, "eventID:" + eventID);
                }
            }
        });
        LinearLayout dayLL = (LinearLayout) rootView.findViewById(R.id.day);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, (int) getResources().getDimension(R.dimen.day_eight), 1);
        params.setMargins(0, 0, 2, 2);

        targetCalendar = getTargetCalendar(year, month);
        int skipCount = getSkipCount(targetCalendar);
        int lastDay = targetCalendar.getActualMaximum(Calendar.DATE);
        int dayCounter = 1;

        TextView mTitleView = (TextView) rootView.findViewById(R.id.year_month);
        String formatString = mTitleView.getContext().getString(R.string.format_month_year);
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        mTitleView.setText(formatter.format(targetCalendar.getTime()));

        Calendar todayCalendar = Calendar.getInstance();
        int todayYear = todayCalendar.get(Calendar.YEAR);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH);

        Log.i(TAG, year + "/" + month);
        for (int i = 0; i < MAX_WEEK; i++) {
            LinearLayout weekLine = new LinearLayout(getActivity());
            weekLine.setOrientation(LinearLayout.HORIZONTAL);
            //weekRow.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            // 1週間分の日付ビュー作成
            int weekCounter = 0;
            for (int j = 0; j < WEEKDAYS; j++) {
                LinearLayout dayLine = new LinearLayout(getActivity());
                dayLine.setOrientation(LinearLayout.VERTICAL);

                TextView dayView = new TextView(getActivity());
                dayView.setText(String.valueOf(j));

                if (i == 0 && skipCount > 0) {
                    // 第一週かつskipCountが残っていれば
                    dayView.setText(" ");
                    skipCount--;
                } else if (lastDay < dayCounter) {
                    // 最終日より大きければ
                    dayView.setText(" ");
                } else {
                    mDays.add(dayLine);
                    // 日付を設定
                    dayView.setText(String.valueOf(dayCounter));

                    boolean isToday = todayYear == year &&
                            todayMonth == month &&
                            todayDay == dayCounter;

                    if (isToday) {
                        dayView.setTextColor(TODAY_COLOR); // 赤文字
                        dayView.setTypeface(null, Typeface.BOLD); // 太字
                        dayLine.setBackgroundColor(TODAY_BACKGROUND_COLOR); // 週の背景グレー
                    } else {
                        dayView.setTextColor(DEFAULT_COLOR);
                        dayView.setTypeface(null, Typeface.NORMAL);
                    }
                    dayCounter++;
                    weekCounter++;
                }
                dayLine.addView(dayView);
                dayLine.setBackgroundColor(Color.WHITE);
                dayLine.setLayoutParams(params);

                weekLine.addView(dayLine);

            }
            if (weekCounter > 0) {

                dayLL.addView(weekLine, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        setEvent();
        return rootView;
    }

    private void setEvent() {
        LinearLayout.LayoutParams mDayLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mDayLayout.setMargins(2, 2, 2, 2);

        Log.i(TAG, "setEvent----------------------");

        try {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            int tempMonth = month + 1;
            Date startDate = df.parse(year+"/"+tempMonth+"/1");
            Date endDate = df.parse(year+"/"+tempMonth+"/"+targetCalendar.getActualMaximum(Calendar.DATE));
            long start = startDate.getTime();
            long end = endDate.getTime();
            Globals globals = (Globals) getActivity().getApplication();
            ArrayList<CalendarEvent> calEvents = globals.getCalendarEvent(start,end);

            for (CalendarEvent calEvent : calEvents) {
                SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.JAPAN);
                while(calEvent.startSec <= calEvent.endSec) {
                    int day = Integer.parseInt(dayFormat.format(calEvent.startSec)) - 1;
                    LinearLayout dayLine = mDays.get(day);
                    TextView scheduleView = new TextView(getActivity());
                    scheduleView.setSingleLine(true);
                    scheduleView.setText(calEvent.title);
                    scheduleView.setBackgroundColor(calEvent.calendarColor);
                    scheduleView.setLayoutParams(mDayLayout);
                    dayLine.addView(scheduleView);
                    calEvent.startSec += 86400000;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private int getSkipCount(Calendar targetCalendar) {
        int skipCount; // 空白の個数
        int firstDayOfWeekOfMonth = targetCalendar.get(Calendar.DAY_OF_WEEK); // 1日の曜日
        if (BIGINNING_DAY_OF_WEEK > firstDayOfWeekOfMonth) {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK + WEEKDAYS;
        } else {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK;
        }
        return skipCount;
    }

    private Calendar getTargetCalendar(int year, int month) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.clear(); // カレンダー情報の初期化
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return targetCalendar;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
