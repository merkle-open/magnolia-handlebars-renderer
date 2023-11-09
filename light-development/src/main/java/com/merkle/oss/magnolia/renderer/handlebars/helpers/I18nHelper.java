package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.merkle.oss.magnolia.renderer.handlebars.utils.LocaleProvider;
import info.magnolia.i18nsystem.FixedLocaleProvider;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.i18nsystem.TranslationService;
import info.magnolia.jcr.util.ContentMap;

import javax.inject.Inject;
import javax.jcr.Node;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class I18nHelper implements NamedHelper<String> {
	private final TranslationService translationService;
	private final LocaleProvider localeProvider;

	@Inject
	public I18nHelper(
			final TranslationService translationService,
			final LocaleProvider localeProvider
	) {
		this.translationService = translationService;
		this.localeProvider = localeProvider;
	}

	@Override
	public CharSequence apply(final String key, final Options options) {
		final Node node = ((ContentMap) options.get("content")).getJCRNode();
		final Locale locale = localeProvider.getLocale(node);
		final SimpleTranslator simpleTranslator = new SimpleTranslator(translationService, new FixedLocaleProvider(locale));
		final String message = simpleTranslator.translate(key, options.params);
		return new Handlebars.SafeString(message);
	}

	@Override
	public Set<String> names() {
		return Set.of(
				"i18n",
				"t"
		);
	}
}
