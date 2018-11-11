package www.amg_witten.de.apptest;

class StundenplanEintragModel {

    final String stunde;
    String fach;
    String lehrer;
    String raum;
    String fachName;


    StundenplanEintragModel(String all){
        stunde=all.split("\\|\\|")[0];
        try {
            fach=all.split("\\|\\|")[1];
            lehrer=all.split("\\|\\|")[2];
            raum=all.split("\\|\\|")[3];
            fachName= all.split("\\|\\|")[4];
        } catch(ArrayIndexOutOfBoundsException e){
            if(fach==null){
                fach="";
            }
            if(lehrer==null){
                lehrer="";
            }
            if(raum==null){
                raum="";
            }
            if(fachName==null){
                fachName="";
            }
        }
    }

    public String toString(){
        return stunde+"||"+fach+"||"+lehrer+"||"+raum;
    }

}
