package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpPostClient {
    private final HttpMessage httpMessage;
    private final int statusCode;

    public HttpPostClient(String host, int port, String requestTarget, String contentBody) throws IOException {
        //  connecting to server and creating request
        Socket socket1 = new Socket(host,port);
        String request = "POST " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + contentBody.length() + "\r\n" +
                "\r\n"+
                contentBody;
        // sending to server as bytes
        socket1.getOutputStream().write(request.getBytes());

        // reading response
        httpMessage = new HttpMessage(socket1);
        String[] statusLineSplitted = httpMessage.startLine.split(" "); // [protocol, status code, status message]
        this.statusCode = Integer.parseInt(statusLineSplitted[1]);
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getHeader(String headerName) {
        return httpMessage.headerFields.get(headerName);
    }

}
