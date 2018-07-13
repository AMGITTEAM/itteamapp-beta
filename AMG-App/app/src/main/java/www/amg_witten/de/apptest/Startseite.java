package www.amg_witten.de.apptest;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Startseite extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int login;
    public static String benutzername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startseite_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        try {
            File file = new File(this.getFilesDir(), "Login.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String permission = br.readLine();
            login = Integer.parseInt(permission); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
            benutzername = br.readLine();
            br.close();
        } catch (Exception e){
            e.printStackTrace();
            login=0;
        }
        System.out.println(login);

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,0);

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        int savedVersionCode = prefs.getInt("version_code", -1);

        // Check for first run or upgrade
        if (savedVersionCode == -1) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getTimePNGBase(),"time.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBookPNGBase(), "book.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getGroupPNGBase(),"group.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getUserPNGBase(),"user.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
        } else if (currentVersionCode > savedVersionCode) {
            prefs.edit().putInt("version_code", 0).apply();
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getTimePNGBase(),"time.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBookPNGBase(), "book.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getGroupPNGBase(),"group.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getUserPNGBase(),"user.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
            CreateHTMLIcons.writeToFile(CreateHTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
        }

        // Update the shared preferences with the current version code
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        return methoden.onNavigationItemSelectedFillIn(item,R.id.nav_startseite,this);
    }
}
