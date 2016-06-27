package com.onpuri;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView tv_userID,  tv_userShell, tv_userProblem, tv_userAverage, tv_userRank;
    TextView tv_userQuestion, tv_userSolving, tv_userAttend, tv_userPurchase, tv_userSale, tv_userDeclaration;
    Button btnOk;

    private CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_main);
        CloseSystem = new CloseSystem(this); //backKey Event

        tv_userID = (TextView)findViewById(R.id.tv_userID);
        tv_userID.setText(PacketUser.userId);

        tv_userShell = (TextView)findViewById(R.id.tv_userShell);
        tv_userShell.setText(PacketUser.shell);

        tv_userProblem = (TextView)findViewById(R.id.tv_userProblem);
        tv_userProblem.setText(PacketUser.problem);

        tv_userAverage = (TextView)findViewById(R.id.tv_userAverage);
        tv_userAverage.setText(PacketUser.average);

        tv_userRank = (TextView)findViewById(R.id.tv_userRank);
        tv_userRank.setText(PacketUser.ranking);

        tv_userQuestion = (TextView)findViewById(R.id.tv_userQuestion);
        tv_userQuestion.setText(PacketUser.question);

        tv_userSolving = (TextView)findViewById(R.id.tv_userSolving);
        tv_userSolving.setText(PacketUser.solving);

        tv_userAttend = (TextView)findViewById(R.id.tv_userAttend);
        tv_userAttend.setText(PacketUser.attend);

        tv_userPurchase = (TextView)findViewById(R.id.tv_userPurchase);
        tv_userPurchase.setText(PacketUser.purchase);

        tv_userSale = (TextView)findViewById(R.id.tv_userSale);
        tv_userSale.setText(PacketUser.sale);

        tv_userDeclaration = (TextView)findViewById(R.id.tv_userDeclaration);
        tv_userDeclaration.setText(PacketUser.declaration);

        btnOk = (Button)findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainSubActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        CloseSystem.onBackPressed();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //   SocketConnection.close();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }
}
