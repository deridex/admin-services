package cc.newmercy.contentservices.web.util;

import cc.newmercy.contentservices.web.exceptions.BadRequestException;

public final class WebPreconditions {

    public static void checkArgument(boolean condition, String messageFormat, Object... args) {
        if (!condition) {
            throw new BadRequestException(String.format(messageFormat, args));
        }
    }

    private WebPreconditions() { }
}
