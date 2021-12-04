import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.utility.Parameters;

import java.util.ArrayList;
import java.util.List;

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
        switch(model) {
            case "rdm":
            case "sdm":
                p.set("processingModel", "org.lemurproject.galago.core.retrieval.processing.RankedDocumentModel");
                break;
            case "rm3":
                p.set("relevanceModel", "org.lemurproject.galago.core.retrieval.prf.RelevanceModel3");
                p.set("fbOrigWeight", 0.7);
                p.set("fbDocs", 5);
                p.set("fbTerms", 20);
                break;
        }
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
                query = "#combine( #rm("+String.join(") #rm(",query.split(" "))+") )";
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
}
