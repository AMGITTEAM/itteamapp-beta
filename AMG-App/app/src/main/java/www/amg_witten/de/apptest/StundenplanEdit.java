package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class StundenplanEdit extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,3,R.layout.stundenplan_edit);

        ((EditText)findViewById(R.id.stundenplan_edit_fach)).setText(getIntent().getExtras().getString("fach").trim());
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
        String lehrer = ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).getText().toString();
        String raum = ((EditText)findViewById(R.id.stundenplan_edit_raum)).getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("fach",fach);
        returnIntent.putExtra("lehrer",lehrer);
        returnIntent.putExtra("raum",raum);
        setResult(0,returnIntent);
        finish();
    }

    public void Loeschen(View view) {
        setResult(1);
        finish();
    }
}
