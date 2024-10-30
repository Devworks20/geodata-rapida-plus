package com.geodata.rapida.plus.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySelectedFallingHazards;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.util.List;

public class RVAdapterFallingHazards extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterFallingHazards.class.getSimpleName();

    Context context;
    List<FallingHazardsModel> fallingHazardsModelList;
    String MissionOrderID, SeismicityRegion;

    public RVAdapterFallingHazards(Context context, List<FallingHazardsModel> fallingHazardsModelList, String MissionOrderID, String SeismicityRegion)
    {
        this.context                 = context;
        this.fallingHazardsModelList = fallingHazardsModelList;
        this.MissionOrderID       = MissionOrderID;
        this.SeismicityRegion     = SeismicityRegion;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_falling_hazards, parent, false);

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
        return fallingHazardsModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        CheckBox cbo_desc;
        EditText edt_others;

        public MyHolder(View view)
        {
            super(view);

            cbo_desc   = itemView.findViewById(R.id.cbo_desc);
            edt_others = itemView.findViewById(R.id.edt_others);
        }

        public void bindView(final int position)
        {
            final FallingHazardsModel current = fallingHazardsModelList.get(position);

            cbo_desc.setText(current.getFallingHazardDesc());

            if (current.getFallingHazardDesc().equalsIgnoreCase("Others"))
            {
                edt_others.setVisibility(View.VISIBLE);
            }


            String UserAccountID = UserAccount.UserAccountID;

            Cursor cursor = RepositorySelectedFallingHazards.realAllData2(context, UserAccountID,
                    MissionOrderID, SeismicityRegion, current.getFallingHazardDesc());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    cbo_desc.setChecked(true);

                    current.setIsActive("1");
                    fallingHazardsModelList.set(getAdapterPosition(), current);

                    String FallingHazardDesc = cursor.getString(cursor.getColumnIndex("FallingHazardDesc"));

                    if (FallingHazardDesc.equalsIgnoreCase("others"))
                    {
                        String OthersField  = cursor.getString(cursor.getColumnIndex("OthersField"));
                        edt_others.setText(OthersField);

                        current.setOthersField(OthersField);
                        fallingHazardsModelList.set(getAdapterPosition(), current);
                    }
                }
            }

            cbo_desc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        Log.e(TAG, "SET ACTIVE 1");

                        current.setIsActive("1");
                    }
                    else
                    {
                        Log.e(TAG, "SET ACTIVE 0");

                        current.setIsActive("0");
                    }

                    fallingHazardsModelList.set(getAdapterPosition(), current);
                }
            });

            edt_others.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    current.setOthersField(s.toString());

                    fallingHazardsModelList.set(getAdapterPosition(), current);
                }
            });
        }
    }
}
