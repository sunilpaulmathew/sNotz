package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.PagerAdapter;
import com.sunilpaulmathew.snotz.fragments.WidgetChecklistsFragment;
import com.sunilpaulmathew.snotz.fragments.WidgetNotesFragment;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class WidgetActivity extends AppCompatActivity {

    public WidgetActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        MaterialTextView mCancel = findViewById(R.id.cancel);
        TabLayout mTabLayout = findViewById(R.id.tab_Layout);
        ViewPager mViewPager = findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new WidgetNotesFragment(), getString(R.string.select_note));
        adapter.AddFragment(new WidgetChecklistsFragment(), getString(R.string.select_checklist));

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mCancel.setOnClickListener(v -> finish());
    }

}