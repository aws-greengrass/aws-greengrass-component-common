package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class ComponentUpdatePolicy {
    private Integer timeout;
    private Action action;

    public enum Action {
        NOTIFY_COMPONENTS,
        SKIP_NOTIFY_COMPONENTS;

        public static Action fromString(String s) {
            return Action.valueOf(s.toUpperCase());
        }
    }
}
