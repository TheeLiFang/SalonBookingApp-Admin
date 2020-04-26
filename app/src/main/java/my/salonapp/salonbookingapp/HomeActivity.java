package my.salonapp.salonbookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import my.salonapp.salonbookingapp.R;

public class HomeActivity extends AppCompatActivity {
    private static FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity_layout);
        fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_appointments:
                                switchToFragment1();
                                return true;
                            case R.id.navigation_booking:
                                switchToFragment2();
                                return true;
                            case R.id.navigation_client:
                                switchToFragment3();
                                return true;
                            case R.id.navigation_staff:
                                switchToFragment4();
                                return true;
                            case R.id.navigation_more:
                                switchToFragment5();
                                return true;
                        }
                        return false;
                    }
                });

        // If savedinstnacestate is null then replace home page fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Home_Fragment(),
                            Utils.Home_Fragment).commit();
        }

    }

    public void switchToFragment1() {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameContainer, new Home_Fragment(),
                        Utils.Home_Fragment).commit();
    }

    public void switchToFragment2() {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameContainer, new Booking_Fragment(),
                        Utils.Booking_Fragment).commit();
    }

    public void switchToFragment3() {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameContainer, new Client_Fragment(),
                        Utils.Client_Fragment).commit();
    }

    public void switchToFragment4() {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameContainer, new Staff_Fragment(),
                        Utils.Staff_Fragment).commit();
    }

    public void switchToFragment5() {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameContainer, new More_Fragment(),
                        Utils.More_Fragment).commit();
    }

    @Override
    public void onBackPressed(){
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }  else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }
}
