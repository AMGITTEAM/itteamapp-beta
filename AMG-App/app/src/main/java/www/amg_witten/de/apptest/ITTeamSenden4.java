package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ITTeamSenden4 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.it_team_senden4_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,null);

        TextView raumPr = (TextView)findViewById(R.id.raumPruefen);
        raumPr.setText("Bitte wähle den Fehler für den Raum "+ITTeamSenden.gebaeude+ITTeamSenden.etage+ITTeamSenden.raum+" aus!");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Methoden methoden = new Methoden();
        return methoden.onNavigationItemSelectedFillIn(item,0,this);
    }

    public void Fehler(View view) {
        switch(view.getId()){
            case R.id.fehler1:
                ITTeamSenden.fehler=getString(R.string.fehler1);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler2:
                ITTeamSenden.fehler=getString(R.string.fehler2);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler3:
                ITTeamSenden.fehler=getString(R.string.fehler3);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler4:
                ITTeamSenden.fehler=getString(R.string.fehler4);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler5:
                ITTeamSenden.fehler=getString(R.string.fehler5);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler6:
                ITTeamSenden.fehler=getString(R.string.fehler6);
                System.out.println(ITTeamSenden.fehler);
                break;

            case R.id.fehlersonstige:
                startActivity(new Intent(this,ITTeamSenden4Sonstige.class));
                return;
        }
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket server=new Socket();
                    server.connect(new InetSocketAddress(Startseite.ip,Startseite.port),Startseite.timeout);
                    BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    PrintWriter pw = new PrintWriter(server.getOutputStream());

                    pw.println("ITTeamHolen");
                    pw.flush();
                    pw.println("select * from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\";");
                    pw.flush();

                    int eintraegeZahl = Integer.parseInt(br.readLine());
                    List<String> eintraege = new ArrayList<>();
                    for(int i=0;i<eintraegeZahl;i++){
                        String bearbeiten = br.readLine();
                        System.out.println(bearbeiten);
                        String[] results = bearbeiten.split("Wichtigkeit: ");
                        String raumDahinter = results[1];
                        results = raumDahinter.split("//");
                        eintraege.add(results[0]);
                    }
                    server.close();
                    if(eintraegeZahl>0){
                        ITTeamSenden.wichtigkeit=eintraege.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(ac,ITTeamSenden4Vorhanden.class));
                            }
                        });
                    }
                    else {
                        startActivity(new Intent(ac,ITTeamSenden5.class));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    startActivity(new Intent(ac,ITTeamSenden5.class));
                }
            }
        }).start();
    }
}
