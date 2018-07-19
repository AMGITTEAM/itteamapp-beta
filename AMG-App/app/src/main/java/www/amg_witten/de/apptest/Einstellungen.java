package www.amg_witten.de.apptest;

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
import android.widget.NumberPicker;

public class Einstellungen extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,901,R.layout.einstellungen);

        final SharedPreferences prefs = getSharedPreferences("Prefs",MODE_PRIVATE);
        String klasseBisher = prefs.getString("klasse","");
        System.out.println(klasseBisher);

        final NumberPicker klasseNr = findViewById(R.id.einstellungen_klasseNr_spinner);
        final NumberPicker klasseEndung = findViewById(R.id.einstellungen_klasseEndung_spinner);
        String[] klassenNr = new String[]{"","05","06","07","08","09","EF","Q1","Q2"};
        final String[] klassenEndung = new String[]{"a","b","c","d"};
        klasseNr.setMaxValue(8);
        klasseNr.setDisplayedValues(klassenNr);

        int position = 0;
        for (int i=0; i<klassenNr.length; i++){
            if(klasseBisher.contains(klassenNr[i])){
                position=i;
                System.out.println(i);
            }
        }
        klasseNr.setValue(position);
        NumberPicker.OnValueChangeListener onValueChangeListenerNr = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String text = picker.getDisplayedValues()[picker.getValue()];
                switch (text) {
                    case "":
                    case "EF":
                    case "Q1":
                    case "Q2":
                        klasseEndung.setValue(0);
                        klasseEndung.setMaxValue(0);
                        klasseEndung.setDisplayedValues(new String[]{" "});
                        klasseEndung.setEnabled(false);
                        prefs.edit().putString("klasse", text).apply();
                        System.out.println(text);
                        break;
                    default:
                        klasseEndung.setDisplayedValues(klassenEndung);
                        klasseEndung.setMaxValue(3);
                        klasseEndung.setEnabled(true);
                        String lastKlasse = prefs.getString("klasse", "");
                        if (lastKlasse.equals("")) {
                            System.out.println(text);
                            prefs.edit().putString("klasse", text + klassenEndung[klasseEndung.getValue()]).apply();
                        } else {
                            System.out.println(text + prefs.getString("klasse", "").substring(2));
                            prefs.edit().putString("klasse", text + prefs.getString("klasse", "").substring(2)).apply();
                            System.out.println(text + prefs.getString("klasse", "").substring(2));
                        }
                        break;
                }
            }
        };
        klasseNr.setOnValueChangedListener(onValueChangeListenerNr);

        klasseEndung.setMaxValue(3);
        klasseEndung.setDisplayedValues(klassenEndung);

        position = 0;
        for (int i=0; i<klassenEndung.length; i++){
            if(klasseBisher.contains(klassenEndung[i])){
                position=i;
                System.out.println(i);
            }
        }
        klasseEndung.setValue(position);
        klasseEndung.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String text = picker.getDisplayedValues()[picker.getValue()];
                if (!text.equals("")) {
                    prefs.edit().putString("klasse",prefs.getString("klasse","").substring(0,2)+text).apply();
                    System.out.println(prefs.getString("klasse","").substring(0,2)+text);
                }
            }
        });
        onValueChangeListenerNr.onValueChange(klasseNr,0,position);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }
}
