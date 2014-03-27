package at.tuwien.mucke.clustering;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.credibility.CredibilityManager;
import at.tuwien.mucke.search.Result;

import java.util.List;

/**
 * Merges results with user credibility. Updates the scores of the original result list.
 */
public class CredibilityResultMerger implements ResultMerger {

    private ConfigurationManager configManager;

    public CredibilityResultMerger(ConfigurationManager configManager){
        this.configManager = configManager;
    }

    @Override
    public List<Result> merge(List<Result> results) {

        CredibilityManager credibilityManager = new CredibilityManager(configManager);

        for (Result result : results){
            float resultScore = result.getScore();
            float userScore = credibilityManager.getUser(result.getUserId()).getCredibilityScore();
            //float newScoare = harmonicMean(new float[]{resultScore, userScore});

        }

        return null;
    }

    /** Calculates the harmonic mean */
    public static double harmonicMean(float[] scores) {

        float sum = 0.0f;

        for (int i = 0; i < scores.length; i++){
            sum += 1.0 / scores[i];
        }

        return scores.length / sum;
    }

}

