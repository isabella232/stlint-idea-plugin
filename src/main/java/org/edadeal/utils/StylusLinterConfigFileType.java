package org.edadeal.utils;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.edadeal.StylusLinterIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StylusLinterConfigFileType extends LanguageFileType {
    static final StylusLinterConfigFileType INSTANCE = new StylusLinterConfigFileType();
    private static final String STYLINTRC_EXT = "stylintrc";
    public static final String STYLINTRC = '.' + STYLINTRC_EXT;

    private StylusLinterConfigFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Stylus Linter";
    }

    @NotNull
    public String getDescription() {
        return "Stylus Linter configuration file";
    }

    @NotNull
    public String getDefaultExtension() {
        return STYLINTRC_EXT;
    }

    @NotNull
    public Icon getIcon() {
        return StylusLinterIcons.ICON;
    }
}