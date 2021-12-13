package com.example.bills.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bills.MainActivity;
import com.example.bills.R;
import com.example.bills.databinding.FragmentHomeBinding;
import com.example.bills.models.Group;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    //check user active groups
    Group[] currGroups = new Group[]{};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        checkGroups();
        return root;
    }

    private void checkGroups() {
        if(currGroups.length == 0) {

        }
        else {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}