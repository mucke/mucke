package mucke.config;

import org.apache.log4j.Logger;

/**
 * A run is an configured IR activity (e.g. creating an index, performing a batch of searches and evaluating against a gold standard, 
 * or any combination of that). A run is kept very general and intends to introduce great flexibility to the system by separating the 
 * process management from the particulars of what is done with a collection within a research agenda.
 * 
 * A run consists of two elements: 1) The implementation of that abstract class Run, in particular the run method which defines what exactly
 * is done and 2) a specific configuration (as a properties file). Whereas the Run class implementation defines the logic, the properties
 * file determines the choices that are imposed upon this logic. That allows for example to execute one type of run multiple times by using
 * multiple configurations. Configurations of runs are named in the 'primary.properties' file via the property "run.properties". This
 * property points to a list of files. These files must exist in the same directory as the 'primary.properties' file. The Run class for each
 * of these files is defined with the property "class".
 * 
 * @author Ralf Bierig
 */
public interface Run {

    static Logger logger = Logger.getLogger(Run.class);

    /**
     * Executes the evaluation run with the given configuration
     * 
     * @param propertiesFile File that further defines the run
     */
    public void run(String propertiesFile);
}