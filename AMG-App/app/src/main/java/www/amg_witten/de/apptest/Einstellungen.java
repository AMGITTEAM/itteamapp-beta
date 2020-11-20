package www.amg_witten.de.apptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.Calendar;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

public class Einstellungen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;
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
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        methoden.onCreateFillIn(this,this,901,R.layout.einstellungen);

        final SharedPreferences prefs = getSharedPreferences("Prefs",MODE_PRIVATE);
        String klasseBisher = prefs.getString("klasse","");

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
                        break;
                    default:
                        klasseEndung.setDisplayedValues(klassenEndung);
                        klasseEndung.setMaxValue(3);
                        klasseEndung.setEnabled(true);
                        String lastKlasse = prefs.getString("klasse", "");
                        if (lastKlasse.equals("")) {
                            prefs.edit().putString("klasse", text + klassenEndung[klasseEndung.getValue()]).apply();
                        } else {
                            prefs.edit().putString("klasse", text + prefs.getString("klasse", "").substring(2)).apply();
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
            }
        }
        klasseEndung.setValue(position);
        klasseEndung.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String text = picker.getDisplayedValues()[picker.getValue()];
                if (!text.equals("")) {
                    prefs.edit().putString("klasse",prefs.getString("klasse","").substring(0,2)+text).apply();
                }
            }
        });
        onValueChangeListenerNr.onValueChange(klasseNr,0,position);

        if(Startseite.theme == R.style.DarkTheme){
            setNumberPickerTextColor(klasseNr,getResources().getColor(R.color.darkTextColor));
            setNumberPickerTextColor(klasseEndung,getResources().getColor(R.color.darkTextColor));
        }

        if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
            findViewById(R.id.einstellungen_darkDesign_layout).setVisibility(View.GONE);
        }
        else {
            final Switch darkDesign = findViewById(R.id.einstellungen_darkDesign_switch);
            darkDesign.setChecked(Startseite.prefs.getBoolean("dunklesDesign",false));
            darkDesign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    prefs.edit().putBoolean("dunklesDesign",isChecked).apply();
                    startActivity(new Intent(context, Einstellungen.class));
                }
            });
        }

        final Switch kalender = findViewById(R.id.einstellungen_kalenderAusblenden_switch);
        kalender.setChecked(!Startseite.prefs.getBoolean("kalenderAusblenden",false));
        kalender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("kalenderAusblenden",!isChecked).apply();
            }
        });


        RelativeLayout notificationLayout = findViewById(R.id.einstellungen_notification_layout);
        RelativeLayout notificationTimeLayout = findViewById(R.id.einstellungen_notificationtime_layout);
        RelativeLayout colorOwn = findViewById(R.id.einstellungen_colorOwn_layout);
        RelativeLayout colorUnter = findViewById(R.id.einstellungen_colorUnter_layout);
        RelativeLayout colorMitte = findViewById(R.id.einstellungen_colorMitte_layout);
        RelativeLayout colorOber = findViewById(R.id.einstellungen_colorOber_layout);
        RelativeLayout iconsVertretungsplanLayout = findViewById(R.id.einstellungen_vertretungsplan_icons_layout);
        final RelativeLayout navLayout = findViewById(R.id.einstellungen_nav_layout);
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,R.style.DarkTimePickerDialog,listener,prefs.getInt("notificationTimeHour",7),prefs.getInt("notificationTimeMinute",0),true);
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
                Switch iconsVertretungsplanSwitch = findViewById(R.id.einstellungen_vertretungsplan_icons_switch);
                iconsVertretungsplanLayout.setVisibility(RelativeLayout.VISIBLE);
                iconsVertretungsplanSwitch.setChecked(prefs.getBoolean("vertretungsplanIconsEnabled",false));
                iconsVertretungsplanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        prefs.edit().putBoolean("vertretungsplanIconsEnabled",isChecked).apply();
                    }
                });
                Spinner spinner = findViewById(R.id.einstellungen_nav_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.einstellungen_nav_options, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(Startseite.navigationType);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position != Startseite.navigationType){
                            prefs.edit().putInt("navigationType",position).apply();
                            Startseite.navigationType = position;
                            Startseite.requiresRecreate = true;
                            recreate();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
            else {
                colorOwn.setVisibility(RelativeLayout.GONE);
                colorUnter.setVisibility(RelativeLayout.GONE);
                colorMitte.setVisibility(RelativeLayout.GONE);
                colorOber.setVisibility(RelativeLayout.GONE);
                iconsVertretungsplanLayout.setVisibility(RelativeLayout.GONE);
                navLayout.setVisibility(RelativeLayout.GONE);
            }
        }
        else {
            notificationLayout.setVisibility(RelativeLayout.GONE);
            notificationTimeLayout.setVisibility(RelativeLayout.GONE);
            colorOwn.setVisibility(RelativeLayout.GONE);
            colorUnter.setVisibility(RelativeLayout.GONE);
            colorMitte.setVisibility(RelativeLayout.GONE);
            colorOber.setVisibility(RelativeLayout.GONE);
            navLayout.setVisibility(RelativeLayout.GONE);
        }

        Button changelogButton = findViewById(R.id.einstellungen_changelog_button);
        changelogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if(Startseite.theme == R.style.DarkTheme){
                    builder = new AlertDialog.Builder(context,R.style.DarkDialog);
                }
                else {
                    builder = new AlertDialog.Builder(context);
                }

                TextView textView = new TextView(context);
                textView.setMovementMethod(new ScrollingMovementMethod());
                textView.setText(R.string.changelog_all);
                textView.setTextColor(getResources().getColor(Startseite.textColor));
                float dpi = getResources().getDisplayMetrics().density;
                textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
                builder.setView(textView);
                builder.setPositiveButton("OK", null)
                        .setTitle("Changelog");
                builder.create().show();
            }
        });

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

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColo", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColo", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColo", e);
                }
            }
        }
        return false;
    }

    private void showColorDialog(int color, String tag, int originalColor){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment;
        if(Startseite.theme == R.style.DarkTheme){
            fragment = AmbilWarnaDialogFragment.newInstance(color, R.style.DarkDialog, originalColor);
        }
        else {
            fragment = AmbilWarnaDialogFragment.newInstance(color, android.R.style.Theme_Dialog, originalColor);
        }
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
        }
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_einstellungen,this);
        return true;
    }
}
