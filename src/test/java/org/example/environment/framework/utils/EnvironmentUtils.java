package org.example.environment.framework.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentUtils {

    public static String createContainerNamePrefix(String prefix) {
        return prefix.replaceFirst("/","");
    }

    public static Map<String,String> updateAndGetDockerLabelsMap(String labelKey, String labelValue, String containerNamePrefix) {
        Map<String, String> labelMap = new HashMap<>();
        labelMap.put(labelKey, labelValue);

        if (containerNamePrefix.length() > 0)
            labelMap.put("tag", containerNamePrefix);

        return labelMap;
    }

    public static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 100);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
