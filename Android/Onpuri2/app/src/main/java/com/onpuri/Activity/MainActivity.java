package com.onpuri.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.onpuri.ActivityList;
import com.onpuri.R;
import com.onpuri.Thread.WorkerLogout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity" ;
    private ActivityList actManager = ActivityList.getInstance();

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    private GoogleApiClient client;

    //Back Preesed
    private final long FINISH_INTERVAL_TIME = 3000;
    private long backPressedTime = 0;

    private WorkerLogout mworker_out;

    //User data - SharedPreferences
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    private String userId = "";
    private String name, joinDate, phone, nowPassword;

    private TextView mNavId;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_main);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setting = getSharedPreferences("setting",0);
        editor = setting.edit();
        mworker_out = null;

        //Setup the DrawerLayout and NavigationView
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view) ;

        //View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main_sub, null);
        //mNavigationView.addHeaderView(header);
        View header = mNavigationView.getHeaderView(0);
        mNavId = (TextView)header.findViewById(R.id.tv_nav_id);

        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        Log.d(TAG, "id : " + userId);
        name = intent.getStringExtra("Name");
        joinDate = intent.getStringExtra("JoinDate");
        phone = intent.getStringExtra("Phone");
        nowPassword = intent.getStringExtra("NowPass");

        mNavId.setText(userId + " 님");

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.containerView,new TabViewPager())
                .commit();

        //Setup click events on the Navigation View Items.
        mNavigationView.setNavigationItemSelectedListener(this);

        // Setup Drawer Toggle of the Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        //Navigation Drawer OPEN / CLOSE
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            //Fragment가 스택에 남아있을 경우
            if(mFragmentManager.getBackStackEntryCount() > 0) {
                Log.d(TAG,"pop");
                mFragmentManager.popBackStack();
                mFragmentManager.beginTransaction()
                        .commit();
            }
            else {
                Log.d(TAG, "close");
                //시간안에 2번 눌렀을 때
                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                    if (setting.getBoolean("autoLogin", false) == false) {
                        editor.clear();
                        editor.commit();
                    }

                    outThreadCheck();

                    ActivityCompat.finishAffinity(this);
                    System.runFinalizersOnExit(true);
                    System.exit(0);
                }

                //First back preesed.
                else{
                    backPressedTime = tempTime;
                    Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Fragment getBaseFragment() {
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment.isVisible()) {
                return (fragment);
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("두 자 이상 입력하세요");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //입력 완료 후 구현 부분
                Toast.makeText(getApplicationContext(), "검색 기능은 구현 예정입니다.", Toast.LENGTH_SHORT).show();
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                Bundle args = new Bundle();
                args.putString("searchText", query);
                searchFragment.setArguments(args);

                searchView.clearFocus();

                fragmentManager.beginTransaction()
                        .replace(R.id.root_home, searchFragment)
                        .addToBackStack(null)
                        .commit();
                fragmentManager.executePendingTransactions();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //입력하는 동안 부분
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    searchItem.collapseActionView();
                    searchView.setQuery("",false);
                }
            }
        });

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mDrawerLayout.closeDrawers();

        switch (item.getItemId()){
            case R.id.nav_mypage :
                UserMyFragment MyFrament = new UserMyFragment();
                bundle = new Bundle();
                bundle.putString("MyId", userId);
                bundle.putString("MyName", name);
                bundle.putString("MyJoin", joinDate);
                bundle.putString("MyPhone", phone);
                bundle.putString("MyPass", nowPassword);
                MyFrament.setArguments(bundle);

                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, MyFrament)
                        .addToBackStack("fragBack")
                        .commit();
                break;

            case R.id.nav_myact:
                UserMyActFragment ActFrament = new UserMyActFragment();
                bundle = new Bundle();
                bundle.putString("ActId", userId);
                ActFrament.setArguments(bundle);

                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, ActFrament)
                        .addToBackStack("fragBack")
                        .commit();
                break;

            case R.id.nav_logout:
                logout();
                break;

            case R.id.nav_set:
                UserSetFragment SetFrament = new UserSetFragment();
                bundle = new Bundle();
                bundle.putString("SetId", userId);
                SetFrament.setArguments(bundle);

                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, SetFrament)
                        .addToBackStack("fragBack")
                        .commit();
                break;
        }
        return false;
    }

    private void logout() {
        outThreadCheck(); //스레드 중지 후 재시작

        if (setting.getBoolean("autoLogin", false)) {
            editor.clear();
            editor.commit();
        }
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void outThreadCheck(){
        if(mworker_out != null && mworker_out.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_out.interrupt();
        }

        mworker_out = new WorkerLogout(true);
        mworker_out.start();

        try {
            mworker_out.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainSub Page", // TODO: Define a title for the content shown.
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "MainSub Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actManager.removeActivity(this);
    }
}
