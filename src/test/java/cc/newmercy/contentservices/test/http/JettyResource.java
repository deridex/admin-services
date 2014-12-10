package cc.newmercy.contentservices.test.http;

import static com.google.common.base.Preconditions.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.hamcrest.*;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

/**
 * <p>
 * An embedded http server wrapped in a junit method rule that starts on a random port. This resource allows you to
 * define expectations of received requests and script responses when expectations are met. This is easier that mocking
 * out a complete request-response interaction in httpclient. With this resource, you can, for example, define an
 * expectation that a GET request has a certain path and query string parameters and also define a response that returns
 * pre-canned data to the caller. If the a request does not match the next expectation, then the client will receive a
 * {@code 400 Bad Request} response.
 * </p>
 *
 * <p>
 * For example:
 * </p>
 *
 * <pre>
 * import static cc.newmercy.contentservices.test.http.JettyResource.bodyParamsOf;
 * import static cc.newmercy.contentservices.test.http.JettyResource.getOf;
 * import static cc.newmercy.contentservices.test.http.JettyResource.hasBody;
 * import static cc.newmercy.contentservices.test.http.JettyResource.hasQueryString;
 * import static cc.newmercy.contentservices.test.http.JettyResource.hasUrlPath;
 * import static cc.newmercy.contentservices.test.http.JettyResource.includesHeaders;
 * import static cc.newmercy.contentservices.test.http.JettyResource.isPost;
 * import static cc.newmercy.contentservices.test.http.JettyResource.noParams;
 * import static cc.newmercy.contentservices.test.http.JettyResource.params;
 * import static cc.newmercy.contentservices.test.http.JettyResource.postOf;
 * import static org.hamcrest.Matchers.allOf;
 *
 * ...
 *
 * &#64;Rule
 * public final JettyResource jetty = new JettyResource();
 *
 * private ClassUnderTest classUnderTest;
 *
 * &#64;Before
 * public void setup() {
 *     classUnderTest = new ClassUnderTest("http://127.0.0.1:" + jetty.getPort());
 * }
 *
 * &#64;Test
 * public void example() {
 *     jetty.expect(allOf(
 *             isPost(),
 *             hasUrlPath("/expected/path"),
 *             hasQueryString(params("single-arg-query-string-param-1", "single-arg-query-string-param-1-value")
 *                     .and("multi-arg-query-string-param-1", "multi-arg-query-string-param-1-value-1", "multi-arg-query-string-param-1-value-2", "multi-arg-query-string-param-1-value-3"))
 *             includesHeaders(params("single-arg-header-1", "single-arg-header-1-value")
 *                      .and("multi-arg-header-1", "multi-arg-header-1-value-1", "multi-arg-header-1-value-2", "multi-arg-header-1-value-3")
 *                      .and("single-arg-header-2", "single-arg-header-2-value")),
 *             hasBody(bodyParamsOf(params("single-arg-body-parameter-1", "single-arg-body-parameter-1-value")]
 *                      .and("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-1", "multi-arg-body-parameter-1-value-2", "multi-arg-body-parameter-1-value-3")))))
 *             .then(new JettyResource.TestResponse() {
 *                 &#64;Override
 *                 public void handle(HttpServletResponse response) throws Exception {
 *                     response.getWriter().print("I'm here!");
 *                 }
 *             });
 *
 *     ...
 *
 *     classUnderTest.doHttpWork();
 * }
 *
 * &#64;Test
 * public void workAroundJavacSilliness() {
 *     /*
 *      * javac cannot infer the type arguments to Matchers.allOf() so use ExpectationAndResponse.and() instead.
 *      * the eclipse compiler is able to infer the type arguments, however. oh, well.
 *      *&#47;
 *     jetty.expect(isPost())
 *             .and(hasUrlPath("/expected/path"))
 *             .and(hasQueryString(params("single-arg-query-string-param-1", "single-arg-query-string-param-1-value")
 *                     .and("multi-arg-query-string-param-1", "multi-arg-query-string-param-1-value-1", "multi-arg-query-string-param-1-value-2", "multi-arg-query-string-param-1-value-3")))
 *             .and(includesHeaders(params("single-arg-header-1", "single-arg-header-1-value")
 *                      .and("multi-arg-header-1", "multi-arg-header-1-value-1", "multi-arg-header-1-value-2", "multi-arg-header-1-value-3")
 *                      .and("single-arg-header-2", "single-arg-header-2-value")))
 *             .and(hasBody(bodyParamsOf(params("single-arg-body-parameter-1", "single-arg-body-parameter-1-value")]
 *                      .and("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-1", "multi-arg-body-parameter-1-value-2", "multi-arg-body-parameter-1-value-3"))))
 *             .then(new JettyResource.TestResponse() {
 *                 &#64;Override
 *                 public void handle(HttpServletResponse response) throws Exception {
 *                     response.getWriter().print("I'm here!");
 *                 }
 *             });
 *
 *     ...
 *
 *     classUnderTest.doHttpWork();
 * }
 * </pre>
 *
 * @see #getOf(String, ParamsMatcher, ParamsMatcher)
 * @see #postOf(String, ParamsMatcher, ParamsMatcher, Matcher)
 * @see #isGet()
 * @see #isPost()
 * @see #hasUrlPath(String)
 * @see #hasQueryString(ParamsMatcher)
 * @see #includesHeaders(ParamsMatcher)
 * @see #hasBody(Matcher)
 * @see #bodyParamsOf(ParamsMatcher)
 * @see #params(String, String, String...)
 * @see #noParams()
 * @see ParamMatcher
 */
