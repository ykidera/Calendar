package com.example.yoshinobu.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = MonthFragment.class.getSimpleName();

    Globals globals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globals = (Globals) this.getApplication();
        globals.init(this);


        Button settingBtn = (Button)findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "settingBtn onClick");
                Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(i);
            }
        });


        setMonthPage();
    }
    public void setMonthPage(){
        ArrayList<Integer> items = new ArrayList<Integer>();
        items.add(0);
        items.add(1);
        items.add(2);

        MonthPagerAdapter adapter = new MonthPagerAdapter(getSupportFragmentManager());
        adapter.addAll(items);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
            MonthPagerAdapter adapter = (MonthPagerAdapter) viewPager.getAdapter();

            ArrayList<Integer> indexes = adapter.getAll();

            int currentPage = viewPager.getCurrentItem();
            if( currentPage != 0 && currentPage != indexes.size() - 1){
                //最初でも最後のページでもない場合処理を抜ける
                return;
            }

            int nextPage = 0;
            if(currentPage == 0){
                //最初のページに到達
                nextPage = 1;
                indexes.add(0, indexes.get(0) - 1);
                //1ページ目は既に存在するため、Fragmentを全て破棄する
                adapter.destroyAllItem(viewPager);
                adapter.notifyDataSetChanged();
            }else if(currentPage == indexes.size() - 1){
                //最後のページに到達
                nextPage = currentPage;
                indexes.add(indexes.get(indexes.size() - 1) + 1);
            }
            adapter.addAll(indexes);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(nextPage);
        }
    }

}
