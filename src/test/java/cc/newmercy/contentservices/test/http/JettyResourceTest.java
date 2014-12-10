package cc.newmercy.contentservices.test.http;

import static cc.newmercy.contentservices.test.http.JettyResource.*;
import static cc.newmercy.contentservices.test.util.Exceptions.hasMsg;
import static cc.newmercy.contentservices.test.util.Exceptions.rethrowUnless;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cc.newmercy.contentservices.test.http.JettyResource.*;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyResourceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public final JettyResource jetty = new JettyResource();

    private final CloseableHttpClient httpClient = HttpClients.createMinimal();

    private String baseUrl;

    @Before
    public void before() {
        baseUrl = "http://127.0.0.1:" + jetty.getPort();
    }

    @Test
    public void getPathDoesNotMatch() throws IOException {
        jetty.expect(getOf("/expected/path",
                noParams(),
                noParams()))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpGet httpGet = new HttpGet(baseUrl + "/actual/path");

        execute(httpGet, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void getHeadersDoNotMatch() throws IOException {
        jetty.expect(getOf("/expected/path",
                noParams(),
                params("foo", "bar")))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpGet httpGet = new HttpGet(baseUrl + "/expected/path");

        execute(httpGet, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void getQueryStringParametersDoNotMatch() throws IOException {
        jetty.expect(getOf("/expected/path",
                params("foo", "bar"),
                noParams()))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpGet httpGet = new HttpGet(baseUrl + "/expected/path?foo=bar&hello=world");

        execute(httpGet, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void get() throws IOException {
        jetty.expect(getOf("/expected/path",
                params("single-arg-query-string-param-1", "single-arg-query-string-param-1-value")
                        .and("multi-arg-query-string-param-1", "multi-arg-query-string-param-1-value-1", "multi-arg-query-string-param-1-value-2", "multi-arg-query-string-param-1-value-3")
                        .and("single-arg-query-string-param-2", "single-arg-query-string-param-2-value")
                        .and("multi-arg-query-string-param-2", "multi-arg-query-string-param-2-value-1", "multi-arg-query-string-param-2-value-2", "multi-arg-query-string-param-2-value-3"),
                params("single-arg-header-1", "single-arg-header-1-value")
                        .and("multi-arg-header-1", "multi-arg-header-1-value-1", "multi-arg-header-1-value-2", "multi-arg-header-1-value-3")
                        .and("single-arg-header-2", "single-arg-header-2-value")
                        .and("multi-arg-header-2", "multi-arg-header-2-value-1", "multi-arg-header-2-value-2", "multi-arg-header-2-value-3")))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        response.setStatus(HttpStatus.SC_OK);
                        response.getWriter().print("It works!");
                    }
                });

        StringBuilder url = new StringBuilder(baseUrl);

        url.append("/expected/path")
                .append("?").append("single-arg-query-string-param-1=single-arg-query-string-param-1-value")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-1")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-1")
                .append("&").append("single-arg-query-string-param-2=single-arg-query-string-param-2-value")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-2")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-2")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-3")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-3");

        HttpGet httpGet = new HttpGet(url.toString());

        httpGet.setHeaders(new Header[] {
                new BasicHeader("single-arg-header-1", "single-arg-header-1-value"),
                new BasicHeader("single-arg-header-2", "single-arg-header-2-value"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-1"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-1"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-2"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-2"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-3"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-3"),
        });

        HttpResponse response = execute(httpGet, HttpStatus.SC_OK);

        try {
            StringWriter stringWriter = new StringWriter();

            copy(new InputStreamReader(response.getEntity().getContent(), UTF_8), stringWriter);

            String body = stringWriter.toString();

            assertThat(body, equalTo("It works!"));
        } finally {
          EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void postPathDoesNotMatch() throws IOException {
        jetty.expect(postOf("/expected/path",
                noParams(),
                noParams(),
                nullValue()))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpPost httpPost = new HttpPost(baseUrl + "/actual/path");

        execute(httpPost, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void postHeadersDoNotMatch() throws IOException {
        jetty.expect(postOf("/expected/path",
                noParams(),
                params("foo", "bar"),
                nullValue()))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpPost httpPost = new HttpPost(baseUrl + "/expected/path");

        execute(httpPost, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void postQueryStringParametersDoNotMatch() throws IOException {
        jetty.expect(postOf("/expected/path",
                params("foo", "bar"),
                noParams(),
                nullValue()))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpPost httpPost = new HttpPost(baseUrl + "/expected/path");

        execute(httpPost, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void postBodyParametersDoNotMatch() throws IOException {
        jetty.expect(postOf("/expected/path",
                noParams(),
                noParams(),
                bodyParamsOf(noParams())))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        fail("request should not match so we should not even be here");
                    }
                });

        HttpPost httpPost = new HttpPost(baseUrl + "/expected/path");

        List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
        bodyParams.add(new BasicNameValuePair("foo", "bar"));

        httpPost.setEntity(new UrlEncodedFormEntity(bodyParams));

        execute(httpPost, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void postWithBodyParameters() throws IOException {
        jetty.expect(postOf("/expected/path",
                params("single-arg-query-string-param-1", "single-arg-query-string-param-1-value")
                        .and("multi-arg-query-string-param-1", "multi-arg-query-string-param-1-value-1", "multi-arg-query-string-param-1-value-2", "multi-arg-query-string-param-1-value-3")
                        .and("single-arg-query-string-param-2", "single-arg-query-string-param-2-value")
                        .and("multi-arg-query-string-param-2", "multi-arg-query-string-param-2-value-1", "multi-arg-query-string-param-2-value-2", "multi-arg-query-string-param-2-value-3"),
                params("single-arg-header-1", "single-arg-header-1-value")
                        .and("multi-arg-header-1", "multi-arg-header-1-value-1", "multi-arg-header-1-value-2", "multi-arg-header-1-value-3")
                        .and("single-arg-header-2", "single-arg-header-2-value")
                        .and("multi-arg-header-2", "multi-arg-header-2-value-1", "multi-arg-header-2-value-2", "multi-arg-header-2-value-3"),
                bodyParamsOf(params("single-arg-body-parameter-1", "single-arg-body-parameter-1-value")
                        .and("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-1", "multi-arg-body-parameter-1-value-2", "multi-arg-body-parameter-1-value-3")
                        .and("single-arg-body-parameter-2", "single-arg-body-parameter-2-value")
                        .and("multi-arg-body-parameter-2", "multi-arg-body-parameter-2-value-1", "multi-arg-body-parameter-2-value-2", "multi-arg-body-parameter-2-value-3"))))
                .then(new JettyResource.TestResponse() {
                    @Override
                    public void handle(HttpServletResponse response) throws Exception {
                        response.getWriter().print("It worked!");
                    }
                });

        StringBuilder url = new StringBuilder(baseUrl);

        url.append("/expected/path")
                .append("?").append("single-arg-query-string-param-1=single-arg-query-string-param-1-value")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-1")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-1")
                .append("&").append("single-arg-query-string-param-2=single-arg-query-string-param-2-value")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-2")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-2")
                .append("&").append("multi-arg-query-string-param-1=multi-arg-query-string-param-1-value-3")
                .append("&").append("multi-arg-query-string-param-2=multi-arg-query-string-param-2-value-3");

        HttpPost httpPost = new HttpPost(url.toString());

        httpPost.setHeaders(new Header[] {
                new BasicHeader("single-arg-header-1", "single-arg-header-1-value"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-1"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-1"),
                new BasicHeader("single-arg-header-2", "single-arg-header-2-value"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-2"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-2"),
                new BasicHeader("multi-arg-header-1", "multi-arg-header-1-value-3"),
                new BasicHeader("multi-arg-header-2", "multi-arg-header-2-value-3"),
        });

        List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();

        bodyParams.add(new BasicNameValuePair("single-arg-body-parameter-1", "single-arg-body-parameter-1-value"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-1"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-2", "multi-arg-body-parameter-2-value-1"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-2", "multi-arg-body-parameter-2-value-2"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-2"));
        bodyParams.add(new BasicNameValuePair("single-arg-body-parameter-2", "single-arg-body-parameter-2-value"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-2", "multi-arg-body-parameter-2-value-3"));
        bodyParams.add(new BasicNameValuePair("multi-arg-body-parameter-1", "multi-arg-body-parameter-1-value-3"));

        httpPost.setEntity(new UrlEncodedFormEntity(bodyParams, "utf-8"));

        HttpResponse response = execute(httpPost, HttpStatus.SC_OK);

        try {
            StringWriter stringWriter = new StringWriter();

            copy(new InputStreamReader(response.getEntity().getContent(), UTF_8), stringWriter);

            String body = stringWriter.toString();

            assertThat(body, equalTo("It worked!"));
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void expectationAndResponseCannotBeReused() throws IOException {
        ExpectationAndResponse expectationAndResponse = jetty.expect(hasUrlPath("/expected/path"))
                .and(hasQueryString(params("foo", "bar")));

        expectationAndResponse.then(new JettyResource.TestResponse() {
            @Override
            public void handle(HttpServletResponse response) throws Exception {
                response.getWriter().print("first");
            }
        });

        try {
            expectationAndResponse.then(new JettyResource.TestResponse() {
                @Override
                public void handle(HttpServletResponse response) throws Exception {
                    response.getWriter().print("second");
                }
            });
            fail();
        } catch (IllegalStateException e) {
            rethrowUnless(e, hasMsg("instances cannot be reused"));
        }

        /*
         * Now actually hit the url so that the test will not fail due to uninvoked expectations.
         */
        httpClient.execute(new HttpGet("http://127.0.0.1:" + jetty.getPort() + "/expected/path?foo=ba"));
    }

    @Test
    public void matcherRequired() {
        try {
            jetty.expect(null);
            fail();
        } catch (NullPointerException e) {
            rethrowUnless(e, hasMsg("request expectation"));
        }
    }

    @Test
    public void responseRequired() {
        ExpectationAndResponse expectationAndResponse = jetty.expect(hasUrlPath("/expected/path"))
                .and(hasQueryString(params("foo", "bar")));

        try {
            expectationAndResponse.then(null);
            fail();
        } catch (NullPointerException e) {
            rethrowUnless(e, hasMsg("response"));
        }
    }

    private HttpResponse execute(HttpUriRequest httpGet, int expectedStatusCode) throws IOException {
        HttpResponse response = httpClient.execute(httpGet);

        StatusLine statusLine = response.getStatusLine();

        if (statusLine.getStatusCode() != expectedStatusCode) {
            try {
                HttpEntity entity = response.getEntity();

                InputStreamReader in = new InputStreamReader(entity.getContent(), UTF_8);

                StringWriter out = new StringWriter();

                copy(in, out);

                String message = out.toString();

                logger.error("about to fail with body content:\n{}", message);

                fail(String.format("server returned status code %s and status phrase '%s'",
                        statusLine.getStatusCode(), statusLine.getReasonPhrase()));
            } finally {
                  EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        return response;
    }

    private static void copy(Reader in, Writer out) throws IOException {
        try {
            char[] buffer = new char[4096];
            int bytesRead = -1;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
        } finally {
            try {
                in.close();
            }
            catch (IOException ex) {
            }
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
    }

    private static final Charset UTF_8 = Charset.forName("utf-8");
}
