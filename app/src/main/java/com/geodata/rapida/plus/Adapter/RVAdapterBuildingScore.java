package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.ScoringBuildingsClass;
import java.util.List;

public class RVAdapterBuildingScore extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterBuildingScore.class.getSimpleName();

    Context context;

    TextView tv_finalScore1, tv_finalScore2;

    double CurrentScoreValue1, CurrentScoreValue2;

    String MissionOrderID, NoOfStories;

    List<ScoringBuildingsClass> scoringBuildingsClassList;

    LinearLayout ll_final_score_failed;

    public RVAdapterBuildingScore(Context context, String MissionOrderID,  String NoOfStories, TextView tv_finalScore1, TextView tv_finalScore2,
                                  List<ScoringBuildingsClass> scoringBuildingsClassList, LinearLayout ll_final_score_failed)
    {
        this.context                   = context;
        this.MissionOrderID            = MissionOrderID;
        this.NoOfStories               = NoOfStories;
        this.tv_finalScore1            = tv_finalScore1;
        this.tv_finalScore2            = tv_finalScore2;
        this.scoringBuildingsClassList = scoringBuildingsClassList;
        this.ll_final_score_failed     = ll_final_score_failed;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.building_score_layout, parent, false);

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
        return scoringBuildingsClassList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_modifiers, tv_scores, tv_scores2;

        public MyHolder(View view)
        {
            super(view);

            tv_modifiers  = itemView.findViewById(R.id.tv_modifiers);

            tv_scores     = itemView.findViewById(R.id.tv_scores);
            tv_scores2    = itemView.findViewById(R.id.tv_scores2);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bindView(final int position)
        {
            final ScoringBuildingsClass current = scoringBuildingsClassList.get(position);

            tv_modifiers.setText(current.getModifiers());

            tv_scores.setText(current.getBuildingScore1());

            //Set Background Color
            if (current.getIsActive1().equals("1"))
            {
                setBlueBackgroundColor(tv_scores, null);
            }

            if (!current.getBuildingScore2().equals("0"))
            {
                tv_scores2.setText(current.getBuildingScore2());

                if (current.getIsActive2().equals("1"))
                {
                    setBlueBackgroundColor(null, tv_scores2);
                }
            }
            else
            {
                tv_scores2.setVisibility(View.GONE);
            }

            tv_scores.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initSelectValidation(getAdapterPosition(), current, "Score1");
                }
            });

            tv_scores2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initSelectValidation(getAdapterPosition(), current, "Score2");
                }
            });

            initAutoComputeFinalScore(current);
        }
    }

    private void setBlueBackgroundColor(TextView textView1, TextView textView2)
    {
        if (textView1 != null)
        {
            textView1.setBackgroundResource(R.drawable.custom_background_square_blue);
            textView1.setTextColor(Color.WHITE);
        }

        if (textView2 != null)
        {
            textView2.setBackgroundResource(R.drawable.custom_background_square_blue);
            textView2.setTextColor(Color.WHITE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSelectValidation(int Position, ScoringBuildingsClass current, String Score)
    {
        try
        {
            if (Position != 0)
            {
                Log.e(TAG, "POSITION : " + Position);

                //Changing Select/Unselect Score
                if (Position == 5 || Position ==6)
                {
                    if (Score.equals("Score1"))
                    {
                        if (current.getIsActive1().equals("0"))
                        {
                            //Log.e(TAG, "SWITCH 1 - ON");

                            current.setIsActive1("1");
                        }
                        else
                        {
                            //Log.e(TAG, "SWITCH 1 - OFF");

                            current.setIsActive1("0");
                        }
                    }

                    if (Score.equals("Score2"))
                    {
                        if (current.getIsActive2().equals("0"))
                        {
                            //Log.e(TAG, "SWITCH 2 - ON");

                            current.setIsActive2("1");
                        }
                        else
                        {
                            //Log.e(TAG, "SWITCH 2 - OFF");

                            current.setIsActive2("0");
                        }
                    }
                }
                else
                {
                    if (Score.equals("Score1"))
                    {
                        if (current.getIsActive1().equals("0"))
                        {
                            //Log.e(TAG, "SWITCH 1 & SWITCH 2 - ON");

                            current.setIsActive1("1");

                            if (current.getIsActive2().equals("0"))
                            {
                                current.setIsActive2("1");
                            }
                        }
                        else
                        {
                            //Log.e(TAG, "SWITCH 1 & SWITCH 2 - OFF");

                            current.setIsActive1("0");
                            current.setIsActive2("0");
                        }
                    }

                    if (Score.equals("Score2"))
                    {
                        if (current.getIsActive2().equals("0"))
                        {
                            //Log.e(TAG, "SWITCH 2 & SWITCH 1 - ON");

                            current.setIsActive2("1");

                            if (current.getIsActive1().equals("0"))
                            {
                                current.setIsActive1("1");
                            }
                        }
                        else
                        {
                            //Log.e(TAG, "SWITCH 2 & SWITCH 1 - OFF");

                            current.setIsActive2("0");
                            current.setIsActive1("0");
                        }
                    }
                }

                //Changing Validations for Selecting/Unselecting
                if (Position == 1 || Position == 2 || Position == 5 || Position == 6)
                {
                    ScoringBuildingsClass scoringBuildingsClass1;

                    if (Position == 1)  //Mid Rise (4 to 7 stories)
                    {
                        scoringBuildingsClass1 = scoringBuildingsClassList.get(2);
                        scoringBuildingsClass1.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(2, scoringBuildingsClass1);
                    }
                    if (Position == 2) //High Rise (> 7 stories)
                    {
                        scoringBuildingsClass1 = scoringBuildingsClassList.get(1);
                        scoringBuildingsClass1.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(1, scoringBuildingsClass1);
                    }

                    if (Position == 5) //Pre-Code
                    {
                        Log.e(TAG, "Score: " + Score);

                        scoringBuildingsClass1 = scoringBuildingsClassList.get(6);

                        if (Score.equals("Score1") && current.getIsActive1().equals("1"))
                        {
                            scoringBuildingsClass1.setIsActive1("0");
                        }
                        if (Score.equals("Score2") && !current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }
                        scoringBuildingsClassList.set(6, scoringBuildingsClass1);
                    }

                    if (Position == 6) //Post-Benchmark
                    {
                        Log.e(TAG, "Score: " + Score);

                        scoringBuildingsClass1 = scoringBuildingsClassList.get(5);

                        if (Score.equals("Score1") && current.getIsActive1().equals("1"))
                        {
                            scoringBuildingsClass1.setIsActive1("0");
                        }
                        if (Score.equals("Score2") && !current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }
                        scoringBuildingsClassList.set(5, scoringBuildingsClass1);
                    }
                }
                else if (Position == 7 || Position == 8 || Position == 9)
                {
                    ScoringBuildingsClass scoringBuildingsClass1, scoringBuildingsClass2;

                    if (Position == 7) //Soil Type C
                    {
                        scoringBuildingsClass1 = scoringBuildingsClassList.get(8);
                        scoringBuildingsClass1.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(8, scoringBuildingsClass1);

                        scoringBuildingsClass2 = scoringBuildingsClassList.get(9);
                        scoringBuildingsClass2.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass2.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(9, scoringBuildingsClass2);
                    }

                    if (Position == 8)
                    {
                        scoringBuildingsClass1 = scoringBuildingsClassList.get(7);
                        scoringBuildingsClass1.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(7, scoringBuildingsClass1);

                        scoringBuildingsClass2 = scoringBuildingsClassList.get(9);
                        scoringBuildingsClass2.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass2.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(9, scoringBuildingsClass2);
                    }

                    if (Position == 9)
                    {
                        scoringBuildingsClass1 = scoringBuildingsClassList.get(7);
                        scoringBuildingsClass1.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass1.setIsActive2("0");
                        }

                        scoringBuildingsClassList.set(7, scoringBuildingsClass1);

                        scoringBuildingsClass2 = scoringBuildingsClassList.get(8);
                        scoringBuildingsClass2.setIsActive1("0");

                        if (!current.getBuildingScore2().equals("0"))
                        {
                            scoringBuildingsClass2.setIsActive2("0");
                        }
                        
                        scoringBuildingsClassList.set(8, scoringBuildingsClass2);
                    }
                }

                notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("DefaultLocale")
    private void initAutoComputeFinalScore(ScoringBuildingsClass current)
    {
        try
        {
            double iFinalScore1 = 0.0, iFinalScore2 = 0.0;

            if (!current.getIsActive1().equals("N/A") && current.getIsActive1().equals("1"))
            {
                if (current.getModifiers().equals("Basic Score"))
                {
                    CurrentScoreValue1 = Double.parseDouble(current.getBuildingScore1());
                }

                if (current.getBuildingScore1().contains("-"))
                {
                    String SelectedScoreValue = current.getBuildingScore1().replace("-", "");

                    CurrentScoreValue1 = CurrentScoreValue1 - Double.parseDouble(SelectedScoreValue);
                }
                else if (current.getBuildingScore1().contains("+"))
                {
                    String SelectedScoreValue = current.getBuildingScore1().replace("+", "");

                    CurrentScoreValue1 = CurrentScoreValue1 + Double.parseDouble(SelectedScoreValue);
                }

                tv_finalScore1.setText(String.format("%.1f", CurrentScoreValue1));

                iFinalScore1 = Double.parseDouble(tv_finalScore1.getText().toString());

                initScoreValidationDisplay(iFinalScore1, iFinalScore2);
            }

            if (!current.getIsActive2().equals("N/A") && current.getIsActive2().equals("1"))
            {
                if (current.getModifiers().equals("Basic Score"))
                {
                    CurrentScoreValue2 = Double.parseDouble(current.getBuildingScore2());
                }

                if (current.getBuildingScore2().contains("-"))
                {
                    String SelectedScoreValue = current.getBuildingScore2().replace("-", "");

                    CurrentScoreValue2 = CurrentScoreValue2 - Double.parseDouble(SelectedScoreValue);
                }
                else if (current.getBuildingScore2().contains("+"))
                {
                    String SelectedScoreValue = current.getBuildingScore2().replace("+", "");

                    CurrentScoreValue2 = CurrentScoreValue2 + Double.parseDouble(SelectedScoreValue);
                }

                tv_finalScore2.setText(String.format("%.1f", CurrentScoreValue2));

                iFinalScore2 = Double.parseDouble(tv_finalScore2.getText().toString());

                Log.e(TAG, "iFinalScore1: " + iFinalScore1 + " - " + "iFinalScore2: " + iFinalScore2);


                initScoreValidationDisplay(iFinalScore1, iFinalScore2);

            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initScoreValidationDisplay(double iFinalScore1, double iFinalScore2)
    {
        if (iFinalScore1 <= iFinalScore2)
        {
            double FinalScore;

            if (iFinalScore1 == 0.0)
            {
                FinalScore = iFinalScore2;
            }
            else
            {
                FinalScore = iFinalScore1;
            }

            if (FinalScore >= 2.0)
            {
                ll_final_score_failed.setVisibility(View.GONE);
            }
            else
            {
                ll_final_score_failed.setVisibility(View.VISIBLE);
            }
        }
        else if (iFinalScore1 >= iFinalScore2)
        {
            double FinalScore;

            if (iFinalScore2 == 0.0)
            {
                FinalScore = iFinalScore1;
            }
            else
            {
                FinalScore = iFinalScore2;
            }

            if (FinalScore >= 2.0)
            {
                ll_final_score_failed.setVisibility(View.GONE);
            }
            else
            {
                ll_final_score_failed.setVisibility(View.VISIBLE);
            }
        }

        if (iFinalScore1 >= 2.0)
        {
            tv_finalScore1.setBackground(context.getDrawable(R.drawable.custom_background_square));
        }
        else
        {
            tv_finalScore1.setBackground(context.getDrawable(R.drawable.custom_background_square_red));
        }

        if (iFinalScore2 >= 2.0)
        {
            tv_finalScore2.setBackground(context.getDrawable(R.drawable.custom_background_square));
        }
        else
        {
            tv_finalScore2.setBackground(context.getDrawable(R.drawable.custom_background_square_red));
        }
    }

}
