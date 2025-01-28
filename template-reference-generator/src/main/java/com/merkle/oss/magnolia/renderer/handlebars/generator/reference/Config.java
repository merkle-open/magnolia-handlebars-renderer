package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import com.squareup.javapoet.ClassName;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class Config {
	private final String packageName;
	private final String classNameSuffix;
	private final ClassName templateScriptClass;
	private final Set<AnnotationClass> annotationClasses;
	private final Path outputDirectory;
	private final Path handlebarsSrcRoot;
	private final Set<String> include;
	private final Set<String> exclude;

	public Config(
			final String packageName,
			final String classNameSuffix,
			final ClassName templateScriptClass,
			final Set<AnnotationClass> annotationClasses,
			final Path outputDirectory,
			final Path handlebarsSrcRoot,
			final Set<String> include,
			final Set<String> exclude
	) {
		this.packageName = packageName;
		this.classNameSuffix = classNameSuffix;
		this.templateScriptClass = templateScriptClass;
		this.annotationClasses = annotationClasses;
		this.outputDirectory = outputDirectory;
		this.handlebarsSrcRoot = handlebarsSrcRoot;
		this.include = include;
		this.exclude = exclude;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	public ClassName getTemplateScriptClass() {
		return templateScriptClass;
	}

	public Set<AnnotationClass> getAnnotationClasses() {
		return annotationClasses;
	}

	public Path getOutputDirectory() {
		return outputDirectory;
	}

	public Path getHandlebarsSrcRoot() {
		return handlebarsSrcRoot;
	}

	public Set<String> getInclude() {
		return include;
	}

	public Set<String> getExclude() {
		return exclude;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Config config = (Config) o;
		return Objects.equals(packageName, config.packageName) && Objects.equals(classNameSuffix, config.classNameSuffix) && Objects.equals(templateScriptClass, config.templateScriptClass) && Objects.equals(annotationClasses, config.annotationClasses) && Objects.equals(outputDirectory, config.outputDirectory) && Objects.equals(handlebarsSrcRoot, config.handlebarsSrcRoot) && Objects.equals(include, config.include) && Objects.equals(exclude, config.exclude);
	}

	@Override
	public int hashCode() {
		return Objects.hash(packageName, classNameSuffix, templateScriptClass, annotationClasses, outputDirectory, handlebarsSrcRoot, include, exclude);
	}

	@Override
	public String toString() {
		return "Config{" +
				"packageName='" + packageName + '\'' +
				", classNameSuffix='" + classNameSuffix + '\'' +
				", templateScriptClass=" + templateScriptClass +
				", annotationClasses=" + annotationClasses +
				", output=" + outputDirectory +
				", frontendSrcRoot=" + handlebarsSrcRoot +
				", include=" + include +
				", exclude=" + exclude +
				'}';
	}

	public static class Factory {
		private static final String JAVA_IDENTIFIER = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
		private static final Pattern PACKAGE_PATTERN = Pattern.compile("^[." + JAVA_IDENTIFIER + "]*");

		public Config create(
				final String packageName,
				final String classNameSuffix,
				final String templateScriptClass,
				final Set<AnnotationClass> annotationClasses,
				final String output,
				final String frontendSrcRoot,
				final Set<String> includeHbs,
				final Set<String> excludeHbs
		) {
			return new Config(
					validatePackage(packageName),
					classNameSuffix,
					ClassName.bestGuess(validatePackage(templateScriptClass)),
					annotationClasses,
					Path.of(output),
					Path.of(frontendSrcRoot),
					includeHbs,
					excludeHbs
			);
		}

		private String validatePackage(final String packageParam) {
			if (PACKAGE_PATTERN.matcher(packageParam).matches()) {
				return packageParam;
			}
			throw new IllegalArgumentException("Invalid package: " + packageParam);
		}
	}
}
