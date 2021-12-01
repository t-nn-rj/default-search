import org.lemurproject.galago.core.index.LengthsReader;
import org.lemurproject.galago.core.index.disk.DiskLengthsReader;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.iterator.LengthsIterator;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.index.stats.FieldStatistics;
import org.lemurproject.galago.core.index.stats.NodeStatistics;
import org.lemurproject.galago.utility.Parameters;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Tag;

public class GalagoSearcher {
    private String pathToIndex;
    private Retrieval retrieval;

    public GalagoSearcher(String path){
        this.pathToIndex = path;

        //throws an error for some reason
        //retrieval = RetrievalFactory.instance(this.pathToIndex, Parameters.create());
    }
}
