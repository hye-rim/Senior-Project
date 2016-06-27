package com.onpuri.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.onpuri.R;
import com.onpuri.Server.ActivityList;
import com.onpuri.Server.CloseSystem;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toast toast;
    private Activity activity;
    private TabHost tabHost;

    private ActivityList actManager = ActivityList.getInstance();
    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close

    private worker_logout mworker_out;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    char check_out;
    char isOut = '0';

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    UserMyFragment userInfo;
    UserMyActFragment userAct;
    UserSetFragment setFrag;
    private MenuItem item;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        CloseSystem = new CloseSystem(this); //backKey Event

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("내노트"));
        tabLayout.addTab(tabLayout.newTab().setText("홈"));
        tabLayout.addTab(tabLayout.newTab().setText("문장 등록"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        userInfo = UserMyFragment.newInstance();
        userAct = UserMyActFragment.newInstance();
        setFrag = UserSetFragment.newInstance();

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        System.out.println("id : " + id);
        if (id == R.id.nav_mypage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity, userInfo).commit();
        } else if (id == R.id.nav_logout) {

            System.out.println("click logout");
            isOut = '1';

            System.out.println("down");
            mworker_out = new worker_logout(true);
            mworker_out.start();

            try {
                mworker_out.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("down_join");

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else if (id == R.id.nav_myact) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity, userAct).commit();
        } else if (id == R.id.nav_set) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity, setFrag).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    class worker_logout extends Thread {
        private boolean isPlay = false;
        private byte isGenerated;

        public worker_logout(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {

                // SocketConnection.start();
                String toServerDataUser;
                System.out.println("1");
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_OUT;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) PacketUser.USR_OUT_LEN;
                outData[4] = (byte) isOut;
                outData[5] = (byte) 85;

                try {
                    System.out.println("1");
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 5); // packet transmission
                    dos.flush();

                    System.out.println("2");
                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(inData);
                    System.out.println("Data form server: " + ((char) inData[4]) + (char) inData[1]);
                    int SOF = inData[0];

                    System.out.println(inData[0]);
                    System.out.println(inData[1]);
                    System.out.println(inData[2]);
                    System.out.println(inData[3]);
                    System.out.println((char) inData[4]); //이게 로그아웃 데이터 값 0,1 인데 서버에서 보내는 패킷보고 바꿔줄겡
                    System.out.println(inData[5]);

                    check_out = (char) inData[4];

                    System.out.println("outData : " + (char) outData[4] + "  inData : " + (char) inData[4]);
                    //System.out.println("cqq"+(char)inData[4]+"\n");
                    // System.out.println("cqq"+check+"\n");
                    if (check_out == '0' || check_out == '1')
                        isPlay = !isPlay;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainSub Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        //   SocketConnection.close();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
