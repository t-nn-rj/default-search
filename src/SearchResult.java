import java.util.Map;

public class SearchResult {
    public String asin;
    public String title;
    public String description;
    public String[][] categories;
    public String imUrl;
    public Map<String, Integer> salesRank;
    public float price;

    public float relevance;

    public SearchResult(){
        relevance = 0;
    }
}
