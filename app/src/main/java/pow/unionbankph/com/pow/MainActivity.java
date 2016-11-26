package pow.unionbankph.com.pow;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;

import static android.R.attr.id;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_RESULT_QR_DRDROID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        View headerLayout = navigationView.getHeaderView(0);

        TextView username = (TextView) headerLayout.findViewById(R.id.username);
        TextView email = (TextView) headerLayout.findViewById(R.id.email);

        SharedPreferences prefs = getSharedPreferences("com.unionbankph.pow", Context.MODE_PRIVATE);
        username.setText("Josh Bosinos");
        email.setText(prefs.getString("email",""));

        ImageView startpowing = (ImageView) findViewById(R.id.img_startpowing);

        startpowing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent("la.droid.qr.scan");

                try {

                    startActivityForResult(i, ACTIVITY_RESULT_QR_DRDROID);
                }
                catch (ActivityNotFoundException activity) {

                    Log.d("error",activity.getLocalizedMessage());
                }

                //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //startActivityForResult(intent, 0);
            }
        });
        //displayView(R.id.nav_startpurchase);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if( ACTIVITY_RESULT_QR_DRDROID == requestCode
                && data != null && data.getExtras() != null ) {

            String result = data.getExtras().getString("la.droid.qr.result");

            String merchant_name = "";
            String merchant_accountno = "";
            String merchant_amount = "";
            String merchant_mobileno = "";
            String[] res = result.split("\\|");

            Log.d("result",result);
            if(res.length > 0) {
                Log.d("res[0]", res[0]);
                Log.d("res[1]", res[1]);
                Log.d("res[2]", res[2]);
                Log.d("res[3]", res[3]);

                merchant_name = res[0];
                merchant_accountno = res[1];
                merchant_mobileno = res[3];

            }
            if(res.length >= 3){
                merchant_amount = res[2];
            }

            //setContentView(R.layout.activity_purchase_details);
            //EditText edittext_merchantName = (EditText) findViewById(R.id.purchasedetails_merchantname);
            //EditText edittext_merchantAmount = (EditText) findViewById(R.id.purchasedetails_amount);
            //edittext_merchantName.setText(merchant_name);
            //edittext_merchantName.setText(merchant_amount);

            Intent intent = new Intent(MainActivity.this,PurchaseDetails.class);
            intent.putExtra("MERCHANT_NAME", merchant_name);
            intent.putExtra("MERCHANT_ACCOUNTNO", merchant_accountno);
            intent.putExtra("MERCHANT_AMOUNT", merchant_amount);
            intent.putExtra("MERCHANT_MOBILENO", merchant_mobileno);
            startActivity(intent);
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displayView(item.getItemId());
        return true;
        /*
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("id");
        alertDialog.setMessage(id);
        if (id == R.id.nav_startpurchase) {
            Intent mainIntent = new Intent(this,MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else if (id == R.id.nav_paymentoptions) {
            // Handle the camera action
        } else if (id == R.id.nav_purchases) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;*/
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("id");
        alertDialog.setMessage(viewId);

        Intent mainIntent;
        switch (viewId) {
            case R.id.nav_startpurchase:
                //fragment = new MainActivity();
                mainIntent = new Intent(this,MainActivity.class);
                startActivity(mainIntent);
                title  = "Start Purchasing";

                break;
            case R.id.nav_paymentoptions:
                //fragment = new EventsFragment();
                title = "Payment Options";
                mainIntent = new Intent(this,PaymentOptions.class);
                startActivity(mainIntent);
                break;
            case R.id.nav_purchases:
                //fragment = new EventsFragment();
                title = "Purchases";
                mainIntent = new Intent(this,PurchasePayments.class);
                startActivity(mainIntent);
                break;
            case R.id.nav_help:
                //fragment = new EventsFragment();
                title = "Help";
                mainIntent = new Intent(this,Help.class);
                startActivity(mainIntent);
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
}
