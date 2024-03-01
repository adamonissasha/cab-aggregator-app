package com.example.passengerservice.util;

import com.example.passengerservice.exception.IncorrectFieldNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FieldValidator {
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";

    public Mono<Void> checkSortField(Class<?> myClass, String sortBy) {
        return Mono.defer(() -> {
            List<String> allowedSortFields = new ArrayList<>();
            getFieldNamesRecursive(myClass, allowedSortFields);
            if (!allowedSortFields.contains(sortBy)) {
                log.error("Invalid sortBy field. Allowed fields: {}", allowedSortFields);
                return Mono.error(new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields));
            }
            return Mono.empty();
        });
    }

    private static void getFieldNamesRecursive(Class<?> myClass, List<String> fieldNames) {
        if (myClass != null) {
            Field[] fields = myClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            getFieldNamesRecursive(myClass.getSuperclass(), fieldNames);
        }
    }
}
