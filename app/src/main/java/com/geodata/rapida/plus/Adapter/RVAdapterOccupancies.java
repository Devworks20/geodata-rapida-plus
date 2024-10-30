package com.geodata.rapida.plus.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingOccupancyList;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySelectedOccupancy;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.util.List;

public class RVAdapterOccupancies extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterOccupancies.class.getSimpleName();

    Context context;
    List<OccupanciesModel> occupanciesModelList;
    String MissionOrderID, SeismicityRegion;

    public RVAdapterOccupancies(Context context, List<OccupanciesModel> occupanciesModelList, String MissionOrderID, String SeismicityRegion)
    {
        this.context              = context;
        this.occupanciesModelList = occupanciesModelList;
        this.MissionOrderID       = MissionOrderID;
        this.SeismicityRegion     = SeismicityRegion;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_occupancies, parent, false);

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
        return occupanciesModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        CheckBox cbo_occupancies_desc;

        public MyHolder(View view)
        {
            super(view);

            cbo_occupancies_desc = itemView.findViewById(R.id.cbo_occupancies_desc);
        }

        public void bindView(final int position)
        {
            final OccupanciesModel current = occupanciesModelList.get(position);

            String UserAccountID = UserAccount.UserAccountID;

            cbo_occupancies_desc.setText(current.getDescription());

            cbo_occupancies_desc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        current.setIsActive("1");
                    }
                    else
                    {
                        current.setIsActive("0");
                    }

                    occupanciesModelList.set(getAdapterPosition(), current);
                }
            });


            Cursor cursor1 = RepositorySelectedOccupancy.realAllData2(context,
                    UserAccountID, MissionOrderID, SeismicityRegion, String.valueOf(current.getUseOfCharacterOccupancyID()));


            if (cursor1.getCount() != 0)
            {
                if (cursor1.moveToFirst())
                {
                    cbo_occupancies_desc.setChecked(true);

                    current.setIsActive("1");
                    occupanciesModelList.set(getAdapterPosition(), current);
                }
            }
            else
            {
                Cursor cursor2 = RepositoryOnlineBuildingOccupancyList.realAllData(context, UserAccountID, MissionOrderID, current.getDescription());

                if (cursor2.getCount()!=0)
                {
                    if (cursor2.moveToFirst())
                    {
                        cbo_occupancies_desc.setChecked(true);

                        current.setIsActive("1");
                        occupanciesModelList.set(getAdapterPosition(), current);
                    }
                    cursor2.close();
                }
            }

        }
    }
}
