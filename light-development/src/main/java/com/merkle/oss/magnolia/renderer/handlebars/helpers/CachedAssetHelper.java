package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.merkle.oss.magnolia.renderer.handlebars.utils.AssetLinkProvider;
import com.merkle.oss.magnolia.renderer.handlebars.utils.AssetLinkProviderFactory;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class CachedAssetHelper implements NamedHelper<Object> {
	private final AssetLinkProvider assetLinkProvider;

	@Inject
	public CachedAssetHelper(final AssetLinkProviderFactory assetLinkProviderFactory) {
		this.assetLinkProvider = assetLinkProviderFactory.create(getFrontendAssetsPath());
	}

	protected String getFrontendAssetsPath() {
		return "/frontend/assets";
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
		final String theme = Optional
				.ofNullable((String) options.context.get("theme.id"))
				.orElseGet(this::getDefaultTheme);

		return assetLinkProvider.getAssetLink(theme, name, timestamp, absolute);
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
