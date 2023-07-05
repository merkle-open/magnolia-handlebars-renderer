package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandlebarTemplatesGenerator {

	private final ClassNameNormalizer classNameNormalizer = new ClassNameNormalizer();
	private final Config config;

	public HandlebarTemplatesGenerator(final Config config) {
		this.config = config;
	}

	public void generate() throws IOException {
		final Path root = config.getHandlebarsSrcRoot();
		final Set<JavaFile> javaFiles = generateRecursive(root.toFile());
		write(javaFiles);
	}

	private void write(final Set<JavaFile> javaFiles) throws IOException {
		for (JavaFile javaFile : javaFiles) {
			javaFile.writeTo(config.getOutputDirectory());
		}
	}

	private Set<JavaFile> generateRecursive(final File file) {
		if (file.isDirectory()) {
			return Optional
					.ofNullable(file.listFiles())
					.stream()
					.flatMap(Arrays::stream)
					.map(this::generateRecursive)
					.flatMap(Collection::stream)
					.collect(Collectors.toSet());
		}
		if (matchesPattern(file)) {
			return Set.of(generate(file));
		}
		return Collections.emptySet();
	}

	private boolean matchesPattern(final File file) {
		return matchesPattern(file, config.getInclude()) && !matchesPattern(file, config.getExclude());
	}

	private boolean matchesPattern(final File file, Collection<String> patternList) {
		final Path relativePath = config.getHandlebarsSrcRoot().relativize(file.toPath());
		return patternList.stream()
				.anyMatch(pattern ->
						FileSystems.getDefault().getPathMatcher("glob:"+pattern).matches(relativePath)
				);
	}

	private JavaFile generate(final File hbsFile) {
		final String location = getLocation(hbsFile);
		final String templateKey = getTemplateKey(hbsFile);

		final AnnotationSpec generated = AnnotationSpec.builder(Generated.class)
				.addMember("value", "$S", this.getClass().getName())
				.addMember("date", "$S", LocalDateTime.now().toString())
				.build();

		final List<AnnotationSpec> annotations = config.getAnnotationClasses()
				.stream()
				.map(annotationClass ->
						getAnnotation(annotationClass, location, templateKey)
				)
				.collect(Collectors.toList());

		final MethodSpec constructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addStatement("super(\"$L\", \"$L\")", location, templateKey)
				.build();

		final TypeSpec type = TypeSpec.classBuilder(getClassName(hbsFile))
				.addAnnotation(generated)
				.addAnnotations(annotations)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(config.getTemplateScriptClass())
				.addMethod(constructor)
				.build();

		return JavaFile.builder(getPackage(hbsFile), type)
				.skipJavaLangImports(true)
				.indent("\t")
				.build();
	}

	private AnnotationSpec getAnnotation(
			final AnnotationClass annotationClass,
			final String location,
			final String templateKey
	) {
		final AnnotationSpec.Builder builder = AnnotationSpec.builder(ClassName.bestGuess(annotationClass.getClassName()));
		annotationClass.getLocationMethods().forEach(method ->
				builder.addMember(method, "$S", location)
		);
		annotationClass.getTemplateKeyMethods().forEach(method ->
				builder.addMember(method, "$S", templateKey)
		);
		return builder.build();
	}

	private String getLocation(File hbsFile) {
		final URI relativeHbsFile = config.getHandlebarsSrcRoot().toUri().relativize(hbsFile.toURI());
		return removeExtension(relativeHbsFile.toString());
	}

	private String getTemplateKey(final File hbsFile) {
		return hbsFile.getParentFile().getName() + "-" + removeExtension(hbsFile.getName());
	}

	private String getClassName(final File hbsFile) {
		final String templateName = removeExtension(hbsFile.getName());
		return classNameNormalizer.normalize(templateName) + config.getClassNameSuffix();
	}

	private String getPackage(final File hbsFile) {
		final Path relativeHbsFolderPath = config.getHandlebarsSrcRoot().relativize(hbsFile.getParentFile().toPath());

		return StringUtils.join(
				Stream.concat(
						Stream.of(config.getPackageName()),
						getPackageNames(relativeHbsFolderPath.toFile()).stream()
				).collect(Collectors.toList()),
				"."
		);
	}

	private List<String> getPackageNames(final File file) {
		return streamPackageNames(file).collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
			Collections.reverse(list);
			return list;
		}));
	}

	private Stream<String> streamPackageNames(final File file) {
		return Stream
				.concat(
						Stream.of(normalizePackage(file.getName())),
						Optional
								.ofNullable(file.getParentFile())
								.stream()
								.flatMap(this::streamPackageNames)
				);
	}

	private String normalizePackage(final String packageName) {
		return packageName.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	private String removeExtension(final String filePath){
		return filePath.substring(0, filePath.lastIndexOf('.'));
	}
}