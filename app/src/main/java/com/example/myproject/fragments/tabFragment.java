package com.example.myproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myproject.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class tabFragment extends Fragment
{
    private View tabView;

    ViewPager2 viewPager;
    TabLayout tabLayout;

    List<Fragment> fragments = new ArrayList<>();
    List<String> names = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        tabView=inflater.inflate ( R.layout.layout_tabs,null );
        return tabView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated ( savedInstanceState );

        tabLayout=tabView.findViewById ( R.id.tabs );
        viewPager=tabView.findViewById ( R.id.viewpager );

        fragments.add ( new Users () );
        fragments.add ( new Friends () );

        names.add ( "Users" );
        names.add ( "Friends" );

        viewpagerAdapter adapter = new viewpagerAdapter ( this,fragments );
        viewPager.setAdapter ( adapter );



    }

    public class viewpagerAdapter extends FragmentStateAdapter
    {
        List<Fragment> list;
        public viewpagerAdapter(Fragment fragment, List<Fragment> fragments)
        {
            super(fragment);
            list=fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position)
        {
            return list.get ( position );
        }

        @Override
        public int getItemCount() {
            return list.size ();
        }
    }
}
