package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;

public class ITTeamHolen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MenuItem offen;
    private MenuItem inBearbeitung;
    private MenuItem fertig;
    private MenuItem gebH;
    private MenuItem gebA;
    private MenuItem gebN;
    private MenuItem et2;
    private MenuItem et1;
    private MenuItem et0;
    private MenuItem etZ;
    private MenuItem etU;
    private MenuItem raum01;
    private MenuItem raum02;
    private MenuItem raum03;
    private MenuItem raum04;
    private MenuItem raum05;
    private MenuItem raum06;
    private MenuItem raum07;
    private MenuItem raum08;
    private MenuItem raum09;
    private MenuItem raum10;
    private MenuItem raum11;
    private MenuItem raum12;
    private MenuItem raum13;
    private MenuItem raum14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);

        methoden.onCreateFillIn(this,this,5, R.layout.it_team_holen);

        ITTeamHolenAnzeigen("select * from fehlermeldungen where status=\"Offen\";");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter_it_team_holen, menu);

        offen=menu.findItem(R.id.Offen);
        inBearbeitung=menu.findItem(R.id.InBearbeitung);
        fertig=menu.findItem(R.id.Fertig);
        gebH=menu.findItem(R.id.FilterGebaeudeH);
        gebA=menu.findItem(R.id.FilterGebaeudeA);
        gebN=menu.findItem(R.id.FilterGebaeudeN);
        et2=menu.findItem(R.id.FilterEtage2);
        et1=menu.findItem(R.id.FilterEtage1);
        et0=menu.findItem(R.id.FilterEtage0);
        etZ=menu.findItem(R.id.FilterEtageZ);
        etU=menu.findItem(R.id.FilterEtageU);
        raum01=menu.findItem(R.id.FilterRaum01);
        raum02=menu.findItem(R.id.FilterRaum02);
        raum03=menu.findItem(R.id.FilterRaum03);
        raum04=menu.findItem(R.id.FilterRaum04);
        raum05=menu.findItem(R.id.FilterRaum05);
        raum06=menu.findItem(R.id.FilterRaum06);
        raum07=menu.findItem(R.id.FilterRaum07);
        raum08=menu.findItem(R.id.FilterRaum08);
        raum09=menu.findItem(R.id.FilterRaum09);
        raum10=menu.findItem(R.id.FilterRaum10);
        raum11=menu.findItem(R.id.FilterRaum11);
        raum12=menu.findItem(R.id.FilterRaum12);
        raum13=menu.findItem(R.id.FilterRaum13);
        raum14=menu.findItem(R.id.FilterRaum14);

        offen.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.FilterEtage:
            case R.id.FilterGebaeude:
            case R.id.FilterRaum:
            case R.id.FilterStatus:
                return true;

            default:
                if(item.isChecked()){
                    item.setChecked(false);
                    ITTeamHolenAnzeigen(AnfrageGenerieren());
                }
                else {
                    item.setChecked(true);
                    ITTeamHolenAnzeigen(AnfrageGenerieren());
                }
                return true;
        }
    }

    private String AnfrageGenerieren() {
        String anfrage = "select * from fehlermeldungen where ";
        boolean etwMarkiert = false;
        boolean neuBereich = true;

        if(offen.isChecked()){
            /*if(etwMarkiert){
                anfrage+=" OR ";
            }*/
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="status=\"Offen\"";
        }
        if(inBearbeitung.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="status=\"In Bearbeitung\"";
        }
        if(fertig.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="status=\"Fertig\"";
        }
        neuBereich=true;
        if(gebH.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="gebaeude=\"H\"";
        }
        if(gebA.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="gebaeude=\"A\"";
        }
        if(gebN.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="gebaeude=\"N\"";
        }
        neuBereich=true;
        if(et2.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="etage=\"2\"";
        }
        if(et1.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="etage=\"1\"";
        }
        if(et0.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="etage=\"0\"";
        }
        if(etZ.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="etage=\"Z\"";
        }
        if(etU.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="etage=\"U\"";
        }
        neuBereich=true;
        if(raum01.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"01\"";
        }
        if(raum02.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"02\"";
        }
        if(raum03.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"03\"";
        }
        if(raum04.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"04\"";
        }
        if(raum05.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"05\"";
        }
        if(raum06.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"06\"";
        }
        if(raum07.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"07\"";
        }
        if(raum08.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"08\"";
        }
        if(raum09.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"09\"";
        }
        if(raum10.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"10\"";
        }
        if(raum11.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"11\"";
        }
        if(raum12.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"12\"";
        }
        if(raum13.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            etwMarkiert=true;
            neuBereich=false;
            anfrage+="raum=\"13\"";
        }
        if(raum14.isChecked()){
            if(etwMarkiert){
                if(neuBereich){
                    anfrage+=" AND ";
                }
                else {
                    anfrage+=" OR ";
                }
            }
            //neuBereich=false;
            //etwMarkiert=true;
            anfrage+="raum=\"14\"";
        }

        anfrage+=";";

        return anfrage;
    }

    private void ITTeamHolenAnzeigen(final String filter){
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamHolen&request="+filter+"&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(url.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    String data = in.readLine();
                    data = URLDecoder.decode(data,"utf-8");
                    System.out.println(data);
                    in.close();

                    int eintraegeZahl = Integer.parseInt(data.split("/newthing/")[0]);

                    final ViewGroup vg = findViewById(R.id.content_itteam_holen);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vg.removeAllViews();
                        }
                    });
                    final String[] texte = new String[eintraegeZahl];
                    for(int i=0;i<eintraegeZahl;i++){
                        String readLine=data.split("/newthing/")[i+1];
                        texte[i]=readLine;
                        readLine = readLine.replaceAll("//","\n");
                        readLine = readLine.replaceAll("ae","ä");
                        System.out.println(texte[i]);
                        String[] result = texte[i].split("Status: ");
                        final String status = result[1].split("//")[0];

                        final View line = new View(ac);
                        line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,2));
                        line.setBackgroundColor(0xff888888);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vg.addView(line);
                            }
                        });

                        final RelativeLayout rl = new RelativeLayout(ac);
                        TextView tv = new TextView(ac);
                        Button button = new Button(ac);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10,10,0,10);
                        tv.setLayoutParams(params);
                        tv.setText(readLine);
                        rl.addView(tv);
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
                        params.setMargins(0,25,10,0);
                        button.setText(getString(R.string.it_team_holen_change_status));
                        switch (status){
                            case "Offen":
                                button.setTextColor(0xffff0000);
                                break;
                            case "In Bearbeitung":
                                button.setTextColor(0xffFFBF00);
                                break;
                            case "Fertig":
                                button.setTextColor(0xff00ff00);
                                break;
                        }
                        final int finali = i;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String[] grpname = {"Offen","In Bearbeitung","Fertig","Löschen"};

                                LinearLayout ll = new LinearLayout(ac);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                ll.setLayoutParams(lp);
                                ll.setOrientation(LinearLayout.VERTICAL);
                                RadioButton rd1 = new RadioButton(ac);
                                RadioButton rd2 = new RadioButton(ac);
                                RadioButton rd3 = new RadioButton(ac);
                                RadioButton rd4 = new RadioButton(ac);

                                rd1.setText(grpname[0]);
                                rd2.setText(grpname[1]);
                                rd3.setText(grpname[2]);
                                rd4.setText(grpname[3]);

                                rd1.setTextColor(getResources().getColor(Startseite.textColor));
                                rd2.setTextColor(getResources().getColor(Startseite.textColor));
                                rd3.setTextColor(getResources().getColor(Startseite.textColor));
                                rd4.setTextColor(getResources().getColor(Startseite.textColor));

                                switch (status){
                                    case "Offen":
                                        rd1.setChecked(true);
                                        break;
                                    case "In Bearbeitung":
                                        rd2.setChecked(true);
                                        break;
                                    case "Fertig":
                                        rd3.setChecked(true);
                                        break;
                                }

                                ll.addView(new TextView(ac));
                                ll.addView(rd1);
                                ll.addView(rd2);
                                ll.addView(rd3);
                                ll.addView(rd4);

                                AlertDialog.Builder dialog;
                                if(Startseite.theme == R.style.DarkTheme){
                                    dialog = new AlertDialog.Builder(ac,R.style.DarkDialog);
                                }
                                else {
                                    dialog = new AlertDialog.Builder(ac);
                                }

                                dialog.setView(ll);
                                dialog.setTitle("Bitte wähle den Status aus!");
                                dialog.setCancelable(true);

                                final Dialog diag = dialog.create();
                                dialog.show();

                                rd1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        StatusAendern(grpname[0],texte[finali]);
                                        diag.dismiss();
                                    }
                                });
                                rd2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        StatusAendern(grpname[1],texte[finali]);
                                        diag.dismiss();
                                    }
                                });
                                rd3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        StatusAendern(grpname[2],texte[finali]);
                                        diag.dismiss();
                                    }
                                });
                                rd4.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        Loeschen(texte[finali]);
                                        diag.dismiss();
                                    }
                                });

                            }
                        });
                        button.setTextSize(12);
                        button.setLayoutParams(params);
                        rl.addView(button);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vg.addView(rl);
                            }
                        });

                    }
                    if(eintraegeZahl==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tv = new TextView(ac);
                                tv.setText(getString(R.string.it_team_holen_nothing));
                                vg.addView(tv);
                            }
                        });
                    }
                } catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Fehler beim Verbinden zum Server",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ac, Startseite.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Startseite.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_it_team_holen,this);
        return true;
    }

    private void StatusAendern(final String neu, String text){
        final Activity ac = this;

        System.out.println(neu);

        final String[] daten = new String[8];
        String[] results = text.split("//");
        daten[0]=results[0].replace("Datum: ","");
        daten[1]=results[2].replace("Gebaeude: ","");
        daten[2]=results[3].replace("Etage: ","");
        daten[3]=results[4].replace("Raum: ","");
        daten[4]=results[5].replace("Wichtigkeit: ","");
        daten[5]=results[6].replace("Fehler: ","");
        daten[6]=results[7].replace("Beschreibung: ","");
        daten[7]=results[8].replace("Status: ","");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamLoeschen&request=delete from fehlermeldungen where gebaeude=\""+daten[1]+"\" and etage=\""+daten[2]+"\" and raum=\""+daten[3]+"\" and fehler=\""+daten[5]+"\";&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    URL oracle = new URL(url);
                    System.out.println(url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();

                    oracle = new URL("https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamMelden&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+daten[0]+"&gebaeude="+daten[1]+"&etage="+daten[2]+"&raum="+daten[3]+"&wichtigkeit="+daten[4]+"&fehler="+daten[5]+"&beschreibung="+daten[6]+"&status="+neu+"&bearbeitetVon="+Startseite.prefs.getString("loginUsername",""));
                    in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();

                    startActivity(new Intent(ac,ITTeamHolen.class));

                } catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Fehler beim Ändern des Status",Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(ac, Startseite.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }).start();
    }


    private void Loeschen(String text){
        final Activity ac = this;

        final String[] daten = new String[8];
        String[] results = text.split("//");
        daten[0]=results[0].replace("Datum: ","");
        daten[1]=results[2].replace("Gebaeude: ","");
        daten[2]=results[3].replace("Etage: ","");
        daten[3]=results[4].replace("Raum: ","");
        daten[4]=results[5].replace("Wichtigkeit: ","");
        daten[5]=results[6].replace("Fehler: ","");
        daten[6]=results[7].replace("Beschreibung: ","");
        daten[7]=results[8].replace("Status: ","");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=ITTeamLoeschen&request=delete from fehlermeldungen where gebaeude=\""+daten[1]+"\" and etage=\""+daten[2]+"\" and raum=\""+daten[3]+"\" and fehler=\""+daten[5]+"\";&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    URL oracle = new URL(url);
                    System.out.println(url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();

                    startActivity(new Intent(ac,ITTeamHolen.class));

                } catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Fehler beim Löschen",Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(ac, Startseite.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }).start();
    }
}
