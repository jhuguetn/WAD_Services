/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

/**
 *
 * @author Ralph Berendsen 
 */
public class ResultsBoolean {
    private String type;
    private String niveau;
//    private String datetime;
    private String waarde;
    private String omschrijving;
    private String volgnummer;
    
    public ResultsBoolean(){
        this.type ="boolean";
        this.niveau="";
//        this.datetime="";
        this.waarde="";
        this.omschrijving="";
        this.volgnummer="";
    }
    
    public String getNiveau(){
        return this.niveau;
    }
    
//    public String getDatetime(){
//        return this.datetime;
//    }
    
    public String getWaarde(){
        return this.waarde;
    }
    
    public String getOmschrijving(){
        return this.omschrijving;
    }
    
    public void setNiveau(String niveau){
        this.niveau = niveau;
    }
    
//    public void setDatetime(String datetime){
//        this.datetime = datetime;
//    }
    
    public void setWaarde(String waarde){
        this.waarde = waarde;
    }
    
    public void setOmschrijving(String omschrijving){
        this.omschrijving = omschrijving;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public String getType(){
        return this.type;
    }
    
    public void setVolgnummer(String volgnummer){
        this.volgnummer = volgnummer;
    }
    
    public String getVolgnummer(){
        return this.volgnummer;
    }
}
