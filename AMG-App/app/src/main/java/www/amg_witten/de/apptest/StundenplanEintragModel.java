package www.amg_witten.de.apptest;

class StundenplanEintragModel {

    final String stunde;
    final String fach;
    final String lehrer;
    final String raum;


    StundenplanEintragModel(String all){
        stunde=all.split("\\|\\|")[0];
        fach=all.split("\\|\\|")[1];
        lehrer=all.split("\\|\\|")[2];
        raum=all.split("\\|\\|")[3];
    }

    public String toString(){
        return stunde+"||"+fach+"||"+lehrer+"||"+raum;
    }

}
