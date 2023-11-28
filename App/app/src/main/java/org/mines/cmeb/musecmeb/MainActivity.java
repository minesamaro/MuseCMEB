package org.mines.cmeb.musecmeb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

import org.mines.cmeb.musecmeb.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setOnItemSelectedListener(item-> {
            //switch () {
                //case R.id.profile4:

                    //break;
                //case R.id.menu3:
                    //replaceFragment(new MenuFragment());
                    //break;

            //}
            //return true;
            if(item.getItemId()==R.id.profile4) {
                replaceFragment(new ProfileFragment());
            }
            if(item.getItemId()==R.id.menu3) {
                replaceFragment(new MenuFragment());
            }
            if(item.getItemId()==R.id.history) {
                replaceFragment(new HistoryFragment());
            }

            return false;
        });

    }
    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}