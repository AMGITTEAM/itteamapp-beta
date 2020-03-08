package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchwarzesBrett extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);

        methoden.onCreateFillIn(this,this,3, R.layout.it_team_holen);

        String klasse = Startseite.prefs.getString("klasse","");
        eintraegeAnzeigen("select * from schwarzesBrett where eingeblendet=\"true\" AND stufe=\""+klasse+"\";");
    }

    private void eintraegeAnzeigen(final String filter){
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://amgitt.de/AMGAppServlet/amgapp?requestType=SchwarzesBrett&request="+filter+"&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=");
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

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

                        String endString = readLine.split("\nEnddatum: ")[1].split("\n")[0];
                        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endString);
                        Date currentDate = new Date();

                        if(endDate.before(currentDate)){
                            continue;
                        }

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

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10,10,10,10);
                        tv.setLayoutParams(params);
                        tv.setTextSize(20);
                        tv.setText(readLine.split("\nTitel: \"")[1].split("\"\nInhalt: ")[0]);
                        tv.setId(View.generateViewId());
                        rl.addView(tv);

                        TextView tv2 = new TextView(ac);
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10,10,10,10);
                        params.addRule(RelativeLayout.BELOW,tv.getId());
                        tv2.setLayoutParams(params);
                        String content = readLine.split("\nInhalt: \"")[1].split("\"\nDatum: ")[0];
                        String preview;
                        boolean doesContainNewlineInFirstHundretChars = false;
                        boolean longerThanAllowed = false;
                        try {
                            doesContainNewlineInFirstHundretChars = content.substring(0,100).contains("\n");
                        }
                        catch (StringIndexOutOfBoundsException e){
                            doesContainNewlineInFirstHundretChars = content.contains("\n");
                            longerThanAllowed=true;
                        }
                        if(doesContainNewlineInFirstHundretChars){
                            preview=content.split("\n")[0]+"\n...";
                        }
                        else {
                            if(!longerThanAllowed) {
                                preview=content.substring(0,100)+"...";
                            }
                            else {
                                preview=content;
                            }
                        }

                        String previewString = preview.replaceAll("\n","<br/>");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            tv2.setText(Html.fromHtml(previewString, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            tv2.setText(Html.fromHtml(previewString));
                        }

                        rl.addView(tv2);

                        final String finalReadLine = readLine;
                        rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ac.getApplicationContext(),SchwarzesBrettDetails.class);
                                intent.putExtra("readLine",finalReadLine);
                                startActivity(intent);
                            }
                        });

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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_schwarzes_brett,this);
        return true;
    }
}
