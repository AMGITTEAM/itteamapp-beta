package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.NumberPicker;

public class ITTeamSenden extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static boolean gebFehler;
    public static String gebaeude;
    public static String etage;
    public static String raum;
    public static String fehler;
    public static String wichtigkeit;
    public static String beschreibung;
    public static boolean ueberschreiben=true;

    private NumberPicker gebaeudePicker;
    private NumberPicker etagePicker;
    private NumberPicker raumPicker;

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
        methoden.onCreateFillIn(this,this,3,R.layout.it_team_senden);

        gebFehler=false;

        gebaeudePicker = findViewById(R.id.gebaeude);
        etagePicker = findViewById(R.id.etage);
        raumPicker = findViewById(R.id.raum);

        gebaeudePicker.setDisplayedValues(getResources().getStringArray(R.array.it_team_melden_gebaeude_array));
        gebaeudePicker.setMaxValue(getResources().getStringArray(R.array.it_team_melden_gebaeude_array).length-1);
        etagePicker.setDisplayedValues(getResources().getStringArray(R.array.it_team_melden_etage_array));
        etagePicker.setMaxValue(getResources().getStringArray(R.array.it_team_melden_etage_array).length-1);
        raumPicker.setDisplayedValues(getResources().getStringArray(R.array.it_team_melden_raum_array));
        raumPicker.setMaxValue(getResources().getStringArray(R.array.it_team_melden_raum_array).length-1);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_it_team_senden,this);
        return true;
    }

    public void fertig(View view) {
        gebaeude = getResources().getStringArray(R.array.it_team_melden_gebaeude_array)[gebaeudePicker.getValue()];
        etage = getResources().getStringArray(R.array.it_team_melden_etage_array)[etagePicker.getValue()];
        raum = getResources().getStringArray(R.array.it_team_melden_raum_array)[raumPicker.getValue()];

        startActivity(new Intent(this,ITTeamSenden4.class));
    }

    public void ohne(View view){
        gebaeude="Ohne";
        etage="Ohne";
        raum="Ohne";
        startActivity(new Intent(this,ITTeamSenden4.class));
    }
}
