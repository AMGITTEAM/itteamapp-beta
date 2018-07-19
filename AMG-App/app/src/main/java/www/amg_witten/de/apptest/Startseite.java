package www.amg_witten.de.apptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Calendar;

public class Startseite extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int login;
    public static String benutzername;

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

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        login = prefs.getInt("login",0); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
        benutzername = prefs.getString("loginUsername","");
        System.out.println(login);

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,0, R.layout.startseite);

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        int savedVersionCode = prefs.getInt("version_code", -1);

        // Check for first run or upgrade
        if (savedVersionCode == -1) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);

            Intent alarmIntent = new Intent(this, NotifyVertretungsplan.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        } else if (currentVersionCode > savedVersionCode) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            System.exit(0);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_startseite,this);
        return true;
    }
}
