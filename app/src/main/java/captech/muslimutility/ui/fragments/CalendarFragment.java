package captech.muslimutility.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.R;
import captech.muslimutility.adapter.CalenderAdapter;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.model.CalendarCell;
import captech.muslimutility.ui.activity.PrayShowActivity;
import captech.muslimutility.utility.Dates;
import captech.muslimutility.utility.NumbersLocal;

public class CalendarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView month, otherMonth, dayMonth, dayWeekName, calendarType, calendarYear;
    private ImageView left, right, calendarBack;
    private List<CalendarCell> monthList;
    private GridView calender;
    private CalenderAdapter adapter;
    private boolean spaceFlag = true;
    public static int space;
    public int mainMonth, mainYear;
    private String otherMonth_a, otherMonth_b;
    private boolean flagCalendar = true;
    Typeface Roboto_Bold, Roboto_Light, Roboto_Reg, Roboto_Thin, ProximaNovaReg, ProximaNovaBold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarBack = (ImageView) rootView.findViewById(R.id.calendar_image);
        month = (TextView) rootView.findViewById(R.id.curr_month_txt);
        otherMonth = (TextView) rootView.findViewById(R.id.curr_month_txt_other);
        left = (ImageView) rootView.findViewById(R.id.curr_month_l);
        right = (ImageView) rootView.findViewById(R.id.curr_month_r);
        dayMonth = (TextView) rootView.findViewById(R.id.textView7);
        dayWeekName = (TextView) rootView.findViewById(R.id.textView8);
        calendarType = (TextView) rootView.findViewById(R.id.textView23);
        calendarYear = (TextView) rootView.findViewById(R.id.textView24);
        right.setOnClickListener(this);
        left.setOnClickListener(this);

        ProximaNovaReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaBold.ttf");
        Roboto_Bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
        Roboto_Light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_Reg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Thin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        FontsOverride.setDefaultFont(getActivity(), "DEFAULT", "fonts/ProximaNovaReg.ttf");

        monthList = new ArrayList<>();
        adapter = new CalenderAdapter(getContext());
        calender = (GridView) rootView.findViewById(R.id.calendar_pager);
        calender.setOnItemClickListener(this);
        calender.setAdapter(adapter);

        final HGDate georgianDate = new HGDate();
        final HGDate islamicDate = new HGDate(georgianDate);
        islamicDate.toHigri();

        loadIslamicCalendar(georgianDate, islamicDate);
        rootView.findViewById(R.id.mainDates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagCalendar) {
                    loadGregorianCalendar(georgianDate, islamicDate);
                    calendarBack.setImageResource(R.drawable.mos);
                    flagCalendar = false;
                } else {
                    loadIslamicCalendar(georgianDate, islamicDate);
                    calendarBack.setImageResource(R.drawable.sliderbackground);
                    flagCalendar = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {

        if (view == right) {
            mainMonth++;
            if (mainMonth >= 13) {
                mainMonth = 1;
                mainYear++;
                calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }

            } else {
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            }
        } else if (view == left) {
            mainMonth--;
            if (mainMonth <= 1) {
                mainMonth = 12;
                mainYear--;
                calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            } else {
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CalendarCell cell = adapter.getItem(position);
        if (cell.day != -1) {
            if (flagCalendar) {
                startActivity(new Intent(getContext(), PrayShowActivity.class)
                        .putExtra("date", cell.day + "-" + cell.hijriMonth + "-" + mainYear));
            } else {
                HGDate hgDate = new HGDate();
                hgDate.setGregorian(mainYear, 1, 1);
                hgDate.toHigri();
                startActivity(new Intent(getContext(), PrayShowActivity.class)
                        .putExtra("date", cell.dayOther + "-" + cell.hijriMonth + "-" + hgDate.getYear()));
            }
        }

    }

    public void loadMonthsDaysIslamic(int month, int year) {
        spaceFlag = true;
        adapter.clear();
        monthList.clear();
        this.month.setText(Dates.islamicMonthName(getContext(), month - 1));
        HGDate hgDate = new HGDate();
        hgDate.setHigri(year, month, 1);
        adapter.enableGregorian(false);
        while (month == hgDate.getMonth()) {
            HGDate od = new HGDate(hgDate);
            od.toGregorian();
            if (spaceFlag == true) {
                space = hgDate.weekDay() + 1;
                spaceFlag = false;
            }
            //get georgian months for same islamic month
            if (hgDate.getDay() == 1)
                otherMonth_a = Dates.gregorianMonthName(getContext(), od.getMonth() - 1);
            if (hgDate.getDay() == 29)
                otherMonth_b = Dates.gregorianMonthName(getContext(), od.getMonth() - 1);

            //add day
            monthList.add(new CalendarCell(hgDate.getDay(), od.getDay()
                    , month
                    , od.getMonth()
                    , hgDate.weekDay() + 1));

            hgDate.nextDay();
        }

        //show months
        String htmlString = "<u>" + otherMonth_a + " - " + otherMonth_b + "</u>";
        otherMonth.setText(Html.fromHtml(htmlString));

        //add spacing to list
        for (int j = 0; j < space; j++) {
            monthList.add(0, new CalendarCell(-1, -1, -1, -1, -1));
        }
        adapter.addAll(monthList);
        adapter.notifyDataSetChanged();
    }

    public void loadMonthsDayGregorian(int month, int year) {
        spaceFlag = true;
        adapter.clear();
        monthList.clear();
        this.month.setText(Dates.gregorianMonthName(getContext(), month - 1));
        adapter.enableGregorian(true);
        HGDate hgDate = new HGDate();
        hgDate.setGregorian(year, month, 1);
        while (month == hgDate.getMonth()) {
            HGDate od = new HGDate(hgDate);
            od.toHigri();

            //add day of month information
            monthList.add(new CalendarCell(hgDate.getDay(),
                    od.getDay(),
                    od.getMonth(),
                    month,
                    od.weekDay() + 1));

            if (spaceFlag == true) {
                space = hgDate.weekDay() + 1;
                spaceFlag = false;
            }

            //get islamic months for same georgian month
            if (hgDate.getDay() == 1)
                otherMonth_a = Dates.islamicMonthName(getContext(), od.getMonth() - 1);
            if (hgDate.getDay() == 29)
                otherMonth_b = Dates.islamicMonthName(getContext(), od.getMonth() - 1);

            hgDate.nextDay();

        }

        //show months
        String htmlString = "<u>" + otherMonth_a + " - " + otherMonth_b + "</u>";
        otherMonth.setText(Html.fromHtml(htmlString));

        //add spacing to list
        for (int j = 0; j < space; j++) {
            monthList.add(0, new CalendarCell(-1, -1, -1, -1, -1));
        }
        adapter.addAll(monthList);
        adapter.notifyDataSetChanged();
    }

    public void loadIslamicCalendar(HGDate dtISO, HGDate dtIslamic) {
        month.setText(Dates.islamicMonthName(getContext(), dtIslamic.getMonth() - 1));
        otherMonth.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth()));
        mainMonth = dtIslamic.getMonth();
        mainYear = dtIslamic.getYear();
        calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
        loadMonthsDaysIslamic(mainMonth, mainYear);
        calendarType.setText(getString(R.string.hijri));
        dayMonth.setText(NumbersLocal.convertNumberType(getContext(), dtIslamic.getDay() + ""));
        Log.d("Week_Day", dtIslamic.weekDay() + "");
        dayWeekName.setText(Dates.getCurrentWeekDay());
    }

    public void loadGregorianCalendar(HGDate dtISO, HGDate dtIslamic) {
        month.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth() - 1));
        otherMonth.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth()));
        mainMonth = dtISO.getMonth();
        mainYear = dtISO.getYear();
        calendarYear.setText(NumbersLocal.convertNumberType(getContext(), dtISO.getYear() + ""));
        loadMonthsDayGregorian(dtISO.getMonth(), dtISO.getYear());
        calendarType.setText(getString(R.string.gregorian));
        HGDate hgDate = new HGDate();
        dayMonth.setText(NumbersLocal.convertNumberType(getContext(), hgDate.getDay() + ""));
        dayWeekName.setText(Dates.getCurrentWeekDay());
    }

}
