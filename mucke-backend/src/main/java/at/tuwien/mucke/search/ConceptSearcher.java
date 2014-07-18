package at.tuwien.mucke.search;

import at.tuwien.mucke.concept.Concept;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.Facet;
import at.tuwien.mucke.documentmodel.TextFacet;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Search with concepts
 * TODO Not so sure what to do with this! Do i need this? the problem of translating text into concepts or image into concepts or tags into concepts is done
 * by the DocumentFacetProcessor. ConceptSearcher competes with the DocumentFacetProcessor!
 */
public class ConceptSearcher {

    static Logger logger = Logger.getLogger(ConceptSearcher.class);

    public ConfigurationManager configurationManager = null;

    /** Constructor */
    public ConceptSearcher(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
    }

    /**
     * Search for documents for the given list of concepts
     * @param concepts A list of concepts
     * @return A list of document results
     */
    public List<Result> search(List<Concept> concepts) {

        // logging
        logger.info("Searching for a list of the following concepts:");
        for (Concept concept : concepts) {
            logger.info("concept: " + concept.getId());
        }

        return new ArrayList<Result>();
    }


    /** Search for concepts for the given facet
     * @param facet A facet
     * @return List of concepts that relate to the given facet */
    public List<Result> search(Facet facet) {

        logger.info("Searching for concepts fitting the facet '" + facet.getName() + "'");

        // search for text facets in the concept index
        List<Result> results = new ArrayList<>();

        if (facet instanceof TextFacet){

            FacetSearcher facetSearcher = new TextFacetSearcher("conceptSearcher", this.configurationManager);
            results = facetSearcher.search(facet.getContent(), null);

        }

        return results;
    }

}