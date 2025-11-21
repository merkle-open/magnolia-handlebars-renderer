package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import info.magnolia.jcr.util.ContentMap;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.templating.elements.ComponentElement;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.github.jknack.handlebars.Options;

import jakarta.inject.Inject;

public class CmsComponentTemplateHelper extends AbstractCmsTemplateHelper<ContentMap> {
	private final RenderingEngine renderingEngine;

	@Inject
	public CmsComponentTemplateHelper(
			final RenderingEngine renderingEngine,
			final TemplatingFunctions templatingFunctions,
			final Appender appender,
			final Prepender prepender
	) {
		super(templatingFunctions);
		this.renderingEngine = new WrappingRenderingEngineWrapper(
				renderingEngine,
				appender,
				prepender
		);
	}

	@Override
	protected Optional<CharSequence> applySafe(final ContentMap contentMap, final Options options) throws RepositoryException, RenderException, IOException {
		final Node node = contentMap.getJCRNode();
		final Map<String, Object> combinedContext = getContext(options);

		//Magnolia's ComponentElement.begin calls render with empty HashMap --> use ContextObjectsRenderingEngineWrapper
		final ComponentElement componentElement = Components.getComponentProvider().newInstance(
				ComponentElement.class,
				new ContextObjectsRenderingEngineWrapper(renderingEngine, combinedContext)
		);

		componentElement.setContent(node);
		componentElement.setWorkspace(node.getSession().getWorkspace().getName());
		componentElement.setNodeIdentifier(node.getIdentifier());
		componentElement.setPath(node.getPath());
		componentElement.setContextAttributes(combinedContext);

		return Optional.of(render(componentElement));
	}

	private Map<String, Object> getContext(final Options options) {
		return Stream
				.concat(
						options.hash.entrySet().stream().filter(entry -> !Objects.equals("data", entry.getKey())),
						Optional
								.ofNullable(options.hash("data"))
								.filter(Map.class::isInstance)
								.map(data -> (Map<String, Object>) data)
								.map(Map::entrySet)
								.stream()
								.flatMap(Collection::stream)
				)
				.filter(entry -> !Objects.isNull(entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value1));
	}

	@Override
	public Set<String> names() {
		return Set.of("cms-component");
	}

	public interface Appender {
		Optional<String> append(Node content, Map<String, Object> contextObjects);
	}

	public interface Prepender {
		Optional<String> prepend(Node content, Map<String, Object> contextObjects);
	}
}
