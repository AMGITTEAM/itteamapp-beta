package www.amg_witten.de.apptest;

class ItemModel {

    private VertretungModel[] RightRows;

    ItemModel(VertretungModel[] rightRows) {
        this.RightRows = rightRows;
    }

    String getHTMLListItems(int id){
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
        if(klasse.contains("5")||klasse.contains("6")){
            color="#4aa3df";
        }
        else if(klasse.contains("7")||klasse.contains("8")||klasse.contains("9")){
            color="#3498db";
        }
        else {
            color="#258cd1";
        }

        return "<li data-panel-id=\"panel"+id+"\" style=\"background-color: "+color+";\">"+klasse+"</li>\n" +
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
                "            </colgroup>\n\n" +
                "                       <tr>\n" +
                "              <td><img src=\"time.png\" alt=\"Stunde\" title=\"Stunde\" id=\"area\"/></td>\n" +
                "              <td><img src=\"group.png\" alt=\"Klasse\" title=\"Klasse\" id=\"area\"/></td>\n" +
                "              <td><img src=\"bullet_error.png\" alt=\"Vertretungsart\" title=\"Vertreungsart\" id=\"area\"/></td>\n" +
                "              <td><img src=\"book.png\" alt=\"Fach\" title=\"Fach\" id=\"area\"/></td>\n" +
                "              <td><img src=\"book_edit.png\" alt=\"Ersatzfach\" title=\"Ersatzfach\" id=\"area\"/></td>\n" +
                "              <td><img src=\"user.png\" alt=\"Vertretungslehrer\" title=\"VertretungslehrerIn\" id=\"area\"/></td>\n" +
                "              <td><img src=\"door_open.png\" alt=\"Raum\" title=\"Raum\" id=\"area\"/></td>\n" +
                "              <td><img src=\"lightbulb.png\" alt=\"Hinweise\" title=\"Hinweise\" id=\"area\"/></td>\n" +
                "            </tr>            " +
                "            "+content.toString()+"\n" +
                "          </table>\n" +
                "        </div>";
    }

}
