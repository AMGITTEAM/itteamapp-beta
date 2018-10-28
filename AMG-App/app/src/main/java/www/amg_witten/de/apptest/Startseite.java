package www.amg_witten.de.apptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class Startseite extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int login;
    private static String benutzername;
    public static SharedPreferences prefs;

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

        prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
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
        } else if (currentVersionCode > savedVersionCode) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            if(savedVersionCode<1){
                HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
            }
            if(savedVersionCode<6){
                startActivity(new Intent(this, Login.class));
            }
            if(savedVersionCode<8){
                prefs.edit().putBoolean("vertretungsplanIconsEnabled",true).apply();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.changelog)
                    .setPositiveButton("OK", null)
                    .setTitle("Changelog");
            builder.create().show();
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
