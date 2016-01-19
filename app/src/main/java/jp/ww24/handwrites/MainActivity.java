package jp.ww24.handwrites;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.alexbbb.uploadservice.UploadService;

import java.util.UUID;

import jp.ww24.handwrites.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int defaultItemId;
    private int selectedItemId;

    private ActivityMainBinding binding;

    public static String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Generate or Get UniqueID
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        uniqueID = sharedPreferences.getString("UNIQUE_ID", null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UNIQUE_ID", uniqueID);
            editor.apply();
        }
        Log.d("DEBUG", "UUID: " + uniqueID);

        // Upload Service
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        // Toolbar
        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        // DrawerLayout
        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // NavigationView
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);
        defaultItemId = selectedItemId = R.id.nav_camara;
        navigationView.setCheckedItem(selectedItemId);

        // Fragment
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                           .add(R.id.content_frame, new MainFragment())
                           .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (selectedItemId != defaultItemId) {
            selectedItemId = defaultItemId;

            NavigationView navigationView = binding.navView;
            navigationView.setCheckedItem(selectedItemId);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new MainFragment())
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = binding.drawerLayout;

        // ignore if same item selected
        if (item.getItemId() == selectedItemId) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        } else {
            selectedItemId = item.getItemId();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (selectedItemId) {
            case R.id.nav_camara:
                MainFragment fragment = new MainFragment();

                fragmentManager.beginTransaction()
                               .replace(R.id.content_frame, fragment)
                               .commit();
                break;
            case R.id.nav_gallery:
                fragmentManager.beginTransaction()
                               .replace(R.id.content_frame, new GalleryFragment())
                               .commit();
                break;
            case R.id.nav_manage:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SettingFragment())
                        .commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
