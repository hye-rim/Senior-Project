package com.onpuri;

import android.app.TabActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MainSubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toast toast;
    private Activity activity;
    private TabHost tabHost;

    private ActivityList actManager = ActivityList.getInstance();
    private CloseSystem CloseSystem; //BackKeyPressed,close

    private worker_logout mworker_out;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    char check_out;
    char isOut = '0';
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_main_sub);

        tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        CloseSystem = new CloseSystem(this); //backKey Event

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

/*
        //클릭할 때 마다 리플래쉬
        TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("내노트");
        tabSpecTab1.setContent(new Intent(this, UserProfile.class));
        tabHost.addTab(tabSpecTab1);

        TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("홈");
        tabSpecTab2.setContent(new Intent(this, UserMain.class));
        tabHost.addTab(tabSpecTab2);

        TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("문장등록");
        tabSpecTab3.setContent(new Intent(this, UserNewSen.class));
        tabHost.addTab(tabSpecTab3);

        tabHost.setCurrentTab(1);
*/
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       switch(item.getItemId()){
           case R.id.action_search:
               Toast.makeText(getApplicationContext(), "구현 예정", Toast.LENGTH_SHORT).show();
               return true;
           case R.id.action_settings:
               Toast.makeText(getApplicationContext(), "구현 예정", Toast.LENGTH_SHORT).show();
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        System.out.println("id : " + id);
        if (id == R.id.nav_mypage) {

            System.out.println("click mypage");
            Intent mainIntent = new Intent(MainSubActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
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

            Intent loginIntent = new Intent(MainSubActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else if( id == R.id.nav_temp1) {
            System.out.println("click temp1");
          Toast.makeText(getApplicationContext(), "구현 예정", Toast.LENGTH_SHORT).show();
        }
        else if( id == R.id.nav_set) {
            System.out.println("click temp2");
            Toast.makeText(getApplicationContext(), "구현 예정", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class worker_logout extends Thread {
        private boolean isPlay = false;
        private byte isGenerated;
        public worker_logout (boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread () {
            isPlay = !isPlay;
        }

        public void run () {
            super.run ();
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
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream ());
                    dos.write (outData,0,outData[3]+5); // packet transmission
                    dos.flush();

                    System.out.println("2");
                    dis = new DataInputStream(SocketConnection.socket.getInputStream ());
                    dis.read (inData);
                    System.out.println("Data form server: " + ((char)inData[4]) + (char)inData[1]);
                    int SOF = inData[0];

                    System.out.println (inData[0]);
                    System.out.println (inData[1]);
                    System.out.println (inData[2]);
                    System.out.println (inData[3]);
                    System.out.println ((char) inData[4]); //이게 로그아웃 데이터 값 0,1 인데 서버에서 보내는 패킷보고 바꿔줄겡
                    System.out.println (inData[5]);

                    check_out = (char) inData[4];

                    System.out.println("outData : " + (char)outData[4] + "  inData : " + (char)inData[4]);
                    //System.out.println("cqq"+(char)inData[4]+"\n");
                    // System.out.println("cqq"+check+"\n");
                    if( check_out == '0' || check_out == '1')
                        isPlay = !isPlay;

                } catch (IOException e) {
                    e.printStackTrace ();
                }

            }
        }


    }

    @Override
    protected void onStop(){
        super.onStop();
        //   SocketConnection.close();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
