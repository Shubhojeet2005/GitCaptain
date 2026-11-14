package org.springframework.ai.vectorstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SimpleVectorStore implements VectorStore {

    private final EmbeddingModel embeddingModel;
    private final List<Entry> store = new CopyOnWriteArrayList<>();

    private record Entry(Document document, float[] embedding, Map<String, Object> metadata) {}

    public SimpleVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        for (Document doc : documents) {
            float[] vec = null;
            if (embeddingModel != null) {
                try {
                    vec = embeddingModel.embed(doc);
                } catch (Exception e) {
                    log.debug("Embedding computation skipped: {}", e.getMessage());
                }
            }
            store.add(new Entry(doc, vec, new HashMap<>(doc.getMetadata())));
        }
    }

    @Override
    public void delete(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            return;
        }
        Set<String> ids = new HashSet<>(idList);
        store.removeIf(entry -> ids.contains(entry.document().getId()));
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        if (filterExpression instanceof FilterExpressionBuilder.OpExpression op) {
            String key = op.getKey();
            Object value = op.getValue();
            store.removeIf(entry -> Objects.equals(String.valueOf(entry.metadata().get(key)), String.valueOf(value)));
        }
    }

    @Override
    public List<Document> similaritySearch(String query) {
        return similaritySearch(SearchRequest.builder().query(query).build());
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        String query = request.getQuery();
        int topK = request.getTopK() > 0 ? request.getTopK() : 4;
        Filter.Expression filter = request.getFilterExpression();

        List<Entry> candidates = new ArrayList<>(store);
        if (filter instanceof FilterExpressionBuilder.OpExpression op) {
            String key = op.getKey();
            Object value = op.getValue();
            candidates.removeIf(entry -> !Objects.equals(String.valueOf(entry.metadata().get(key)), String.valueOf(value)));
        }

        if (candidates.isEmpty()) {
            return List.of();
        }

        float[] queryVec = null;
        if (embeddingModel != null && query != null && !query.isBlank()) {
            try {
                queryVec = embeddingModel.embed(query);
            } catch (Exception e) {
                log.debug("Query embedding computation skipped: {}", e.getMessage());
            }
        }

        final float[] finalQueryVec = queryVec;
        record ScoredDoc(Document doc, double score) {}

        List<ScoredDoc> scored = new ArrayList<>();
        String[] queryTerms = query != null ? query.toLowerCase().split("\\W+") : new String[0];

        for (Entry candidate : candidates) {
            double score = 0.0;
            if (finalQueryVec != null && candidate.embedding() != null) {
                score = cosineSimilarity(finalQueryVec, candidate.embedding());
            } else {
                String text = candidate.document().getText() != null ? candidate.document().getText().toLowerCase() : "";
                for (String term : queryTerms) {
                    if (!term.isBlank() && text.contains(term)) {
                        score += 1.0;
                    }
                }
            }
            scored.add(new ScoredDoc(candidate.document(), score));
        }

        scored.sort((a, b) -> Double.compare(b.score(), a.score()));

        return scored.stream()
                .limit(topK)
                .map(ScoredDoc::doc)
                .toList();
    }

    private static double cosineSimilarity(float[] v1, float[] v2) {
        if (v1.length != v2.length) return 0.0;
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        double denom = Math.sqrt(norm1) * Math.sqrt(norm2);
        return denom == 0.0 ? 0.0 : dot / denom;
    }
}

