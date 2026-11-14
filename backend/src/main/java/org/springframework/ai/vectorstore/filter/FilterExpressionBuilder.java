package org.springframework.ai.vectorstore.filter;

public class FilterExpressionBuilder {
    public static class OpExpression implements Filter.Expression {
        private final String key;
        private final Object value;

        public OpExpression(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public Object getValue() { return value; }
    }

    private OpExpression expression;

    public FilterExpressionBuilder eq(String key, Object value) {
        this.expression = new OpExpression(key, value);
        return this;
    }

    public Filter.Expression build() {
        return this.expression;
    }
}

