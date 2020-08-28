package com.nnoboa.istudy.ui.file_manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.TabAdapter;
import com.nnoboa.istudy.ui.file_manager.tabs.ExcelFragment;
import com.nnoboa.istudy.ui.file_manager.tabs.PdfFragment;
import com.nnoboa.istudy.ui.file_manager.tabs.PowerPointFragment;
import com.nnoboa.istudy.ui.file_manager.tabs.WordFragment;
import com.nnoboa.istudy.ui.file_manager.loaders.pdfLoader;

public class FileFragment extends Fragment {

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_files, container, false);
        viewPager = root.findViewById(R.id.fileViewPager);
        tabLayout = root.findViewById(R.id.filetabLayout);
        tabAdapter = new TabAdapter(getFragmentManager());
        tabAdapter.addFragment(new PdfFragment(),getString(R.string.FILE_TAB_TILTE_PDF));
        tabAdapter.addFragment(new WordFragment(),getString(R.string.file_tab_title_word));
        tabAdapter.addFragment(new ExcelFragment(), getString(R.string.file_tab_title_excel));
        tabAdapter.addFragment(new PowerPointFragment(), getString(R.string.tab_title_powerpoint));
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }
}