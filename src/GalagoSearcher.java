import org.lemurproject.galago.core.index.LengthsReader;
import org.lemurproject.galago.core.index.disk.DiskLengthsReader;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.iterator.LengthsIterator;
import org.lemurproject.galago.core.retrieval.processing.ProcessingModel;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.index.stats.FieldStatistics;
import org.lemurproject.galago.core.index.stats.NodeStatistics;
import org.lemurproject.galago.utility.Parameters;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Tag;

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
        p.set("processingModel", model);
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

        String[] terms = query.split(" ");
        StringBuilder sb = new StringBuilder();
        sb.append("#combine(");
        for (String t : terms) {
            //sb.append("#extents:part=postings.krovetz:");
            sb.append("#bm25(");
            sb.append(t);
            sb.append(") ");
        }
        sb.append(")");

        Node root = StructuredQuery.parse(sb.toString());
        Node transformed = retrieval.transformQuery(root, this.p);
        List<ScoredDocument> docs = retrieval.executeQuery(transformed, this.p).scoredDocuments;
        for (ScoredDocument sd : docs) {
            result.add(sd.documentName);
        }

        return result;
    }
}
