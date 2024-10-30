package com.geodata.rapida.plus.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.geodata.rapida.plus.Fragment.BuildingInformationFragment;
import com.geodata.rapida.plus.Fragment.DESAFragment;
import com.geodata.rapida.plus.Fragment.RESAFragment;
import com.geodata.rapida.plus.Fragment.RVSScoringFragment;
import com.geodata.rapida.plus.Fragment.MissionOrderFragment;
import com.geodata.rapida.plus.Fragment.ReportsFragment;

public class AdapterMissionOrder extends FragmentStatePagerAdapter
{
    private static final String TAG = AdapterMissionOrder.class.getSimpleName();

    CharSequence[] TitleOfTab;
    int NoOfTabs;
    String Status, Status2, ReasonForScreening;

    public AdapterMissionOrder(FragmentManager fm, CharSequence[] TitleOfTab, int NoOfTabs, String Status, String Status2, String ReasonForScreening)
    {
        super(fm);

        this.TitleOfTab         = TitleOfTab;
        this.NoOfTabs           = NoOfTabs;
        this.Status             = Status;
        this.Status2            = Status2;
        this.ReasonForScreening = ReasonForScreening;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        if (Status.equals("Complete"))
        {
            switch (position)
            {
                case 0:
                    return new BuildingInformationFragment();
                case 1:
                    return new MissionOrderFragment();
                case 2:
                    return new ReportsFragment();
                default:
                    return null;
            }
        }
        else
        {
            if (Status2.equals("1") && NoOfTabs != 2)
            {
                switch (position)
                {
                    case 0:
                        return new BuildingInformationFragment();
                    case 1:
                        return new MissionOrderFragment();
                    case 2:
                        if (ReasonForScreening.contains("RESA"))
                        {
                            return new RESAFragment();
                        }
                        else if (ReasonForScreening.contains("DESA"))
                        {
                            return new DESAFragment();
                        }
                        else
                        {
                            return new RVSScoringFragment();
                        }
                    default:
                        return null;
                }
            }
            else
            {
                switch (position)
                {
                    case 0:
                        return new BuildingInformationFragment();
                    case 1:
                        return new MissionOrderFragment();
                    default:
                        return null;
                }
            }

        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TitleOfTab[position];
    }

    @Override
    public int getCount()
    {
        return NoOfTabs;
    }
}
