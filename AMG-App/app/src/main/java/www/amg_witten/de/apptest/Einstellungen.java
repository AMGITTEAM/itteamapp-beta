package www.amg_witten.de.apptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

public class Einstellungen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;

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


        RelativeLayout notificationLayout = findViewById(R.id.einstellungen_notification_layout);
        RelativeLayout notificationTimeLayout = findViewById(R.id.einstellungen_notificationtime_layout);
        RelativeLayout colorOwn = findViewById(R.id.einstellungen_colorOwn_layout);
        RelativeLayout colorUnter = findViewById(R.id.einstellungen_colorUnter_layout);
        RelativeLayout colorMitte = findViewById(R.id.einstellungen_colorMitte_layout);
        RelativeLayout colorOber = findViewById(R.id.einstellungen_colorOber_layout);
        if(Startseite.login>0){
            Switch notificationSwitch = findViewById(R.id.einstellungen_notification_switch);
            notificationLayout.setVisibility(RelativeLayout.VISIBLE);
            notificationTimeLayout.setVisibility(RelativeLayout.VISIBLE);
            notificationSwitch.setChecked(prefs.getBoolean("notificationEnabled",false));
            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    prefs.edit().putBoolean("notificationEnabled",isChecked).apply();
                    if(isChecked){
                        Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, prefs.getInt("notificationTimeHour",7));
                        calendar.set(Calendar.MINUTE, prefs.getInt("notificationTimeMinute",0));
                        calendar.set(Calendar.SECOND, 1);

                        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                    else {
                        Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        manager.cancel(pendingIntent);
                    }
                }
            });

            Button notificationTimeBearb = findViewById(R.id.einstellungen_notificationtime_button);
            notificationTimeBearb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener(){
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            prefs.edit().putInt("notificationTimeHour",hourOfDay).putInt("notificationTimeMinute",minute).apply();

                            Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            manager.cancel(pendingIntent);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 1);

                            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,listener,prefs.getInt("notificationTimeHour",7),prefs.getInt("notificationTimeMinute",0),true);
                    timePickerDialog.show();
                }
            });
            if(prefs.getBoolean("all_settings",false)){
                colorOwn.setVisibility(RelativeLayout.VISIBLE);
                colorUnter.setVisibility(RelativeLayout.VISIBLE);
                colorMitte.setVisibility(RelativeLayout.VISIBLE);
                colorOber.setVisibility(RelativeLayout.VISIBLE);
                findViewById(R.id.einstellungen_colorOwn_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorDialog(Color.parseColor(prefs.getString("vertretungEigeneKlasseFarbe","#FF0000")),"vertretungEigeneKlasseFarbe",Color.parseColor("#FF0000"));
                    }
                });
                findViewById(R.id.einstellungen_colorUnter_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorDialog(Color.parseColor(prefs.getString("vertretungUnterstufeFarbe","#4aa3df")),"vertretungUnterstufeFarbe",Color.parseColor("#4aa3df"));
                    }
                });
                findViewById(R.id.einstellungen_colorMitte_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorDialog(Color.parseColor(prefs.getString("vertretungMittelstufeFarbe","#3498db")),"vertretungMittelstufeFarbe",Color.parseColor("#3498db"));
                    }
                });
                findViewById(R.id.einstellungen_colorOber_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorDialog(Color.parseColor(prefs.getString("vertretungOberstufeFarbe","#258cd1")),"vertretungOberstufeFarbe",Color.parseColor("#258cd1"));
                    }
                });
            }
            else {
                colorOwn.setVisibility(RelativeLayout.GONE);
                colorUnter.setVisibility(RelativeLayout.GONE);
                colorMitte.setVisibility(RelativeLayout.GONE);
                colorOber.setVisibility(RelativeLayout.GONE);
            }
        }
        else {
            notificationLayout.setVisibility(RelativeLayout.GONE);
            notificationTimeLayout.setVisibility(RelativeLayout.GONE);
            colorOwn.setVisibility(RelativeLayout.GONE);
            colorUnter.setVisibility(RelativeLayout.GONE);
            colorMitte.setVisibility(RelativeLayout.GONE);
            colorOber.setVisibility(RelativeLayout.GONE);
        }

        CheckBox allSettingsCheckbox = findViewById(R.id.einstellungen_all_settings_checkbox);
        allSettingsCheckbox.setChecked(prefs.getBoolean("all_settings",false));

        allSettingsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("all_settings",true).apply();
                }
                else {
                    prefs.edit().putBoolean("all_settings",false).apply();
                }
                startActivity(new Intent(context, Einstellungen.class));
            }
        });
    }

    private void showColorDialog(int color, String tag, int originalColor){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(color, android.R.style.Theme_Dialog, originalColor);
        fragment.setOnAmbilWarnaListener(new onAmbilWarnaListener());

        fragment.show(ft, tag);
    }

    private class onAmbilWarnaListener implements OnAmbilWarnaListener {
        @Override
        public void onCancel(AmbilWarnaDialogFragment dialogFragment) {

        }

        @Override
        public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {
            Startseite.prefs.edit().putString(dialogFragment.getTag(),String.format("#%06X", (0xFFFFFF & color))).apply();
            System.out.println(String.format("#%06X", (0xFFFFFF & color)));
        }
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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_einstellungen,this);
        return true;
    }
}
