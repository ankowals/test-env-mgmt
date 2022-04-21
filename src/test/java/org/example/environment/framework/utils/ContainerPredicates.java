package org.example.environment.framework.utils;

import com.github.dockerjava.api.model.Container;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class ContainerPredicates {

    public static Predicate<Container> hasOpenPort(int port) {
        return container -> container.getPorts() != null
                && Arrays.stream(container.getPorts())
                .anyMatch(p -> Objects.equals(p.getPublicPort(), port));
    }

    public static Predicate<Container> doesNotHaveOpenPort(int port) {
        return container -> container.getPorts() != null
                && Arrays.stream(container.getPorts()).noneMatch(p -> Objects.equals(p.getPublicPort(), port));
    }

    public static Predicate<Container> doesNotHaveOpenPrivatePort(int port) {
        return container -> container.getPorts() != null
                && Arrays.stream(container.getPorts()).noneMatch(p -> Objects.equals(p.getPrivatePort(), port));
    }

    public static Predicate<Container> hasAnyNetwork() {
        return container -> container.getNetworkSettings() != null
                && container.getNetworkSettings().getNetworks() != null;
    }

    public static Predicate<Container> hasValidNetworkSettings(String network) {
        return container -> Objects.requireNonNull(container.getNetworkSettings())
                .getNetworks()
                .keySet()
                .stream()
                .anyMatch(net -> net.equals(network));
    }

    public static Predicate<Container> hasName(String name) {
        return container -> Arrays.stream(container.getNames())
                .map(containerName -> containerName.replaceFirst("/",""))
                .anyMatch(name::equals);
    }

    public static Predicate<Container> hasImageContaining(String name) {
        return container -> container.getImage().contains(name);
    }
}
