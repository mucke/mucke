package at.tuwien.mucke.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name="conceptService", eager = true)
@ApplicationScoped
public class ConceptService {

    private List<Concept> concepts;

    @PostConstruct
    public void init() {

        //
        // TODO: Dynamically read concept filenames and map them to array
        //

        concepts = new ArrayList<Concept>();
        concepts.add(new Concept(0, "Red", "red"));
        concepts.add(new Concept(1, "Green", "green"));
        concepts.add(new Concept(2, "Blue", "blue"));
    }

    public List<Concept> getConcepts() {
        return concepts;
    }
}