package cc.newmercy.contentservices.web.id;

class Base62 {

    private static final char[] DIGITS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    static final Base62 INSTANCE = new Base62();

    private Base62() { }

    String encode(long l) {
        StringBuilder sb = new StringBuilder();

        long q = l;

        while (q != 0) {
            int r = (int) (q % DIGITS.length);

            sb.append(DIGITS[r]);

            q = q / DIGITS.length;
        }

        return sb.reverse().toString();
    }
}
