package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class StundenplanEdit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;

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
        methoden.onCreateFillIn(this,this,3,R.layout.stundenplan_edit);

        ((EditText)findViewById(R.id.stundenplan_edit_fach)).setText(getIntent().getExtras().getString("fach").trim());
        ((EditText)findViewById(R.id.stundenplan_edit_fachName)).setText(getIntent().getExtras().getString("fachName").trim());
        ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).setText(getIntent().getExtras().getString("lehrer").trim());
        ((EditText)findViewById(R.id.stundenplan_edit_raum)).setText(getIntent().getExtras().getString("raum").trim());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }

    public void Fertig(View view) {
        String fach = ((EditText)findViewById(R.id.stundenplan_edit_fach)).getText().toString();
        String fachName = ((EditText)findViewById(R.id.stundenplan_edit_fachName)).getText().toString();
        String lehrer = ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).getText().toString();
        String raum = ((EditText)findViewById(R.id.stundenplan_edit_raum)).getText().toString();
        boolean fehler=false;
        if(fach.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_fach));
        }
        if(fachName.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_fachName));
        }
        if(lehrer.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_lehrer));
        }
        if(raum.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_raum));
        }
        if(!fehler) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("fach",fach);
            returnIntent.putExtra("lehrer",lehrer);
            returnIntent.putExtra("raum",raum);
            returnIntent.putExtra("fachName",fachName);
            setResult(0,returnIntent);
            finish();
        }
    }

    public void blink(final EditText view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)view.getParent()).setBackground(ContextCompat.getDrawable(context,R.drawable.border));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)view.getParent()).setBackground(null);
                    }
                });
            }
        }).start();
    }

    public void Loeschen(View view) {
        setResult(1);
        finish();
    }
}
