package com.merkle.oss.magnolia.renderer.handlebars.renderer;

import info.magnolia.context.ProtectingWebContextWrapper;
import info.magnolia.context.WebContext;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ImmutableResponseContentTypeWebContextWrapper extends ProtectingWebContextWrapper {

	public ImmutableResponseContentTypeWebContextWrapper(final WebContext ctx) {
		super(ctx, Collections.emptySet());
	}

	@Override
	public HttpServletResponse getResponse() {
		return new HttpServletResponseWrapper(super.getResponse()) {
			@Override
			public void setContentType(final String type) {
			}
		};
	}
}
