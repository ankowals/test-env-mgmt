package org.example.environment.helm.commands;

import org.example.environment.shell.ShellCommand;
import org.example.environment.shell.ShellExecutor;
import org.example.environment.shell.ShellExecutorResult;
import java.io.IOException;
import java.util.StringJoiner;

public class HelmUninstall extends HelmParentCommand implements ShellCommand {

    private String releaseName;

    HelmUninstall() {}

    public ShellExecutorResult run(ShellExecutor executor) throws IOException, InterruptedException {
        return executor.execute(buildCommand(), timeout.plusMinutes(1));
    }

    public static MandatoryReleaseName builder() {
        return new Builder();
    }

    @Override
    protected String buildCommand() {
        StringJoiner stringJoiner = new StringJoiner(" ")
                .add("helm uninstall")
                .add(releaseName);

        if (!isNullOrEmpty(kubeContext))
            stringJoiner.add("--kube-context " + kubeContext);

        if (!isNullOrEmpty(namespace))
            stringJoiner.add("-n " + namespace);

        if (wait)
            stringJoiner.add("--wait");

        return stringJoiner.toString();
    }

    public static class Builder extends HelmParentCommandBuilder<Builder, HelmUninstall, OptionalParam>
            implements MandatoryReleaseName, OptionalParam {

        public Builder() {
            super(HelmUninstall.class);
        }

        @Override
        public OptionalParam releaseName(String releaseName) {
            command.releaseName = releaseName;
            return self();
        }
    }

    public interface MandatoryReleaseName {
        OptionalParam releaseName(String releaseName);
    }

    public interface OptionalParam extends OptionalParentCommandParams<OptionalParam, HelmUninstall> {}
}
