package restful.bean;

/**
 * @author lwz
 * @create 2021-11-28 18:30
 * @description:
 */
public class ChatData {
    private boolean isBroadcast;
    private String fromWho;
    private String toWho;
    private String text;

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    public String getFromWho() {
        return fromWho;
    }

    public void setFromWho(String fromWho) {
        this.fromWho = fromWho;
    }

    public String getToWho() {
        return toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ChatData{" +
                "isBroadcast=" + isBroadcast +
                ", fromWho='" + fromWho + '\'' +
                ", toWho='" + toWho + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
