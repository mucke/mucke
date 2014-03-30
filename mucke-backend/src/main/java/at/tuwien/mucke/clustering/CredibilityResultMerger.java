package at.tuwien.mucke.clustering;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.credibility.CredibilityManager;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Merges results with user credibility. Updates the scores of the original result list.
 */
public class CredibilityResultMerger implements ResultMerger {

    static Logger logger = Logger.getLogger(CredibilityResultMerger.class);

    private ConfigurationManager configManager;

    public CredibilityResultMerger(ConfigurationManager configManager){
        this.configManager = configManager;
    }

    @Override
    public List<Result> merge(List<Result> results) {

        logger.info("Merging crediblities...");

        CredibilityManager credibilityManager = new CredibilityManager(this.configManager);

        for (Result result : results){

            // extract scores
            float resultScore = result.getScore();
            float userScore = credibilityManager.getUser(result.getUserId()).getCredibilityScore();
            float[] scores = {resultScore, userScore};

            // calculate harmonic mean
            float newScore = CredibilityResultMerger.harmonicMean(scores);

            logger.info("Merge: resultScore: " + resultScore + "     userScore: " + userScore + "      --> newScore: " + newScore);

            // re-assign the new score
            result.setScore(newScore);

        }

        // sort results
        Collections.sort(results);

        return results;
    }

    /** Calculates the harmonic mean */
    public static float harmonicMean(float[] scores) {

        float sum = 0.0f;

        for (int i = 0; i < scores.length; i++){
            sum += 1.0 / scores[i];
        }

        return scores.length / sum;
    }

}