package org.example.environment.helm;

import java.io.File;
import java.net.URL;

public class Chart {

    private final String chart;

    public Chart(String chartReference) {
        this.chart = chartReference;
    }

    public Chart(File file) {
        this.chart = file.getAbsolutePath();
    }

    public Chart(URL url) {
        this.chart = url.toExternalForm();
    }

    public String getChart() {
        return chart;
    }
}
