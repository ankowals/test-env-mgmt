package org.example.environment.helm.commands;

import io.fabric8.kubernetes.api.model.Namespace;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

public abstract class HelmParentCommand {

    protected String namespace;
    protected String kubeContext;

    //default settings
    protected boolean wait;
    protected Duration timeout = Duration.ofMinutes(15);
    protected HelmCommandOutputType output = HelmCommandOutputType.JSON;

    protected abstract String buildCommand();

    protected boolean isNullOrEmpty(String input) {
        return input == null || input.equals("");
    }

    @Override
    public String toString() {
        return buildCommand();
    }

    public static abstract class HelmParentCommandBuilder<SELF extends HelmParentCommandBuilder<SELF, COMMAND, LAST_STEP>,
            COMMAND extends HelmParentCommand,
            LAST_STEP extends OptionalParentCommandParams<LAST_STEP, COMMAND>>
            implements OptionalParentCommandParams<LAST_STEP, COMMAND> {

        protected COMMAND command;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public HelmParentCommandBuilder(Class clazz) {
            this.command = (COMMAND) createObject(clazz);
        }

        @SuppressWarnings("unchecked")
        @Override
        public LAST_STEP namespace(Namespace namespace) {
            command.namespace = namespace.getMetadata().getName();
            return (LAST_STEP) self();
        }

        @SuppressWarnings("unchecked")
        @Override
        public LAST_STEP kubeContext(String kubeContext) {
            command.kubeContext = kubeContext;
            return (LAST_STEP) self();
        }

        @SuppressWarnings("unchecked")
        @Override
        public LAST_STEP timeout(Duration timeout) {
            command.timeout = timeout;
            return (LAST_STEP) self();
        }

        @SuppressWarnings("unchecked")
        @Override
        public LAST_STEP output(HelmCommandOutputType output) {
            command.output = output;
            return (LAST_STEP) self();
        }

        @SuppressWarnings("unchecked")
        @Override
        public LAST_STEP await() {
            command.wait = true;
            return (LAST_STEP) self();
        }

        @Override
        public COMMAND build() {
            return command;
        }

        @SuppressWarnings("unchecked")
        public SELF self() {
            return (SELF) this;
        }

        private <K extends HelmParentCommand> K createObject(Class<K> clazz) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface OptionalParentCommandParams<SELF extends OptionalParentCommandParams<SELF, COMMAND>, COMMAND extends HelmParentCommand> {
        SELF namespace(Namespace namespace);
        SELF kubeContext(String kubeContext);
        SELF timeout(Duration timeout);
        SELF output(HelmCommandOutputType output);
        SELF await();
        COMMAND build();
    }
}
