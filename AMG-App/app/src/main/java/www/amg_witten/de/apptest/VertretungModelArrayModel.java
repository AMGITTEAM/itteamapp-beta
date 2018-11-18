package www.amg_witten.de.apptest;

import android.content.Context;
import android.content.SharedPreferences;

class VertretungModelArrayModel {

    private final VertretungModel[] RightRows;
    private final String klasse;

    VertretungModelArrayModel(VertretungModel[] rightRows, String klasse) {
        this.RightRows = rightRows;
        this.klasse = klasse;
    }

    String getKlasse(){
        return klasse;
    }

    VertretungModel[] getRightRows(){
        return RightRows;
    }

    String getHTMLListItems(int id, String ownKlasse, SharedPreferences prefs, Context context){
        StringBuilder content = new StringBuilder();
        String klasse = "";
        for(VertretungModel s : RightRows){
            klasse = s.getKlasse();
            content.append("" +
                    "            <tr>\n" +
                    "              <td>"+s.getStunde()+"</td>\n" +
                    "              <td>"+s.getKlasse()+"</td>\n" +
                    "              <td>"+s.getArt()+"</td>\n" +
                    "              <td>"+s.getFach()+"</td>\n" +
                    "              <td>"+s.getErsatzFach()+"</td>\n" +
                    "              <td>"+s.getVertretungslehrer()+"</td>\n" +
                    "              <td>"+s.getRaum()+"</td>\n" +
                    "              <td>"+s.getHinweise()+"</td>\n" +
                    "            </tr>");
        }
        String color;
        if(klasse.equals(ownKlasse)){
            color=prefs.getString("vertretungEigeneKlasseFarbe","#FF0000");
        }
        else if(klasse.contains("5")||klasse.contains("6")){
            color=prefs.getString("vertretungUnterstufeFarbe","#4aa3df");
        }
        else if(klasse.contains("7")||klasse.contains("8")||klasse.contains("9")){
            color=prefs.getString("vertretungMittelstufeFarbe","#3498db");
        }
        else if(klasse.equals("EF")||klasse.equals("Q1")||klasse.equals("Q2")){
            color=prefs.getString("vertretungOberstufeFarbe","#258cd1");
        }
        else {
            color=prefs.getString("vertretungErrorFarbe","#FF0000");
        }

        String returns =  "<li data-panel-id=\"panel"+id+"\" style=\"background-color: "+color+";\">"+klasse+"</li>\n" +
                "        <div class=\"panel panel"+id+"\">\n" +
                "          <table width=\"99%\">\n" +
                "            <colgroup>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"18%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"27%\"/>\n" +
                "            </colgroup>\n\n";
        if(prefs.getBoolean("vertretungsplanIconsEnabled",true)){
            String stunde = context.getString(R.string.vertretungsplan_stunde);
            String altklasse = context.getString(R.string.vertretungsplan_klasse);
            String vertretungsart = context.getString(R.string.vertretungsplan_vertretungsart);
            String fach = context.getString(R.string.vertretungsplan_fach);
            String ersatzfach = context.getString(R.string.vertretungsplan_ersatzfach);
            String vertretungslehrer = context.getString(R.string.vertretungsplan_vertretungslehrer);
            String raum = context.getString(R.string.vertretungsplan_raum);
            String hinweise = context.getString(R.string.vertretungsplan_hinweise);
            returns+="                       <tr>\n" +
                    "              <td><img src=\"time.png\" alt=\""+stunde+"\" title=\""+stunde+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"group.png\" alt=\""+altklasse+"\" title=\""+altklasse+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"bullet_error.png\" alt=\""+vertretungsart+"\" title=\""+vertretungsart+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"book.png\" alt=\""+fach+"\" title=\""+fach+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"book_edit.png\" alt=\""+ersatzfach+"\" title=\""+ersatzfach+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"user.png\" alt=\""+vertretungslehrer+"\" title=\""+vertretungslehrer+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"door_open.png\" alt=\""+raum+"\" title=\""+raum+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"lightbulb.png\" alt=\""+hinweise+"\" title=\""+hinweise+"\" id=\"area\"/></td>\n" +
                    "            </tr>            ";
        }
        returns+="            "+content.toString()+"\n" +
                        "          </table>\n" +
                        "        </div>";
        return returns;
    }
}
