package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.OutputProvider;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.listeners.AbstractRenderingListener;
import info.magnolia.rendering.template.RenderableDefinition;

import jakarta.annotation.Nullable;
import javax.jcr.Node;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class WrappingRenderingEngineWrapper implements RenderingEngine {
	private final RenderingEngine renderingEngine;
	private final CmsComponentTemplateHelper.Appender appender;
	private final CmsComponentTemplateHelper.Prepender prepender;

	public WrappingRenderingEngineWrapper(
			final RenderingEngine renderingEngine,
			final CmsComponentTemplateHelper.Appender appender,
			final CmsComponentTemplateHelper.Prepender prepender
	) {
		this.renderingEngine = renderingEngine;
		this.appender = appender;
		this.prepender = prepender;
	}

	@Override
	public void render(final Node content, final Map<String, Object> contextObjects, final OutputProvider out) throws RenderException {
		prepend(content, contextObjects, out);
		renderingEngine.render(content, contextObjects, out);
		append(content, contextObjects, out);
	}

	@Override
	public void render(final Node content, final RenderableDefinition definition, final Map<String, Object> contextObjects, final OutputProvider out) throws RenderException {
		prepend(content, contextObjects, out);
		renderingEngine.render(content, definition, contextObjects, out);
		append(content, contextObjects, out);
	}

	private void prepend(final Node content, final Map<String, Object> contextObjects, final OutputProvider out) throws RenderException {
		try {
			@Nullable
			final String prepend = prepender.prepend(content, contextObjects).orElse(null);
			if(prepend != null) {
				out.getAppendable().append(prepend);
			}
		} catch (IOException e) {
			throw new RenderException(e);
		}
	}

	private void append(final Node content, final Map<String, Object> contextObjects, final OutputProvider out) throws RenderException {
		try {
			@Nullable
			final String prepend = appender.append(content, contextObjects).orElse(null);
			if(prepend != null) {
				out.getAppendable().append(prepend);
			}
		} catch (IOException e) {
			throw new RenderException(e);
		}
	}

	@Override
	public void render(Node content, OutputProvider out) throws RenderException {
		render(content, Collections.emptyMap(), out);
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
