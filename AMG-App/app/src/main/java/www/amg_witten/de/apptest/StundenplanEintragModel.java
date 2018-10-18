package www.amg_witten.de.apptest;

public class StundenplanEintragModel {

    public String stunde;
    public String fach;
    public String lehrer;
    public String raum;


    public StundenplanEintragModel(String all){
        stunde=all.split("\\|\\|")[0];
        fach=all.split("\\|\\|")[1];
        lehrer=all.split("\\|\\|")[2];
        raum=all.split("\\|\\|")[3];
    }

    public String toString(){
        return stunde+"||"+fach+"||"+lehrer+"||"+raum;
    }

}
