package com.masstudio.selmy.tmc.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.masstudio.selmy.tmc.Fragments.InstFragment;
import com.masstudio.selmy.tmc.Fragments.ResultsFragment;
import com.masstudio.selmy.tmc.Fragments.TableMapFragment;
import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Services.InstructionsListenerService;
import com.masstudio.selmy.tmc.Services.UpdateDataService;
import com.masstudio.selmy.tmc.Utils.Excel;
import com.google.firebase.auth.FirebaseAuth;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class AuthActivity extends AppCompatActivity  implements MaterialTabListener {
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private MaterialTabHost tabHost;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth=FirebaseAuth.getInstance();
        tabHost=(MaterialTabHost) findViewById(R.id.materialTabHost);
        viewPager=(ViewPager) findViewById(R.id.pager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        // to make the tab changed when the page changed
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                //edit on tab selected
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {

            Drawable drawable=adapter.getIcon(i);
            drawable.setBounds(0,0,66,66);
            tabHost.addTab(
                    tabHost.newTab()
                            // .setText(adapter.getPageTitle(i))
                            .setIcon(drawable)
                            .setTabListener(this)
            );
        }
        // TODO : remove call survey from here
        /*
        Intent intent = new Intent(this, SurveyListenerService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("LOC",new LatLng(30.058285, 31.378871));
        intent.putExtras(bundle);
        startService(intent);
        */
        startService(new Intent(this, InstructionsListenerService.class));
        startService(new Intent(this, UpdateDataService.class));

    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        //without these line when you click on the tab the page wouldn't be changed
        // to make the page changed when the tab changed
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {


    }

    @Override
    public void onTabUnselected(MaterialTab tab) {


    }

    public void signOut(View view) {
        mAuth.signOut();
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void creatExcelSheet(View view) {
        Excel excel = new Excel(this);
        Log.d("Excelsheet","clicked");
        excel.getData();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int[]  icons ={R.drawable.ic_cast_dark,R.drawable.ic_playlist_add_check_white_24dp,R.drawable.quantum_ic_refresh_white_24};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show image
                    return InstFragment.getInstance();
                case 1: // Fragment # 1 - This will show image
                    return TableMapFragment.getInstance();
                default:// Fragment # 2-9 - Will show list
                    return ResultsFragment.getInstance();
            }
        }

        @Override
        public int getCount() {
            //return 12; // to be scrollable
            return 3; //fixed
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "tab";
        }

        private Drawable getIcon(int position){
            return ResourcesCompat.getDrawable(getResources(),icons[position], null);
        }
    }
}
