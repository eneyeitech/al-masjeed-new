package captech.muslimutility.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import captech.muslimutility.R;
import captech.muslimutility.SharedData.SharedDataClass;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.Event;
import captech.muslimutility.ui.activity.PrayShowActivity;
import captech.muslimutility.utility.Dates;
import captech.muslimutility.utility.NumbersLocal;

public class IslamicEventAdapter extends RecyclerView.Adapter<IslamicEventAdapter.ViewHolder> {
    private List<Event> eventList;
    private List<Event> urdueventList;
    private Context context;
    private InterstitialAd mInterstitialAd;

    public IslamicEventAdapter(Context context, List<Event> eventList, List<Event> urdueventList) {
        this.context = context;
        this.eventList = eventList;
        this.urdueventList = urdueventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventscustomdesign, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        final Event eventurdu = urdueventList.get(position);

        String[] date = event.hejriDate.split("/");
        HGDate hgd = new HGDate();
        hgd.setHigri(Integer.valueOf(date[2]), Integer.valueOf(date[1]), Integer.valueOf(date[0]));

        MobileAds.initialize(context, "ca-app-pub-5379314308386326~5125464320");
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-5379314308386326/9778901499");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        holder.eventName.setText(event.eventName.trim().equals(context.getString(R.string.milad_al_naby)) ? event.eventName + " " + "ﷺ" : event.eventName);
        holder.textViewurdu.setText(eventurdu.eventName.trim().equals(context.getString(R.string.milad_al_naby)) ? eventurdu.eventName + " " + "ﷺ" : eventurdu.eventName);
        //set event icon in right or left according to language
        if (ConfigPreferences.getApplicationLanguage(context).equals("en"))
            holder.eventimg.setImageResource(event.icon);
        else
            holder.eventimg.setImageResource(event.icon);

        //Show dates
        holder.hejriDate.setText(NumbersLocal.convertNumberType(context, date[0]) + " " +
                Dates.islamicMonthName(context, Integer.valueOf(date[1]) - 1) + " " +
                NumbersLocal.convertNumberType(context, date[2]));
        hgd.toGregorian();
        holder.meladyDate.setText(NumbersLocal.convertNumberType(context, hgd.getDay() + "") +
                " " + Dates.gregorianMonthName(context, hgd.getMonth() - 1) + " " +
                NumbersLocal.convertNumberType(context, hgd.getYear() + ""));

        //make layout is clickable to fo to show event prayer
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedDataClass.interstitialflagtwo==0){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                    SharedDataClass.interstitialflagtwo=1;
                }
                context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", event.hejriDate));
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, hejriDate, meladyDate, textViewurdu;
        ImageView eventimg;

        public ViewHolder(View itemView) {
            super(itemView);
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNovaReg.ttf");
            eventName = (TextView) itemView.findViewById(R.id.textView15);
            eventName.setTypeface(tf);
            hejriDate = (TextView) itemView.findViewById(R.id.hejri);
            meladyDate = (TextView) itemView.findViewById(R.id.melady);
            eventimg = (ImageView) itemView.findViewById(R.id.eventimg);
            textViewurdu = (TextView) itemView.findViewById(R.id.textViewurdu);
            Typeface utf = Typeface.createFromAsset(context.getAssets(), "fonts/simple.otf");
            textViewurdu.setTypeface(utf);
        }
    }

}
