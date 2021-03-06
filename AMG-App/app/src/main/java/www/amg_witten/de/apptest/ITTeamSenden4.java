package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ITTeamSenden4 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean shouldExecResume = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecResume)
            Methoden.onResumeFillIn(this);
        else
            shouldExecResume = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden4);

        TextView raumPr = findViewById(R.id.raumPruefen);
        if(ITTeamSenden.raum.equals("Ohne")){
            raumPr.setText(getString(R.string.it_team_melden_fehler_ohneRaum));
        }
        else {
            raumPr.setText(getString(R.string.it_team_melden_fehler,ITTeamSenden.gebaeude+ITTeamSenden.etage+ITTeamSenden.raum));
        }
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this, false, true))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

    public void Fehler(View view) {
        switch(view.getId()){
            case R.id.fehler1:
                ITTeamSenden.fehler=getString(R.string.fehler1it);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler2:
                ITTeamSenden.fehler=getString(R.string.fehler2it);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler3:
                ITTeamSenden.fehler=getString(R.string.fehler3it);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler4:
                ITTeamSenden.fehler=getString(R.string.fehler4it);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler5:
                ITTeamSenden.fehler=getString(R.string.fehler5it);
                System.out.println(ITTeamSenden.fehler);
                break;
            case R.id.fehler6:
                ITTeamSenden.fehler=getString(R.string.fehler6it);
                System.out.println(ITTeamSenden.fehler);
                break;

            case R.id.fehlersonstige:
                startActivity(new Intent(this,ITTeamSenden4Sonstige.class));
                return;
            /*case R.id.fehlergebaeude:
                ITTeamSenden.gebFehler=true;
                System.out.println("Gebaeude");
                startActivity(new Intent(this,ITTeamSenden4Sonstige.class));
                return;*/

        }
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamHolen&request="+"select * from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\";&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    URL oracle = new URL(url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    String data = (in.readLine());
                    in.close();

                    int eintraegeZahl = Integer.parseInt(data.split("/newthing/")[0]);
                    List<String> eintraege = new ArrayList<>();
                    for(int i=0;i<eintraegeZahl;i++){
                        String bearbeiten = data.split("/newthing/")[i+1];
                        System.out.println(bearbeiten);
                        String[] results = bearbeiten.split("Wichtigkeit: ");
                        String raumDahinter = results[1];
                        results = raumDahinter.split("//");
                        eintraege.add(results[0]);
                    }
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
