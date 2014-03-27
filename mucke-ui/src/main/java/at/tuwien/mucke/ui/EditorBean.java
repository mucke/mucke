package at.tuwien.mucke.ui;

import at.tuwien.mucke.SystemManager;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "editor")
public class EditorBean {

    private String value;
    private boolean credibilityEnabled;
    private List<Result> resultList;

    {
        resultList = new ArrayList<Result>();
    }

    public void run() {

        // Initialize manager
        System.out.println("Creating a new System Manager ...");
        SystemManager manager = new SystemManager();
        System.out.println("Executing Batch Mode ...");
        manager.executeBatchMode();

    }

    public void testResults() {
        if (!this.value.isEmpty()) {
            resultList = new ArrayList<Result>();

            for (int i = 1; i < 18; i++) {
                Result result = new Result("images/dog" + i + ".jpg", "Results No. " + i);
                resultList.add(result);
            }
        }
    }

    public void testSettings() {
        // Initialize manager
        //SystemManager manager = new SystemManager();
        // test stuff
        //manager.executeBatchMode();

        if (this.credibilityEnabled == true){
            System.out.println("Credibility is enabled!");
        } else {
            System.out.println("Credibility is disabled!");
        }

    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getCredibilityEnabled() {
        return credibilityEnabled;
    }

    public void setCredibilityEnabled(boolean credibilityEnabled) {
        this.credibilityEnabled = credibilityEnabled;
    }

    public void addCredibility() {
        String summary = credibilityEnabled ? "Checked" : "Unchecked";

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }


}
