package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vertretungsplan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Activity thise = this;
    private static String klasse = "";
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public Vertretungsplan(){
        thise = this;
    }
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
        Startseite.prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        klasse = Startseite.prefs.getString("klasse","");

        Startseite.login = Startseite.prefs.getInt("login",0); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
        Startseite.benutzername = Startseite.prefs.getString("loginUsername","");

        methoden.onCreateFillIn(this,this,1,R.layout.vertretungsplan_activity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                File heute = action(thise, "Heute");
                File folgetag = action(thise, "Folgetag");
                generateLayout(heute, folgetag);
            }
        }).start();
    }

    private File action(final Activity thise, String date){
        String fuerDatum;
        String stand;
        final List<String> urlEndings = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        final List<String> klassen = new ArrayList<>();
        final List<String> realEintraege = new ArrayList<>();
        final List<VertretungModel> vertretungModels = new ArrayList<>();
        List<VertretungModel> fertigeMulti = new ArrayList<>();
        final List<VertretungModelArrayModel> data = new ArrayList<>();
        final List<String> fertigeKlassen = new ArrayList<>();
        final File lFile = new File(thise.getFilesDir() + "/Vertretungsplan"+date+".htm");
        try {
            try {
                Looper.prepare();
            }
            catch (Exception ignored){}
            final ProgressDialog pDialog;
            if(Startseite.theme == R.style.DarkTheme){
                pDialog = new ProgressDialog(thise,R.style.DarkDialog);
            }
            else {
                pDialog = new ProgressDialog(thise);
            }

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pDialog.setTitle(thise.getString(R.string.vertretungsplan_dialog_title));
                        pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_counting));
                        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialog.setProgress(0);
                        pDialog.show();
                    }
                    catch(Exception ignored){}
                }
            });

            Authenticator.setDefault(new MyAuthenticator(thise));
            urlEndings.add("001.htm");
            String main = "http://sus.amg-witten.de/"+date+"/";

            getAllEndings(main,urlEndings);

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(urlEndings.size());
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_downloading));
                }
            });

            String[] stands = getTablesWithProcess(main,urlEndings,tables,pDialog);
            stand=stands[0];
            fuerDatum=stands[1];

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(urlEndings.size());
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_reading));
                }
            });

            getKlassenListWithProcess(tables,klassen,pDialog);

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(klassen.size());
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_checking));
                }
            });

            getOnlyRealKlassenListWithProcess(tables,realEintraege,pDialog);

            int i=0;

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(realEintraege.size());
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_extracting));
                }
            });

            for(String s : realEintraege){
                i++;
                tryMatcher(s,fertigeMulti,vertretungModels);
                pDialog.setProgress(i);
            }

            final String finalstand = stand;
            final String finalfuerDatum = fuerDatum;
            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(klassen.size());
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_compiling));

                    parseKlassenWithProcess(klassen,fertigeKlassen,vertretungModels,data,pDialog);

                    pDialog.setMax(fertigeKlassen.size()+3);
                    pDialog.setMessage(thise.getString(R.string.vertretungsplan_dialog_generating));

                    writeToFileWithProcess(pDialog, lFile, finalfuerDatum, finalstand, thise, data, fertigeKlassen);

                    pDialog.hide();
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lFile;
    }

    static void tryMatcher(String s, List<VertretungModel> fertigeMulti, List<VertretungModel> vertretungModels){
        try {
            Matcher matcher = Pattern.compile("<td class=\"list\"(?s)(.*?)</td>").matcher(s);

            List<String> allMatches = new ArrayList<>();
            while (matcher.find()) {
                String match = matcher.group();
                allMatches.add(match
                        .replace("<td class=\"list\" align=\"center\">","")
                        .replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\">","")
                        .replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" >","")
                        .replace("<td class=\"list\" style=\"background-color: #FFFFFF\">","")
                        .replace("<td class=\"list\" style=\"background-color: #FFFFFF\" >","")
                        .replace("<td class=\"list\">","")
                        .replace("</td>","")
                        .replace("<b>","")
                        .replace("</b>","")
                        .replace("<span style=\"color: #800000\">","")
                        .replace("<span style=\"color: #0000FF\">","")
                        .replace("<span style=\"color: #010101\">","")
                        .replace("<span style=\"color: #008040\">","")
                        .replace("<span style=\"color: #008000\">","")
                        .replace("<span style=\"color: #FF00FF\">","")
                        .replace("</span>","")
                        .replace("&nbsp;","")
                        .replaceFirst(">",""));
            }

            VertretungModel model = new VertretungModel(allMatches.get(0),allMatches.get(1),allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7));

            if(Pattern.compile("\\d{1,2}([a-d]){2,4}").matcher(allMatches.get(1)).matches()){
                boolean found = false;
                for (VertretungModel existModel : fertigeMulti) {
                    if(existModel.toString().equals(model.toString())){
                        found = true;
                    }
                }
                if(!found) {
                    if(allMatches.get(1).contains("a")){
                        vertretungModels.add(new VertretungModel(allMatches.get(0),allMatches.get(1).substring(0,2)+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                    }
                    if(allMatches.get(1).contains("b")){
                        vertretungModels.add(new VertretungModel(allMatches.get(0),allMatches.get(1).substring(0,2)+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                    }
                    if(allMatches.get(1).contains("c")){
                        vertretungModels.add(new VertretungModel(allMatches.get(0),allMatches.get(1).substring(0,2)+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                    }
                    if(allMatches.get(1).contains("d")){
                        vertretungModels.add(new VertretungModel(allMatches.get(0),allMatches.get(1).substring(0,2)+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                    }
                    fertigeMulti.add(model);
                }
            }
            else {
                vertretungModels.add(model);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static void getAllEndings(String main, List<String> urlEndings) throws IOException {
        boolean exit=false;
        String next="001.htm";
        while(!exit) {
            URL mainUrl = new URL(main+"subst_"+next);

            InputStream stream = mainUrl.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            StringBuilder full = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                full.append(str);
            }
            in.close();

            if(full.toString().contains("<frame name=\"ticker\" src=\"")){
                main+="f1/";
            }
            else {
                String head = onlyElement(full.toString(),"head");
                String contentMeta = onlyArgumentOfElement(head,"meta http-equiv=\"refresh\"","content");
                String nextURL = contentMeta.split("URL=subst_")[1];
                next=nextURL;
                if(next.equals("001.htm")) {
                    exit=true;
                }
                else {
                    urlEndings.add(nextURL);
                }
            }
        }
    }

    static String[] getTablesWithProcess(String main, List<String> urlEndings, List<String> tables, ProgressDialog pDialog) throws IOException {
        String stand = "";
        String fuerDatum = "";
        for (int i=0;i<urlEndings.size();i++) {
            URL mainUrl = new URL(main+"subst_"+urlEndings.get(i));
            BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
            StringBuilder full = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                full.append(str);
            }
            in.close();

            String body;
            try {
                body = onlyElement(full.toString(),"body");
            }
            catch (ArrayIndexOutOfBoundsException e){
                body = onlyElement(full.toString(),"body"," bgcolor=\"#F0F0F0\"");
            }
            String center = onlyElement(body,"center");
            if(center.contains("http://www.untis.at")){
                center = onlyElement(body,"CENTER");
            }
            String table = onlyElement(center,"table"," class=\"mon_list\" ");
            tables.add(table);
            if(urlEndings.get(i).equals("001.htm")){
                String headData = onlyElement(body,"td"," align=\"right\" valign=\"bottom\"");
                stand = (headData.split("Stand: ")[1]).split("</p>")[0].trim();
                String datum = onlyElement(center,"div"," class=\"mon_title\"");
                String[] datumParts = datum.split(" ");
                fuerDatum = datumParts[1]+", "+datumParts[0];
            }
            pDialog.setProgress(i+1);
        }
        return new String[] {stand,fuerDatum};
    }

    static String[] getTables(String main, List<String> urlEndings, List<String> tables) throws IOException {
        String stand = "";
        String fuerDatum = "";
        for (int i=0;i<urlEndings.size();i++) {
            URL mainUrl = new URL(main+"subst_"+urlEndings.get(i));
            BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
            StringBuilder full = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                full.append(str);
            }
            in.close();

            String body;
            try {
                body = onlyElement(full.toString(),"body");
            }
            catch (ArrayIndexOutOfBoundsException e){
                body = onlyElement(full.toString(),"body"," bgcolor=\"#F0F0F0\"");
            }
            String center = onlyElement(body,"center");
            if(center.contains("http://www.untis.at")){
                center = onlyElement(body,"CENTER");
            }
            String table = onlyElement(center,"table"," class=\"mon_list\" ");
            tables.add(table);
            if(urlEndings.get(i).equals("001.htm")){
                String headData = onlyElement(body,"td"," align=\"right\" valign=\"bottom\"");
                stand = (headData.split("Stand: ")[1]).split("</p>")[0].trim();
                String datum = onlyElement(center,"div"," class=\"mon_title\"");
                String[] datumParts = datum.split(" ");
                fuerDatum = datumParts[1]+", "+datumParts[0];
            }
        }
        return new String[] {stand,fuerDatum};
    }

    static void getKlassenListWithProcess(List<String> tables, List<String> klassen, ProgressDialog pDialog) {
        for(int i=0; i<tables.size(); i++){
            String[] klassenArrayUnfertig = tables.get(i).split("td class=\"list inline_header\" colspan=\"8\"");
            for(int ie = 0; ie<klassenArrayUnfertig.length; ie++){
                klassenArrayUnfertig[ie] = klassenArrayUnfertig[ie].replaceFirst(">","");
            }
            for (int ie = 1; ie<klassenArrayUnfertig.length; ie++){
                klassen.add(klassenArrayUnfertig[ie].split("</td>")[0].trim());
            }
            pDialog.setProgress(i+1);
        }
    }

    static void getKlassenList(List<String> tables, List<String> klassen) {
        for(int i=0; i<tables.size(); i++){
            String[] klassenArrayUnfertig = tables.get(i).split("td class=\"list inline_header\" colspan=\"8\"");
            for(int ie = 0; ie<klassenArrayUnfertig.length; ie++){
                klassenArrayUnfertig[ie] = klassenArrayUnfertig[ie].replaceFirst(">","");
            }
            for (int ie = 1; ie<klassenArrayUnfertig.length; ie++){
                klassen.add(klassenArrayUnfertig[ie].split("</td>")[0].trim());
            }
        }
    }

    static void getOnlyRealKlassenListWithProcess(List<String> tables, List<String> realEintraege, ProgressDialog pDialog) {
        for(int i=0; i<tables.size(); i++){
            String[] eintraegeArrayUnfertigZwei = tables.get(i).split("tr ");
            for (String eintraegeArrayUnfertigEin : eintraegeArrayUnfertigZwei) {
                if (!(eintraegeArrayUnfertigEin.contains("class=\"list inline_header\"")||eintraegeArrayUnfertigEin.contains("class='list inline_header'")||eintraegeArrayUnfertigEin.contains("(Fach)"))) {
                    if(eintraegeArrayUnfertigEin.length()!=1){
                        realEintraege.add(eintraegeArrayUnfertigEin);
                    }
                }
            }
            pDialog.setProgress(i+1);
        }
    }

    static void getOnlyRealKlassenList(List<String> tables, List<String> realEintraege) {
        for(int i=0; i<tables.size(); i++){
            String[] eintraegeArrayUnfertigZwei = tables.get(i).split("tr ");
            for (String eintraegeArrayUnfertigEin : eintraegeArrayUnfertigZwei) {
                if (!(eintraegeArrayUnfertigEin.contains("class=\"list inline_header\"")||eintraegeArrayUnfertigEin.contains("class='list inline_header'")||eintraegeArrayUnfertigEin.contains("(Fach)"))) {
                    if(eintraegeArrayUnfertigEin.length()!=1){
                        realEintraege.add(eintraegeArrayUnfertigEin);
                    }
                }
            }
        }
    }

    static void parseKlassenWithProcess(List<String> klassen, List<String> fertigeKlassen, List<VertretungModel> vertretungModels, List<VertretungModelArrayModel> data, ProgressDialog pDialog){
        for(int i=0; i<klassen.size(); i++){
            if(!fertigeKlassen.contains(klassen.get(i))){
                int rightRowsCount = 0;
                for(int ie=0; ie<vertretungModels.size(); ie++){
                    if(vertretungModels.get(ie).getKlasse().equals(klassen.get(i))) {
                        rightRowsCount++;
                    }
                }
                VertretungModel[] rightRows = new VertretungModel[rightRowsCount];
                rightRowsCount=0;
                for(int ie=0; ie<vertretungModels.size(); ie++){
                    if(vertretungModels.get(ie).getKlasse().equals(klassen.get(i))){
                        rightRows[rightRowsCount] = vertretungModels.get(ie);
                        rightRowsCount++;
                    }
                }
                data.add(new VertretungModelArrayModel(
                        rightRows,klassen.get(i)));
                fertigeKlassen.add(klassen.get(i));
            }
            pDialog.setProgress(i+1);
        }
    }

    private void writeToFileWithProcess(ProgressDialog pDialog, File lFile, String finalfuerDatum, String finalstand, Context thise, List<VertretungModelArrayModel> data, List<String> fertigeKlassen){
        try {
            FileWriter fw = new FileWriter(lFile);
            fw.write("<!DOCTYPE html>\n" +
                    "<html>\n");
            fw.flush();
            printHead(fw);
            pDialog.setProgress(1);
            fw.write( "\t<body>\n" +
                    "  <div class=\"container\">\n" +
                    "  <div class=\"aktuell\">"+thise.getString(R.string.vertretungsplan_for_date,finalfuerDatum)+"</div>\n" +
                    "    <div id=\"accordion\">\n" +
                    "      <ul class=\"panels\">\n");
            fw.flush();
            for(int i=0;i<fertigeKlassen.size(); i++){
                fw.write(data.get(i).getHTMLListItems(i, klasse,thise.getSharedPreferences("Prefs", MODE_PRIVATE),thise));
                fw.flush();
                pDialog.setProgress(i+2);
            }
            fw.write("      </ul>\n" +
                    "    </div>\n" +
                    "   </div>\n" +
                    "   <div class=\"stand\">"+thise.getString(R.string.vertretungsplan_last_update,finalstand)+"</div>\n" +
                    "  </body>\n" +
                    "</html>");
            fw.close();
            pDialog.setProgress(pDialog.getMax());
        }
        catch(IOException ignored){}

    }

    private void printHead(FileWriter fw) throws IOException{
        fw.write("<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Accordion</title>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<link href='http://fonts.googleapis.com/css?family=roboto:400,600,700' rel='stylesheet' type='text/css'>\n" +
                "\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script> \n" +
                "<script type = \"text/javascript\"> \n" +
                "$(function() {\n" +
                "\n" +
                "\tvar $navLink = $('#accordion').find('li');\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t$navLink.on('click', function() {\n" +
                "\t\tvar panelToShow = $(this).data('panel-id');\n" +
                "\t\tvar $activeLink = $('#accordion').find('.active');\n" +
                "\n" +
                "\t\t// show new panel\n" +
                "\t\t// .stop is used to prevent the animation from repeating if you keep clicking the same link\n" +
                "\t\t$('.' + panelToShow).stop().slideDown();\n" +
                "\t\t$('.' + panelToShow).addClass('active');\n" +
                "\n" +
                "\n" +
                "\t\t// hide the previous panel \n" +
                "\t\t$activeLink.stop().slideUp()\n" +
                "\t\t.removeClass('active');\n" +
                "\t});\n" +
                "\n" +
                "});\n" +
                "\n" +
                "    var xOffset = 30;\n" +
                "    var yOffset = 10;\n" +
                "    $(document).ready(function() {\n" +
                "        $(\"body\").append(\"<div id='tooltip'>\");\n" +
                "\n" +
                "  var $navLink = $('#accordion').find('img');\n" +
                "  console.log($navLink);\n" +
                "  $navLink.on('click', function(e) {\n" +
                "  console.log(\"CLICKED\");\n" +
                "            e.preventDefault();\n" +
                "            $(\"#tooltip\")\n" +
                "                .text($(this).attr('title'))\n" +
                "                .css(\"top\",(e.pageY - xOffset) + \"px\")\n" +
                "                .css(\"left\",(e.pageX + yOffset) + \"px\")\n" +
                "                .show().fadeIn(\"fast\");\n" +
                "        })\n" +
                "            .on('mouseout',function(){\n" +
                "                $(\"#tooltip\").fadeOut(\"slow\").hide();\n" +
                "            })\n" +
                "    })\n" +
                "</script>\n");
        fw.flush();
        fw.write("\n" +
                "<style>\n" +
                "body {\n");
        if(Startseite.theme == R.style.DarkTheme){
            fw.write("  background-color: #"+Integer.toHexString(thise.getResources().getColor(R.color.darkBackground) & 0x00ffffff)+";\n");
        }
        else {
            fw.write("  background-color: #ccc;\n");
        }
         fw.write("  margin: auto auto;\n" +
                "  padding: 0;\n" +
                "  width:100%;\n" +
                "}\n" +
                "/**/\n" +
                "#accordion {\n" +
                "  margin: 10px auto;\n" +
                "  height: 50%;\n" +
                "  position: relative;\n" +
                "}\n" +
                "\n" +
                "#accordion ul {\n" +
                "  text-align: center;\n" +
                "  margin: 0;\n" +
                "}\n" +
                "\n" +
                "#accordion ul li {\n" +
                "  list-style-type: none;\n" +
                "  cursor: pointer;\n" +
                "  font-family: \"roboto\", sans-serif;\n" +
                "  padding: 0.4em;\n" +
                "  font-size: 1.4em;\n" +
                "  color: white;\n" +
                "  letter-spacing: 0.2em;\n" +
                "  transition: 0.3s ease all;\n" +
                "  text-shadow: -1px 0 grey, 0 1px grey, 1px 0 grey, 0 -1px grey;\n" +
                "}\n" +
                "\n" +
                "#accordion ul li:hover { color: #ccc; }\n" +
                "\n" +
                "#accordion ul a { color: #333; }\n");
        fw.flush();
        fw.write("/**/\n" +
                ".panels {\n" +
                "padding: 0;\n" +
                "}\n" +
                "\n" +
                " \n" +
                ".panel {\n" +
                "  display: none;\n" +
                "   padding: 25px;\n" +
                "  font-family: \"roboto\", sans-serif;\n" +
                "  padding: 0.3em;\n" +
                "  font-size: 1.0em;\n" +
                "  background-color: white;\n" +
                "  color: #333;\n" +
                "}\n" +
                "@media only screen and (max-width:480px) and (orientation:portrait) {\n" +
                "      nav { display:none;}\n" +
                "\n" +
                ".panel {\n" +
                "  padding: 0.2em;\n" +
                "  font-size: 0.7em;\n" +
                " }   \n" +
                " }\n" +
                "\n" +
                "    #tooltip{\n" +
                "        position:absolute;\n" +
                "        border:1px solid #222;\n" +
                "        border-radius: 6px; \n" +
                "        background:#444;\n" +
                "        padding:3px 6px;\n" +
                "        color:#fff;\n" +
                "        font-family:verdana, sans-serif;\n" +
                "        display:none;\n" +
                "    }\n" +
                "    \n" +
                "   table {\n" +
                "    border-collapse: collapse;\n" +
                "    width:100%;" +
                "}\n" +
                "\n" +
                "   table, td, th {\n");
        if(Startseite.theme == R.style.DarkTheme){
            fw.write("    border: 1px solid #"+Integer.toHexString(thise.getResources().getColor(R.color.darkTextColor) & 0x00ffffff)+";\n");
        }
        else {
            fw.write("    border: 1px solid black;\n");
        }
        fw.write("} \n");
        fw.flush();
        fw.write(".aktuell {\n" +
                "font-family: roboto, sans-serif; \n" +
                "padding-top: 10px;\n" +
                "font-size: 1.4em; \n" +
                "font-weight:bold;\n" +
                "text-align: center;\n");
        if(Startseite.theme == R.style.DarkTheme){
            fw.write("color: #"+Integer.toHexString(thise.getResources().getColor(R.color.darkTextColor) & 0x00ffffff)+";\n");
        }
        else {
            fw.write("color: white;\n");
        }
        fw.write("text-shadow: -1px 0 grey, 0 1px grey, 1px 0 grey, 0 -1px grey;\n" +
                "}\n" +
                "\n" +
                ".stand {\n" +
                "font-family: verdana, sans-serif; \n" +
                "padding: 0 20px 10px 0;\n" +
                "font-size: 0.8em; \n" +
                "text-align: right;\n");
        if(Startseite.theme == R.style.DarkTheme){
            fw.write("color: #"+Integer.toHexString(thise.getResources().getColor(R.color.darkTextColor) & 0x00ffffff)+";\n");
        }
        else {
            fw.write("color: #232323;\n");
        }

        fw.write( "}\n" +
                "</style>\n" +
                "\t</head>\n");
        fw.flush();
    }

    private static String onlyElement(String full, String element, String params) {
        String partOne;
        partOne = full.split("<"+element+params+">")[1];
        return partOne.split("</"+element+">")[0];
    }

    private static String onlyElement(String full, String element) {
        return onlyElement(full,element,"");
    }

    private static String onlyArgumentOfElement(String full, String element, String argument) {
        String partOne = full.split("<"+element)[1];
        String partTwo = partOne.split(argument+"=\"")[1];
        return partTwo.split("\"")[0];
    }

    private void generateLayout(final File heute, final File folgetag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), heute, folgetag);
                mSectionsPagerAdapter.notifyDataSetChanged();

                final ViewPager mViewPager = findViewById(R.id.vertretungsplan_container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                mViewPager.getCurrentItem();
                //FIXME Übernehmen in Apple
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mSectionsPagerAdapter.getCurrentItem(0)==null){
                            return;
                        }
                        ((SwipeRefreshLayout)mSectionsPagerAdapter.getCurrentItem(0).getView().findViewById(R.id.vertretungsplan_swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final File heute = action(thise, "Heute");
                                        final File folgetag = action(thise, "Folgetag");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int page = mViewPager.getCurrentItem();
                                                System.out.println(page);
                                                generateLayout(heute,folgetag);
                                                mViewPager.setCurrentItem(page);
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });

                        ((SwipeRefreshLayout)mSectionsPagerAdapter.getCurrentItem(1).getView().findViewById(R.id.vertretungsplan_swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final File heute = action(thise, "Heute");
                                        final File folgetag = action(thise, "Folgetag");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int page = mViewPager.getCurrentItem();
                                                generateLayout(heute,folgetag);
                                                mViewPager.setCurrentItem(page);
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }
                }).start();

                if(!Startseite.prefs.getBoolean("vplantutorial_shown",false)){
                    findViewById(R.id.vertretungsplan_darken_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.vertretungsplan_darken_view).setAlpha(0.6f);
                    final ShowcaseView view = new ShowcaseView.Builder(thise)
                            .setTarget(new ViewTarget(mViewPager))
                            .withMaterialShowcase()
                            .setContentTitle("Vertretungsplan")
                            .setContentText("Nach links wischen, um die nächste Seite des Vertretungsplan zu öffnen")
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
                                    findViewById(R.id.vertretungsplan_darken_view).setAlpha(0f);
                                    findViewById(R.id.vertretungsplan_darken_view).setVisibility(View.GONE);
                                }
                            });
                            Startseite.prefs.edit().putBoolean("vplantutorial_shown",true).apply();
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mSectionsPagerAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if(mSectionsPagerAdapter!=null) {
            mSectionsPagerAdapter.onRestoreInstanceState(savedInstanceState);
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File heute = action(thise, "Heute");
                    File folgetag = action(thise, "Folgetag");
                    generateLayout(heute, folgetag);
                }
            }).start();
        }
    }

    class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        final File heute;
        final File folgetag;

        PlaceholderFragment heuteF;
        PlaceholderFragment folgetagF;

        SectionsPagerAdapter(FragmentManager fm, File heute, File folgetag) {
            super(fm);
            this.heute = heute;
            this.folgetag = folgetag;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                heuteF = PlaceholderFragment.newInstance(heute);
                return heuteF;
            }
            else if(position==1){
                folgetagF = PlaceholderFragment.newInstance(folgetag);
                return folgetagF;
            }
            return null;
        }

        Fragment getCurrentItem(int position){
            if(position==0){
                return heuteF;
            }
            else if(position==1){
                return folgetagF;
            }
            return null;
        }

        void onSaveInstanceState(Bundle outState){
            heuteF.onSaveInstanceState(outState);
            folgetagF.onSaveInstanceState(outState);
        }

        void onRestoreInstanceState(Bundle savedInstanceState){
            heuteF.onRestoreInstanceState(savedInstanceState);
            folgetagF.onRestoreInstanceState(savedInstanceState);
        }

        @Override
        public int getItemPosition(@NonNull Object object){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        View view;

        public PlaceholderFragment() {}

        static PlaceholderFragment newInstance(File file) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString("file",file.getAbsolutePath());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            view = inflater.inflate(R.layout.vertretungsplan_fragment, container, false);
            WebView webView = view.findViewById(R.id.webView);

            webView.loadUrl("file://"+getArguments().getString("file"));
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            return view;
        }

        @Override
        public void onSaveInstanceState(Bundle outState){
            ((WebView)view.findViewById(R.id.webView)).saveState(outState);
        }

        void onRestoreInstanceState(Bundle savedInstanceState){
            ((WebView)view.findViewById(R.id.webView)).restoreState(savedInstanceState);
        }

        @Override
        public View getView(){
            return view;
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
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_vertretungsplan,this);
        return true;
    }
}