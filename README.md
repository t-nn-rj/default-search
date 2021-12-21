# default-search
A simple search tool for comparing Retrieval Models using Galago

## description
To investigate the performance of text-based retrieval models on commercial products, we created a simple search engine for Amazon product and review data that allows us to compare the performance and efficiency of different retrieval models in the Galago ecosystem. The search engine uses typical retrieval models and optionally refines results with a hand-written model making use of explicit user feedback. We then compared the results from each retrieval model by using nDCG evaluation of user tagged relevance data, and the processing time of different lengths of search queries.

## dataset
We tested this search application with the Amazon 5-core datasets - specifically, the \textit{Cell Phones and Accessories} category. The datasets are hosted publicly by Stanford. We also pulled product pictures from Amazon.com.

## retrieval models
The first model implemented in the search application is the Ranked Document Model (RDM). It uses Okapi BM25 to assign a score to each document, and uses Krovetz stemming on the query. The model returns exact matches only and word independence is assumed, making this a simple bag-of-words model.

The second model is the Sequential Dependence Model (SDM), which uses linear combinations of BM25 scores for different combinations of neighboring words in a query. The transformed query consists of every set of neighboring stemmed words in a given query, in addition to each word alone with lower term weight. This serves to prioritize documents with the same order of words as the original query, making SDM imitate beyond bag-of-words models (because it does not assume term independence).

The last model uses query expansion based on explicit user feedback, and interpolates these results with the original query, matching the well-known RM3 algorithm. Documents are marked relevant by users after an initial search and the top \textit{k} terms from relevant docs are then added to the original query with proportional weight. Stopwords are loaded and skipped when considering top terms. This model (which we wrote) should perform much better than pseudo-relevance feedback (which is already implemented by default in Galago), because in this case, the assumption that the top ranked documents are relevant often does not hold.

Special thanks to Dr. Qingyao Ai.
