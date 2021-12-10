package captech.muslimutility.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.model.CalendarCell;
import captech.muslimutility.model.Event;
import captech.muslimutility.utility.NumbersLocal;

public class CalenderAdapter extends ArrayAdapter<CalendarCell> {
    private Context context;
    private boolean gregorianCalendar;
    private List<Event> eventList;
    Typeface Roboto_Bold, Roboto_Light, Roboto_Reg, Roboto_Thin, ProximaNovaReg, ProximaNovaBold;

    public CalenderAdapter(Context context) {
        super(context, R.layout.calender_cell);
        this.context = context;

        //events array list
        eventList = new ArrayList<>();
        eventList.add(new Event(context.getString(R.string.ramdanstart), "1-9-1437"));
        eventList.add(new Event(context.getString(R.string.laylt_kader), "27-9-1437"));
        eventList.add(new Event(context.getString(R.string.eid_el_feter), "1-10-1437"));
        eventList.add(new Event(context.getString(R.string.wafet_el_arafa), "9-12-1437"));
        eventList.add(new Event(context.getString(R.string.el_adha), "10-12-1437"));
        eventList.add(new Event(context.getString(R.string.islamic_year), "1-1-1438"));
        eventList.add(new Event(context.getString(R.string.milad_al_naby), "1-3-1438"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        CalendarCell cell = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calender_cell, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ProximaNovaReg = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNovaBold.ttf");
        Roboto_Bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        Roboto_Light = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_Reg = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Thin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        FontsOverride.setDefaultFont(context, "DEFAULT", "fonts/ProximaNovaReg.ttf");
        
        //check if the day is space
        if (cell.day == -1) {
            viewHolder.mainDate.setVisibility(View.GONE);
            viewHolder.secondDate.setVisibility(View.GONE);
        } else {
            viewHolder.mainDate.setVisibility(View.VISIBLE);
            viewHolder.secondDate.setVisibility(View.VISIBLE);
        }

        //check if the day is event
        for (Event event : eventList) {
            String[] date = event.hejriDate.split("-");
            if ((!gregorianCalendar ? cell.day : cell.dayOther) == Integer.parseInt(date[0].trim())
                    && cell.hijriMonth == Integer.parseInt(date[1].trim())) {
                viewHolder.mainDate.setTextColor(Color.WHITE);
                viewHolder.secondDate.setTextColor(context.getResources().getColor(R.color.white));
                ((LinearLayout) viewHolder.mainDate.getParent()).setBackgroundColor(context.getResources().getColor(R.color.weatherbgcolordark));
                break;
            } else {
                viewHolder.secondDate.setTextColor(context.getResources().getColor(R.color.white));
                ((LinearLayout) viewHolder.mainDate.getParent()).setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

        //check if the day current day
        HGDate hgDate = new HGDate();
        hgDate.toHigri();
        if (cell.hijriMonth == hgDate.getMonth() && (gregorianCalendar ? cell.dayOther : cell.day)
                == hgDate.getDay()) {
            viewHolder.mainDate.setTextColor(Color.WHITE);
            viewHolder.secondDate.setTextColor(Color.WHITE);
            ((LinearLayout) viewHolder.mainDate.getParent()).setBackgroundColor(context.getResources().getColor(R.color.contrast));
        } else {
            viewHolder.secondDate.setTextColor(Color.GRAY);
        }

        //show the date of the day
        viewHolder.mainDate.setText(NumbersLocal.convertNumberType(context, cell.day + ""));
        viewHolder.secondDate.setText(NumbersLocal.convertNumberType(context, cell.dayOther + ""));

        return convertView;
    }

    class ViewHolder {
        private TextView mainDate, secondDate;

        public ViewHolder(View view) {
            mainDate = (TextView) view.findViewById(R.id.textView30);
            mainDate.setTypeface(ProximaNovaReg);
            secondDate = (TextView) view.findViewById(R.id.textView31);
            secondDate.setTypeface(ProximaNovaReg);
        }
    }

    public void enableGregorian(boolean gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
    }

}
