package org.springframework.ai.vectorstore;

import org.springframework.ai.vectorstore.filter.Filter;

public class SearchRequest {
    private final String query;
    private final int topK;
    private final Filter.Expression filterExpression;
    private final double similarityThreshold;

    public SearchRequest(String query, int topK, Filter.Expression filterExpression, double similarityThreshold) {
        this.query = query;
        this.topK = topK;
        this.filterExpression = filterExpression;
        this.similarityThreshold = similarityThreshold;
    }

    public String getQuery() { return query; }
    public int getTopK() { return topK; }
    public Filter.Expression getFilterExpression() { return filterExpression; }
    public double getSimilarityThreshold() { return similarityThreshold; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String query = "";
        private int topK = 4;
        private Filter.Expression filterExpression;
        private double similarityThreshold = 0.0;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder topK(int topK) {
            this.topK = topK;
            return this;
        }

        public Builder filterExpression(Filter.Expression filterExpression) {
            this.filterExpression = filterExpression;
            return this;
        }

        public Builder similarityThreshold(double threshold) {
            this.similarityThreshold = threshold;
            return this;
        }

        public SearchRequest build() {
            return new SearchRequest(query, topK, filterExpression, similarityThreshold);
        }
    }
}

