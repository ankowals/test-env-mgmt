package org.example.environment.shell;

public interface ShellQuery<T> {
    T query(ShellExecutor executor) throws Exception;
}
