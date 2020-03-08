package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Stundenplan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private final Context context = this;
    private static boolean transistioning = false;
    private static boolean bearbeiten=false;

    private static String wochentagPicking;
    private static int stundePicking;

    private static int lastSelected=0;
    private static TabLayout.Tab lastTab = null;

    private static VertretungModelArrayModel eigeneKlasseHeute = null;
    private static VertretungModelArrayModel eigeneKlasseFolgetag = null;
    private static int noOfDayOfTheWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);

        methoden.onCreateFillIn(this,this,2,R.layout.stundenplan_activity);

        tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.stundenplan_mo_lang)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.stundenplan_di_kurz)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.stundenplan_mi_kurz)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.stundenplan_do_kurz)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.stundenplan_fr_kurz)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if(tab!=null){
            tab.select();
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(!transistioning){
                    transistioning=true;
                    mViewPager.setCurrentItem(tab.getPosition());
                    transistioning=false;
                }
                LinearLayout layout = ((LinearLayout)((LinearLayout)tabLayout.getChildAt(0)).getChildAt(lastSelected));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)layout.getLayoutParams();
                layoutParams.weight=1.0f;
                layout.setLayoutParams(layoutParams);

                layout = ((LinearLayout)((LinearLayout)tabLayout.getChildAt(0)).getChildAt(tab.getPosition()));
                layoutParams = (LinearLayout.LayoutParams)layout.getLayoutParams();
                layoutParams.weight=2.0f;
                layout.setLayoutParams(layoutParams);

                lastTab.setText(getWochentagAbk(lastSelected+1,context));

                tab.setText(getWochentag(tab.getPosition()+1,context));

                lastTab=tab;
                lastSelected=tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {this.onTabSelected(tab);}
        });
        LinearLayout layout = ((LinearLayout)((LinearLayout)tabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)layout.getLayoutParams();
        layoutParams.weight=2.0f;
        layout.setLayoutParams(layoutParams);

        lastTab = tabLayout.getTabAt(0);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageSelected(int i) {
                if(!transistioning){
                    transistioning=true;
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if(tab!=null){
                        tab.select();
                    }
                    transistioning=false;
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        transistioning=false;
        bearbeiten=false;

        final Activity thise = this;

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(day==1){
            noOfDayOfTheWeek=7;
        }
        else {
            noOfDayOfTheWeek=day-1;
        }

        if (noOfDayOfTheWeek > 5) {
            noOfDayOfTheWeek=1;
        }
        TabLayout.Tab newTab = tabLayout.getTabAt(noOfDayOfTheWeek-1);
        if(newTab!=null){
            newTab.select();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> urlEndings = new ArrayList<>();
                List<String> tables = new ArrayList<>();
                final List<String> klassen = new ArrayList<>();
                final List<String> realEintraege = new ArrayList<>();
                final List<VertretungModel> vertretungModels = new ArrayList<>();
                List<VertretungModel> fertigeMulti = new ArrayList<>();
                final List<VertretungModelArrayModel> data = new ArrayList<>();
                final List<String> fertigeKlassen = new ArrayList<>();

                Looper.prepare();
                final ProgressDialog pDialog;
                if(Startseite.theme == R.style.DarkTheme){
                    pDialog = new ProgressDialog(thise,R.style.DarkDialog);
                }
                else {
                    pDialog = new ProgressDialog(thise);
                }
                try {
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setTitle(getString(R.string.vertretungsplan_dialog_title));
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_counting));
                            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            pDialog.setProgress(0);
                            pDialog.show();
                        }
                    });

                    Authenticator.setDefault(new MyAuthenticator(thise));
                    urlEndings.add("001.htm");
                    String main = "http://sus.amg-witten.de/Heute/";

                    Vertretungsplan.getAllEndings(main,urlEndings);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(urlEndings.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_downloading));
                        }
                    });

                    Vertretungsplan.getTablesWithProcess(main,urlEndings,tables,pDialog);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(urlEndings.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_reading));
                        }
                    });

                    Vertretungsplan.getKlassenListWithProcess(tables,klassen,pDialog);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(klassen.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_checking));
                        }
                    });

                    Vertretungsplan.getOnlyRealKlassenListWithProcess(tables,realEintraege,pDialog);

                    int i=0;

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(realEintraege.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_extracting));
                        }
                    });

                    for(String s : realEintraege){
                        i++;
                        Vertretungsplan.tryMatcher(s,fertigeMulti,vertretungModels);
                        pDialog.setProgress(i);
                    }
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(klassen.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_compiling));

                            Vertretungsplan.parseKlassenWithProcess(klassen,fertigeKlassen,vertretungModels,data,pDialog);

                            includeVertretungsplanHeuteInViews(data);

                            pDialog.hide();
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.hide();
                        }
                    });
                }
                final List<String> urlEndingsFolgetag = new ArrayList<>();
                List<String> tablesFolgetag = new ArrayList<>();
                final List<String> klassenFolgetag = new ArrayList<>();
                final List<String> realEintraegeFolgetag = new ArrayList<>();
                final List<VertretungModel> vertretungModelsFolgetag = new ArrayList<>();
                List<VertretungModel> fertigeMultiFolgetag = new ArrayList<>();
                final List<VertretungModelArrayModel> dataFolgetag = new ArrayList<>();
                final List<String> fertigeKlassenFolgetag = new ArrayList<>();
                try {
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setTitle(getString(R.string.vertretungsplan_dialog_title));
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_counting));
                            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            pDialog.setProgress(0);
                            pDialog.show();
                        }
                    });

                    Authenticator.setDefault(new MyAuthenticator(thise));
                    urlEndingsFolgetag.add("001.htm");
                    String main = "http://sus.amg-witten.de/Folgetag/";

                    Vertretungsplan.getAllEndings(main,urlEndingsFolgetag);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(urlEndingsFolgetag.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_downloading));
                        }
                    });

                    Vertretungsplan.getTablesWithProcess(main,urlEndingsFolgetag,tablesFolgetag,pDialog);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(urlEndingsFolgetag.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_reading));
                        }
                    });

                    Vertretungsplan.getKlassenListWithProcess(tablesFolgetag,klassenFolgetag,pDialog);

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(klassenFolgetag.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_checking));
                        }
                    });

                    Vertretungsplan.getOnlyRealKlassenListWithProcess(tablesFolgetag,realEintraegeFolgetag,pDialog);

                    int i=0;

                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(realEintraegeFolgetag.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_extracting));
                        }
                    });

                    for(String s : realEintraegeFolgetag){
                        i++;
                        Vertretungsplan.tryMatcher(s,fertigeMultiFolgetag,vertretungModelsFolgetag);
                        pDialog.setProgress(i);
                    }
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setMax(klassenFolgetag.size());
                            pDialog.setMessage(getString(R.string.vertretungsplan_dialog_compiling));

                            Vertretungsplan.parseKlassenWithProcess(klassenFolgetag,fertigeKlassenFolgetag,vertretungModelsFolgetag,dataFolgetag,pDialog);

                            includeVertretungsplanFolgetagInViews(dataFolgetag);

                            pDialog.hide();
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                    thise.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.hide();
                        }
                    });
                }
            }
        }).start();


        String klasse = Startseite.prefs.getString("klasse","");
        if(klasse.equals("EF")||klasse.equals("Q1")||klasse.equals("Q2")){
            if(!Startseite.prefs.getBoolean("splantutorial_shown",false)){
                findViewById(R.id.stundenplan_darken_view).setVisibility(View.VISIBLE);
                findViewById(R.id.stundenplan_darken_view).setAlpha(0.6f);
                final ShowcaseView view = new ShowcaseView.Builder(this)
                        .setTarget(new ViewTarget(mViewPager))
                        .withMaterialShowcase()
                        .setContentTitle("Kurssprecher")
                        .setContentText("Um den Kurssprecher für das jeweilige Fach anzuzeigen, tippe es an!")
                        .hideOnTouchOutside()
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(view.isShowing()){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.stundenplan_darken_view).setAlpha(0f);
                                findViewById(R.id.stundenplan_darken_view).setVisibility(View.GONE);
                            }
                        });
                        Startseite.prefs.edit().putBoolean("splantutorial_shown",true).apply();
                    }
                }).start();
            }
        }
    }

    private void includeVertretungsplanHeuteInViews(List<VertretungModelArrayModel> data) {
        for(VertretungModelArrayModel model : data){
            if(model.getKlasse().equals(Startseite.prefs.getString("klasse",""))){
                eigeneKlasseHeute=model;
            }
        }
        if(eigeneKlasseHeute!=null){
            mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
            mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
        }
    }

    private void includeVertretungsplanFolgetagInViews(List<VertretungModelArrayModel> data) {
        for(VertretungModelArrayModel model : data){
            if(model.getKlasse().equals(Startseite.prefs.getString("klasse",""))){
                eigeneKlasseFolgetag=model;
            }
        }
        if(eigeneKlasseFolgetag!=null){
            mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
            mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stundenplan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            bearbeiten=!item.isChecked();
            mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
            mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
            item.setChecked(!item.isChecked());
            return true;
        }
        else if(id == R.id.stundenplan_komplett_loeschen){
            AlertDialog.Builder builder;
            if(Startseite.theme == R.style.DarkTheme){
                builder = new AlertDialog.Builder(this,R.style.DarkDialog);
            }
            else {
                builder = new AlertDialog.Builder(this);
            }
            TextView textView = new TextView(this);
            textView.setText(R.string.stundenplan_ask_deleteAll);
            textView.setTextColor(getResources().getColor(Startseite.textColor));
            float dpi = getResources().getDisplayMetrics().density;
            textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
            builder.setView(textView);
            builder.setPositiveButton(getString(R.string.stundenplan_ask_deleteAll_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Startseite.prefs.edit().remove("stundenplanMontag").apply();
                            Startseite.prefs.edit().remove("stundenplanDienstag").apply();
                            Startseite.prefs.edit().remove("stundenplanMittwoch").apply();
                            Startseite.prefs.edit().remove("stundenplanDonnerstag").apply();
                            Startseite.prefs.edit().remove("stundenplanFreitag").apply();
                            mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
                            mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
                        }
                    })
                    .setNegativeButton(getString(R.string.stundenplan_ask_deleteAll_negative),null)
                    .setTitle(getString(R.string.stundenplan_ask_deleteAll_title));
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void NewStunde(View view) {
        wochentagPicking = (String)view.getTag();
        stundePicking=0;
        showEditStunde("","","","");
    }

    public void EditStunde(View view){
        wochentagPicking = getWochentag(tabLayout.getSelectedTabPosition()+1,this);
        stundePicking = Integer.parseInt((String) view.getTag());
        CustomListAdapter.ViewHolder holder = (CustomListAdapter.ViewHolder)((ConstraintLayout)view.getParent()).getTag();
        showEditStunde(holder.fachID, holder.raumText, holder.lehrerText, holder.fachNameText);
    }

    private void Loeschen(){
        Set<String> stundenplan = loadStundenplanOrdered(wochentagPicking);
        String[] array;
        int i=0;
        if(stundenplan==null){
            return;
        }
        if(stundenplan.size()==stundePicking){
            array = new String[stundenplan.size()-1];
            for(String stunde:stundenplan){
                if(!(Integer.parseInt(new StundenplanEintragModel(stunde).stunde)==stundePicking)){
                    array[i]=stunde;
                }
                else {
                    i--;
                }
                i++;
            }
        }
        else {
            array = new String[stundenplan.size()];
            for(String stunde:stundenplan){
                if(!(Integer.parseInt(new StundenplanEintragModel(stunde).stunde)==stundePicking)){
                    array[i]=stunde;
                }
                else {
                    array[i]=new StundenplanEintragModel(stunde).stunde+"||"+" "+"||"+" "+"||"+" "+"||"+" ";
                }
                i++;
            }
        }
        saveStundenplan(wochentagPicking,array);
        wochentagPicking=null;
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    private void showEditStunde(String stunde,String raum,String lehrer, String fachName){
        Bundle bundle = new Bundle();
        bundle.putString("fach",stunde);
        bundle.putString("raum",raum);
        bundle.putString("lehrer",lehrer);
        bundle.putString("fachName",fachName);
        Intent intent = new Intent(this,StundenplanEdit.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,0,bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==1){
            if(stundePicking!=0){
                Loeschen();
            }
            return;
        }
        if(data==null){
            return;
        }
        String fach = data.getStringExtra("fach");
        String lehrer = data.getStringExtra("lehrer");
        String raum = data.getStringExtra("raum");
        String fachName = data.getStringExtra("fachName");

        Set<String> stundenplan = loadStundenplanOrdered(wochentagPicking);
        String[] array;
        if(stundenplan==null){
            array = new String[1];
        }
        else {
            if(stundePicking==0){
                array = new String[stundenplan.size()+1];
            }
            else {
                array = new String[stundenplan.size()];
            }
            int i=0;
            for(String stunde:stundenplan){
                if(!(Integer.parseInt(new StundenplanEintragModel(stunde).stunde)==stundePicking)){
                    array[i]=stunde;
                }
                i++;
            }
        }
        if(stundePicking==0){
            array[array.length-1]=array.length+"||"+fach+"||"+lehrer+"||"+raum+"||"+fachName;
        }
        else {
            array[stundePicking-1]=stundePicking+"||"+fach+"||"+lehrer+"||"+raum+"||"+fachName;
        }
        saveStundenplan(wochentagPicking,array);
        wochentagPicking=null;
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    private static void saveStundenplan(String wochentag, LinkedHashSet<String> stundenplan){
        saveStundenplanOrdered(stundenplan,wochentag);
    }

    private static void saveStundenplan(String wochentag, String[] stundenplan){
        Arrays.sort(stundenplan);
        LinkedHashSet<String> stundenplanSet = new LinkedHashSet<>();
        Collections.addAll(stundenplanSet, stundenplan);
        saveStundenplan(wochentag,stundenplanSet);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            final View rootView = inflater.inflate(R.layout.stundenplan_fragment, container, false);
            ListView listView = rootView.findViewById(R.id.stundenplan_listView);
            if(getArguments()==null){
                return null;
            }
            int wochentagNo = getArguments().getInt(ARG_SECTION_NUMBER);
            String wochentag = getWochentag(wochentagNo,getContext());
            Set<String> stundenplanGeneral = loadStundenplanOrdered(wochentag);
            LinkedHashSet<String> stundenplan = null;
            try {
                if(stundenplanGeneral==null){
                    throw new NullPointerException();
                }
                String[] stundenplanGeneralArray = Arrays.copyOf(stundenplanGeneral.toArray(), stundenplanGeneral.toArray().length, String[].class);
                Arrays.sort(stundenplanGeneralArray);
                if(stundenplanGeneralArray.length==10){
                    String temp = stundenplanGeneralArray[0];
                    System.arraycopy(stundenplanGeneralArray, 1, stundenplanGeneralArray, 0, stundenplanGeneralArray.length - 1);
                    stundenplanGeneralArray[stundenplanGeneralArray.length-1]=temp;
                }
                stundenplan = new LinkedHashSet<>(Arrays.asList(stundenplanGeneralArray));
            }
            catch (NullPointerException ignored){}
            if(bearbeiten){
                if(stundenplan==null){
                    stundenplan = new LinkedHashSet<>();
                }
                rootView.findViewById(R.id.stundenplan_new).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.stundenplan_new).setTag(wochentag);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone((ConstraintLayout) rootView.findViewById(R.id.constraintLayout));
                constraintSet.setMargin(R.id.stundenplan_listView,ConstraintSet.BOTTOM,100);
                constraintSet.applyTo((ConstraintLayout) rootView.findViewById(R.id.constraintLayout));
            }
            if(stundenplan==null){
                TextView textView = rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.stundenplan_nothing,wochentag));
                return rootView;
            }
            List<StundenplanEintragModel> array = new ArrayList<>();
            for(String stunde:stundenplan){
                array.add(new StundenplanEintragModel(stunde));
            }
            saveStundenplan(wochentag,Arrays.copyOf(stundenplan.toArray(), stundenplan.toArray().length, String[].class));
            listView.setAdapter(new CustomListAdapter(array, getContext(),wochentagNo));
            if(Startseite.KURSSPRECHER_ENABLED){ //FIXME Apple
                final String klasse = Startseite.prefs.getString("klasse","");
                if(klasse.equals("EF")||klasse.equals("Q1")||klasse.equals("Q2")){
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final String fachId = ((CustomListAdapter.ViewHolder)(view).getTag()).fachID;
                                        String url = "https://amgitt.de:8080/AMGAppServlet/amgapp?requestType=KurssprecherRequest&request=&username="+Startseite.benutzername+"&password="+Startseite.passwort+"&datum="+fachId+"&gebaeude="+klasse+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                                        url = url.replaceAll(" ","%20");
                                        URL oracle = new URL(url);
                                        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

                                        boolean end = false;

                                        while (!end){
                                            if ((in.readLine()).equals("<body>")){
                                                end=true;
                                            }
                                        }
                                        in.readLine();
                                        String serverReturn = URLDecoder.decode(in.readLine(),"UTF-8");
                                        in.close();

                                        try {
                                            System.out.println(serverReturn);
                                            final String sprecher = serverReturn.split("//")[1].split("Kurssprecher: ")[1].split("//")[0];
                                            final String sprecherVertretung = serverReturn.split("//")[2].split("Vertretung: ")[1];

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder builder;
                                                    if(Startseite.theme == R.style.DarkTheme){
                                                        builder = new AlertDialog.Builder(getContext(),R.style.DarkDialog);
                                                    }
                                                    else {
                                                        builder = new AlertDialog.Builder(getContext());
                                                    }
                                                    TextView textView = new TextView(getContext());
                                                    textView.setText("Kurs: "+fachId+"\n\n"+
                                                            "Kurssprecher/in: "+sprecher+"\n"+
                                                            "Vertretung: "+sprecherVertretung);
                                                    textView.setTextColor(getResources().getColor(Startseite.textColor));
                                                    float dpi = getResources().getDisplayMetrics().density;
                                                    textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
                                                    builder.setView(textView);
                                                    builder.setPositiveButton("OK", null)
                                                            .setTitle("Kurssprecher");
                                                    builder.create().show();
                                                }
                                            });
                                        }
                                        catch (ArrayIndexOutOfBoundsException e) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(),"Es sind keine Kurssprecher für diesen Kurs angegeben! Bitte deine Kurssprecher, sich bei mir zu melden.",Toast.LENGTH_LONG).show();//FIXME Text?
                                                }
                                            });
                                        }
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getContext(),"Abrufen der Daten fehlgeschlagen",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).start();
                        }
                    });
                }
            }
            return rootView;
        }
    }

    private static String getWochentag(int noOfDay, Context context){
        switch (noOfDay){
            case 1:
                return context.getString(R.string.stundenplan_mo_lang);
            case 2:
                return context.getString(R.string.stundenplan_di_lang);
            case 3:
                return context.getString(R.string.stundenplan_mi_lang);
            case 4:
                return context.getString(R.string.stundenplan_do_lang);
            case 5:
                return context.getString(R.string.stundenplan_fr_lang);
            default:
                return null;
        }
    }

    private static String getWochentagAbk(int noOfDay, Context context){
        switch (noOfDay){
            case 1:
                return context.getString(R.string.stundenplan_mo_kurz);
            case 2:
                return context.getString(R.string.stundenplan_di_kurz);
            case 3:
                return context.getString(R.string.stundenplan_mi_kurz);
            case 4:
                return context.getString(R.string.stundenplan_do_kurz);
            case 5:
                return context.getString(R.string.stundenplan_fr_kurz);
            default:
                return null;
        }
    }

    static class CustomListAdapter extends BaseAdapter {

        final List<StundenplanEintragModel> listData;
        final Context context;
        final int noOfDayOfWeek;


        CustomListAdapter(List<StundenplanEintragModel> listData, Context context, int noOfDayOfWeek){
            this.listData = listData;
            this.context = context;
            this.noOfDayOfWeek = noOfDayOfWeek;
        }

        @Override
        public int getCount() {
            return listData.size();
        }
        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.stundenplan_eintrag, null);
                holder = new ViewHolder();
                holder.bearbeitenView = convertView.findViewById(R.id.stundenplan_eintrag_stift);
                holder.stundeView = convertView.findViewById(R.id.stundenplan_eintrag_stunde);
                holder.stundenzeitView = convertView.findViewById(R.id.stundenplan_eintrag_stundenzeit);
                holder.fachNameView = convertView.findViewById(R.id.stundenplan_eintrag_fach);
                holder.lehrerView = convertView.findViewById(R.id.stundenplan_eintrag_lehrer);
                holder.raumView = convertView.findViewById(R.id.stundenplan_eintrag_raum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StundenplanEintragModel stunde = this.listData.get(position);
            holder.stundeView.setText(stunde.stunde);
            holder.stundenzeitView.setText(zuZeit(stunde.stunde));
            holder.fachNameView.setText(stunde.fachName);
            holder.lehrerView.setText(stunde.lehrer);
            holder.raumView.setText(stunde.raum);
            holder.stundeText = stunde.stunde;
            holder.stundenzeitText = zuZeit(stunde.stunde);
            holder.fachNameText = stunde.fachName;
            holder.lehrerText = stunde.lehrer;
            holder.raumText = stunde.raum;
            holder.fachID = stunde.fach;

            holder.bearbeitenView.setImageResource(R.drawable.table_edit);
            if(bearbeiten){
                holder.bearbeitenView.setVisibility(View.VISIBLE);
            }
            holder.bearbeitenView.setTag(stunde.stunde);

            if(noOfDayOfTheWeek==noOfDayOfWeek) {
                if (eigeneKlasseHeute != null) {
                    VertretungModel[] rightRows = eigeneKlasseHeute.getRightRows();
                    for (VertretungModel row : rightRows) {
                        try {
                            if (Integer.parseInt(row.getStunde()) == Integer.parseInt(stunde.stunde)) {
                                if (row.getFach().equals(stunde.fach)) {
                                    vertretung(row, holder);
                                }
                            }
                        }
                        catch(NumberFormatException e){
                            for(String stundeNr : row.getStunde().split(" - ")){
                                if (Integer.parseInt(stundeNr) == Integer.parseInt(stunde.stunde)) {
                                    if (row.getFach().equals(stunde.fach)) {
                                        vertretung(row, holder);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(noOfDayOfTheWeek==noOfDayOfWeek-1){
                if (eigeneKlasseFolgetag != null) {
                    VertretungModel[] rightRows = eigeneKlasseFolgetag.getRightRows();
                    for (VertretungModel row : rightRows) {
                        try {
                            if (Integer.parseInt(row.getStunde()) == Integer.parseInt(stunde.stunde)) {
                                if (row.getFach().equals(stunde.fach)) {
                                    vertretung(row, holder);
                                }
                            }
                        }
                        catch(NumberFormatException e){
                            for(String stundeNr : row.getStunde().split(" - ")){
                                if (Integer.parseInt(stundeNr) == Integer.parseInt(stunde.stunde)) {
                                    if (row.getFach().equals(stunde.fach)) {
                                        vertretung(row, holder);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return convertView;
        }

        private void vertretung(VertretungModel row,ViewHolder holder){
            if(!(row.getErsatzFach().contentEquals(holder.fachID))){
                if(!(row.getErsatzFach().equals("---"))){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.fachNameView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.fachNameView.getText()+"</del></font><font color=\"#04B404\"> "+row.getErsatzFach()+"</font>", Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.fachNameView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.fachNameView.getText()+"</del></font><font color=\"#04B404\"> "+row.getErsatzFach()));
                    }
                }
                else {
                    holder.fachNameView.setPaintFlags(holder.fachNameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.fachNameView.setTextColor(Color.parseColor("#FE2E2E"));
                }
            }
            if(!(row.getVertretungslehrer().contentEquals(holder.lehrerView.getText()))){
                if(!(row.getVertretungslehrer().equals("---"))){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.lehrerView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.lehrerView.getText()+"</del></font><font color=\"#04B404\"> "+row.getVertretungslehrer()+"</font>", Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.lehrerView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.lehrerView.getText()+"</del></font><font color=\"#04B404\"> "+row.getVertretungslehrer()+"</font>"));
                    }
                }
                else {
                    holder.lehrerView.setPaintFlags(holder.lehrerView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.lehrerView.setTextColor(Color.parseColor("#FE2E2E"));
                }
            }
            if(!(row.getRaum().contentEquals(holder.raumView.getText()))){
                if(!(row.getRaum().equals("---"))){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.raumView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.raumView.getText()+"</del></font><font color=\"#04B404\"> "+row.getRaum()+"</font>", Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.raumView.setText(Html.fromHtml("<font color=\"#FE2E2E\"><del>"+holder.raumView.getText()+"</del></font><font color=\"#04B404\"> "+row.getRaum()+"</font>"));
                    }
                }
                else {
                    holder.raumView.setPaintFlags(holder.raumView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.raumView.setTextColor(Color.parseColor("#FE2E2E"));
                }
            }
        }

        class ViewHolder{
            TextView stundeView;
            TextView stundenzeitView;
            TextView fachNameView;
            TextView lehrerView;
            TextView raumView;
            ImageView bearbeitenView;

            String stundeText;
            String stundenzeitText;
            String fachNameText;
            String lehrerText;
            String raumText;
            String fachID;
        }

        String zuZeit(String stunde){
            switch (stunde){
                case "1":
                    return "7:45-8:30";
                case "2":
                    return "8:30-9:15";
                case "3":
                    return "9:35-10:20";
                case "4":
                    return "10:20-11:05";
                case "5":
                    return "11:25-12:10";
                case "6":
                    return "12:15-13:00";
                case "7":
                    return "13:15-14:00";
                case "8":
                    return "14:00-14:45";
                case "9":
                    if(getCount()==10){
                        return "15:00-15:45";
                    }
                    else {
                        return "14:45-15:30";
                    }
                case "10":
                    return "15:45-16:30";
                default:
                    return context.getString(R.string.stundenplan_time_notDefined);
            }
        }
    }

    private static void saveStundenplanOrdered(LinkedHashSet<String> stundenplan,String wochentag){
        if(stundenplan.isEmpty()){
            Startseite.prefs.edit().remove("stundenplan"+wochentag).apply();
            return;
        }
        JSONArray array = new JSONArray(stundenplan);
        Startseite.prefs.edit().putString("stundenplan"+wochentag,array.toString()).apply();
    }

    static  LinkedHashSet<String> loadStundenplanOrdered(String wochentag){
        try {
            LinkedHashSet<String> list = new LinkedHashSet<>();
            JSONArray array = new JSONArray(Startseite.prefs.getString("stundenplan"+wochentag,null));
            for (int i = 0; i < array.length(); i++) {
                list.add((String)array.get(i));
            }
            return list;
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        } catch (NullPointerException e){
            return null;
        }
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return getCountOfDays();
        }
    }

    private static int getCountOfDays(){
        return 5;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_stundenplan,this);
        return true;
    }
}
