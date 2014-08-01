package at.tuwien.mucke.ui;

import at.tuwien.mucke.SystemManager;
import at.tuwien.mucke.concept.Concept;

//import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
//import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "editor")
public class EditorBean {

    private String value;
    private List<Concept> conceptList;

    {
        conceptList = new ArrayList<Concept>();

    }

    public void run() {

        // Initialize manager
        System.out.println("Creating a new System Manager ...");
        SystemManager manager = new SystemManager();

        System.out.println("Executing Interactive Mode ...");
        this.conceptList = manager.executeConceptExtraction(value);

        System.out.println("Editor run() called...");

    }

    public List<Concept> getConceptList() {
        return conceptList;
    }

    public void setConceptList(List<Concept> conceptList) {
        this.conceptList = conceptList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}