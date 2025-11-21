package com.github.jknack.handlebars;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Lookup order: extendedContext.model -> context.model
 */
public class InversedLookupOrderContext extends Context {

	public InversedLookupOrderContext(final Context parent, final Object model) {
		super(model);
		this.extendedContext = new Context(new HashMap<>());
		this.extendedContext.resolver = parent.resolver;
		this.extendedContext.extendedContext = new Context(Collections.emptyMap());
		this.resolver = parent.resolver;
		this.parent = parent;
		this.data = parent.data;
	}

	@Override
	public Object get(List<PathExpression> path) {
		final Object value = this.extendedContext.get(path);
		return value == null ? super.get(path) : value;
	}
}
