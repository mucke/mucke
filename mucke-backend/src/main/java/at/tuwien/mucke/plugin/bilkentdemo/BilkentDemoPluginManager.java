package at.tuwien.mucke.plugin.bilkentdemo;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.PluginManager;
import at.tuwien.mucke.plugin.bilkentdemo.index.BilkenDemoConceptIndexer;
import at.tuwien.mucke.plugin.bilkentdemo.search.BilkentDemoConceptSearcher;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Classifies images into concepts with probabilities and builds a concept index that can be searched.
 *
 * @author Ralf Bierig
 */
public class BilkentDemoPluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(BilkentDemoPluginManager.class);


    /**
     * Constructor
     */
    public BilkentDemoPluginManager(ConfigurationManager configManager) {
        super(configManager);
    }


    /**
     * Runs the plugin as a complete process in batch mode
     */
    public void run(String propertiesFile) {
        super.run(propertiesFile);

        logger.info("Start BilkentDemoPluginManager run...");

        // step 1: prepare collection
        // TODO, currently done in step 2, will be separated later

        // step 2: classfication call (Python/Anaconda)
        this.classify();

        // Step 3: index
        this.index();

        // Step 4: search
        this.search("red");

        logger.info("Done!");
    }

    /** Prepares data set for classifier */
    public void prepare(){
        // TODO
    }

    /** Trains classifiers and determines concepts for images  */
    public void classify(){

        // determine current working directory for Python call
        String currentDir = "";
        try {
            currentDir = new File(".").getCanonicalPath();
        } catch (Exception e){
            logger.error("Error while determining current location: " + e.getMessage());
        }

        String call = "python " + currentDir +
        "/src/main/java/at/tuwien/mucke/plugin/bilkentdemo/FullClassification.py " +
                configManager.getProperty(ConfigConstants.TRAIN_FOLDER) + " " +
                configManager.getProperty(ConfigConstants.TEST_FOLDER) + " " +
                configManager.getProperty(ConfigConstants.MAPPING_FILE) + " " +
                configManager.getProperty(ConfigConstants.RESULT_FOLDER);
        logger.info("call: " + call);
        String resultFull = externalCall(call);
        logger.info("Result full:" + resultFull);
    }

    /** Build concept index */
    public void index(){
        logger.info("index() entered...");
        BilkenDemoConceptIndexer indexer = new BilkenDemoConceptIndexer(configManager);
        indexer.index();
    }

    /** Serach for the given query */
    public List<Result> search(String query){
        logger.info("search() entered ...");
        logger.info("search() query: " + query);
        BilkentDemoConceptSearcher searcher = new BilkentDemoConceptSearcher(configManager);
        return searcher.search(query);
    }

    /** Calls external command
     * @param commandLine
     * @return Result */
    private String externalCall(String commandLine){

        String resultValue = "";
        try {

            // create Runtime environment and call
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(commandLine);
            p.waitFor();

            // log output return values
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = b.readLine()) != null) {
                resultValue = resultValue + line;
            }
            b.close();

        } catch (Exception e){
            logger.error("Exception while executing command: " + e.getMessage());
        }
        return resultValue;

    }

}