package com.merkle.oss.magnolia.renderer.handlebars.configuration;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;

import javax.inject.Inject;
import java.util.List;

public class HandlebarsRendererModuleVersionHandler extends DefaultModuleVersionHandler {
	private final InstallHandlebarsRendererSetupTask installHandlebarsRendererSetupTask;

	@Inject
	public HandlebarsRendererModuleVersionHandler(final InstallHandlebarsRendererSetupTask installHandlebarsRendererSetupTask) {
		this.installHandlebarsRendererSetupTask = installHandlebarsRendererSetupTask;
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
