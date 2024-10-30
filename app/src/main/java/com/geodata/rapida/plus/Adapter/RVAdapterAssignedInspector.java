package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;

import java.util.List;

public class RVAdapterAssignedInspector extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterAssignedInspector.class.getSimpleName();

    Context context;
    List<AssignedInspectorsListModel> assignedInspectorsListModelList;

    public RVAdapterAssignedInspector(Context context, List<AssignedInspectorsListModel> assignedInspectorsListModelList)
    {
        this.context                         = context;
        this.assignedInspectorsListModelList = assignedInspectorsListModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_assigned_inspector, parent, false);

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
        return assignedInspectorsListModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_inspectorName, tv_team_leader;

        public MyHolder(View view)
        {
            super(view);

            tv_inspectorName = itemView.findViewById(R.id.tv_inspectorName);
            tv_team_leader   = itemView.findViewById(R.id.tv_team_leader);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(final int position)
        {
            final AssignedInspectorsListModel current = assignedInspectorsListModelList.get(position);

            tv_inspectorName.setText("  " + current.getInspector());

            if (current.getTL())
            {
                tv_team_leader.setVisibility(View.VISIBLE);
            }
        }
    }
}
