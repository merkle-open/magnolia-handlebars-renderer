package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.*;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.TemplateScriptLocator;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentHelper implements NamedHelper<Object> {
	private static final Set<String> IGNORED_PROPERTIES = Set.of("name", "data", "type");

	private final TemplateScriptLocator locator;

	@Inject
	public ComponentHelper(final TemplateScriptLocator locator) {
		this.locator = locator;
	}

	/*
	 * {{component name='Bubble' template="bubble" data='bubble' firstRow='CHF'}}
	 */
	public CharSequence apply(final Object modelParam, final Options options) throws IOException {
		final String componentName = Optional.ofNullable((String) options.hash("name")).orElseThrow(() ->
				new IllegalArgumentException("No name supplied!")
		);
		final String templateScriptLocation = getTemplateScriptLocation(componentName, options);
		final Template componentTemplate = options.handlebars.compile(templateScriptLocation);

		final Object model = getModelFromDataField(options).orElse(modelParam);

		final Context combinedContext = new InversedLookupOrderContext(options.context, model)
				.combine(filter(options.hash, IGNORED_PROPERTIES));

		final String result = componentTemplate.apply(combinedContext);
		return new Handlebars.SafeString(result);
	}

	private String getTemplateScriptLocation(final String componentName, final Options options) {
		final String locatorKey = componentName+"-"+Optional
				.ofNullable((String) options.hash("template"))
				.orElse(componentName);

		return Optional
				.ofNullable((String) options.hash("type"))
				.map(type ->
						locator.get(locatorKey, type)
				)
				.orElseGet(() -> locator.get(locatorKey));
	}

	private Optional<Object> getModelFromDataField(final Options options) {
		return Optional
				.ofNullable(options.hash("data"))
				.map(data -> {
					if (data instanceof String) {
						return options.context.get((String) data);
					}
					return data;
				});
	}

	private Map<String, Object> filter(final Map<String, Object> hash, final Collection<String> ignoreKeys) {
		return hash
				.entrySet()
				.stream()
				.filter(entry ->
						!ignoreKeys.contains(entry.getKey())
				)
				.filter(entry ->
						entry.getValue() != null
				)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public Set<String> names() {
		return Set.of("pattern");
	}
}