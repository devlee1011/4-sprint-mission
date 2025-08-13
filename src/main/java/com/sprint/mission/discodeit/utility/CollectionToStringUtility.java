package com.sprint.mission.discodeit.utility;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CollectionToStringUtility {
    public static <T> String joinToStringByComma(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return collection.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
