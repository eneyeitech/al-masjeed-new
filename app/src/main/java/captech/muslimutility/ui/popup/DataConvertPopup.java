package captech.muslimutility.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.ui.activity.PrayShowActivity;
import captech.muslimutility.utility.Dates;
import captech.muslimutility.utility.NumbersLocal;

public class DataConvertPopup {
    private Context context;
    private TextView newDate;
    private Button convert, prayershow;
    private String hDay, hMonth, hyear;
    private NumberPicker dayShow, monthShow, yearShow;
    private RadioGroup convertType;
    private String[] Gregorian, Hijri;
    private boolean convertTypeFlag;

    public DataConvertPopup(Context context) {
        this.context = context;
        init();
    }

    private void init() {

        Gregorian = new String[]{context.getString(R.string.month1g), context.getString(R.string.month2g)
                , context.getString(R.string.month3g), context.getString(R.string.month4g)
                , context.getString(R.string.month5g), context.getString(R.string.month6g)
                , context.getString(R.string.month7g), context.getString(R.string.month8g)
                , context.getString(R.string.month9g), context.getString(R.string.month10g)
                , context.getString(R.string.month11g), context.getString(R.string.month12g)};


        Hijri = new String[]{context.getString(R.string.month1), context.getString(R.string.month2)
                , context.getString(R.string.month3), context.getString(R.string.month4)
                , context.getString(R.string.month5), context.getString(R.string.month6)
                , context.getString(R.string.month7), context.getString(R.string.month8)
                , context.getString(R.string.month9), context.getString(R.string.month10)
                , context.getString(R.string.month11), context.getString(R.string.month12)};

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_convert);

        newDate = (TextView) dialog.findViewById(R.id.textView26);
        monthShow = (NumberPicker) dialog.findViewById(R.id.numberPicker1);
        dayShow = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        yearShow = (NumberPicker) dialog.findViewById(R.id.numberPicker2);
        convertType = (RadioGroup) dialog.findViewById(R.id.convertType);

        datePickerShow(true);

        convertType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.hijri:
                        datePickerShow(true);
                        break;
                    case R.id.gregorian:
                        datePickerShow(false);
                        break;
                }

            }
        });


        convert = (Button) dialog.findViewById(R.id.button);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertDate();
            }
        });

        prayershow = (Button) dialog.findViewById(R.id.prayershow);
        prayershow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!convertTypeFlag) {
                    convertDate();
                    context.startActivity(new Intent(context, PrayShowActivity.class)
                            .putExtra("date", hDay + "-" + hMonth + "-" + hyear));
                } else {
                    context.startActivity(new Intent(context, PrayShowActivity.class)
                            .putExtra("date", dayShow.getValue() + "-" + (monthShow.getValue() + 1) + "-" + yearShow.getValue()));
                }
                dialog.cancel();

            }
        });

        dialog.show();

    }

    public void convertDate() {

        try {
            if (!convertTypeFlag) {
                HGDate hgDate = new HGDate();
                hgDate.setGregorian(yearShow.getValue(), monthShow.getValue() + 1, dayShow.getValue());
                hgDate.toHigri();
                hDay = hgDate.getDay() + "";
                hMonth = hgDate.getMonth() + "";
                hyear = hgDate.getYear() + "";
                newDate.setText(NumbersLocal.convertNumberType(context,
                        NumbersLocal.convertNumberType(context, hgDate.getDay() + "")
                                + " " + Dates.islamicMonthName(context, hgDate.getMonth() - 1) + " "
                                + NumbersLocal.convertNumberType(context, hgDate.getYear() + "")));
            } else {
                HGDate hgDate = new HGDate();
                hgDate.setHigri(yearShow.getValue(), monthShow.getValue() + 1, dayShow.getValue());
                hgDate.toGregorian();
                newDate.setText(NumbersLocal.convertNumberType(context, String.valueOf(hgDate.getDay()))
                        + " " + Dates.gregorianMonthName(context, hgDate.getMonth() - 1) + " "
                        + NumbersLocal.convertNumberType(context, hgDate.getYear() + ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            newDate.setText(context.getString(R.string.invalid_date));
        }

    }

    public void datePickerShow(boolean islamicView) {
        if (islamicView) {
            monthShow.setDisplayedValues(Hijri);
            monthShow.setMinValue(0);
            monthShow.setMaxValue(11);
            dayShow.setMaxValue(30);
            dayShow.setMinValue(1);
            yearShow.setMaxValue(3000);
            yearShow.setMinValue(0);
            HGDate hgDate = new HGDate();
            hgDate.toHigri();
            yearShow.setValue(hgDate.getYear());
            dayShow.setValue(hgDate.getDay());
            monthShow.setValue(hgDate.getMonth() - 1);
            convertTypeFlag = true;
        } else {
            //Load georgian months
            monthShow.setDisplayedValues(Gregorian);
            monthShow.setMinValue(0);
            monthShow.setMaxValue(11);
            dayShow.setMaxValue(31);
            dayShow.setMinValue(1);
            yearShow.setMaxValue(3000);
            yearShow.setMinValue(0);
            HGDate hgDate = new HGDate();
            yearShow.setValue(hgDate.getYear());
            dayShow.setValue(hgDate.getDay());
            monthShow.setValue(hgDate.getMonth() - 1);
            convertTypeFlag = false;
        }
    }


}
