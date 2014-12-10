package cc.newmercy.contentservices.test.util;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Defines utility methods for asserting exceptions.
 */
public final class Exceptions {

    private Exceptions() { }

    /**
     * Wraps checked exceptions as as run-time exception.
     *
     * @see Exceptions#rethrowUnless(Exception, Matcher)
     */
    public static class CheckedExceptionWrapper extends RuntimeException {
        public CheckedExceptionWrapper(Throwable cause) {
            super(cause);
        }

        private static final long serialVersionUID = 2425114611199943290L;
    }

    /**
     * Rethrows the exception unless it matches. Useful for testing exception throwing logic. If the exception does not
     * match, then it will be throw back to the caller, preserving the complete stack trace.
     */
    public static <E extends Exception> void rethrowUnless(E e, Matcher<E> matcher) {
        if (!matcher.matches(e)) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new CheckedExceptionWrapper(e);
        }
    }

    /**
     * Returns a matcher returns true if an exception's cause matches the argument. Useful for checking wrapped exceptions.
     */
    public static <E extends Exception> Matcher<E> causedBy(final Matcher<?> causeMatcher) {

        notNull(causeMatcher, "Expected cause");

        return new BaseMatcher<E>() {

            private final Matcher<?> expected = causeMatcher;

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Exception)) {
                    return false;
                }

                Exception e = (Exception) o;

                return expected.matches(e.getCause());
            }

            @Override
            public void describeTo(Description desc) {
                desc.appendText("caused by").appendDescriptionOf(causeMatcher);
            }
        };
    }

    public static <E extends Exception> Matcher<E> hasMsg(String msg) {
        return hasMsg(equalTo(msg));
    }

    public static <E extends Exception> Matcher<E> hasMsg(final Matcher<String> msgMatcher) {
        return new BaseMatcher<E>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Exception)) {
                    return false;
                }

                Exception e = (Exception) o;

                return msgMatcher.matches(e.getMessage());
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("has message").appendDescriptionOf(msgMatcher);
            }
        };
    }

    private static <T> T notNull(T instance, String message) {
        if (null == instance) {
            throw new NullPointerException(message);
        }

        return instance;
    }
}
