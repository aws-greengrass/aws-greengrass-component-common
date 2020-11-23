package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@JsonSerialize
@JsonDeserialize(builder = ComponentUpdatePolicy.ComponentUpdatePolicyBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentUpdatePolicy {

    public static final Integer DEFAULT_TIMEOUT = 60;
    public static final Action DEFAULT_ACTION = Action.NOTIFY_COMPONENTS;

    @NonNull
    @Builder.Default
    private Integer timeout = DEFAULT_TIMEOUT;

    @NonNull
    @Builder.Default
    private Action action = DEFAULT_ACTION;

    public enum Action {
        NOTIFY_COMPONENTS,
        SKIP_NOTIFY_COMPONENTS;

        public static Action fromString(String s) {
            return Action.valueOf(s.toUpperCase());
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentUpdatePolicyBuilder {
    }
}
