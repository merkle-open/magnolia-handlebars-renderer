package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import org.apache.commons.text.CaseUtils;

import java.util.stream.Collector;
import java.util.stream.IntStream;

public class ClassNameNormalizer {
	private static final String INVALID_CHAR_REPLACEMENT = "_";

	public String normalize(final String name) {
		final String normalized = IntStream
				.range(0, name.length())
				.mapToObj(index -> {
					final char character = name.charAt(index);
					if ("-".equals(String.valueOf(character))) {
						return " ";
					}
					if ((index == 0 && Character.isJavaIdentifierStart(character)) || (index > 0 && Character.isJavaIdentifierPart(character))) {
						return character;
					}
					return INVALID_CHAR_REPLACEMENT;
				})
				.collect(Collector.of(
						StringBuilder::new,
						StringBuilder::append,
						StringBuilder::append,
						StringBuilder::toString)
				);
		return CaseUtils.toCamelCase(normalized, true);
	}
}
