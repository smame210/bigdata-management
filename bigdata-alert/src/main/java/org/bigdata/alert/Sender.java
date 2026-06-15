package org.bigdata.alert;

/**
 * alert message sender interface
 */
public interface Sender {
    /**
     * sender unique identifier
     */
    String getSenderName();

    /**
     * send alert message
     */
    void send(SenderRequest request);
}