public class JettyResource implements TestRule {

    private final BlockingQueue<Object[]>  scheduledResponses = new LinkedBlockingQueue<Object[]>();

    private int port;

    private Server server;

    /**
     * Sets a random port number for the jetty instance.
     */
    public JettyResource() {
        for (int i = 0; i < 3 && server == null; i++) {
            port = new Random(System.nanoTime()).nextInt((1 << 16 - 1) - 1024) + 1024;

            try {
                server = startServer(port);
            } catch (IllegalArgumentException e) {
                if (i == 2) {
                    throw e;
                }
            }
        }
    }

    /**
     * Sets the given port number for the jetty instance.
     */
    public JettyResource(int portArg) {
        checkArgument(portArg > 0, "port");
        port = portArg;
        server = startServer(port);
    }

    public int getPort() {
        return port;
    }

    @Override
    public Statement apply(final Statement base, org.junit.runner.Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                Throwable propogate = null;

                try {
                    base.evaluate();
                } catch (Throwable t) {
                    propogate = t;
                } finally {
                    stopServer(propogate);
                }
            }
        };
    }

    private Server startServer(int port) throws IllegalArgumentException {
        Server server = new Server(port);

        try {

            ContextHandler context = new ContextHandler();
            context.setContextPath("/");
            context.setResourceBase(".");
            context.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.setHandler(context);

            context.setHandler(new RequestVerifyingAndResponseInvokingHandler());

            server.start();

            return server;
        } catch (Exception e) {
            try {
                server.stop();
            } catch (Exception stopExc) {
                stopExc.printStackTrace();
            }

            throw new IllegalArgumentException("port " + port, e);
        }
    }

    private void stopServer(Throwable propogate) throws Throwable {
        try {
            if (propogate == null) {
                List<Object> uninvoked = new ArrayList<Object>();

                scheduledResponses.drainTo(uninvoked);

                for (int i = 0; i < uninvoked.size(); i++) {
                    uninvoked.set(i, (((Object[]) uninvoked.get(i))[0]));
                }

                assertThat("there are uninvoked expectations and responses", uninvoked, empty());
            }
        } catch (Throwable t) {
            if (propogate == null) {
                propogate = t;
            }
        } finally {
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception e) {
                    if (propogate == null) {
                        propogate = e;
                    }
                }
            }

            if (propogate != null) {
                throw propogate;
            }
        }
    }

    /**
     * Compares the request with expectations, invoking the {@link TestResponse} if expectations are met or throwing an
     * exception otherwise.
     */
    private class RequestVerifyingAndResponseInvokingHandler extends AbstractHandler {
                @SuppressWarnings("unchecked")
        @Override
        public void handle(
                String target,
                Request baseRequest,
                HttpServletRequest request,
                HttpServletResponse response) throws IOException, ServletException {
            Object[] scheduledResponse = scheduledResponses.poll();

            Matcher<HttpServletRequest> when = (Matcher<HttpServletRequest>) scheduledResponse[0];
            TestResponse then = (TestResponse) scheduledResponse[1];

            if (!when.matches(request)) {
                Description description = new StringDescription();
                description.appendText("Expected: ")
                           .appendDescriptionOf(when)
                           .appendText("\n     but: ");
                when.describeMismatch(request, description);

                response.sendError(BAD_REQUEST_STATUS_CODE, description.toString());

                return;
            }

            try {
                then.handle(response);
            } catch (Exception e) {
                Throwables.propagate(e);
            }

            baseRequest.setHandled(true);
        }
    }

    /**
     * Functional interface for scripting a response to an expected request.
     */
    public interface TestResponse {
        void handle(HttpServletResponse response) throws Exception;
    }

    /**
     * Fluent builder for defining expectations of parameters' arguments.
     */
    public static class ParamsMatcher {

        private final Map<String, ParamMatcher> nameToExpectation = new HashMap<String, ParamMatcher>();

        private ParamsMatcher() { }

        protected ParamsMatcher(String firstParamName, String expectedArg0, String... moreExpectedArgs) {
            and(firstParamName, expectedArg0, moreExpectedArgs);
        }

        /**
         * Adds the expectation for the parameter's arguments and returns this.
         */
        public ParamsMatcher and(String paramName, String expectedArg0, String... moreExpectedArgs) {
            checkNotNull(paramName, "paramater name");
            checkArgument(!paramName.isEmpty(), "parameter name is empty");

            checkNotNull(expectedArg0, "first argument");
            checkNotNull(moreExpectedArgs, "subsequent argumens");

            String[] allArgs = new String[moreExpectedArgs.length + 1];

            allArgs[0] = expectedArg0;

            for (int src = 0, dst = 1; src < moreExpectedArgs.length; src++, dst++) {
                checkNotNull(moreExpectedArgs[src], "argument %s is null", dst);

                allArgs[dst] = moreExpectedArgs[src];
            }

            ParamMatcher expectation = new ParamMatcher(Arrays.asList(allArgs));

            if (nameToExpectation.put(paramName, expectation) != null) {
                throw new IllegalArgumentException("expectation for parameter '" + paramName + "' already defined");
            }

            return this;
        }

        @Override
        public String toString() {
            return nameToExpectation.toString();
        }
    }

    /**
     * Sets argument expectations for a parameter and returns a builder that can be used to define more expectations.
     */
    public static ParamsMatcher params(String paramName, String arg0, String... moreArgs) {
        return new ParamsMatcher(paramName, arg0, moreArgs);
    }

    /**
     * Returns an expectation for no parameters.
     */
    public static ParamsMatcher noParams() {
        return EMPTY_PARAMS;
    }

    /**
     * Matches parameter arguments.
     */
    private final static class ParamMatcher {

        private final List<String> expected;

        private ParamMatcher(List<String> expectedArg) {
            expected = checkNotNull(expectedArg);
        }

        private boolean matches(List<String> item) {
            return Objects.equal(item, expected);
        }

        @Override
        public String toString() {
            return expected.toString();
        }
    }

    public final class ExpectationAndResponse {

        private final List<Matcher<HttpServletRequest>> requestMatchers = new ArrayList<Matcher<HttpServletRequest>>();

        private boolean done;

        private ExpectationAndResponse(Matcher<HttpServletRequest> requestMatcherArg) {
            and(requestMatcherArg);
        }

        /**
         * Add another request expectation. All expectations must be satisfied (logical AND). This is only here because
         * javac 6 cannot infer the type arguments to {@link Matchers}.allOf() (the eclipse compiler can).
         */
        public ExpectationAndResponse and(Matcher<HttpServletRequest> anotherExpectation) {
            checkState(!done, "instances cannot be reused");
            checkNotNull(anotherExpectation, "request expectation");

            requestMatchers.add(anotherExpectation);

            return this;
        }

        /**
         * Defines the response when the request satisfies the expectation.
         */
        @SuppressWarnings("unchecked")
        public void then(TestResponse respondWith) {
            checkState(!done, "instances cannot be reused");
            checkNotNull(respondWith, "response");

            done = true;

            Matcher<?>[] array = requestMatchers.toArray(new Matcher<?>[requestMatchers.size()]);

            Matcher<HttpServletRequest> matcher = Matchers.<HttpServletRequest> allOf((Matcher<HttpServletRequest>[]) array);

            boolean taken = scheduledResponses.offer(new Object[] { matcher, respondWith });

            /*
             * Shouldn't ever happen.
             */
            checkState(taken, "queue full");
        }
    }

    /**
     * Returns a builder that defines an expectation of a request and a response when that expectation is satisfied.
     *
     * @see ExpectationAndResponse#then(TestResponse)
     */
    public ExpectationAndResponse expect(Matcher<HttpServletRequest> when) {
        checkNotNull(when, "request expectation");

        return new ExpectationAndResponse(when);
    }

    /**
     * Returns a matcher for a GET request.
     *
     * @param expectedPath
     *            The path of the request.
     * @param queryStringExpectations
     *            The query parameters to expect. Missing or extra query string parameters are not allowed.
     * @param headerExpectations
     *            The headers to expect. Extra headers are allowed since they are part of the http spec.
     */
    public static Matcher<HttpServletRequest> getOf(
            final String expectedPath,
            final ParamsMatcher queryStringExpectations,
            final ParamsMatcher headerExpectations) {
        return allOf(isGet(),
                hasUrlPath(expectedPath),
                hasQueryString(queryStringExpectations),
                includesHeaders(headerExpectations));
    }

    /**
     * Returns a matcher for a PUT request.
     *
     * @param expectedPath
     *            The path of the request.
     * @param queryStringExpectations
     *            The query parameters to expect. Missing or extra query string parameters are not allowed.
     * @param headerExpectations
     *            The headers to expect. Extra headers are allowed since they are part of the http spec.
     */
    public static Matcher<HttpServletRequest> putOf(
            final String expectedPath,
            final ParamsMatcher queryStringExpectations,
            final ParamsMatcher headerExpectations) {
        return allOf(isPut(),
                hasUrlPath(expectedPath),
                hasQueryString(queryStringExpectations),
                includesHeaders(headerExpectations));
    }

    /**
     * Returns a matcher for a POST request.
     *
     * @param expectedPath
     *            The path of the request.
     * @param queryStringExpectations
     *            The query parameters to expect. Missing or extra query string parameters are not allowed.
     * @param headerExpectations
     *            The headers to expect. Extra headers are allowed since they are part of the http spec.
     * @param postBodyExpectation
     *            The post body to expect.
     */
    public static Matcher<HttpServletRequest> postOf(
            final String expectedPath,
            final ParamsMatcher queryStringExpectations,
            final ParamsMatcher headerExpectations,
            final Matcher<? super InputStream> postBodyExpectation) {
        return allOf(isPost(),
                hasUrlPath(expectedPath),
                hasQueryString(queryStringExpectations),
                includesHeaders(headerExpectations),
                hasBody(postBodyExpectation));
    }

    /**
     * Returns a matcher for a GET request.
     */
    public static Matcher<HttpServletRequest> isGet() {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("is GET");
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                return request.getMethod().equals("GET");
            }
        };
    }

    /**
     * Returns a matcher for a POST request.
     */
    public static Matcher<HttpServletRequest> isPost() {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("is POST");
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                return request.getMethod().equals("POST");
            }
        };
    }

    /**
     * Returns a matcher for a PUT request.
     */
    public static Matcher<HttpServletRequest> isPut() {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("is PUT");
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                return request.getMethod().equals("PUT");
            }
        };
    }

    /**
     * Returns a matcher for a DELETE request.
     */
    public static Matcher<HttpServletRequest> isDelete() {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("is DELETE");
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                return request.getMethod().equals("DELETE");
            }
        };
    }

    /**
     * Returns a matcher for the url path (does <b>not</b> consider query string paramaters).
     */
    public static Matcher<HttpServletRequest> hasUrlPath(final String expected) {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("has url path ").appendValue(expected);
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                return Objects.equal(request.getPathInfo(), expected);
            }
        };
    }

    /**
     * Returns a matcher for query string parameters.
     */
    public static Matcher<HttpServletRequest> hasQueryString(final ParamsMatcher expected) {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("has query string ").appendValue(expected);
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest item) {
                List<JettyResourceStringUtils.NameValuePair> nameValuePairs = new ArrayList<JettyResourceStringUtils.NameValuePair>();

                String queryString = item.getQueryString();

                if (queryString == null) {
                    return expected.nameToExpectation.isEmpty();
                }

                Scanner scanner = new Scanner(queryString);

                JettyResourceStringUtils.parse(scanner, "utf-8", nameValuePairs);

                scanner.close();

                Map<String, List<String>> nameToArguments = new HashMap<String, List<String>>();

                for (JettyResourceStringUtils.NameValuePair nameValuePair : nameValuePairs) {
                    List<String> arguments = nameToArguments.get(nameValuePair.getName());

                    if (arguments == null) {
                        arguments = new ArrayList<String>();
                        nameToArguments.put(nameValuePair.getName(), arguments);
                    }

                    arguments.add(nameValuePair.getValue());
                }

                for (Map.Entry<String, ParamMatcher> queryStringExpectation : expected.nameToExpectation.entrySet()) {
                    List<String> actualArguments = nameToArguments.get(queryStringExpectation.getKey());

                    if (!queryStringExpectation.getValue().matches(actualArguments)) {
                        return false;
                    }
                }

                if (!expected.nameToExpectation.keySet().containsAll(nameToArguments.keySet())) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Returns a matcher for an empty query string.
     */
    public static Matcher<HttpServletRequest> hasNoQueryString() {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("has no query string ");
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest item) {
                return item.getQueryString() == null;
            }
        };
    }

    /**
     * Returns a matcher for request headers. The matcher does <b>not</b> return false if the request includes headers
     * not in the passed expectations since the http spec requires some headers to be present that have nothing at all
     * to do with the business logic being exercised.
     */
    public static Matcher<HttpServletRequest> includesHeaders(final ParamsMatcher expected) {
        return new TypeSafeMatcher<HttpServletRequest>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("includes headers ").appendValue(expected);
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest item) {
                for (Map.Entry<String, ParamMatcher> headerExpectation : expected.nameToExpectation.entrySet()) {
                    Enumeration<String> parameterValuesIter = item.getHeaders(headerExpectation.getKey());

                    List<String> parameterValues = new ArrayList<String>();

                    while (parameterValuesIter.hasMoreElements()) {
                        parameterValues.add(parameterValuesIter.nextElement());
                    }

                    if (!headerExpectation.getValue().matches(parameterValues)) {
                        return false;
                    }
                }

                /*
                 * Don't check for extra headers because the http spec calls for a bunch of them.
                 */
                return true;
            }
        };
    }

    /**
     * Returns a matcher for the request body.
     */
    public static Matcher<HttpServletRequest> hasBody(final Matcher<? super InputStream> expected) {
        return new TypeSafeMatcher<HttpServletRequest>() {
            /**
             * We can only consume an input stream once, but hamcrest can invoke the matcher multiple times. So we cache the
             * result.
             */
            private final IdentityHashMap<HttpServletRequest, Boolean> isMatch = new IdentityHashMap<>();

            @Override
            public void describeTo(Description desc) {
                desc.appendText("has body ").appendDescriptionOf(expected);
            }

            @Override
            protected boolean matchesSafely(HttpServletRequest request) {
                if (isMatch.get(request) == null) {
                    try {
                        isMatch.put(request, expected.matches(request.getInputStream()));
                    } catch (IOException e) {
                        Throwables.propagate(e);
                    }
                }

                return isMatch.get(request);
            }
        };
    }

    /**
     * Returns a matcher for parameters passed in the body of the http request.
     */
    public static Matcher<InputStream> bodyParamsOf(final ParamsMatcher expectedParameters) {
        return new TypeSafeMatcher<InputStream>() {
            @Override
            public void describeTo(Description desc) {
                desc.appendText("body parameters ").appendValue(expectedParameters.nameToExpectation);
            }

            @Override
            protected boolean matchesSafely(InputStream item) {
                List<JettyResourceStringUtils.NameValuePair> parametersList = new ArrayList<JettyResourceStringUtils.NameValuePair>();

                Scanner scanner = new Scanner(item);

                JettyResourceStringUtils.parse(scanner, "utf-8", parametersList);

                scanner.close();

                Map<String, List<String>> actualNameToValues = new HashMap<String, List<String>>();

                for (JettyResourceStringUtils.NameValuePair nameValuePair : parametersList) {
                    List<String> values = actualNameToValues.get(nameValuePair.getName());

                    if (values == null) {
                        values = new ArrayList<String>();
                        actualNameToValues.put(nameValuePair.getName(), values);
                    }

                    values.add(nameValuePair.getValue());
                }

                for (Map.Entry<String, ParamMatcher> entry : expectedParameters.nameToExpectation.entrySet()) {
                    String name = entry.getKey();
                    ParamMatcher expectation = entry.getValue();

                    List<String> actualValues = actualNameToValues.get(name);

                    if (!expectation.matches(actualValues)) {
                        return false;
                    }
                }

                if (actualNameToValues.size() != expectedParameters.nameToExpectation.size()) {
                    return false;
                }

                return true;
            }
        };
    }

    public static Matcher<InputStream> stringContent(final String expectedContent, final String charset) {
        return new TypeSafeMatcher<InputStream>() {
            @Override
            public void describeTo(Description d) {
                d.appendText("string content of ").appendValue(expectedContent);
            }

            @Override
            protected boolean matchesSafely(InputStream is) {
                StringBuilder stringContent = new StringBuilder();

                byte[] buffer = new byte[1024];

                int numBytes;

                try {
                    while ((numBytes = is.read(buffer)) != -1) {
                        stringContent.append(new String(buffer, 0, numBytes, charset));
                    }
                } catch (IOException e) {
                    Throwables.propagate(e);
                };

                return Objects.equal(stringContent.toString(), expectedContent);
            }
        };
    }

    private static final ParamsMatcher EMPTY_PARAMS = new ParamsMatcher() {
        @Override
        public ParamsMatcher and(String paramName, String arg0, String... moreArgs) {
            throw new UnsupportedOperationException();
        }
    };

    private static final int BAD_REQUEST_STATUS_CODE = 400;
}
