/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import selector.Selector;
import wad.xml.Instance;
import wad.xml.Patient;
import wad.xml.ReadConfigXML;
import wad.xml.Series;

/**
 *
 * @author Ralph Berendsen 
 */
public class GetPatientFromIqcDatabase {
    private Patient patient;
    
    private static Log log = LogFactory.getLog(GetPatientFromIqcDatabase.class);
    
    public GetPatientFromIqcDatabase(Connection dbConnection, String pk, String level){
        patient = new Patient();
        if (level.equals("study")||level.equals("STUDY") || level.equals("studie")||level.equals("STUDIE")){
            getPatientByStudy(dbConnection,pk);
        } else if (level.equals("series")||level.equals("SERIES") || level.equals("serie")||level.equals("SERIE")){
            getPatientBySeries(dbConnection,pk);
        } else if (level.equals("instance")||level.equals("INSTANCE")){
            getPatientByInstance(dbConnection,pk);
        }
    }
    
    public Patient getPatient(){
        return this.patient;
    }
    
    //Patient middels studyPk geeft alle series van deze study
    private void getPatientByStudy(Connection dbConnection, String studyPk){
//        DatabaseParameters iqcDBParams = new DatabaseParameters();
//        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(dbConnection, studyPk);
        setPatient(dbConnection, patientPk);
        setStudyByStudyFk(dbConnection, studyPk);
        //voor alle series in deze study series vullen
        ArrayList<String> seriesPkList = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(dbConnection, studyPk);
        for (int i=0;i<seriesPkList.size();i++){
            setSeries(dbConnection, seriesPkList.get(i));
            ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(dbConnection, seriesPkList.get(i));
            for (int j=0;j<instancePkList.size();j++){
                setInstance(dbConnection, instancePkList.get(j),i);                
            }
        }        
    }
    
    //Patient middels seriesPk geeft maar 1 serie en bijbehorende study
    private void getPatientBySeries(Connection dbConnection, String seriesPk){
//        DatabaseParameters iqcDBParams = new DatabaseParameters();
//        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String studyPk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(dbConnection, seriesPk);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(dbConnection, studyPk);
        setPatient(dbConnection, patientPk);
        setStudyByStudyFk(dbConnection, studyPk);
        //voor alleen deze series in deze study vullen
        setSeries(dbConnection, seriesPk);
        ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(dbConnection, seriesPk);
        for (int j=0;j<instancePkList.size();j++){
            setInstance(dbConnection, instancePkList.get(j),0);                
        }
         
    }
    
    //Patient middels instancePk geeft maar 1 serie en bijbehorende study
    private void getPatientByInstance(Connection dbConnection, String instancePk){
//        DatabaseParameters iqcDBParams = new DatabaseParameters();
//        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String seriesPk = ReadFromIqcDatabase.getSeriesFkByInstanceFkFromInstance(dbConnection, instancePk);
        String studyPk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(dbConnection, seriesPk);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(dbConnection, studyPk);
        setPatient(dbConnection, patientPk);
        setStudyByStudyFk(dbConnection, studyPk);
        //voor alleen deze series in deze study vullen
        setSeries(dbConnection, seriesPk);
        ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(dbConnection, seriesPk);
        setInstance(dbConnection, instancePk,0);                
        
         
    }
    
    //Get patientinformation from database
    private void setPatient(Connection dbConnection, String patientFk){        
        ResultSet rs_patient;        
        Statement stmt_patient;
        
        try {
            stmt_patient = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_patient = stmt_patient.executeQuery("SELECT * FROM patient WHERE pk='"+patientFk+"'");
            while (rs_patient.next()) {
                this.patient.setId(rs_patient.getString("pat_id"));
                this.patient.setName(rs_patient.getString("pat_name"));                            
            }
            stmt_patient.close();
            rs_patient.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{GetPatientFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
    
    //Gets study information from db and add to patient
    private void setStudyByStudyFk(Connection dbConnection, String studyFk){        
        ResultSet rs_study;        
        Statement stmt_study; 
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study WHERE pk='"+studyFk+"'");
            while (rs_study.next()) {
                this.patient.getStudy().setUid(rs_study.getString("study_iuid"));
                this.patient.getStudy().setDescription(rs_study.getString("study_desc"));                            
            }
            stmt_study.close();
            rs_study.close();
        } catch (SQLException ex) {            
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{GetPatientFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
    
    private void setSeries(Connection dbConnection, String seriesFk){        
        ResultSet rs_series;        
        Statement stmt_series; 
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM series WHERE pk='"+seriesFk+"'");
            while (rs_series.next()) {
                Series series = new Series();
                series.setDescription(rs_series.getString("series_desc"));
                series.setNumber(rs_series.getString("series_no"));
                this.patient.getStudy().addSeries(series);
            }
            stmt_series.close();
            rs_series.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{GetPatientFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
    
    private void setInstance(Connection dbConnection, String instanceFk, int serieNo){        
        ResultSet rs_instance;        
        Statement stmt_instance; 
        
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_instance = stmt_instance.executeQuery("SELECT * FROM instance WHERE pk='"+instanceFk+"'");
            while (rs_instance.next()) {
                Instance instance = new Instance();                
                String filepath = "";
                String dirpath = "";
                instance.setNumber(rs_instance.getString("inst_no"));
                Statement stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs_files = stmt_files.executeQuery("SELECT * FROM files WHERE instance_fk='"+instanceFk+"'");
                while (rs_files.next()){
                    String filessystemFk = rs_files.getString("filesystem_fk");
                    filepath = rs_files.getString("filepath");
                    filepath = filepath.replace("/", File.separator);
                    Statement stmt_filesystem = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs_filesystem = stmt_filesystem.executeQuery("SELECT * FROM filesystem WHERE pk='"+filessystemFk+"'");
                    while (rs_filesystem.next()){
                        dirpath = rs_filesystem.getString("dirpath");
                    }
                    rs_filesystem.close();
                    stmt_filesystem.close();
                }
                String archivePath = ReadConfigXML.readFileElement("archive");
                archivePath = archivePath.replace("/", File.separator);
                // VUmc - JK - 20121203 - allow multiple archive locations
                // If database table filesystem column dirpath starts with '/', '\' or has ':' as 2nd character
                // then we assume the dirpath is absolute and we don't need to add the archivePath. However,
                // if no archive location is configured in DCM4CHEE, dirpath is relative and starting from
                // the archive path, so it needs to be added in that case.
                if ( (dirpath.indexOf("/") == 0) || (dirpath.indexOf("\\") == 0) || (dirpath.indexOf(":") == 1) ) {
                    // assume this is an absolute path
                    instance.setFilename(dirpath+File.separator+filepath);
                } else {
                    // assume this is a relative path and need to add configured archivePath in front
                    instance.setFilename(archivePath+dirpath+File.separator+filepath);
                }
                // End VUmc - JK - 20121203
                rs_files.close();
                stmt_files.close();  
                this.patient.getStudy().getSeries(serieNo).addInstance(instance);
            }
            stmt_instance.close();
            rs_instance.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{GetPatientFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
}
