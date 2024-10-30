package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.rapida.plus.Activity.MissionOrderTabHostActivity;
import com.geodata.rapida.plus.SQLite.Class.MissionOrdersClass;
import com.geodata.rapida.plus.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RVAdapterMissionOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterMissionOrder.class.getSimpleName();

    Context context;
    List<MissionOrdersClass> missionOrdersModelList;

    public RVAdapterMissionOrder(Context context, List<MissionOrdersClass> missionOrdersModelList)
    {
        this.context                = context;
        this.missionOrdersModelList = missionOrdersModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission_orders_layout, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return missionOrdersModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        CardView cv_details;

        LinearLayout ll_dateReported;

        TextView tv_MissionOrderNo, tv_DateIssued, tv_BuildingName, tv_SeismicityRegion,
                 tv_ScreeningDate, tv_ScreeningType, tv_DateReported, tv_statusPending, tv_ReasonForInspector_display, tv_ReasonForInspector;

        public MyHolder(View view)
        {
            super(view);

            cv_details          = itemView.findViewById(R.id.cv_details);

            ll_dateReported     = itemView.findViewById(R.id.ll_dateReported);

            tv_MissionOrderNo   = itemView.findViewById(R.id.tv_MissionOrderNo);
            tv_DateIssued       = itemView.findViewById(R.id.tv_DateIssued);
            tv_BuildingName     = itemView.findViewById(R.id.tv_BuildingName);
            tv_SeismicityRegion = itemView.findViewById(R.id.tv_SeismicityRegion);
            tv_ScreeningDate    = itemView.findViewById(R.id.tv_ScreeningDate);
            tv_ScreeningType    = itemView.findViewById(R.id.tv_ScreeningType);
            tv_DateReported     = itemView.findViewById(R.id.tv_DateReported);
            tv_statusPending    = itemView.findViewById(R.id.tv_statusPending);
            tv_ReasonForInspector_display = itemView.findViewById(R.id.tv_ReasonForInspector_display);
            tv_ReasonForInspector  = itemView.findViewById(R.id.tv_ReasonForInspector);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bindView(final int position)
        {
            final MissionOrdersClass current = missionOrdersModelList.get(position);

            tv_MissionOrderNo.setText(current.getMissionOrderNo());

            tv_BuildingName.setText(current.getBuildingName());

            tv_SeismicityRegion.setText(current.getSeismicityRegion());


            tv_ScreeningDate.setText(current.getScreeningDate());
            tv_ScreeningType.setText(current.getScreeningType());

            tv_ReasonForInspector.setText(current.getReasonForInspector());

            //tv_ReasonForInspector_display.setVisibility(View.GONE);
            //tv_ReasonForInspector.setVisibility(View.GONE);

            if (!current.getDateReported().equals("") && current.getInspectionStatus().equalsIgnoreCase("complete"))
            {
                ll_dateReported.setVisibility(View.VISIBLE);

                if (current.getDateReported().contains("T"))
                {
                    String[] DateReported =  current.getDateReported().split("T");

                    tv_DateReported.setText(FormatDate(DateReported[0]));
                }

                tv_statusPending.setText(current.getInspectionStatus());

                tv_statusPending.setBackground(context.getDrawable(R.drawable.custom_green_background));
            }
            else
            {
                ll_dateReported.setVisibility(View.GONE);
                tv_statusPending.setText(current.getInspectionStatus());
                tv_statusPending.setBackground(context.getDrawable(R.drawable.custom_red_background));
            }

            if (current.getDateIssued().contains("T"))
            {
                String[] DateIssued =  current.getDateIssued().split("T");

                tv_DateIssued.setText(FormatDate(DateIssued[0]));
            }

            cv_details.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if (current.getSeismicityRegion() == null ||
                            current.getSeismicityRegion().equalsIgnoreCase("") ||
                            current.getSeismicityRegion().equalsIgnoreCase("null"))
                        {
                            Toast.makeText(context, "No Seismicity Region.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(context, MissionOrderTabHostActivity.class);
                            intent.putExtra("TabType","Seismic Resiliency Inspection");
                            intent.putExtra("MissionOrderID",       current.getMissionOrderID());
                            intent.putExtra("MissionOrderID",       current.getMissionOrderID());
                            intent.putExtra("MissionOrderNo",       current.getMissionOrderNo());
                            intent.putExtra("AssetID",              current.getAssetID());
                            intent.putExtra("ReasonForInspector",   current.getReasonForInspector());
                            intent.putExtra("dtAdded",               current.getDtAdded());

                            if (current.getSeismicityRegion().equalsIgnoreCase("high"))
                            {
                                intent.putExtra("SeismicityRegion", "High Seismicity");
                            }
                            else  if (current.getSeismicityRegion().equalsIgnoreCase("moderate") &&
                                      current.getSeismicityRegion().equalsIgnoreCase("medium"))
                            {
                                intent.putExtra("SeismicityRegion", "Moderate Seismicity");
                            }
                            else  if (current.getSeismicityRegion().equalsIgnoreCase("low") &&
                                      current.getSeismicityRegion().equalsIgnoreCase("very low"))
                            {
                                intent.putExtra("SeismicityRegion", "Low Seismicity");
                            }

                            else
                            {
                                intent.putExtra("SeismicityRegion", current.getSeismicityRegion());
                            }

                            ((Activity) context).startActivityForResult(intent, 100);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    private String FormatDate(String strDate)
    {
        String datetime;

        if (strDate != null)
        {
            @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") DateFormat outputFormat  = new SimpleDateFormat("MM/dd/yyyy");
            try
            {
                Date convertedDate = inputFormat.parse(strDate);
                datetime = outputFormat.format(Objects.requireNonNull(convertedDate));
            }
            catch (ParseException e)
            {
                datetime = "";
            }
        }
        else
        {
            datetime = "";
        }

        return  datetime;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterMissionOrderList(ArrayList<MissionOrdersClass> filteredList)
    {
        missionOrdersModelList = filteredList;

        notifyDataSetChanged();
    }
}
