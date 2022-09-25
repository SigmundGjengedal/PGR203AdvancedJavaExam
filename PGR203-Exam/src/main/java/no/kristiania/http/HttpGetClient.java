package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpGetClient {
    private final int statusCode;
    private HttpMessage httpMessage;

    //************************************* constructor
    public HttpGetClient(String host , int port, String requestTarget) throws IOException {
        // request
        Socket socket1 = new Socket(host,port);
        // creating http-request. Requestline  and two Request Headers
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        // outputs
        socket1.getOutputStream().write(request.getBytes());
        // respons
        httpMessage = new HttpMessage(socket1);
        //parsing statusline
        String[] statusLineSplitted = httpMessage.startLine.split(" "); // [protocol, statuscode, statusmessage]
        this.statusCode = Integer.parseInt(statusLineSplitted[1]);    // Vi er bare interessert i statuscoden(f.eks 200).
    }

    // getters
    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return httpMessage.headerFields.get(headerName);// headerFields get of headerName
    }

    public String getMessageBody() {
        return httpMessage.messageBody;
    }
}
