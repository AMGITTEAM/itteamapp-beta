package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

public class Feedback extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String type = "";
    private String description = "";
    private EditText descriptionText = null;
    private Spinner typeSpinner = null;
    private final String[] types = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,902,R.layout.feedback);

        types[0] = getString(R.string.feedback_type_none);
        types[1] = getString(R.string.feedback_type_bug);
        types[2] = getString(R.string.feedback_type_idea);
        types[3] = getString(R.string.feedback_type_other);
        descriptionText = findViewById(R.id.feedback_description);
        typeSpinner = findViewById(R.id.feedback_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_feedback,this);
        return true;
    }

    private void Start() {
        Intent intent = new Intent(this, Startseite.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void Senden(View view){
        description = descriptionText.getText().toString();
        description+="\nVersion: "+BuildConfig.VERSION_NAME;
        description+="\nAndroidApi: "+Build.VERSION.SDK_INT;
        type = types[typeSpinner.getSelectedItemPosition()];
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=Feedback&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+type+"&gebaeude="+description+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    url = url.replaceAll("\n","%30");
                    URL oracle = new URL(url);
                    System.out.println(oracle);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_success),Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_failure,e.getMessage()),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        Start();
    }

    private void askCompleteDebug(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.feedback_ask_completeDebug))
                .setPositiveButton(getString(R.string.feedback_completeDebug_positiveButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCompleteDebug();
                    }
                })
                .setNegativeButton(getString(R.string.feedback_completeDebug_negativeButton),null)
                .setTitle(getString(R.string.feedback_completeDebug_title));
        builder.create().show();
    }

    private void doCompleteDebug(){
        final Context context = this;
        description = "KOMPLETT-DEBUG";
        description+="\nVersion: "+BuildConfig.VERSION_NAME;
        description+="\nAndroidApi: "+Build.VERSION.SDK_INT;
        Map<String, ?> map = Startseite.prefs.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            description+="\n"+entry.getKey()+": "+entry.getValue();
        }
        final String id = UUID.randomUUID().toString();
        description+="\nID: "+id;
        type = types[typeSpinner.getSelectedItemPosition()];
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("WORKING");
                try {
                    description = URLEncoder.encode(description,"utf-8");
                    String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=Feedback&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+type+"&gebaeude="+description+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    url = url.replaceAll("\n","%30");
                    URL oracle = new URL(url);
                    System.out.println(oracle);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_success),Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            TextView tv = new TextView(context);
                            tv.setText(getString(R.string.feedback_full_id,id));
                            tv.setTextIsSelectable(true);
                            builder.setView(tv)
                                    .setPositiveButton(getString(R.string.feedback_completeDebug_positiveButton), null)
                                    .setTitle(getString(R.string.feedback_completeDebug_title));
                            builder.create().show();
                        }
                    });
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_failure,e.getMessage()),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_complete_debug) {
            askCompleteDebug();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
