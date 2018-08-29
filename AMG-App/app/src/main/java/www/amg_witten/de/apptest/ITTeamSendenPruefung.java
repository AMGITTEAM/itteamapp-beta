package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ITTeamSendenPruefung extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden_pruefung);

        TextView raumPr = findViewById(R.id.pruefen);
        raumPr.setText("Raum: "+ITTeamSenden.gebaeude+ITTeamSenden.etage+ITTeamSenden.raum+"\n\n\nFehler: "+ITTeamSenden.fehler+"\n\n\nWichtigkeit: "+ITTeamSenden.wichtigkeit+"\n\n\nBeschreibung: "+ITTeamSenden.beschreibung);

        ITTeamSenden.beschreibung = ITTeamSenden.beschreibung.replaceAll("\n","//");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

    private void Start(View view) {
        Intent intent = new Intent(this, Startseite.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void Senden(View view){
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("WORKING");
                try {
                    if(!ITTeamSenden.ueberschreiben){
                        String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=ITTeamHolen&request=select * from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\"$username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                        url = url.replaceAll(" ","%20");
                        URL oracle = new URL(url);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(oracle.openStream()));

                        while (!(in.readLine()).equals("<body>")){}
                        in.readLine();
                        String fullBisher = (in.readLine());
                        in.close();
                        String bisher = fullBisher.split("/newthing/")[1];
                        String[] beschrs = bisher.split("Beschreibung: ");
                        String beschr = beschrs[1];
                        System.out.println(beschr);
                        String[] datums = bisher.split("Datum: ");
                        String datum = datums[1];

                        url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=ITTeamLoeschen&request=delete from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\"$username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                        url = url.replaceAll(" ","%20");
                        oracle = new URL(url);
                        in = new BufferedReader(
                                new InputStreamReader(oracle.openStream()));

                        while (!(in.readLine()).equals("<body>")){}
                        in.readLine();
                        System.out.println(in.readLine());
                        in.close();

                        url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=ITTeamMelden&request=$username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+datum+"&gebaeude="+ITTeamSenden.gebaeude+"&etage="+ITTeamSenden.etage+"&raum="+ITTeamSenden.raum+"&wichtigkeit="+ITTeamSenden.wichtigkeit+"&fehler="+ITTeamSenden.fehler+"&beschreibung="+ITTeamSenden.beschreibung+"&status=Offen&bearbeitetVon=Keiner";
                        url = url.replaceAll(" ","%20");
                        oracle = new URL(url);
                        in = new BufferedReader(
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
                    }
                    else {
                        String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=ITTeamMelden&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"&gebaeude="+ITTeamSenden.gebaeude+"&etage="+ITTeamSenden.etage+"&raum="+ITTeamSenden.raum+"&wichtigkeit="+ITTeamSenden.wichtigkeit+"&fehler="+ITTeamSenden.fehler+"&beschreibung="+ITTeamSenden.beschreibung+"&status=Offen&bearbeitetVon=Keiner";
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
                    }
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
        Start(new View(ac));
    }
}
