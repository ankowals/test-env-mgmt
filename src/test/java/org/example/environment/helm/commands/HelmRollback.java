package org.example.environment.helm.commands;

import org.example.environment.shell.ShellCommand;
import org.example.environment.shell.ShellExecutor;
import org.example.environment.shell.ShellExecutorResult;
import java.io.IOException;
import java.util.StringJoiner;

public class HelmRollback extends HelmParentCommand implements ShellCommand {

    private String releaseName;
    private int revision;

    HelmRollback() {}

    public ShellExecutorResult run(ShellExecutor executor) throws IOException, InterruptedException {
        return executor.execute(buildCommand(), timeout.plusMinutes(1));
    }

    public static MandatoryReleaseName builder() {
        return new Builder();
    }

    @Override
    protected String buildCommand() {
        StringJoiner stringJoiner = new StringJoiner(" ")
                .add("helm rollback")
                .add(releaseName);

        if (revision > 0)
            stringJoiner.add(String.valueOf(revision));

        if (!isNullOrEmpty(kubeContext))
            stringJoiner.add("--kube-context " + kubeContext);

        if (!isNullOrEmpty(namespace))
            stringJoiner.add("-n " + namespace);

        if (wait)
            stringJoiner.add("--wait");

        return stringJoiner.toString();
    }

    public static class Builder extends HelmParentCommandBuilder<Builder, HelmRollback, OptionalParam>
            implements MandatoryReleaseName, OptionalParam {

        public Builder() {
            super(HelmRollback.class);
        }

        @Override
        public OptionalParam releaseName(String releaseName) {
            command.releaseName = releaseName;
            return self();
        }

        @Override
        public OptionalParam revision(int revision) {
            command.revision = revision;
            return self();
        }
    }

    public interface MandatoryReleaseName {
        OptionalParam releaseName(String releaseName);
    }

    public interface OptionalParam extends OptionalParentCommandParams<OptionalParam, HelmRollback> {
        OptionalParam revision(int revision);
    }
}
