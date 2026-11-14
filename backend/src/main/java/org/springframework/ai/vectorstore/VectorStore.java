package org.springframework.ai.vectorstore;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.List;

public interface VectorStore {
    void add(List<Document> documents);
    void delete(List<String> idList);
    void delete(Filter.Expression filterExpression);
    List<Document> similaritySearch(String query);
    List<Document> similaritySearch(SearchRequest request);
}

