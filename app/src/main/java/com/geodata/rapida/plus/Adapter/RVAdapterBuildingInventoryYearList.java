package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geodata.rapida.plus.Fragment.NewRVSFragment;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInventoryYearDataModel;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterBuildingInventoryYearList extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterBuildingInventoryYearList.class.getSimpleName();

    Context context;
    List<BuildingInventoryYearDataModel> buildingInventoryYearDataModelList;
    NewRVSFragment newRVSFragment;

    public RVAdapterBuildingInventoryYearList(Context context, List<BuildingInventoryYearDataModel> buildingInventoryYearDataModelList, NewRVSFragment newRVSFragment)
    {
        this.context                            = context;
        this.buildingInventoryYearDataModelList = buildingInventoryYearDataModelList;
        this.newRVSFragment                     = newRVSFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_building_edi, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return buildingInventoryYearDataModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_buildingName, tv_date_built_age,tv_location;
        LinearLayout ll_rvs_data;

        public MyHolder(View view)
        {
            super(view);

            tv_buildingName   = itemView.findViewById(R.id.tv_buildingName);
            tv_date_built_age = itemView.findViewById(R.id.tv_date_built_age);
            tv_location       = itemView.findViewById(R.id.tv_location);
            ll_rvs_data       = itemView.findViewById(R.id.ll_rvs_data);
        }

        public void bindView(final int position)
        {
            try
            {
                final BuildingInventoryYearDataModel current = buildingInventoryYearDataModelList.get(position);

                tv_buildingName.setText(current.getBuildingName());
                tv_date_built_age.setText(current.getDateFinished());
                tv_location.setText(current.getLocation());

                ll_rvs_data.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.e(TAG, "SELECTED BUILDING ID: " + current.getAssetID());

                        newRVSFragment.initFetchBuildingInformation(current.getAssetID());
                       // newRVSFragment.loadMarker(current.getLatitude(), current.getLongitude());

                   /*  Intent intent = new Intent(context, NewRVSActivity.class);
                    Gson gson = new Gson();
                    String myJson = gson.toJson(current);
                    intent.putExtra("MyJson", myJson);
                    context.startActivity(intent);*/
                    }
                });
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<BuildingInventoryYearDataModel> filteredList)
    {
        buildingInventoryYearDataModelList = filteredList;

        notifyDataSetChanged();
    }
}
