package cc.newmercy.contentservices.util;

import java.util.Objects;

public final class Arguments {
    public static String requireNotEmpty(String argument, String message) {
        Objects.requireNonNull(argument, "argument");

        if (argument.isEmpty()) {
            throw new IllegalArgumentException(message);
        }

        return argument;
    }

    private Arguments() { }
}
