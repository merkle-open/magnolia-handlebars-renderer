package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Set;


@Mojo(name = "reference-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ReferenceGeneratorMojo extends AbstractMojo {
	@Parameter(property = "packageName", required = true)
	private String packageName;
	@Parameter(property = "classNameSuffix", defaultValue = "HbsTemplate")
	private String classNameSuffix;
	@Parameter(property = "templateScriptClass", required = true)
	private String templateScriptClass;
	@Parameter(property = "annotationClasses", required = true)
	private Set<AnnotationClass> annotationClasses;
	@Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources")
	private String outputDirectory;
	@Parameter(property = "handlebarsSrcRoot", required = true)
	private String handlebarsSrcRoot;
	@Parameter(property = "includeHandlebarFiles", required = true)
	private Set<String> includeHandlebarFiles;
	@Parameter(property = "excludeHandlebarFiles")
	private Set<String> excludeHandlebarFiles;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			final Config config = new Config.Factory().create(
					packageName,
					classNameSuffix,
					templateScriptClass,
					annotationClasses,
					outputDirectory,
					handlebarsSrcRoot,
					includeHandlebarFiles,
					excludeHandlebarFiles
			);

			new HandlebarTemplatesGenerator(config).generate();
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to generate hbs references", e);
		}
	}
}
