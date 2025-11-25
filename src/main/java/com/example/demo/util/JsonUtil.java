package com.example.demo.util;

import java.util.*;

public final class JsonUtil {

    // ===========================================================
    //  Public API
    // ===========================================================
    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        writeValue(obj, sb);
        return sb.toString();
    }

    public static Object parse(String json) {
        return new Parser(json).parseValue();
    }

    // ===========================================================
    //  Encode
    // ===========================================================
    private static void writeValue(Object obj, StringBuilder sb) {
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof String) {
            sb.append('"').append(escape((String) obj)).append('"');
        } else if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj.toString());
        } else if (obj instanceof Map) {
            writeObject((Map<?, ?>) obj, sb);
        } else if (obj instanceof List) {
            writeArray((List<?>) obj, sb);
        } else {
            sb.append('"').append(escape(String.valueOf(obj))).append('"');
        }
    }

    private static void writeObject(Map<?, ?> map, StringBuilder sb) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escape(String.valueOf(e.getKey()))).append('"');
            sb.append(':');
            writeValue(e.getValue(), sb);
        }
        sb.append('}');
    }

    private static void writeArray(List<?> list, StringBuilder sb) {
        sb.append('[');
        boolean first = true;
        for (Object o : list) {
            if (!first) sb.append(',');
            first = false;
            writeValue(o, sb);
        }
        sb.append(']');
    }

    private static String escape(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ===========================================================
    //  Parser (Minimal JSON Parser)
    // ===========================================================
    private static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) {
            this.s = s.trim();
        }

        Object parseValue() {
            skipWhitespace();
            if (match("null")) return null;
            if (match("true")) return Boolean.TRUE;
            if (match("false")) return Boolean.FALSE;

            char c = peek();
            if (c == '"') return parseString();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            return parseNumber();
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return map;
            }

            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = expect(',', '}');
                if (c == '}') break;
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return list;
            }

            while (true) {
                skipWhitespace();
                Object v = parseValue();
                list.add(v);
                skipWhitespace();
                char c = expect(',', ']');
                if (c == ']') break;
            }
            return list;
        }

        String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(pos++);
                if (c == '"') break;
                if (c == '\\') {
                    char esc = s.charAt(pos++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Number parseNumber() {
            int start = pos;
            while (pos < s.length() && "-+0123456789.eE".indexOf(s.charAt(pos)) >= 0) pos++;
            String num = s.substring(start, pos);
            if (num.contains(".") || num.contains("e") || num.contains("E")) {
                return Double.parseDouble(num);
            }
            return Long.parseLong(num);
        }

        // ===== Helpers =====
        boolean match(String expected) {
            if (s.startsWith(expected, pos)) {
                pos += expected.length();
                return true;
            }
            return false;
        }

        char expect(char... options) {
            char c = peek();
            for (char op : options) {
                if (c == op) {
                    pos++;
                    return c;
                }
            }
            throw new RuntimeException("Expected " + Arrays.toString(options) + " at pos " + pos);
        }

        char peek() {
            return s.charAt(pos);
        }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }
    }
}
