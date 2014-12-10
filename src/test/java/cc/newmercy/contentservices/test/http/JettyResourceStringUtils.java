/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package cc.newmercy.contentservices.test.http;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

/**
 * Copied from httpclient URLEncodedUtils, which we do not want to bring into this project.
 */
final class JettyResourceStringUtils {

    private JettyResourceStringUtils() { }


    /**
     * Adds all parameters within the Scanner to the list of <code>parameters</code>, as encoded by
     * <code>encoding</code>. For example, a scanner containing the string <code>a=1&b=2&c=3</code> would add the
     * {@link NameValuePair NameValuePairs} a=1, b=2, and c=3 to the list of parameters. By convention, {@code '&'} and
     * {@code ';'} are accepted as parameter separators.
     * @param scanner
     *            Input that contains the parameters to parse.
     * @param charset
     *            Encoding to use when decoding the parameters.
     * @param parametersOut
     *            List to add parameters to.
     */
    public static void parse(
            final Scanner scanner,
            final String charset,
            final List <NameValuePair> parametersOut) {
        parse(scanner, QP_SEP_PATTERN, charset, parametersOut);
    }
    /**
     * Adds all parameters within the Scanner to the list of
     * <code>parameters</code>, as encoded by <code>encoding</code>. For
     * example, a scanner containing the string <code>a=1&b=2&c=3</code> would
     * add the {@link NameValuePair NameValuePairs} a=1, b=2, and c=3 to the
     * list of parameters.
     * @param scanner
     *            Input that contains the parameters to parse.
     * @param parameterSepartorPattern
     *            The Pattern string for parameter separators, by convention {@code "[&;]"}
     * @param charset
     *            Encoding to use when decoding the parameters.
     * @param parametersOut
     *            List to add parameters to.
     */
    public static void parse(
            final Scanner scanner,
            final String parameterSepartorPattern,
            final String charset,
            final List <NameValuePair> parametersOut) {
        scanner.useDelimiter(parameterSepartorPattern);
        while (scanner.hasNext()) {
            String name = null;
            String value = null;
            final String token = scanner.next();
            final int i = token.indexOf(NAME_VALUE_SEPARATOR);
            if (i != -1) {
                name = decodeFormFields(token.substring(0, i).trim(), charset);
                value = decodeFormFields(token.substring(i + 1).trim(), charset);
            } else {
                name = decodeFormFields(token.trim(), charset);
            }
            parametersOut.add(new BasicNameValuePair(name, value));
        }
    }

    /**
     * Decode/unescape www-url-form-encoded content.
     *
     * @param content the content to decode, will decode '+' as space
     * @param charset the charset to use
     * @return encoded string
     */
    private static String decodeFormFields (final String content, final String charset) {
        if (content == null) {
            return null;
        }
        return urlDecode(content, charset != null ? Charset.forName(charset) : UTF_8, true);
    }
    /**
     * Decode/unescape a portion of a URL, to use with the query part ensure {@code plusAsBlank} is true.
     *
     * @param content the portion to decode
     * @param charset the charset to use
     * @param plusAsBlank if {@code true}, then convert '+' to space (e.g. for www-url-form-encoded content), otherwise leave as is.
     * @return encoded string
     */
    private static String urlDecode(
            final String content,
            final Charset charset,
            final boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        final ByteBuffer bb = ByteBuffer.allocate(content.length());
        final CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            final char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                final char uc = cb.get();
                final char lc = cb.get();
                final int u = Character.digit(uc, 16);
                final int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte) ((u << 4) + l));
                } else {
                    bb.put((byte) '%');
                    bb.put((byte) uc);
                    bb.put((byte) lc);
                }
            } else if (plusAsBlank && c == '+') {
                bb.put((byte) ' ');
            } else {
                bb.put((byte) c);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }

    private static final char QP_SEP_A = '&';

    private static final char QP_SEP_S = ';';

    /**
     * Query parameter separators.
     */
    private static final char[] QP_SEPS = new char[] { QP_SEP_A, QP_SEP_S };

    /**
     * Query parameter separator pattern.
     */
    private static final String QP_SEP_PATTERN = "[" + new String(QP_SEPS) + "]";

    private static final String NAME_VALUE_SEPARATOR = "=";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public interface NameValuePair {

        String getName();

        String getValue();

    }

    private static class BasicNameValuePair implements NameValuePair {

        private final String name;

        private final String value;

        /**
         * Default Constructor taking a name and a value. The value may be null.
         *
         * @param name The name.
         * @param value The value.
         */
        public BasicNameValuePair(final String name, final String value) {
            super();
            if (name == null) {
                throw new NullPointerException("Name");
            }
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            // don't call complex default formatting for a simple toString

            if (this.value == null) {
                return name;
            }
            final int len = this.name.length() + 1 + this.value.length();
            final StringBuilder buffer = new StringBuilder(len);
            buffer.append(this.name);
            buffer.append("=");
            buffer.append(this.value);
            return buffer.toString();
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof NameValuePair) {
                final BasicNameValuePair that = (BasicNameValuePair) object;

                if (!this.name.equals(that.name)) {
                    return false;
                }

                if (this.value == null) {
                    return that.value == null;
                }

                if (!this.value.equals(that.value)) {
                    return false;
                }

                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            final int mult = 37;
            int hash = 19;

            hash = hash * mult + this.name.hashCode();
            hash = hash * mult + (this.value == null ? 0 : this.value.hashCode());

            return hash;
        }
    }
}
