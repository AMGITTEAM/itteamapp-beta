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
        System.out.println(content);
        return "<li data-panel-id=\"panel"+id+"\">"+klasse+"</li>\n" +
                "        <div class=\"panel panel"+id+"\">\n" +
                "          <table width=\"100%\">\n" +
                "            <colgroup>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"18%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"9%\"/>\n" +
                "              <col width=\"27%\"/>\n" +
                "            </colgroup>\n" +
                "            "+content.toString()+"\n" +
                "          </table>\n" +
                "        </div>";
    }

}
