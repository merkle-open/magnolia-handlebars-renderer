package com.merkle.oss.magnolia.renderer.handlebars.configuration;

import com.merkle.oss.magnolia.renderer.handlebars.renderer.HandlebarsRenderer;
import info.magnolia.jcr.nodebuilder.AbstractNodeOperation;
import info.magnolia.jcr.nodebuilder.ErrorHandler;
import info.magnolia.jcr.nodebuilder.NodeOperation;
import info.magnolia.jcr.nodebuilder.Ops;
import info.magnolia.jcr.nodebuilder.task.ErrorHandling;
import info.magnolia.jcr.nodebuilder.task.NodeBuilderTask;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public class InstallHandlebarsRendererSetupTask extends AbstractRepositoryTask {
	private static final String TASK_NAME = "HandlebarsRenderer Configuration Task";
	private static final String TASK_DESCRIPTION = "This task configures the handlebars renderer.";
	private static final String PATH = "/modules/rendering/renderers";

	public InstallHandlebarsRendererSetupTask() {
		super(TASK_NAME, TASK_DESCRIPTION);
	}

	@Override
	protected void doExecute(final InstallContext ctx) throws TaskExecutionException {
		new NodeBuilderTask(getName(), getDescription(), ErrorHandling.strict, RepositoryConstants.CONFIG, PATH,
				Ops.getOrAddNode(HandlebarsRenderer.NAME, NodeTypes.ContentNode.NAME).then(
						setOrAddProperty("class", getHandlebarsRendererImplementationClass().getName())
				)
		).execute(ctx);
	}

	protected Class<? extends HandlebarsRenderer> getHandlebarsRendererImplementationClass() {
		return HandlebarsRenderer.class;
	}

	public static NodeOperation setOrAddProperty(final String name, final Object newValue) {
		return new AbstractNodeOperation() {
			@Override
			protected Node doExec(final Node context, final ErrorHandler errorHandler) throws RepositoryException {
				final Value value = PropertyUtil.createValue(newValue, context.getSession().getValueFactory());
				context.setProperty(name, value);
				return context;
			}
		};
	}
}
