package amgapp;

import java.util.HashMap;

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

    String getHTMLListItems(int id, String ownKlasse, HashMap<String,String> settings){
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
            color=getFromMap(settings,"vertretungEigeneKlasseFarbe","#FF0000");
        }
        else if(klasse.contains("5")||klasse.contains("6")){
            color=getFromMap(settings,"vertretungUnterstufeFarbe","#4aa3df");
        }
        else if(klasse.contains("7")||klasse.contains("8")||klasse.contains("9")){
            color=getFromMap(settings,"vertretungMittelstufeFarbe","#3498db");
        }
        else if(klasse.equals("EF")||klasse.equals("Q1")||klasse.equals("Q2")){
            color=getFromMap(settings,"vertretungOberstufeFarbe","#258cd1");
        }
        else {
            color=getFromMap(settings,"vertretungErrorFarbe","#FF0000");
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
        if(getFromMap(settings,"vertretungsplanIconsEnabled","true").equals("true")){
            String stunde = "Stunde";
            String altklasse = "Klasse";
            String vertretungsart = "Vertretungsart";
            String fach = "Fach";
            String ersatzfach = "Ersatzfach";
            String vertretungslehrer = "Vertretungslehrer";
            String raum = "Raum";
            String hinweise = "Hinweise";
            returns+="                       <tr>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKrSURBVDjLpdPbT9IBAMXx/qR6qNbWUy89WS5rmVtutbZalwcNgyRLLMyuoomaZpRQCt5yNRELL0TkBSXUTBT5hZSXQPwBAvor/fZGazlb6+G8nIfP0znbgG3/kz+Knsbb+xxNV63DLxVLHzqV0vCrfMluzFmw1OW8ePEwf8+WgM1UXDnapVgLePr5Nj9DJBJGFEN8+TzKqL2RzkenV4yl5ws2BXob1WVeZxXhoB+PP0xzt0Bly0fKTePozV5GphYQPA46as+gU5/K+w2w6Ev2Ol/KpNCigM01R2uPgDcQIRSJEYys4JmNoO/y0tbnY9JlxnA9M15bfHZHCnjzVN4x7TLz6fMSJqsPgLAoMvV1niSQBGIbUP3Ki93t57XhItVXjulTQHf9hfk5/xgGyzQTgQjx7xvE4nG0j3UsiiLR1VVaLN3YpkTuNLgZGzRSq8wQUoD16flkOPSF28/cLCYkwqvrrAGXC1UYWtuRX1PR5RhgTJTI1Q4wKwzwWHk4kQI6a04nQ99mUOlczMYkFhPrBMQoN+7eQ35Nhc01SvA7OEMSFzTv8c/0UXc54xfQcj/bNzNmRmNy0zctMpeEQFSio/cdvqUICz9AiEPb+DLK2gE+2MrR5qXPpoAn6mxdr1GBwz1FiclDcAPCEkTXIboByz8guA75eg8WxxDtFZloZIdNKaDu5rnt9UVHE5POep6Zh7llmsQlLBNLSMTiEm5hGXXDJ6qb3zJiLaIiJy1Zpjy587ch1ahOKJ6XHGGiv5KeQSfFun4ulb/josZOYY0di/0tw9YCquX7KZVnFW46Ze2V4wU1ivRYe1UWI1Y1vgkDvo9PGLIoabp7kIrctJXSS8eKtjyTtuDErrK8jIYHuQf8VbK0RJUsLfEg94BfIztkLMvP3v3XN/5rfgIYvAvmgKE6GAAAAABJRU5ErkJggg==\" alt=\""+stunde+"\" title=\""+stunde+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKDSURBVDjLjdFNTNJxHAZw69CWHjp16O2AZB3S1ovOObaI8NBYuuZAhqjIQkzJoSIZBmSCpVuK/sE/WimU6N9SDM0R66IHbabie1hrg0MK3Zo5a8vwidgym8w8PKffvp89e35RAKJ2ipp7WDxvjltZ6jwCr5W2bpHHtqUnx+77877jsZxzlO3roAWXuw5ha1pl9MZdAW2ig8RyXyL8rnx8G6uH387AMnUMC2b6l10BJPdAfWDGhZVREuszT7D6hsTStBNDurO+XQEZnEypx1a28XW2F8HFPqwtOBAYJlCde9EeEZCy4sTN4ksrRA4LZB57vZCfMElUyH4E7Ap86r+LwIAGIy03cDr/lDNJGR/zDyBiHGc3i1ODjUIWtqbdIIexVY86kwZ3HijR/86GmqFqJGhPWs8oTkRvAgb+uZGHhVfRV3UNni41OhU8EDlstBSkwjKjhnmqAg3uUtS6y9Dzvg0ljmKkFCaRm4CJT+/5OERtG4yqZMEwdQt1biV0EyW4PVEE1dsiiMk8eMn0/w9Wp+PCNK1CQ6iBYeommkIpH5Qhy5AF/6Mrf4G955tUJlXxtsHieeWQ2LJxvVuAAkoASUcmLugZPqW0qsprEQjDx3sY3ZIMhXt1+DNw77kdmnYKSsKKx+PfoTQtYX9KtzWG2Rod6aujaJwWHk8+uDawGITeA+SPA7nDQOYgwKcAYhQQajyIY9eQEYE5feLPyV4jFC8CELkAkWMDQmoDPGsQaWYgzRjEU8vL8GARAV8T099bUwqBdgzS14D4VaiBA8gZALJ/t6j1Qqu4Hx4sIvChoyDFWZ1RmcyzORJLJsDSzoUyD5Z6FsxKN+iXn/mM5ZLwYJGAX0F/sgCQt3xBAAAAAElFTkSuQmCC\" alt=\""+altklasse+"\" title=\""+altklasse+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAFYSURBVDjLY/z//z8DJYCJgUJAsQEsuCQeHIgP/f/vX/H/f//9lFyWvCLJBff2xPD9+/27kV/O3xxIl5HsBaCmAj5Zb00+SUOGPz9/J19fF2BKtAG3NoVoATXl84oIMPz9tIlBXC9F4O/PX7WXl3iwEjQAaBPTn5+/KkW1ooUYfpxjOLVoKQOPwHeGPz9++QCxH0EDgDa5cQnrxfAKfmP49/M+A8P/fwx/v5xmUHQoZvzz82fzqUmWvDgNuLjQjQ1oS4uAnAHDv2+XgHq/MxgHqzP8+/WMgYPjFoO4boQm0HWFOA0A2p4qpOJtzMX7huH/n7cMDIzMDGfX3QIFKcO/H7cYRNXkgWp+Zx9q0tHCmg7+/PgJ9Ls/0MgHDEx8okCR/wxmSQFwe5g5lRmUXMvFbm1uagQKhGIa8PMXx7nZwd+BCQfo/H9I+D+cZgDR//9LILuAcehnJgBMs6gZ4tipDAAAAABJRU5ErkJggg==\" alt=\""+vertretungsart+"\" title=\""+vertretungsart+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAHjSURBVDjLdZO/alVBEMZ/5+TemxAbFUUskqAoSOJNp4KC4AsoPoGFIHY+gA+jiJXaKIiChbETtBYLUbSMRf6Aydndmfks9kRjvHdhGVh2fvN9uzONJK7fe7Ai6algA3FZCAmQqEF/dnihpK1v7x7dPw0woF64Izg3Xl5s1n9uIe0lQYUFCtjc+sVuEqHBKfpVAXB1vLzQXFtdYPHkGFUCoahVo1Y/fnie+bkBV27c5R8A0pHxyhKvPn5hY2MHRQAQeyokFGJze4cuZfav3gLNYDTg7Pklzpw4ijtIQYRwFx6BhdjtCk+erU0CCPfg+/o2o3ZI13WUlLGo58YMg+GIY4dmCWkCAAgPzAspJW5ePFPlV3VI4uHbz5S5IQfy/yooHngxzFser30iFcNcuAVGw3A0Ilt91IkAsyCXQg5QO0szHEIrogkiguwN2acCoJhjnZGKYx4Ujz5WOA2YD1BMU+BBSYVUvNpxkXuIuWgbsOxTHrG3UHIFWIhsgXtQQpTizNBS5jXZQkhkcywZqQQlAjdRwiml7wU5xWLaL1AvZa8WIjALzIRZ7YVWDW5CiIj48Z8F2pYLl1ZR0+AuzEX0UX035mxIkLq0dhDw5vXL97fr5O3rfwQHJhPx4uuH57f2AL8BfPrVlrs6xwsAAAAASUVORK5CYII=\" alt=\""+fach+"\" title=\""+fach+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAK/SURBVDjLbZNNaFRXFMd/72U+dDLNRItGUSeJiagTJ6IQhdhaWoopFCJiF10UBAXpSlHcddHi0oUbkXYRFURE/NiIIjSkpCpdtGoTJG20iUMsMZJokhmqee/de8/p4jmDggcuFw73/s7/nPu/nqrSe/hch6peUZhD6VYUVUCVeNPaEmcwYbn06/nv1gIkiA8cVNhQLOS96ZkyqtVLEMMEFZgvv2IhVEQTrbyJGAA7i4U13qeda8ivLKIxAVGJq0pcfVljhsyiBDt2f8s7AFSXFDuauXVvjLm516gIAFJVoYqKMl95TRBGvB1vWsBLpBKs29RMe9NSnANVQURxTnEiWFEWAsPlq4PvAyjOCRPTFVJ+kiAIMGGElThvqSORTFFID3Oy+xfqdnUyfLZHvWByX3UGiBOsM4RhyJ5t7bH8WB2qyp27fWxLP2dx8RtyrVuYL61n9Oe+EzUFxgnOWKzzuTD4F6GxWKc4K7Sk/2DPpjINuR3Mjv9Nyov4oGEF2Q/zuRrAWiEyhkhA/TReMgm+sjr1gL0bZ2lc20M4dYlUxmNiaBQTRC+Dhf+6q0PEWIcNLKFxWCcYJ6zkPl93lMi19RJM/oSfsiSzzQSzI4j1P+862v/YrylwggkNoXEExrGkfJuv2sbJtfcSTP6InzRElRaeDtzj+4EGth7tHwLw327BRDGgsXKXL/LPWN7xJdHzPupSSlhpZur2fX4Y+Yyx+XTtGf2qYSLrsKGl/lk/vflphFVMPTyFEPBqdhWlwYdcW3SYF1H2vUaKDRM5CjpA4aMzPLp0jMd3fiOd30x5ZoqbyYNkMktRxhCRp+8oUFXwfbq2d/JofIZo5Aatmz+mvn49//75D0NNh8g2tWGtoAphENbs6Kkqn+w/3afKAUVZ8eQ4W1uX0bWhhYmonqulTuZMtvYzUa7/fvHI7irgf/y+taODWkwAAAAAAElFTkSuQmCC\" alt=\""+ersatzfach+"\" title=\""+ersatzfach+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJ3SURBVDjLpZNtSNNRFIcNKunF1rZWBMJqKaSiX9RP1dClsjldA42slW0q5oxZiuHrlqllLayoaJa2jbm1Lc3QUZpKFmmaTMsaRp+kMgjBheSmTL2//kqMBJlFHx44XM7vOfdyuH4A/P6HFQ9zo7cpa/mM6RvCrVDzaVDy6C5JJKv6rwSnIhlFd0R0Up/GwF2KWyl01CTSkM/dQoQRzAurCjRCGnRUUE2FaoSL0HExiYVzsQwcj6RNrSqo4W5Gh6Yc4+1qDDTkIy+GhYK4nTgdz0H2PrrHUJzs71NQn86enPn+CVN9GnzruoYR63mMPbkC59gQzDl7pt7rc9f7FNyUhPY6Bx9gwt4E9zszhWWpdg6ZcS8j3O7zCTuEpnXB+3MNZkUUZu0NmHE8XsL91oSWwiiEc3MeseLrN6woYCWa/Zl8ozyQ3w3Hl2lYy0SwlCUvsVi/Gv2JwITnYPDun2Hy6jYuEzAF1jUBCVYpO6kXo+NuGMeBAgcgfwNkvgBOPgUqXgKvP7rBFvRhE1crp8Vq1noFYSlacVyqGk0D86gbART9BDk9BFnPCNJbCY5aCFL1Cyhtp0RWAp74MsKSrkq9guHyvfMTtmLc1togpZoyqYmyNoITzVTYRJCiXYBIQ3CwFqi83o3JDhX6C0M8XsGIMoQ4OyuRlq1DdZcLkmbgGDX1iIEKNxAcbgTEOqC4ZRaJ6Ub86K7CYFEo8Qo+GBQlQyXBczLZpbloaQ9k1NUz/kD2myBBKxRZpa5hVcQslalatoUxizxAVVrN3CW21bFj9F858Q9dnIRmDyeuybM71uxmH9BNBB1q6zybV7H9s1Ue4PM3/gu/AEbfqfWy2twsAAAAAElFTkSuQmCC\" alt=\""+vertretungslehrer+"\" title=\""+vertretungslehrer+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAGOSURBVDjLlZPNapNBFIafSdOvtbFgSi1dREEtguDSnTfStbgW9A6y9BICinfkRosRFw1mE5BoS4rNzPlzEfOrYjJwOGfzPvO+h5kUEWx6zt6+eO1ur8x0VN9E+Ondyy/udlLdPua8d8ZBrdIZoN1uh7szLTOb9WePgxpOdXjMzXsnuDlx/gGRzAxgZrRaLQBSSks94iPNJ0+BRL4aYpKJcER0GbAqns5mhptRRgNMC1Aj3P50sChanFULboJpwbUAiXCnlPEcoKr/BJgWQhWXMnEQE4DKmNrfHKyW/L7ZJBNyzVGzR4RSSp4DFh2sOhEpmCpWMo0bPzi4NWR76xqR/0SYA8a4ZkwyF9+3cD0kl8HyEqeA1fwpJUrJuAouGRNhmOvgjkhZD6AynuxABdNMSnXcHdU1AUXyRCwZl0JKTsQGAJFJhL3mHVwFzT8hBpgpqdPpRLfbpd/vL73/xX56v0djf5+d3QbV7h7b1Q6jqwu+fn7/La3znd88v3tkpg/M5JGZPnS3Vq1enZrky19GcE/tIr8QhwAAAABJRU5ErkJggg==\" alt=\""+raum+"\" title=\""+raum+"\" id=\"area\"/></td>\n" +
                    "              <td><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKgSURBVDjLlZLrS1NxGMd90ZvovdEfEBEUEhZIb0xMjdyLIuyGkiHGUFKydFKKJiRegjIyFJRwojMxzfJSaVOYeTfxtpSNuZ1tXnY2z27nsss5334uWloG9uLD7/A7z/fzPPx4IgBE7ISl3qWyelUvu9JIueZqeOdUmcCMFDgcQ3fntjSK0j/rwx+csesIZ3jbL1j6EbCPIej5DpE3QRIoBJ3LEFb74BjIxkbXVYNdrTixS8Ca3h/y6pSTfloD0UcRjCS8BJGbRdA7QRgjd1pIfhruyeewKOMdm+rCw2GBV1tXKZh7SIEVoqAjpwVS0AlIvhBSkCGyeQRcPYDogO1DNixvrveFBa6ZCkuAmSe1OtJpFVLATkJboWCIAE3+GYngI6ENgnUK+hcxfFiw9fWRT+RWEWTHEeRmyPhaMvYCgu5ZEpgkbzCCgPszBNsr8NY8iF4Ky5WnpLDArs41+zYnSPdF8OYi0qEcTHc6mF45mJ4M2Ftl4C1lYPU34KerwFNTWKmO/j2BfbiwghmvJuPawZsUsNVHgTPlEx6ANcjJeR9r5QfhWUqEJOlhbc+FoV42FBY4R0sPbPbKlz2LLeQB9aCbYkJhzpIFlkoDZ8zDRk0kRHYYrm8d0JYeEyyduUd37QH9pTBqvSOV9iy0wtmZ+VNAOm+HOeM92JtlYDQN0JYcD1BtmTf/WqRtbJ/yTxtUt9fXGhPBq5MhriVBtMYhoLkMQ1Ek5sqi3eb2O4l7buIvhlRPkmsfZ/ibax+iruosnpacQUFOOq7Fn5TUypJz/1zlnRQr5JSypRVKZRvq6htR/ewlriTH03vV7ilQ5NwaHRgchM1GY3p6Bq+bmpEii9XtWzCgqkhLuXSBTUg4L8XFxUoXk2K57obirH0L/ocfNQ8V8wE+uE0AAAAASUVORK5CYII=\" alt=\""+hinweise+"\" title=\""+hinweise+"\" id=\"area\"/></td>\n" +
                    "            </tr>            ";
        }
        returns+="            "+content.toString()+"\n" +
                        "          </table>\n" +
                        "        </div>";
        return returns;
    }
    
    private String getFromMap(HashMap<String,String> settings, String key, String defaultValue) {
    	return settings.containsKey(key) ? (String) settings.get(key) : defaultValue;
    }
}
