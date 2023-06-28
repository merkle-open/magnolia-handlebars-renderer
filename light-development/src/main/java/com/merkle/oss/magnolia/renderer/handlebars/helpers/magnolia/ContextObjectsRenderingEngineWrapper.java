package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.OutputProvider;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.listeners.AbstractRenderingListener;
import info.magnolia.rendering.template.RenderableDefinition;

import javax.jcr.Node;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ContextObjectsRenderingEngineWrapper implements RenderingEngine {
	private final RenderingEngine renderingEngine;
	private final Map<String, Object> contextObjects;

	ContextObjectsRenderingEngineWrapper(
			final RenderingEngine renderingEngine,
			final Map<String, Object> contextObjects
	) {
		this.renderingEngine = renderingEngine;
		this.contextObjects = contextObjects;
	}

	@Override
	public void render(Node content, Map<String, Object> contextObjects, OutputProvider out) throws RenderException {
		renderingEngine.render(content, merge(this.contextObjects, contextObjects), out);
	}

	@Override
	public void render(Node content, RenderableDefinition definition, Map<String, Object> contextObjects, OutputProvider out) throws RenderException {
		renderingEngine.render(content, definition, merge(this.contextObjects, contextObjects), out);
	}

	@Override
	public void render(Node content, OutputProvider out) throws RenderException {
		render(content, Collections.emptyMap(), out);
	}

	private Map<String, Object> merge(final Map<String, Object> first, final Map<String, Object> second) {
		return Stream
				.concat(
						first.entrySet().stream(),
						second.entrySet().stream()
				)
				.filter(entry -> entry.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value2));
	}

	// ---- delegate ----
	@Override
	public RenderingContext getRenderingContext() {
		return renderingEngine.getRenderingContext();
	}

	@Override
	public Collection<AbstractRenderingListener.RenderingListenerReturnCode> initListeners(OutputProvider out, HttpServletResponse response) {
		return renderingEngine.initListeners(out, response);
	}

	@Override
	public Boolean getRenderEmptyAreas() {
		return renderingEngine.getRenderEmptyAreas();
	}

	@Override
	public Boolean getAutoPopulateFromRequest() {
		return renderingEngine.getAutoPopulateFromRequest();
	}
}
