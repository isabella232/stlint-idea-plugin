package org.edadeal.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class StylusLinterRunner {
    private StylusLinterRunner() {
    }

    private static final Logger LOG = Logger.getInstance(StylusLinterRunner.class);
    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);
    private static final int FILES_NOT_FOUND = 66;

    public static class StylusLinterSettings {
        StylusLinterSettings(String config, String cwd, String targetFile, String StylusLinterExe) {
            this.config = config;
            this.cwd = cwd;
            this.targetFile = targetFile;
            this.StylusLinterExe = StylusLinterExe;
        }

        public String config;
        public String cwd;
        String targetFile;
        public String StylusLinterExe;
    }

    public static StylusLinterSettings buildSettings(@NotNull String cwd, @NotNull String path, @NotNull String StylusLinterExe, @Nullable String config) {
        return new StylusLinterSettings(config, cwd, path, StylusLinterExe);
    }

    public static Result runLint(@NotNull String cwd, @NotNull String file, @NotNull String StylusLinterExe, @Nullable String text) {
        Result result = new Result();

        try {
            ProcessOutput out = lint(cwd, file, StylusLinterExe, text);
            result.errorOutput = out.getStderr();

            try {
                if (out.getExitCode() != FILES_NOT_FOUND) {
                    result.output = out.getStdout();
                    result.isOk = true;
                }
            } catch (Exception e) {
                result.errorOutput = out.getStdout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }

        return result;
    }

    public static class Result {
        public boolean isOk = false;
        public String output;
        public String errorOutput;
    }

    @NotNull
    public static ProcessOutput lint(@NotNull String cwd, @NotNull String file, @NotNull String StylusLinterExe, @Nullable String text) throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(cwd);
        commandLine.setExePath(StylusLinterExe);

        commandLine.addParameter(file);
        commandLine.addParameter("--reporter");
        commandLine.addParameter("json");

        if (StringUtils.isNotEmpty(text)) {
            commandLine.addParameter("--content");
            commandLine.addParameter(text);
        }

        return NodeRunner.execute(commandLine, TIME_OUT);
    }

    @NotNull
    private static ProcessOutput version(@NotNull StylusLinterSettings settings) throws ExecutionException {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("-v");

        return NodeRunner.execute(commandLine, TIME_OUT);
    }

    @NotNull
    public static String runVersion(@NotNull StylusLinterSettings settings) throws ExecutionException {
        if (!new File(settings.StylusLinterExe).exists()) {
            LOG.warn("Calling version with invalid StylusLinterExe exe " + settings.StylusLinterExe);
            return "";
        }

        ProcessOutput out = version(settings);
        if (out.getExitCode() == 0) {
            return out.getStdout().trim();
        }

        return "";
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull StylusLinterSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(settings.cwd);
        commandLine.setExePath(settings.StylusLinterExe);

        return commandLine;
    }
}