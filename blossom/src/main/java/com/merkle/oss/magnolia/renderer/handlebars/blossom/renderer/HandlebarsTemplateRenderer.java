package com.merkle.oss.magnolia.renderer.handlebars.blossom.renderer;

import com.merkle.oss.magnolia.renderer.handlebars.renderer.HandlebarsRenderer;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.rendering.engine.AppendableOnlyOutputProvider;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.rendering.template.configured.DefaultTemplateAvailability;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Map;

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

			RenderContext.push();
			final StringBuilder rendered = new StringBuilder();
			renderingEngine.render(page, definition, context, new AppendableOnlyOutputProvider(rendered));
			return rendered.toString();
		} finally {
			RenderContext.pop();
			MgnlContext.setInstance(originalWebContext);
		}
	}
}
