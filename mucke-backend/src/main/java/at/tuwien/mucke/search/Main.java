/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tuwien.mucke.search;

import at.tuwien.mucke.concept.Concept;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Alexandra-Mihaela Siriteanu
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        // search for concepts
        /*ConceptSearcher csearcher = new ConceptSearcher();
        List<Concept> concepts = new ArrayList<Concept>();
        try {
            Concept concept1 = new Concept(new URI("tree"));
            Concept concept2 = new Concept(new URI("leaf"));
            concepts.add(concept1);
            concepts.add(concept2);
        } catch (Exception e) {
            logger.info("Exception while creating concepts: " + e.getMessage());
        }
        List<Result> cResults = csearcher.search(concepts);

        // refine search with text results
        String query = "forest";
        TextFacetSearcher tsearcher = new TextFacetSearcher();
        //List<Result> tResults = tsearcher.search(query, cResults);

        // refine search with image results
        String imageId = "appletree";
        ImageFacetSearcher isearcher = new ImageFacetSearcher();
        //List<Result> iResults = isearcher.search(imageId, cResults);
           */
    }
}
