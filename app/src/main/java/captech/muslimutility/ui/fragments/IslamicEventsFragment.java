package captech.muslimutility.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.R;
import captech.muslimutility.adapter.IslamicEventAdapter;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.model.Event;

public class IslamicEventsFragment extends Fragment {
    private RecyclerView eventRecyclerView;
    private IslamicEventAdapter adapter;
    private List<Event> eventList;
    private List<Event> urdueventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_islamic_events, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {

        HGDate hgDate = new HGDate();
        hgDate.toHigri();
        hgDate.setHigri(hgDate.getYear() , 9 , 1);

        urdueventList = new ArrayList<>();
        eventList = new ArrayList<>();
        eventList.add(new Event(" "+getString(R.string.ramdanstart), hgDate.toString() , R.drawable.ramdan));
        urdueventList.add(new Event(" "+getString(R.string.ramdanstarturdu), hgDate.toString() , R.drawable.ramdan));

        hgDate.setHigri(hgDate.getYear() , 9 , 27);
        eventList.add(new Event(" "+getString(R.string.laylt_kader), hgDate.toString() , R.drawable.ic_azkar_n));
        urdueventList.add(new Event(" "+getString(R.string.laylt_kader_urdu), hgDate.toString() , R.drawable.ic_azkar_n));

        hgDate.setHigri(hgDate.getYear() , 10 , 1);
        eventList.add(new Event(" "+getString(R.string.eid_el_feter), hgDate.toString() , R.drawable.ballon));
        urdueventList.add(new Event(" "+getString(R.string.eid_el_feter_urdu), hgDate.toString() , R.drawable.ballon));

        hgDate.setHigri(hgDate.getYear() , 12 , 9);
        eventList.add(new Event(" "+getString(R.string.wafet_el_arafa), hgDate.toString() , R.drawable.ic_kaaba));
        urdueventList.add(new Event(" "+getString(R.string.wafet_el_arafa_urdu), hgDate.toString() , R.drawable.ic_kaaba));

        hgDate.setHigri(hgDate.getYear() , 12 , 10);
        eventList.add(new Event(" "+getString(R.string.el_adha), hgDate.toString() , R.drawable.eldaha));
        urdueventList.add(new Event(" "+getString(R.string.el_adha_urdu), hgDate.toString() , R.drawable.eldaha));

        hgDate.setHigri(hgDate.getYear()+1 , 1 , 1);
        eventList.add(new Event(" "+getString(R.string.islamic_year), hgDate.toString() , R.drawable.laytkadr));
        urdueventList.add(new Event(" "+getString(R.string.islamic_year_urdu), hgDate.toString() , R.drawable.laytkadr));

        hgDate.setHigri(hgDate.getYear()+1 , 3 , 1);
        eventList.add(new Event(" "+getString(R.string.milad_al_naby), hgDate.toString() , R.drawable.mosque));
        urdueventList.add(new Event(" "+getString(R.string.milad_al_naby_urdu), hgDate.toString() , R.drawable.mosque));

        eventRecyclerView = (RecyclerView) rootView.findViewById(R.id.events);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        eventRecyclerView.setLayoutManager(mLayoutManager);
        eventRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new IslamicEventAdapter(getActivity(), eventList, urdueventList);
        eventRecyclerView.setAdapter(adapter);
    }

}
