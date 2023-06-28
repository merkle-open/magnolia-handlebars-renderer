package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Helper;

import java.util.Set;

public interface NamedHelper<T> extends Helper<T> {
	Set<String> names();
}
