package pow.unionbankph.com.pow;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import android.content.Intent;
import android.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import static android.R.attr.button;
import static android.R.attr.id;
import static android.R.attr.name;
import static cz.msebera.android.httpclient.extras.PRNGFixes.apply;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.mime.Header;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchaseDetails extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DecimalFormat numberFormat;
    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchasedetails);
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

        Intent intent = getIntent();
        final String merchant_accountno = intent.getStringExtra("MERCHANT_ACCOUNTNO");
        String merchant_mobileno = "";
        final String merchant_name = intent.getStringExtra("MERCHANT_NAME");
        final String merchant_amount = (intent.getStringExtra("MERCHANT_AMOUNT").equals("") ? "0" : intent.getStringExtra("MERCHANT_AMOUNT"));
        merchant_mobileno = intent.getStringExtra("MERCHANT_MOBILENO");

        numberFormat = new DecimalFormat("#,###.00");


        TextView edittext_merchantName = (TextView) findViewById(R.id.purchasedetails_merchantname);
        final EditText edittext_merchantAmount = (EditText) findViewById(R.id.purchasedetails_amount);
        edittext_merchantName.setText(merchant_name);
        if(merchant_amount.trim().equals("") || merchant_amount.equals("0")){
            edittext_merchantAmount.setEnabled(true);
        }else{
            edittext_merchantAmount.setText(numberFormat.format(Double.parseDouble(merchant_amount)));
//            /edittext_merchantAmount.setEnabled(false);
            edittext_merchantAmount.setInputType(0);
        }

        TextView paymentlabel = (TextView) findViewById(R.id.paymentlabel);

        prefs = getSharedPreferences("com.unionbankph.pow", Context.MODE_PRIVATE);
        String paymentinfo = prefs.getString("PAYMENTINFO","");

        String[] res = paymentinfo.split("\\*");
        String acctno = "";
        ArrayList<PaymentOption> payment_list = new ArrayList<PaymentOption>();
        for (int i = 0; i < res.length; i++) {
            String[] fields = res[i].split("\\|");
            PaymentOption p = new PaymentOption();
            p.cardtype = fields[0];
            p.isUBP = fields[1];
            p.acctno = fields[2];
            p.cardnumber = fields[3];
            p.selected = fields[4];
            payment_list.add(p);

            if(p.selected.equals("1")){
                paymentlabel.setText("Paying using **** " + right(p.cardnumber,4));
                if(p.isUBP.equals("UBP")) {
                    acctno = p.acctno;
                }
            }
        }


        Button buttonSubmit = (Button) findViewById(R.id.purchasedetails_submit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                pdialog=new ProgressDialog(PurchaseDetails.this);
                pdialog.setCancelable(false);
                pdialog.setMessage("Processing...");
                new AlertDialog.Builder(PurchaseDetails.this)
                        .setTitle("Confirm Purchase")
                        .setMessage("Finalize your purchase?")
                        .setPositiveButton("Ok to proceed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String stringUrl = "https://api.us.apiconnect.ibmcloud.com/ubpapi-dev/sb/api/RESTs/transfer";
                                pdialog.show();
                                ConnectivityManager connMgr = (ConnectivityManager)PurchaseDetails.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                if (networkInfo != null && networkInfo.isConnected()) {
                                    char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
                                    String tid = randomString(CHARSET_AZ_09,8);
                                    new DownloadLibTask(tid,"101709454274",merchant_accountno,String.valueOf(edittext_merchantAmount.getText()).replaceAll(",",""), merchant_name, null,null).execute(stringUrl);
                                }
                            }

                        }).setNegativeButton("Wait, I'll have one more final look!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        ImageView buttonChangePaymentMethod = (ImageView) findViewById(R.id.icon_change);

        buttonChangePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PurchaseDetails.this,PaymentOptions.class);
                startActivity(intent);
            }
        });
    }

    private String EncodeURLString(String str){
        try {
            str = URLEncoder.encode(str,"UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return str;
    }



    private class DownloadLibTask extends AsyncTask<String, Void, String> {

        private String tid;
        private String sacc;
        private String tacc;
        private String amt;
        private String mName;
        private View rootView;
        private ViewGroup container;
        private DownloadLibTask(String tid,String sacc,String tacc, String amt, String mName, View rootView, ViewGroup container){
            this.tid = tid;
            this.sacc = sacc;
            this.tacc = tacc;
            this.amt = amt;
            this.mName = mName;
            this.rootView = rootView;
            this.container = container;
        }
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
	        	   /*
	        	   if(urls[0].indexOf("newappservice2") > 0){
			        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		  	   			alertDialog.setTitle("URL");
		  	   			alertDialog.setMessage(urls[0]);
		  	   			alertDialog.show();
		        	}*/
                return downloadUrl(urls[0], this.tid, this.sacc, this.tacc, this.amt);
            } catch (IOException e) {
                return "Unable to retrieve data.Your connection may be too slow or unstable. Pleas reload the form.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String result) {
            try{
                if(true) {
                    SharedPreferences prefs = getSharedPreferences("com.unionbankph.pow", Context.MODE_PRIVATE);
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                    prefs.edit().putString("PAYMENTHIST",  this.tid + "|" + this.sacc + "|" + this.tacc + "|" + this.amt + "|" + this.mName + "|" + dateFormat.format(new Date()) + "*" + prefs.getString("PAYMENTHIST","")).apply();

                    pdialog.dismiss();
                    String refno = this.tid;
                    new AlertDialog.Builder(PurchaseDetails.this)
                            .setTitle("Purchase Complete")
                            .setMessage("Your purchase payment was successful! (Ref#" + refno + ")")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(PurchaseDetails.this,MainActivity.class);
                                    startActivity(intent);
                                }

                            }).show();



                    /*
                    String[] res = result.split("_");
                    if(res[0].equals("SUCCESS")){
                        pdialog.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PurchaseDetails.this);
                        alertDialog.setTitle("Purchase Complete");
                        alertDialog.setMessage("Your purchase payment was successful! (Ref#" + res[1] + ")");
                        alertDialog.show();
                    }else{
                        pdialog.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PurchaseDetails.this);
                        alertDialog.setTitle("Connection Problem");
                        alertDialog.setMessage("There seems to be a problem your connection. Please try it again.");
                        alertDialog.show();
                    }*/
                }



            }catch(Exception e){
                pdialog.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PurchaseDetails.this);
                alertDialog.setTitle("Connection Problem");
                alertDialog.setMessage("There seems to be a problem your connection. Please try it again. " + result);
                alertDialog.show();
                e.printStackTrace();
                Log.d("exception", e.toString());
            }

        }
        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl,String tid,String sacc,String tacc, String amt) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            //int len = 999999999;

            try {
                URL url = new URL(myurl);
                System.setProperty("http.keepAlive", "false");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                // Create the SSL connection
                SSLContext sc;
                sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());

                conn.setUseCaches(false);
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("content-type", "application/json");
                conn.setRequestProperty("x-ibm-client-id", "66dd2e20-a62e-40c2-853f-8cc2c0a69e1b");
                conn.setRequestProperty("x-ibm-client-secret", "V2aL4hE4cR2dT7qI8rC5mK6eC6fT7hM1kJ4wB4wE8qE1jA6nH1");
                conn.setRequestProperty("connection", "close");
                conn.getOutputStream().write(("data={\"channel_id\":\"BLUEMIX\",\"transaction_id\":\"" + tid + "\",\"source_account\":\"\" + sacc + \"\",\"source_currency\":\"php\",\"target_account\":\"\" + tacc + \"\",\"target_currency\":\"php\",\"amount\":\" + amt + \"}").getBytes());

                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("debug", myurl);
                Log.d("debug", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                //String contentAsString = readIt(is);

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String contentAsString = sb.toString();
                conn.getInputStream().close();
                conn.disconnect();

                //Log.d(DEBUG_TAG, "Actual Response " + contentAsString);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }catch(Exception e){
                if (is != null) {
                    is.close();
                }
                e.printStackTrace();
                return e.getLocalizedMessage();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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

    public static String right(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }

    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }
}
