/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import wad.xml.ReadConfigXML;

/**
 *
 * @author titulaer
 */
public class WAD_Processor {

    Timer processListTimer;
    private static Log log = LogFactory.getLog(WAD_Processor.class);
    private static Properties logProperties = new Properties();

    private WAD_Processor() {
        processListTimer = new Timer();
        CheckNewJobs c = new CheckNewJobs();
        //c.aantalConcurrentJobs=2;
        //Lezen van de inetrval timer setting uit config.xml
        Integer interval = Integer.parseInt(ReadConfigXML.readSettingsElement("timer"));
        //lezen van aantal jobs uit de config.xml
        c.aantalConcurrentJobs = Integer.parseInt(ReadConfigXML.readSettingsElement("jobs"));
        processListTimer.schedule(c, 0, interval);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // gebruik config/log4j.properties indien aanwezig
        // wanneer niet gevonden wordt de log4j.properties gebruikt uit de src folder (komt in jar-file terecht)
        try {
            logProperties.load(new FileInputStream("config/log4j.properties"));
            PropertyConfigurator.configure(logProperties);
        } catch (IOException ex) {
            log.error(ex);
        }

        WAD_Processor client = new WAD_Processor();
    }
}