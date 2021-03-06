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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ITTeamSendenPruefung extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden_pruefung);

        TextView raumPr = findViewById(R.id.pruefen);
        if(ITTeamSenden.raum.equals("Ohne")){
            raumPr.setText(getString(R.string.it_team_melden_pruefung_daten, getString(R.string.it_team_melden_gebaeude_none), ITTeamSenden.fehler, ITTeamSenden.wichtigkeit, ITTeamSenden.beschreibung));
        }
        else {
            raumPr.setText(getString(R.string.it_team_melden_pruefung_daten, ITTeamSenden.gebaeude+ITTeamSenden.etage+ITTeamSenden.raum, ITTeamSenden.fehler, ITTeamSenden.wichtigkeit, ITTeamSenden.beschreibung));
        }

        ITTeamSenden.beschreibung = ITTeamSenden.beschreibung.replaceAll("\n","//");
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

    public void Abbrechen(View view) {
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
                        String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamHolen&request=select * from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\"&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
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
                        String beschr = beschrs[1].split("//")[0];
                        System.out.println(beschr);
                        String[] datums = bisher.split("Datum: ");
                        String datum = datums[1].split("//")[0];

                        url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamLoeschen&request=delete from fehlermeldungen where gebaeude=\""+ITTeamSenden.gebaeude+"\" and etage=\""+ITTeamSenden.etage+"\" and raum=\""+ITTeamSenden.raum+"\" and fehler=\""+ITTeamSenden.fehler+"\"&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                        url = url.replaceAll(" ","%20");
                        oracle = new URL(url);
                        in = new BufferedReader(
                                new InputStreamReader(oracle.openStream()));

                        while (!(in.readLine()).equals("<body>")){}
                        in.readLine();
                        System.out.println(in.readLine());
                        in.close();

                        url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamMelden&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+datum+"&gebaeude="+ITTeamSenden.gebaeude+"&etage="+ITTeamSenden.etage+"&raum="+ITTeamSenden.raum+"&wichtigkeit="+ITTeamSenden.wichtigkeit+"&fehler="+ITTeamSenden.fehler+"&beschreibung="+beschr+";"+ITTeamSenden.beschreibung+"&status=Offen&bearbeitetVon=Keiner";
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
                                Toast.makeText(ac,getString(R.string.it_team_melden_erfolgreich),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        if(ITTeamSenden.gebFehler){
                            String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=GebaeudefehlerMelden&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"&gebaeude="+ITTeamSenden.gebaeude+"&etage="+ITTeamSenden.etage+"&raum="+ITTeamSenden.raum+"&wichtigkeit="+ITTeamSenden.wichtigkeit+"&fehler="+ITTeamSenden.fehler+"&beschreibung="+ITTeamSenden.beschreibung+"&status=Offen&bearbeitetVon=Keiner";
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
                                    Toast.makeText(ac,getString(R.string.it_team_melden_erfolgreich),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamMelden&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"&gebaeude="+ITTeamSenden.gebaeude+"&etage="+ITTeamSenden.etage+"&raum="+ITTeamSenden.raum+"&wichtigkeit="+ITTeamSenden.wichtigkeit+"&fehler="+ITTeamSenden.fehler+"&beschreibung="+ITTeamSenden.beschreibung+"&status=Offen&bearbeitetVon=Keiner";
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
                                    Toast.makeText(ac,getString(R.string.it_team_melden_erfolgreich),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.it_team_melden_nicht_erfolgreich),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        Intent intent = new Intent(this, Startseite.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
