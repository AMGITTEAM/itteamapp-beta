package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ITTeamSendenPruefung extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.it_team_senden_pruefung_main);
        TextView raumPr = (TextView)findViewById(R.id.pruefen);
        raumPr.setText("Raum: "+ITTeamSenden.gebaeude+ITTeamSenden.etage+ITTeamSenden.raum+"\n\n\nFehler: "+ITTeamSenden.fehler+"\n\n\nWichtigkeit: "+ITTeamSenden.wichtigkeit+"\n\n\nBeschreibung: "+ITTeamSenden.beschreibung);

        ITTeamSenden.beschreibung = ITTeamSenden.beschreibung.replaceAll("\n","//");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Methoden methoden = new Methoden();
        return methoden.onNavigationItemSelectedFillIn(item,0,this);
    }

    public void Start(View view) {
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
                        Socket s = new Socket();
                        s.connect(new InetSocketAddress(Startseite.ip,Startseite.port),5000);
                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter pw = new PrintWriter(s.getOutputStream());
                        pw.println("ITTeamHolen");
                        pw.flush();
                        pw.println("select * from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\";");
                        pw.flush();
                        br.readLine();
                        String bisher = br.readLine();
                        String[] beschrs = bisher.split("Beschreibung: ");
                        String beschr = beschrs[1];
                        System.out.println(beschr);
                        String[] datums = bisher.split("Datum: ");
                        String datum = datums[1];
                        br.close();
                        pw.close();
                        s.close();

                        s = new Socket();
                        s.connect(new InetSocketAddress(Startseite.ip,Startseite.port),5000);
                        pw=new PrintWriter(s.getOutputStream());
                        pw.println("ITTeamLoeschen");
                        pw.flush();
                        pw.println("delete from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\";");
                        pw.flush();
                        pw.close();
                        s.close();

                        s = new Socket();
                        s.connect(new InetSocketAddress(Startseite.ip,Startseite.port),5000);
                        pw = new PrintWriter(s.getOutputStream());
                        pw.println("ITTeamMelden");
                        pw.flush();
                        pw.println(datum);
                        pw.flush();
                        pw.println(ITTeamSenden.gebaeude);
                        pw.flush();
                        pw.println(ITTeamSenden.etage);
                        pw.flush();
                        pw.println(ITTeamSenden.raum);
                        pw.flush();
                        pw.println(ITTeamSenden.wichtigkeit);
                        pw.flush();
                        pw.println(ITTeamSenden.fehler);
                        pw.flush();
                        pw.println(ITTeamSenden.beschreibung);
                        pw.flush();
                        pw.println("Offen");
                        pw.flush();
                        pw.println("Keiner");
                        pw.flush();
                        pw.close();
                        s.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ac,"Gemeldet!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        Socket s = new Socket();
                        s.connect(new InetSocketAddress(Startseite.ip,Startseite.port),5000);
                        PrintWriter pw = new PrintWriter(s.getOutputStream());
                        pw.println("ITTeamMelden");
                        pw.flush();
                        pw.println(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                        pw.flush();
                        pw.println(ITTeamSenden.gebaeude);
                        pw.flush();
                        pw.println(ITTeamSenden.etage);
                        pw.flush();
                        pw.println(ITTeamSenden.raum);
                        pw.flush();
                        pw.println(ITTeamSenden.wichtigkeit);
                        pw.flush();
                        pw.println(ITTeamSenden.fehler);
                        pw.flush();
                        pw.println(ITTeamSenden.beschreibung);
                        pw.flush();
                        pw.println("Offen");
                        pw.flush();
                        pw.println("Keiner");
                        pw.flush();
                        pw.close();
                        s.close();
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
