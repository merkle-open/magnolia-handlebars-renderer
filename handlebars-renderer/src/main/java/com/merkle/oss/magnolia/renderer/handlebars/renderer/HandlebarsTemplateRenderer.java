package com.merkle.oss.magnolia.renderer.handlebars.renderer;

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.engine.AppendableOnlyOutputProvider;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.rendering.template.configured.DefaultTemplateAvailability;

import java.util.Map;

import jakarta.inject.Inject;
import javax.jcr.Node;


public class HandlebarsTemplateRenderer {
	private final RenderingEngine renderingEngine;

	@Inject
	public HandlebarsTemplateRenderer(final RenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}

	public String render(final Node page, final String templateLocation, final Map<String, Object> context) throws RenderException {
		final ConfiguredTemplateDefinition definition = new ConfiguredTemplateDefinition(new DefaultTemplateAvailability());
		definition.setTemplateScript(templateLocation);
		definition.setRenderType(HandlebarsRenderer.NAME);

		/*
		 * info.magnolia.rendering.renderer.AbstractRenderer sets the servletResponse Content-Type to MIMEMapping.DEFAULT_EXTENSION (html) if not present.
		 * While this is fine for normal rendered pages, the servletResponse content-type must be defined by spring
		 *
		 * --> set webContext with immutable contentType response and reset originalWebContext after page has been rendered
		 */
		final WebContext originalWebContext = MgnlContext.getWebContext();
		try {
			final WebContext webContext = new ImmutableResponseContentTypeWebContextWrapper(originalWebContext);
			MgnlContext.setInstance(webContext);

			final StringBuilder rendered = new StringBuilder();
			renderingEngine.render(page, definition, context, new AppendableOnlyOutputProvider(rendered));
			return rendered.toString();
		} finally {
			MgnlContext.setInstance(originalWebContext);
		}
	}
}
