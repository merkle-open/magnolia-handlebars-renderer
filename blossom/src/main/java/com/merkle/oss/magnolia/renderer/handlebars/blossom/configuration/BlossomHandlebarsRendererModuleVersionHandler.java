package com.merkle.oss.magnolia.renderer.handlebars.blossom.configuration;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;

import jakarta.inject.Inject;
import java.util.List;

public class BlossomHandlebarsRendererModuleVersionHandler extends DefaultModuleVersionHandler {
	private final InstallBlossomHandlebarsRendererSetupTask installHandlebarsRendererSetupTask;

	@Inject
	public BlossomHandlebarsRendererModuleVersionHandler(final InstallBlossomHandlebarsRendererSetupTask installBlossomHandlebarsRendererSetupTask) {
		this.installHandlebarsRendererSetupTask = installBlossomHandlebarsRendererSetupTask;
	}

	@Override
	protected final List<Task> getExtraInstallTasks(final InstallContext installContext) { // when module node does not exist
		return List.of(installHandlebarsRendererSetupTask);
	}

	@Override
	protected final List<Task> getDefaultUpdateTasks(final Version forVersion) { //on every module update
		return List.of(installHandlebarsRendererSetupTask);
	}
}
