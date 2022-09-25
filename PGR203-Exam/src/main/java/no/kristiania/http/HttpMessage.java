package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    public String messageBody;
    public String startLine;
    public final Map<String, String> headerFields = new HashMap<>();

    public HttpMessage(Socket socket) throws IOException {

        // leser statusLine
        startLine = HttpMessage.readLine(socket);
        // leser headers
        readHeaders(socket);
        // Dersom request har body(altså en post request), så må vi parse den. Det har den visst vi har content-length i header. Da skal vi lese hele body som kommer etter headere. Bruker readBodyBytes()
        if(headerFields.containsKey("Content-Length")) {
            messageBody = HttpMessage.readBody(socket, getContentLength());
        }
    }

    public HttpMessage(String startLine, String messageBody){
        this.startLine = startLine;
        this.messageBody = messageBody;
    }


    //********************************* class methods ***********************************

    // reading requestline or responsline.
    // (eg "HTTP/1.1 200 OK").
    static String readLine(Socket socket) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r'){
            result.append((char)c);
        }
        int expectedNewLine = socket.getInputStream().read();
        assert expectedNewLine == '\n';
        return result.toString();
    }

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0,equalsPos);
            String parameterValue = queryParameter.substring(equalsPos +1);
            queryMap.put(parameterName,parameterValue);
        }
        return queryMap;
    }

    // parsing headers
    private void readHeaders(Socket socket) throws IOException {
        // skal parse Headerlines fra server. altså det før body. Lagrer Field og value i hashmap
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket) ).isBlank() ){ // ved blank linje er headers ferdig, da kommer body.
            int colonPos = headerLine.indexOf(":");
            String headerField = headerLine.substring(0,colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim(); // trim fjerner WS fra begge sider.
            headerFields.put(headerField,headerValue);  // lagres i hashmap

        }
    }

    // reading body as bytes. Return as String
    static String readBody(Socket socket, int contentLength) throws IOException {
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; i++) {
            buffer[i] = (byte) socket.getInputStream().read();
        }
        return new String(buffer , StandardCharsets.UTF_8);
    }

    // getters:
    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }
    public String getHeader(String headerName) {
        return headerFields.get(headerName);// headerFields sin get av headerName
    }
    public void setHeader(String headerName, String headerValue){
        headerFields.put(headerName,headerValue);
    }

    // writing httpmessage to socket

    //***** OBS!!! Nå ligger det fast content-type her, det burde det kanskje ikke gjøre, men trengte det for å liste
    //ting i en drop-down menu i en Select
    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n" +
                "Content-Length: " + messageBody.getBytes().length + "\r\n" +
                "Connection: close\r\n" +
                "Content-type: text/html\r\n" +
                "Location: " + headerFields.get("Location") +"\r\n" +
                "\r\n" +
                messageBody;
        // sending
        socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    }
}
