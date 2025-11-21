package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Options;
import com.merkle.oss.magnolia.renderer.handlebars.utils.AssetLinkProviderFactory;

import jakarta.inject.Inject;

public class CachedAssetHelper implements NamedHelper<Object> {
	private final AssetLinkProviderFactory assetLinkProviderFactory;

	@Inject
	public CachedAssetHelper(final AssetLinkProviderFactory assetLinkProviderFactory) {
		this.assetLinkProviderFactory = assetLinkProviderFactory;
	}

	protected String getFrontendAssetsPath(final Object context, final Options options) {
		return "/frontend/assets";
	}

	protected Optional<String> getTheme(final Object context, final Options options) {
		return Optional.ofNullable((String) options.context.get("theme.id"));
	}

	protected String getDefaultTheme() {
		return "default";
	}

	/*
	 * Examples:
	 *
	 * <link href="{{asset name='/css/ui.rev.min.css'}}" rel="stylesheet">
	 * --> https://<DOMAIN>/.resources/frontend/assets/theme/css/ui.rev.min~2021-09-20-03-50-02-000~cache.css
	 *
	 * <link href="{{asset name='svg/icons.svg#home')}}" rel="stylesheet">
	 * --> https://<DOMAIN>/.resources/frontend/assets/theme/svg/icons.svg~2021-09-20-03-50-02-000~cache.svg#home
	 *
	 * <link href="{{asset name='/css/ui.rev.min.css' absolute=false}}" rel="stylesheet">
	 * --> /.resources/frontend/assets/theme/css/ui.rev.min~2021-09-20-03-50-02-000~cache.css
	 *
	 * <link href="{{asset name='/css/ui.rev.min.css' timestamp=false}}" rel="stylesheet">
	 * --> https://<DOMAIN>/.resources/frontend/assets/theme/css/ui.rev.min.css
	 */
	@Override
	public CharSequence apply(final Object context, final Options options) throws IOException {
		final String name = Optional
				.ofNullable((String) options.hash("name"))
				.map(n -> StringUtils.removeStart(n, "/"))
				.map(n -> replaceLang(n, options))
				.map(n -> replaceModifier(n, options))
				.orElseThrow(() -> new NullPointerException("name is missing!"));
		final boolean timestamp = options.hash("timestamp", true);
		final boolean absolute = options.hash("absolute", false);
		final String theme = getTheme(context, options).orElseGet(this::getDefaultTheme);

		return assetLinkProviderFactory.create(getFrontendAssetsPath(context, options)).getAssetLink(theme, name, timestamp, absolute);
	}

	private String replaceLang(final String path, final Options options) {
		return replaceVariable(path, options, "lang");
	}

	private String replaceModifier(final String path, final Options options) {
		return replaceVariable(path, options, "modifier");
	}

	private String replaceVariable(final String path, final Options options, final String variableName) {
		return Optional
				.ofNullable((String) options.hash(variableName))
				.map(placeholder ->
						StringUtils.replace(path, "{" + variableName + "}", placeholder)
				)
				.orElse(path);
	}

	@Override
	public Set<String> names() {
		return Set.of("asset");
	}
}
