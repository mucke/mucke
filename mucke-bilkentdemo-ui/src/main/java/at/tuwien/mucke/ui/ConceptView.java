package at.tuwien.mucke.ui;

import at.tuwien.mucke.SystemManager;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
public class ConceptView {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(ConceptView.class);

    private String txt1;
    private String txt2;
    private String txt3;
    private String txt4;
    private String txt5;
    private String txt6;
    private String txt7;
    private String txt8;
    private Concept concept1;
    private Concept concept2;
    private Concept concept3;
    private Concept concept4;
    private List<Concept> selectedConcepts;
    private List<Result> resultList;

    @ManagedProperty("#{conceptService}")
    private ConceptService service;


    public void run() {

        // Initialize manager
        System.out.println("Creating a new System Manager ...");
        SystemManager manager = new SystemManager();

        System.out.println("Executing Interactive Mode ...");

        // build query
        logger.info("Selected concepts: " + this.getSelectedConcepts());
        String query = "";
        for (Concept c : selectedConcepts){
            logger.info("Selected concept being added to query: " + c.getName());
            query = query + c.getName() + " ";
        }
        query = query.trim();

        // search for concepts in the backend
        logger.info("Calling executeConceptSearch() with query: " + query);
        this.resultList = manager.executeConceptSearch(query);

    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }


    public List<String> completeText(String query) {
        List<String> results = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            results.add(query + i);
        }

        return results;
    }

    public List<Concept> completeConcept(String query) {

        List<Concept> allConcepts = service.getConcepts();
        List<Concept> filteredConcepts = new ArrayList<Concept>();

        for (int i = 0; i < allConcepts.size(); i++) {
            Concept c = allConcepts.get(i);
            if (c.getName().toLowerCase().contains(query)) {
                filteredConcepts.add(c);
            }
        }

        return filteredConcepts;
    }

    public void onItemSelect(SelectEvent event) {

        logger.info("onItemSelected called!");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public String getTxt1() {
        return txt1;
    }

    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    public String getTxt2() {
        return txt2;
    }

    public void setTxt2(String txt2) {
        this.txt2 = txt2;
    }

    public String getTxt3() {
        return txt3;
    }

    public void setTxt3(String txt3) {
        this.txt3 = txt3;
    }

    public String getTxt4() {
        return txt4;
    }

    public void setTxt4(String txt4) {
        this.txt4 = txt4;
    }

    public String getTxt5() {
        return txt5;
    }

    public void setTxt5(String txt5) {
        this.txt5 = txt5;
    }

    public String getTxt6() {
        return txt6;
    }

    public void setTxt6(String txt6) {
        this.txt6 = txt6;
    }

    public String getTxt7() {
        return txt7;
    }

    public void setTxt7(String txt7) {
        this.txt7 = txt7;
    }

    public String getTxt8() {
        return txt8;
    }

    public void setTxt8(String txt8) {
        this.txt8 = txt8;
    }

    public Concept getConcept1() {
        return concept1;
    }

    public void setConcept1(Concept concept1) {
        this.concept1 = concept1;
    }

    public Concept getConcept2() {
        return concept2;
    }

    public void setConcept2(Concept concept2) {
        this.concept2 = concept2;
    }

    public Concept getConcept3() {
        return concept3;
    }

    public void setConcept3(Concept concept3) {
        this.concept3 = concept3;
    }

    public Concept getConcept4() {
        return concept4;
    }

    public void setConcept4(Concept concept4) {
        this.concept4 = concept4;
    }

    public List<Concept> getSelectedConcepts() {
        return selectedConcepts;
    }

    public void setSelectedConcepts(List<Concept> selectedConcepts) {
        this.selectedConcepts = selectedConcepts;
    }

    public void setService(ConceptService service) {
        this.service = service;
    }
}