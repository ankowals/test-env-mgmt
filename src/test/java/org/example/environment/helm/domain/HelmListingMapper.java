package org.example.environment.helm.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelmListingMapper {
    String name;
    String namespace;
    String revision;
    String updated;
    String status;
    String chart;
    String app_version;
}
