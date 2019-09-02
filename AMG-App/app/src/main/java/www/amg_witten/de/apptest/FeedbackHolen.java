package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class FeedbackHolen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,5, R.layout.it_team_holen);

        ITTeamHolenAnzeigen("select * from feedback;");
    }

    private void ITTeamHolenAnzeigen(final String filter){
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=FeedbackHolen&request="+filter+"&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=");
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
                        params.resolveLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        params.setMargins(10,10,0,10);
                        button.setId(View.generateViewId());
                        params.addRule(RelativeLayout.LEFT_OF,button.getId());
                        tv.setLayoutParams(params);
                        tv.setText(readLine);
                        rl.addView(tv);
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.removeRule(RelativeLayout.LEFT_OF);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
                        params.setMargins(0,25,10,0);
                        button.setText(getString(R.string.feedback_holen_loeschen));
                        final int finali = i;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(getString(R.string.feedback_holen_dialog_loeschen))
                                        .setPositiveButton(getString(R.string.feedback_holen_dialog_positive), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Loeschen(texte[finali]);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.feedback_holen_dialog_negative),null)
                                        .setTitle(getString(R.string.feedback_holen_loeschen));
                                builder.create().show();
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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_feedback_holen,this);
        return true;
    }

    private void Loeschen(String text){
        System.out.println(text);
        final Activity ac = this;

        final String[] daten = new String[2];
        String[] results = text.split("//");
        daten[0]=results[0].replace("Typ: ","");
        daten[1]=text.replace("Beschreibung: ","").replace("Typ: "+daten[0]+"//","");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    daten[1]=URLEncoder.encode(daten[1].replaceAll("\"","\"\""),"utf-8");
                    String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=ITTeamLoeschen&request=delete from feedback where type=\""+daten[0]+"\" and description=\""+daten[1]+"\";&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    URL oracle = new URL(url);
                    System.out.println(url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();

                    startActivity(new Intent(ac, FeedbackHolen.class));

                } catch (Exception e){
                    e.printStackTrace();
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
