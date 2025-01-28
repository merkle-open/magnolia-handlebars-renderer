package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ClassNameNormalizerTest {
	private ClassNameNormalizer classNameNormalizer;

	@BeforeEach
	void setUp() {
		classNameNormalizer = new ClassNameNormalizer();
	}

	@ParameterizedTest
	@CsvSource({
			"accordion,				Accordion",
			"gdpr-button,			GdprButton",
			"gdpr-cookie-category,  GdprCookieCategory",
			"component?`,  			Component__",
	})
	void normalize(final String input, final String expected) {
		assertEquals(expected, classNameNormalizer.normalize(input));
	}
}