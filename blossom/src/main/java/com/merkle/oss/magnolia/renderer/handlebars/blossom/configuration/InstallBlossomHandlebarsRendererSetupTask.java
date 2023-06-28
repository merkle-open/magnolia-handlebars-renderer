package com.merkle.oss.magnolia.renderer.handlebars.blossom.configuration;

import com.merkle.oss.magnolia.renderer.handlebars.blossom.BlossomHandlebarsRenderer;
import com.merkle.oss.magnolia.renderer.handlebars.configuration.InstallHandlebarsRendererSetupTask;
import com.merkle.oss.magnolia.renderer.handlebars.renderer.HandlebarsRenderer;

public class InstallBlossomHandlebarsRendererSetupTask extends InstallHandlebarsRendererSetupTask {

	@Override
	protected Class<? extends HandlebarsRenderer> getHandlebarsRendererImplementationClass() {
		return BlossomHandlebarsRenderer.class;
	}
}
