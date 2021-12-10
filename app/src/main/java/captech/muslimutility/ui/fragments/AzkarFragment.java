package captech.muslimutility.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.R;
import captech.muslimutility.adapter.AzkarAdapter;
import captech.muslimutility.model.ZekerType;

public class AzkarFragment extends Fragment {
    private RecyclerView azkarRecyclerView;
    private AzkarAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    private View rootview;
    DBManager dbManager;
    public static List<ZekerType> zekerTypeList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_azkar, container, false);

        dbManager = new DBManager(getActivity());
        dbManager.open();
        try {
            dbManager.copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            zekerTypeList = dbManager.getAllAzkarTypes();
            azkarRecyclerView = (RecyclerView) rootview.findViewById(R.id.AzkarList);
            mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
            azkarRecyclerView.setLayoutManager(mLayoutManager);
            adapter = new AzkarAdapter(dbManager.getAllAzkarTypes(), getActivity());
            azkarRecyclerView.setAdapter(adapter);

            if (adapter != null) {
                Log.i("ADAPTER_COUNT", "Size : " + adapter.getItemCount());
            }
        } catch (Exception e) {

        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            zekerTypeList = dbManager.getAllAzkarTypes();
            azkarRecyclerView = (RecyclerView) rootview.findViewById(R.id.AzkarList);
            mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
            azkarRecyclerView.setLayoutManager(mLayoutManager);
            adapter = new AzkarAdapter(dbManager.getAllAzkarTypes(), getActivity());
            azkarRecyclerView.setAdapter(adapter);

            if (adapter != null) {
                Log.i("ADAPTER_COUNT", "Size : " + adapter.getItemCount());
            }
        } catch (Exception e) {

        }
    }


}
