package amgapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPlan {
	static String klasse = "";
	static HashMap<String,String> settings = new HashMap<String,String>();
	
	public static String getVPlan(String pwd, String date) {
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
        try {
            Authenticator.setDefault(new MyAuthenticator(pwd));
            urlEndings.add("001.htm");
            String main = "https://www.amg-witten.de/fileadmin/VertretungsplanSUS/"+date+"/";
            System.out.println(main);

            getAllEndings(main,urlEndings);

            String[] stands = getTables(main,urlEndings,tables);
            stand=stands[0];
            fuerDatum=stands[1];

            getKlassenList(tables,klassen);

            getOnlyRealKlassenList(tables,realEintraege);

            for(String s : realEintraege){
                tryMatcher(s,fertigeMulti,vertretungModels);
            }

            final String finalstand = stand;
            final String finalfuerDatum = fuerDatum;
            
            parseKlassenWithProcess(klassen,fertigeKlassen,vertretungModels,data);

            return getHTML(finalfuerDatum, finalstand, data, fertigeKlassen);

        }
        catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Generieren";
        }
	}
	
	static void tryMatcher(String s, List<VertretungModel> fertigeMulti, List<VertretungModel> vertretungModels){
        try {
            Matcher matcher = Pattern.compile("<td class=\"list\"(?s)(.*?)</td>").matcher(s);

            List<String> allMatches = new ArrayList<>();
            while (matcher.find()) {
                String match = matcher.group();
                allMatches.add(match.replace("<td class=\"list\" align=\"center\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" >","").replace("<td class=\"list\">","").replace("</td>","").replace("<b>","").replace("</b>","").replace("<span style=\"color: #800000\">","").replace("<span style=\"color: #0000FF\">","").replace("<span style=\"color: #010101\">","").replace("<span style=\"color: #008040\">","").replace("<span style=\"color: #008000\">","").replace("<span style=\"color: #FF00FF\">","").replace("</span>","").replace("&nbsp;","").replaceFirst(">",""));
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

            BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
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

    static void parseKlassenWithProcess(List<String> klassen, List<String> fertigeKlassen, List<VertretungModel> vertretungModels, List<VertretungModelArrayModel> data){
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
        }
    }

    private static String getHTML(String finalfuerDatum, String finalstand, List<VertretungModelArrayModel> data, List<String> fertigeKlassen){
        try {
            String returnString = "<!DOCTYPE html>\n" +
                    "<html>\n";
            returnString+=printHead();
            returnString += "\t<body>\n" +
                    "  <div class=\"container\">\n" +
                    "  <div class=\"aktuell\">"+"Für "+finalfuerDatum+"</div>\n" +
                    "    <div id=\"accordion\">\n" +
                    "      <ul class=\"panels\">\n";
            for(int i=0;i<fertigeKlassen.size(); i++){
                returnString+=data.get(i).getHTMLListItems(i, klasse, settings);
            }
            returnString+="      </ul>\n" +
                    "    </div>\n" +
                    "   </div>\n" +
                    "   <div class=\"stand\">"+"Stand: "+finalstand+"</div>\n" +
                    "  </body>\n" +
                    "</html>";
            return returnString;
        }
        catch(IOException ignored){}
        return "Fehler beim Generieren!";
    }

    private static String printHead() throws IOException{
        String returnString = "<head>\n" +
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
                "</script>\n";
        returnString+="\n" +
                "<style>\n" +
                "body {\n" +
                "  background-color: #ccc;\n" +
                "  margin: auto auto;\n" +
                "  padding: 0;\n" +
                "  width:100%;\n" +
                "}\n" +
                "/**/\n" +
                "#accordion {\n" +
                //"  width: 80%;\n" +
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
                "#accordion ul a { color: #333; }\n";
        returnString+="/**/\n" +
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
                "  color: white;\n" +
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
                "   table, td, th {\n" +
                "    border: 1px solid black;\n" +
                "} \n";
        returnString+=".aktuell {\n" +
                "font-family: roboto, sans-serif; \n" +
                "padding-top: 10px;\n" +
                "font-size: 1.4em; \n" +
                "font-weight:bold;\n" +
                "text-align: center;\n" +
                "color: white;\n" +
                "text-shadow: -1px 0 grey, 0 1px grey, 1px 0 grey, 0 -1px grey;\n" +
                "}\n" +
                "\n" +
                ".stand {\n" +
                "font-family: verdana, sans-serif; \n" +
                "padding: 0 20px 10px 0;\n" +
                "font-size: 0.8em; \n" +
                "text-align: right;\n" +
                "color: #232323;\n" +
                "}\n" +
                "</style>\n" +
                "\t</head>\n";
        return returnString;
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


}


class MyAuthenticator extends Authenticator {

    final private String password;

    MyAuthenticator(String password){
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        String username = "Schueler";
        return new PasswordAuthentication(username, password.toCharArray());
    }
}