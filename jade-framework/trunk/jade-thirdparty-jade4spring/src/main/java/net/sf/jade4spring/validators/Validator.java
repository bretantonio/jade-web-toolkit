package net.sf.jade4spring.validators;

public class Validator {

    public static <T> void checkIfNullAndThrowIllegalException(T check) {

        if (check == null)
            throw new IllegalStateException();
    }

}
