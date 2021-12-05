import gnu.trove.iterator.TObjectIntIterator;
import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.util.WordLists;
import org.lemurproject.galago.utility.Parameters;

import java.util.*;

public class GalagoSearcher {
    private String pathToIndex;
    private Parameters p;
    private Retrieval retrieval;
    private String model;

    public GalagoSearcher(String path, String model){
        this.pathToIndex = path;
        this.model = model;
        this.p = Parameters.create();
        p.set("index", path);
        p.set("processingModel", "org.lemurproject.galago.core.retrieval.processing.RankedDocumentModel");
        p.set("scorer", "bm25");
        p.set("casefold", true);
        p.set("requested", 100);

        try {
            this.retrieval = RetrievalFactory.instance(this.pathToIndex, this.p);
        }
        catch (Exception e) {
            System.err.println("Error creating retrieval factory: " + e);
        }
    }

    public ArrayList<String> search(String query) throws Exception {
        ArrayList<String> result = new ArrayList<>();



        switch (model) {
            case "rdm":
                query = "#combine("+query+")";
                break;
            case "sdm":
                query = "#sdm("+query+")";
                break;
            case "rm3":
                query = getQueryWithRelevance(query, Home.relevantDocs);//"#combine( #rm("+String.join(") #rm(",query.split(" "))+") )";
                break;
        }
        Node root = StructuredQuery.parse(query);
        Node transformed = retrieval.transformQuery(root, this.p);
        System.out.println(transformed.toPrettyString());
        List<ScoredDocument> docs = retrieval.executeQuery(transformed, this.p).scoredDocuments;
        for (ScoredDocument sd : docs) {
            result.add(sd.documentName);
        }

        return result;
    }

    public String getQueryWithRelevance(String query, HashSet<String> relevantDocs) {
        try {
            int N = 15;
            float origQueryWeight = 0.7f;
            ArrayList<String> allWords = new ArrayList<>();
            ArrayList<Integer> allWeights = new ArrayList<>();
            query = query.toLowerCase();
            String[] splitQuery = query.split(" ");

            Set<String> stopwords = WordLists.getWordList("rmstop");

            DiskIndex di = new DiskIndex(pathToIndex);
            for (String docid : relevantDocs) {
                Document d = di.getDocument(docid, new Document.DocumentComponents(true, true, true));
                TObjectIntIterator<String> w = d.getBagOfWords().iterator();
                TreeMap<Integer, String> mp = new TreeMap<>();
                while (w.hasNext()) {
                    w.advance();
                    if (!stopwords.contains(w.key()) && !query.contains(w.key())) {
                        if (mp.containsKey(w.value())) {
                            mp.put(w.value(), mp.get(w.value()) + " " + w.key());
                        } else {
                            mp.put(w.value(), w.key());
                        }
                    }
                }
                int count = 0;
                for (Integer key : mp.descendingKeySet()) {
                    String[] ss = mp.get(key).split(" ");
                    for (String s : ss) {
                        count++;
                        int i = allWords.indexOf(s);
                        if (i != -1) {
                            allWeights.set(i, allWeights.get(i) + key);
                        } else {
                            allWords.add(s);
                            allWeights.add(key);
                        }
                        if (count == N) {
                            break;
                        }
                    }
                    if (count == N) {
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            if (allWords.size() == 0) {
                origQueryWeight = 1.0f;
            }
            sb.append("#combine (");
            for (String qs : splitQuery) {
                sb.append("#bm25:w=");
                sb.append(origQueryWeight / splitQuery.length);
                sb.append("(");
                sb.append(qs);
                sb.append(") ");
            }
            float relevanceWeight = 1 - origQueryWeight;
            float totalWeight = allWeights.stream().mapToInt(Integer::intValue).sum();
            for (int i = 0; i < allWords.size(); i++) {
                sb.append("#bm25:w=");
                sb.append(relevanceWeight * allWeights.get(i) / totalWeight);
                sb.append("(");
                sb.append(allWords.get(i));
                sb.append(") ");
            }
            sb.append(")");
            System.out.println(sb.toString());
            System.out.println("becomes");
            return sb.toString();
        }
        catch (Exception e) {
            System.err.println("Error opening index: " + e.toString());
            return "";
        }
    }
}
