package org.example.environment.helm.commands;

import org.example.environment.helm.Chart;
import org.example.environment.shell.ShellCommand;
import org.example.environment.shell.ShellExecutor;
import org.example.environment.shell.ShellExecutorResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HelmInstall extends HelmParentCommand implements ShellCommand {

    private String releaseName;
    private Chart chart;
    private List<File> valuesFiles;
    private Map<String, String> sets;
    private String repo;

    HelmInstall() {}

    public ShellExecutorResult run(ShellExecutor executor) throws IOException, InterruptedException {
        return executor.execute(buildCommand(), timeout.plusMinutes(1));
    }

    public static MandatoryReleaseName builder() {
        return new Builder();
    }

    @Override
    protected String buildCommand() {
        StringJoiner stringJoiner = new StringJoiner(" ")
                .add("helm install")
                .add(releaseName)
                .add(chart.getChart())
                .add("--timeout " + timeout.toMinutes() + "m")
                .add("-o " + output.toString());

        if (valuesFiles != null)
            valuesFiles.forEach(file -> stringJoiner.add("--values " + file.getAbsolutePath()));

        if (sets != null)
            sets.forEach((k, v) -> stringJoiner.add("--set " + k + "=" + v));

        if (!isNullOrEmpty(kubeContext))
            stringJoiner.add("--kube-context " + kubeContext);

        if (!isNullOrEmpty(namespace))
            stringJoiner.add("-n " + namespace);

        if (wait)
            stringJoiner.add("--wait");

        if (!isNullOrEmpty(repo))
            stringJoiner.add("--repo " + repo);

        return stringJoiner.toString();
    }

    public static class Builder extends HelmParentCommandBuilder<Builder, HelmInstall, OptionalParam>
            implements MandatoryReleaseName, MandatoryChart, OptionalParam {

        public Builder() {
            super(HelmInstall.class);
        }

        @Override
        public MandatoryChart releaseName(String releaseName) {
            command.releaseName = releaseName;
            return self();
        }

        @Override
        public OptionalParam chart(Chart chart) {
            command.chart = chart;
            return self();
        }

        @Override
        public OptionalParam valuesFiles(List<File> valuesFiles) {
            command.valuesFiles = valuesFiles;
            return self();
        }

        @Override
        public OptionalParam valuesFiles(File... valuesFiles) {
            command.valuesFiles = Arrays.asList(valuesFiles);
            return self();
        }

        @Override
        public OptionalParam sets(Map<String, String> sets) {
            command.sets = sets;
            return self();
        }

        @Override
        public OptionalParam repo(String repo) {
            command.repo = repo;
            return self();
        }
    }

    public interface MandatoryReleaseName {
        MandatoryChart releaseName(String releaseName);
    }

    public interface MandatoryChart {
        OptionalParam chart(Chart chart);
    }

    public interface OptionalParam extends OptionalParentCommandParams<OptionalParam, HelmInstall> {
        OptionalParam valuesFiles(List<File> valuesFiles);
        OptionalParam valuesFiles(File... valuesFiles);
        OptionalParam sets(Map<String, String> sets);
        OptionalParam repo(String repo);
    }
}
