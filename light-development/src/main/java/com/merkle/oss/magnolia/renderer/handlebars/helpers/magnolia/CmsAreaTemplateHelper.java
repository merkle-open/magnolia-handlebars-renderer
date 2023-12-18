package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Options;
import com.machinezoo.noexception.Exceptions;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.PermissionUtil;
import info.magnolia.config.registry.Registry;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.lock.PathLockManager;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.rendering.template.variation.RenderableVariationResolver;
import info.magnolia.templating.elements.AreaElement;
import info.magnolia.templating.elements.attribute.ElementAttribute;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.templating.module.TemplatingModule;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.*;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CmsAreaTemplateHelper extends AbstractCmsTemplateHelper<Object> {
	public static final String SUPPLIER_PAGE_PROPERTY = "mgnl:supplierPage";
	private final TemplateDefinitionRegistry templateDefinitionRegistry;
	private final RenderingEngine renderingEngine;
	private final AreaCreationListener areaCreationListener;

	@Inject
	public CmsAreaTemplateHelper(
			final TemplatingFunctions templatingFunctions,
			final TemplateDefinitionRegistry templateDefinitionRegistry,
			final RenderingEngine renderingEngine,
			final AreaCreationListener areaCreationListener
	) {
		super(templatingFunctions);
		this.templateDefinitionRegistry = templateDefinitionRegistry;
		this.renderingEngine = renderingEngine;
		this.areaCreationListener = areaCreationListener;
	}

	@Override
	public Optional<CharSequence> applySafe(final Object ignored, final Options options) throws IOException, RepositoryException, RenderException {
		// placeholder/area name used by FE (fallback for BE if 'area' is not set)
		final String name = options.hash("name");
		// area name for BE, ignored by FE
		final String area = StringUtils.defaultIfBlank(options.hash("area"), name);
		final boolean editable = options.hash("editable", true);

		final RenderingModel<?> model = getRenderingModel(options.context).orElseThrow(() ->
				new IllegalArgumentException("Rendering model not present!")
		);
		final Node node = getSupplierPage(model.getNode()).orElseGet(model::getNode);

		final AreaState areaState = getOrCreateAreaState(area, node);
		final Node areaStateNode = areaState.getNode();
		final String workspace = areaStateNode.getSession().getWorkspace().getName();
		final String nodeIdentifier = areaStateNode.getIdentifier();
		final String path = areaStateNode.getPath();

		//Magnolia's AreaElement.begin calls render with empty HashMap --> use ContextObjectsRenderingEngineWrapper
		final AreaElement areaElement = Components.getComponentProvider().newInstance(
				CustomAvailableComponentsAreaElement.class,
				new ContextObjectsRenderingEngineWrapper(renderingEngine, options.hash)
		);
		areaElement.setContent(areaStateNode);
		areaElement.setWorkspace(workspace);
		areaElement.setNodeIdentifier(nodeIdentifier);
		areaElement.setPath(path);
		areaElement.setArea(areaState.getAreaDefinition());
		areaElement.setName(name);
		areaElement.setContextAttributes(options.hash);

		areaElement.setEditable(editable);

		return Optional.of(render(areaElement));
	}

	@SuppressWarnings("rawtypes")
	private Optional<RenderingModel> getRenderingModel(final Context context) {
		/*
		 * (context.get("model", true) would also consider parent contexts,
		 * but without instanceof check a child with a field named "model" would break it!
		 */
		return Optional
				.ofNullable(context.get("model", false))
				.filter(value -> value instanceof RenderingModel)
				.map(RenderingModel.class::cast)
				.or(() ->
						Optional.ofNullable(context.parent()).flatMap(this::getRenderingModel)
				);
	}

	private AreaDefinition getAreaDefinition(final String name, final Node node) throws RenderException {
		return getNearestAncestorTemplate(node)
				.flatMap(this::getTemplateDefinition)
				.flatMap(templateDefinition ->
						getAreaDefinition(name, templateDefinition.getAreas())
				)
				.orElseThrow(() ->
						new RenderException("Couldn't get areaDefinition. " + name + " " + NodeUtil.getPathIfPossible(node))
				);
	}

	private Optional<String> getNearestAncestorTemplate(final Node node) {
		return Optional
				.ofNullable(PropertyUtil.getString(node, NodeTypes.Renderable.TEMPLATE))
				.or(() -> getParent(node).flatMap(this::getNearestAncestorTemplate));
	}

	private Optional<TemplateDefinition> getTemplateDefinition(final String templateId) {
		try {
			return Optional.of(templateDefinitionRegistry.getProvider(templateId).get());
		} catch (Registry.NoSuchDefinitionException e) {
			return Optional.empty();
		}
	}

	private Optional<Node> getParent(final Node node) {
		try {
			return Optional.of(node.getParent());
		} catch (ItemNotFoundException e) {
			return Optional.empty();
		} catch (RepositoryException e) {
			throw Exceptions.sneak().handle(e);
		}
	}

	private Optional<AreaDefinition> getAreaDefinition(final String name, final Map<String, AreaDefinition> areas) {
		if (areas.containsKey(name)) {
			return Optional.of(areas.get(name));
		}
		return areas.values().stream()
				.map(area ->
						getAreaDefinition(name, area.getAreas())
				)
				.flatMap(Optional::stream)
				.findFirst();
	}

	private AreaState getOrCreateAreaState(final String name, final Node node) throws RenderException {
		final AreaDefinition areaDefinition = getAreaDefinition(name, node);
		if(Boolean.FALSE.equals(areaDefinition.getCreateAreaNode())) {
			return new AreaState(areaDefinition, node);
		}
		return getOrCreateAreaNode(name, node, areaDefinition)
				.map(areaNode ->
						new AreaState(areaDefinition, areaNode)
				)
				.orElseThrow(() ->
						new RenderException("Couldn't create areaState. " + name + " " + NodeUtil.getPathIfPossible(node))
				);
	}

	private Optional<Node> getOrCreateAreaNode(final String name, final Node node, final AreaDefinition areaDefinition) {
		try {
			return Optional.of(node.getNode(name));
		} catch (PathNotFoundException e) {
			return createAreaNode(name, node, areaDefinition);
		} catch (RepositoryException e) {
			throw Exceptions.sneak().handle(e);
		}
	}

	private Optional<Node> createAreaNode(final String name, final Node node, final AreaDefinition areaDefinition) {
		return Exceptions.wrap().get(() -> {
			if (PermissionUtil.isGranted(node, Permission.WRITE)) {
				final Node areaNode = NodeUtil.createPath(node, name, NodeTypes.Area.NAME);
				final Session session = node.getSession();
				session.save();
				Exceptions.wrap().run(() -> areaCreationListener.onAreaCreated(areaNode, areaDefinition));
				return Optional.of(areaNode);
			}
			return Optional.empty();
		});
	}

	private Optional<Node> getSupplierPage(final Node node) {
		return Optional.ofNullable(PropertyUtil.getString(node, SUPPLIER_PAGE_PROPERTY)).flatMap(supplierPageId -> {
			try {
				return Optional.of(node.getSession().getNodeByIdentifier(supplierPageId));
			} catch (ItemNotFoundException e) {
				return Optional.empty();
			} catch (RepositoryException e) {
				throw Exceptions.sneak().handle(e);
			}
		});
	}

	@Override
	public Set<String> names() {
		return Set.of(
				"cms-area",
				"placeholder"
		);
	}

	public static final class AreaState {
		private final AreaDefinition areaDefinition;
		private final Node node;

		private AreaState(final AreaDefinition areaDefinition, final Node node) {
			this.areaDefinition = areaDefinition;
			this.node = node;
		}

		public AreaDefinition getAreaDefinition() {
			return areaDefinition;
		}

		public Node getNode() {
			return node;
		}
	}

	public static class CustomAvailableComponentsAreaElement extends AreaElement {
		private final AvailableComponentsResolver availableComponentsResolver;

		@Inject
		public CustomAvailableComponentsAreaElement(
				final ServerConfiguration server,
				final RenderingContext renderingContext,
				final RenderingEngine renderingEngine,
				final RenderableVariationResolver variationResolver,
				final Provider<TemplatingModule> templatingModuleProvider,
				final WebContext webContext,
				final PathLockManager lockManager,
				final AvailableComponentsResolver availableComponentsResolver) {
			super(server, renderingContext, renderingEngine, variationResolver, templatingModuleProvider, webContext, lockManager);
			this.availableComponentsResolver = availableComponentsResolver;
		}

		@Override
		protected String resolveAvailableComponents() {
			return availableComponentsResolver.getValue(this).orElseGet(super::resolveAvailableComponents);
		}
	}

	public interface AvailableComponentsResolver extends ElementAttribute<AreaElement> {
	}

	public interface AreaCreationListener {
		void onAreaCreated(Node areaNode, AreaDefinition areaDefinition) throws RenderException;
	}
}
