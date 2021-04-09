package ch.baselzock.twittertagsorter.converter;

public class ConverterFactory {
    public static Converter getConverterFor(ConverterType type) {
        return switch (type) {
            case JSON -> new JsonConverter();
        };
    }
}
