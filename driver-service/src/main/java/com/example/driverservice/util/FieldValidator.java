package com.example.driverservice.util;

import com.example.driverservice.exception.IncorrectFieldNameException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class FieldValidator {
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";

    public void checkSortField(Class<?> myClass, String sortBy) {
        List<String> allowedSortFields = new ArrayList<>();
        getFieldNamesRecursive(myClass, allowedSortFields);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
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
