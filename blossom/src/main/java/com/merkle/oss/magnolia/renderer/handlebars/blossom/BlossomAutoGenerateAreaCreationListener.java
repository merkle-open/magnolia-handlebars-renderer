package com.merkle.oss.magnolia.renderer.handlebars.blossom;

import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsAreaTemplateHelper;
import info.magnolia.module.blossom.template.TemplateDefinitionBuilder;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.AutoGenerationConfiguration;

import javax.jcr.Node;

public class BlossomAutoGenerateAreaCreationListener implements CmsAreaTemplateHelper.AreaCreationListener {

	@Override
	public void onAreaCreated(final Node areaNode, final AreaDefinition areaDefinition) throws RenderException {
		final AutoGenerationConfiguration<?> autoGenConfig = areaDefinition.getAutoGeneration();
		if (autoGenConfig instanceof TemplateDefinitionBuilder.BlossomAutoGenerationConfiguration configuration) {
			final TemplateDefinitionBuilder.BlossomGenerator generator = new TemplateDefinitionBuilder.BlossomGenerator(areaNode);
			generator.generate(configuration);
		}
	}
}
