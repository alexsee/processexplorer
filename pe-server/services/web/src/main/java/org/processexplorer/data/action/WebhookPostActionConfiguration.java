package org.processexplorer.data.action;

/**
 * @author Alexander Seeliger on 09.12.2020.
 */
public class WebhookPostActionConfiguration {

    private String url;

    private String requestBody;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
