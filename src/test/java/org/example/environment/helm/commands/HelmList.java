package org.example.environment.helm.commands;

import org.example.environment.helm.domain.HelmListingMapper;
import org.example.environment.shell.ShellExecutor;
import org.example.environment.shell.ShellQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.StringJoiner;

public class HelmList extends HelmParentCommand implements ShellQuery<HelmListingMapper > {

    HelmList() {}

    public HelmListingMapper query(ShellExecutor executor) throws IOException, InterruptedException, JSONException {
        JSONArray jsonArray = new JSONArray(executor.execute(buildCommand(), timeout.plusMinutes(1)).getStdout());

        if(jsonArray.isNull(0))
            return new HelmListingMapper();
        else
            return new ObjectMapper().readValue(jsonArray.getJSONObject(0).toString(), HelmListingMapper.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected String buildCommand() {
        StringJoiner stringJoiner = new StringJoiner(" ")
                .add("helm list")
                .add("--output " + HelmCommandOutputType.JSON);

        if (!isNullOrEmpty(kubeContext))
            stringJoiner.add("--kube-context " + kubeContext);

        if (!isNullOrEmpty(namespace))
            stringJoiner.add("-n " + namespace);

        if (wait)
            stringJoiner.add("--wait");

        return stringJoiner.toString();
    }

    public static class Builder extends HelmParentCommandBuilder<Builder, HelmList, OptionalParam> implements OptionalParam {
        public Builder() {
            super(HelmList.class);
        }
    }

    public interface OptionalParam extends OptionalParentCommandParams<OptionalParam, HelmList> {}
}
