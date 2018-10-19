package www.amg_witten.de.apptest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Stundenplan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private static boolean transistioning = false;
    private static boolean bearbeiten=false;

    private static String wochentagPicking;
    private static int stundePicking;

    private static int lastSelected=0;
    private static TabLayout.Tab lastTab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,3,R.layout.stundenplan_activity);

        tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Montag"));
        tabLayout.addTab(tabLayout.newTab().setText("Di."));
        tabLayout.addTab(tabLayout.newTab().setText("Mi."));
        tabLayout.addTab(tabLayout.newTab().setText("Do."));
        tabLayout.addTab(tabLayout.newTab().setText("Fr."));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.getTabAt(0).select();
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

                lastTab.setText(getWochentagAbk(lastSelected+1));

                tab.setText(getWochentag(tab.getPosition()+1));

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
                    tabLayout.getTabAt(i).select();
                    transistioning=false;
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        transistioning=false;
        bearbeiten=false;
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
        return super.onOptionsItemSelected(item);
    }

    public void NewStunde(View view) {
        wochentagPicking = (String)view.getTag();
        stundePicking=0;
        showEditStunde("","","");
    }

    public void EditStunde(View view){
        wochentagPicking = getWochentag(tabLayout.getSelectedTabPosition()+1);
        stundePicking = Integer.parseInt((String) view.getTag());
        CustomListAdapter.ViewHolder holder = (CustomListAdapter.ViewHolder)((ConstraintLayout)view.getParent()).getTag();
        showEditStunde(holder.fach.getText().toString(), holder.raum.getText().toString(), holder.lehrer.getText().toString());
    }

    private void Loeschen(){
        Set<String> stundenplan = loadStundenplanOrdered(wochentagPicking);
        String[] array;
        int i=0;
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
                    array[i]=new StundenplanEintragModel(stunde).stunde+"||"+" "+"||"+" "+"||"+" ";
                }
                i++;
            }
        }
        saveStundenplan(wochentagPicking,array);
        wochentagPicking=null;
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    private void showEditStunde(String stunde,String raum,String lehrer){
        Bundle bundle = new Bundle();
        bundle.putString("fach",stunde);
        bundle.putString("raum",raum);
        bundle.putString("lehrer",lehrer);
        Intent intent = new Intent(this,StundenplanEdit.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,0,bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==1){
            Loeschen();
            return;
        }
        if(data==null){
            return;
        }
        String fach = data.getStringExtra("fach");
        String lehrer = data.getStringExtra("lehrer");
        String raum = data.getStringExtra("raum");

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
            array[array.length-1]=array.length+"||"+fach+"||"+lehrer+"||"+raum;
        }
        else {
            array[stundePicking-1]=stundePicking+"||"+fach+"||"+lehrer+"||"+raum;
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
            View rootView = inflater.inflate(R.layout.stundenplan_fragment, container, false);
            ListView listView = rootView.findViewById(R.id.stundenplan_listView);
            String wochentag = getWochentag(getArguments().getInt(ARG_SECTION_NUMBER));
            Set<String> stundenplanGeneral = loadStundenplanOrdered(wochentag);
            LinkedHashSet<String> stundenplan = null;
            try {
                String[] stundenplanGeneralArray = Arrays.copyOf(stundenplanGeneral.toArray(), stundenplanGeneral.toArray().length, String[].class);
                Arrays.sort(stundenplanGeneralArray);
                stundenplan = new LinkedHashSet<>(Arrays.asList(stundenplanGeneralArray));
            }
            catch (NullPointerException ignored){}
            if(bearbeiten){
                if(stundenplan==null){
                    stundenplan = new LinkedHashSet<>();
                }
                rootView.findViewById(R.id.stundenplan_new).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.stundenplan_new).setTag(wochentag);
            }
            if(stundenplan==null){
                TextView textView = rootView.findViewById(R.id.section_label);
                textView.setText("Du musst zuerst einen Stundenplan für "+wochentag+" eingeben!");
                return rootView;
            }
            List<StundenplanEintragModel> array = new ArrayList<>();
            int i=0;
            for(String stunde:stundenplan){
                array.add(new StundenplanEintragModel(stunde));
                i++;
            }
            saveStundenplan(wochentag,Arrays.copyOf(stundenplan.toArray(), stundenplan.toArray().length, String[].class));
            System.out.println(Arrays.toString(array.toArray()));
            listView.setAdapter(new CustomListAdapter(array, getContext()));
            return rootView;
        }
    }

    private static String getWochentag(int noOfDay){
        switch (noOfDay){
            case 1:
                return "Montag";
            case 2:
                return "Dienstag";
            case 3:
                return "Mittwoch";
            case 4:
                return "Donnerstag";
            case 5:
                return "Freitag";
            default:
                return null;
        }
    }

    private static String getWochentagAbk(int noOfDay){
        switch (noOfDay){
            case 1:
                return "Mo.";
            case 2:
                return "Di.";
            case 3:
                return "Mi.";
            case 4:
                return "Do.";
            case 5:
                return "Fr.";
            default:
                return null;
        }
    }

    static class CustomListAdapter extends BaseAdapter {

        final List<StundenplanEintragModel> listData;
        final Context context;

        CustomListAdapter(List<StundenplanEintragModel> listData, Context context){
            this.listData = listData;
            this.context = context;
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
                holder.bearbeiten = convertView.findViewById(R.id.stundenplan_eintrag_stift);
                holder.stunde = convertView.findViewById(R.id.stundenplan_eintrag_stunde);
                holder.stundenzeit = convertView.findViewById(R.id.stundenplan_eintrag_stundenzeit);
                holder.fach = convertView.findViewById(R.id.stundenplan_eintrag_fach);
                holder.lehrer = convertView.findViewById(R.id.stundenplan_eintrag_lehrer);
                holder.raum = convertView.findViewById(R.id.stundenplan_eintrag_raum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StundenplanEintragModel stunde = this.listData.get(position);
            holder.stunde.setText(stunde.stunde);
            holder.stundenzeit.setText(zuZeit(stunde.stunde));
            holder.fach.setText(stunde.fach);
            holder.lehrer.setText(stunde.lehrer);
            holder.raum.setText(stunde.raum);

            holder.bearbeiten.setImageResource(R.drawable.table_edit);
            if(bearbeiten){
                holder.bearbeiten.setVisibility(View.VISIBLE);
            }
            holder.bearbeiten.setTag(stunde.stunde);

            return convertView;
        }

        class ViewHolder{
            TextView stunde;
            TextView stundenzeit;
            TextView fach;
            TextView lehrer;
            TextView raum;
            ImageView bearbeiten;
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
                    return "14:45-15:30";
                default:
                    return "Nicht definiert";
            }
        }
    }

    private static void saveStundenplanOrdered(LinkedHashSet<String> stundenplan,String wochentag){
        JSONArray array = new JSONArray(stundenplan);
        Startseite.prefs.edit().putString("stundenplan"+wochentag,array.toString()).apply();
    }

    private static  LinkedHashSet<String> loadStundenplanOrdered(String wochentag){
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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }
}
