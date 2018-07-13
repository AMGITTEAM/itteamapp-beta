package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

public class Login extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,900);

        if(Startseite.login>0){
            File file = new File(this.getFilesDir(),"Login.txt");
            boolean erfolg = file.delete();
            if(erfolg){
                Toast.makeText(this,"Logout erfolgreich",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Logout fehlgeschlagen",Toast.LENGTH_LONG).show();
            }
            Startseite.login=0;
            Intent intent = new Intent(this, Startseite.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        final Activity ac = this;
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Verbinde...");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL oracle = new URL("http://amgitt.de:8080/AMGAppServlet/");
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                    in.close();
                    progress.dismiss();
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            System.out.println("ERROR");
                            e.printStackTrace();
                            Toast.makeText(ac,"Fehler beim Verbinden zum Server.",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ac, Startseite.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        return methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
    }

    public void LoginAction(View view) {
        final String benutzername = ((EditText)findViewById(R.id.benutzername)).getText().toString();
        final String passwort = ((EditText)findViewById(R.id.passwort)).getText().toString().hashCode() + "";
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://amgitt.de:8080/AMGAppServlet/amgapp?requestType=Login&request=select * from login where benutzername=\""+benutzername+"\" and passwort=\""+passwort+"\";&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    System.out.println(url);
                    URL oracle = new URL(url);
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

                    boolean end = false;

                    while (!end){
                        if ((in.readLine()).equals("<body>")){
                            end=true;
                        }
                    }
                    in.readLine();
                    String intAnmeldungErfolgreich = in.readLine();
                    in.close();
                    System.out.println(intAnmeldungErfolgreich);
                    int rechthoehe = Integer.parseInt(intAnmeldungErfolgreich);
                    System.out.println("Gelesen");
                    Startseite.login = rechthoehe;

                    File file = new File(ac.getFilesDir(),"Login.txt");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(""+rechthoehe+"\n");
                    bw.write(benutzername);
                    bw.flush();
                    bw.close();

                    System.out.println(Startseite.login);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Login erfolgreich",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ac, Startseite.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,"Fehler beim Verbinden zum Server",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ac, Startseite.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).start();

    }
}
