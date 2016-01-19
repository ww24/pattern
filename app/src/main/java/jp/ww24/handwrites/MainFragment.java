package jp.ww24.handwrites;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ww24.handwrites.databinding.ContentMainBinding;

/**
 * Created by ww24 on 2015/11/06.
 */
public class MainFragment extends Fragment {

    private ContentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ContentMainBinding.inflate(inflater);

        binding.drawView.passBinding(binding);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
