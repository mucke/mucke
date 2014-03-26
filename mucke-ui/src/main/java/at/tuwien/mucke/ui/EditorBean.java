package at.tuwien.mucke.ui;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "editor")
public class EditorBean {

    private String value;
    private List<Result> resultList;

    {
        resultList = new ArrayList<Result>();
    }

    public void test() {
        if (!this.value.isEmpty()) {
            resultList = new ArrayList<Result>();

            for (int i = 1; i < 18; i++) {
                Result result = new Result("images/dog" + i + ".jpg", "Results No. " + i);
                resultList.add(result);
            }
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
}
