package cc.newmercy.contentservices.web.api.v1.sermon;

public class Mp3 extends Asset {

    private int seconds;

    /**
     * @return Length in seconds.
     */
    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
