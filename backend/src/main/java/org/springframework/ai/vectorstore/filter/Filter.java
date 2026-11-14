package org.springframework.ai.vectorstore.filter;

public class Filter {
    public interface Expression {
    }

    public static class Key {
        private final String key;
        public Key(String key) { this.key = key; }
        public String getKey() { return key; }
    }

    public static class Value {
        private final Object value;
        public Value(Object value) { this.value = value; }
        public Object getValue() { return value; }
    }
}

