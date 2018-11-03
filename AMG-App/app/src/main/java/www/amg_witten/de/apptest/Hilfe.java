package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class Hilfe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,902,R.layout.hilfe);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_feedback,this);
        return true;
    }

    public void regenerateVertretungsplanIcons(View view) {
        HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
    }

    public void startFeedback(View view) {
        Intent intent = new Intent(this, Feedback.class);
        startActivity(intent);
    }
}
