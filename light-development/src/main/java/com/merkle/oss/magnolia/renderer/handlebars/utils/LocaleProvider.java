package com.merkle.oss.magnolia.renderer.handlebars.utils;

import javax.jcr.Node;
import java.util.Locale;

public interface LocaleProvider {
	Locale getLocale(Node node);
}
