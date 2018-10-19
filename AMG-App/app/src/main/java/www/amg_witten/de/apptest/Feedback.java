package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Feedback extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String type = "";
    private String description = "";
    private EditText descriptionText = null;
    private Spinner typeSpinner = null;
    private final String[] types = new String[]{"Typ","Bug/Fehler","Idee/Anregung","Anderes"};

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
            Intent intent = new Intent(this, Startseite.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
        type = types[typeSpinner.getSelectedItemPosition()];
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("WORKING");
                try {String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=Feedback&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+type+"&gebaeude="+description+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
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
                            Toast.makeText(ac,"Gemeldet!",Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Fehler beim Melden des Fehlers",Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        Start();
    }
}
