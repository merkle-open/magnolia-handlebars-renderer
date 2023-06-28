package com.merkle.oss.magnolia.renderer.handlebars.utils;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.magnolia.context.MgnlContext;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoFactory
public class AssetLinkProvider {
    private static final Pattern PATH_SUFFIX_PATTERN = Pattern.compile("^([^#]*)((#.*)|$)$");

    private final LinkUtil linkUtil;
    private final TimestampUtil timestampUtil;
    private final String frontendAssetsPath;

    public AssetLinkProvider(
        @Provided final LinkUtil linkUtil,
        @Provided final TimestampUtil timestampUtil,
        final String frontendAssetsPath
    ) {
        this.linkUtil = linkUtil;
        this.timestampUtil = timestampUtil;
        this.frontendAssetsPath = frontendAssetsPath;
    }

    public String getAssetLink(
        final String theme,
        final String assetPath,
        final boolean timestamp,
        final boolean absolute
    ) throws IOException {
        return toAbsolutePath(
            addContextPath(addResourcePrefix(addTimestamp(theme, assetPath, timestamp))),
            absolute
        );
    }

    private String addResourcePrefix(final String path) {
        return "/.resources" + path;
    }

    private String addContextPath(final String path) {
        return MgnlContext.getContextPath() + path;
    }

    private String toAbsolutePath(final String path, final boolean shouldApply) {
        if (shouldApply) {
            return linkUtil.toExternalLink(path);
        }
        return path;
    }

    private String addTimestamp(final String theme, final String path, final boolean shouldApply) throws IOException {
        if (shouldApply) {
            final Matcher matcher = PATH_SUFFIX_PATTERN.matcher(path);
            if (matcher.find()) {
                final String filePath = format("${frontendAssetsPath}/${theme}/${path}", Map.of(
                    "frontendAssetsPath", frontendAssetsPath,
                    "theme", theme,
                    // check if path is a svg icon (e.g. /svg/icons.svg#arrowLeft)
                    "path", matcher.group(1)
                ));
                return timestampUtil.addTimestamp(filePath) + Optional.ofNullable(matcher.group(2)).orElse("");
            }
        }
        return path;
    }

    private String format(final String template, Map<String, String> placeholder) {
        return new StringSubstitutor(placeholder).replace(template);
    }
}
