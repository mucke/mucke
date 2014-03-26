package at.tuwien.mucke.ui;

/**
 * Created by georgios on 3/20/14.
 */
public class Result {
    private String url;
    private String title;

    public Result(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
