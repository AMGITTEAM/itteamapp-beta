package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;

public class Login extends AppCompatActivity
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
        final Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        methoden.onCreateFillIn(this,this,900,R.layout.login);

        if(Startseite.login>0){
            final Login context = this;
            AlertDialog.Builder builder;
            if(Startseite.theme == R.style.DarkTheme) {
                builder = new AlertDialog.Builder(this, R.style.DarkDialog);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            AlertDialog dialog = builder.setMessage("Möchtest du dich wirklich ausloggen?")
                    .setTitle("Logout")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            prefs.edit().putInt("login",0).apply(); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
                            prefs.edit().putString("loginUsername","").apply();
                            prefs.edit().putString("loginPassword","").apply();
                            prefs.edit().putString("passwordVertretungsplanSchueler","").apply();
                            Toast.makeText(context,getString(R.string.login_logout_success),Toast.LENGTH_LONG).show();
                            Startseite.login=0;
                            Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            manager.cancel(pendingIntent);
                            prefs.edit().putBoolean("notificationEnabled",false).apply();

                            methoden.onCreateFillIn(context,context,900,R.layout.login);
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .create();
            dialog.show();
            return;
        }

        final Activity ac = this;
        final ProgressDialog progress = new ProgressDialog(this,R.style.DarkDialog);
        progress.setTitle(getString(R.string.login_process_connecting));
        progress.setMessage(getString(R.string.login_process_wait));
        progress.setCancelable(false);
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL oracle = new URL("https://amgitt.de/AMGAppServlet/");
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                    in.close();
                    progress.dismiss();
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            e.printStackTrace();
                            Toast.makeText(ac,getString(R.string.login_connection_error),Toast.LENGTH_LONG).show();
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
        if(Methoden.onBackPressedFillIn(this))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }

    public void LoginAction(View view) {
        final Context context = this;
        final String benutzername = ((EditText)findViewById(R.id.benutzername)).getText().toString();
        final String passwort = ((EditText)findViewById(R.id.passwort)).getText().toString().hashCode() + "";
        final Activity ac=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=Login&request=&username="+benutzername+"&password="+passwort+"&datum=&gebaeude=&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
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
                    String data = in.readLine();
                    data = URLDecoder.decode(data,"utf-8");
                    System.out.println(data);
                    String intAnmeldungErfolgreich = data.split("//")[0];
                    String passwordVertretungsplanSchueler = data.split("//")[1];
                    in.close();
                    System.out.println(intAnmeldungErfolgreich);
                    int rechthoehe = Integer.parseInt(intAnmeldungErfolgreich);
                    if(rechthoehe==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ac,getString(R.string.login_wrong_password),Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    Startseite.login = rechthoehe;

                    final SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    prefs.edit().putInt("login",rechthoehe).apply(); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
                    prefs.edit().putString("loginUsername",benutzername).apply();
                    prefs.edit().putString("loginPassword",passwort).apply();
                    prefs.edit().putString("passwordVertretungsplanSchueler",passwordVertretungsplanSchueler).apply();

                    System.out.println(Startseite.login);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.login_success),Toast.LENGTH_LONG).show();

                            Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, 7);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 1);

                            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent);
                            prefs.edit().putBoolean("notificationEnabled",true).apply();
                            prefs.edit().putInt("notificationTimeHour",7).putInt("notificationTimeMinute",0).apply();
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
                            Toast.makeText(ac,getString(R.string.login_connection_error),Toast.LENGTH_LONG).show();
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
