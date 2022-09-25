package no.kristiania.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private HashMap<String, String> queryMap = new HashMap<>();

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
    }

    // reading from client connection(requestline)- Responding after parsing. Uses Help method handleClient()
    private void handleClients(){

        try {
            while(true){ // Keeps the server running
                handleClient();
            }
        }catch(IOException | SQLException e){ // network issues, invalid port etc
            e.printStackTrace();
        }
    }

    // Parsing request from client:
    private void handleClient() throws IOException, SQLException {
        // accepting request from client
        Socket clientSocket = serverSocket.accept();
        // instance of httpMessage to access method
        HttpMessage httpMessage = new HttpMessage(clientSocket);
        //parsing requestline. Using readline without while. Splitting on space og adding to array.
        String[] requestLine = httpMessage.startLine.split(" ");
        // parsing requestTarget from requestLine.
        String requestTarget = requestLine[1];// from requestline =  [HTTP-METHOD, requestTarget, HTTP-PROTOCOL];
        // parsing requestTarget in fileTarget and query ( eg: /hello?firstName=geir&lastName=hansen)
        int questionPos = requestTarget.indexOf("?");
        String fileTarget;
        String query = null;
        if(requestTarget.equals("/")){
            requestTarget = "/index.html";
        }
        if (questionPos != -1){
            fileTarget = requestTarget.substring(0,questionPos); // splitting in query and filetarget
            query = requestTarget.substring(questionPos+1);

        }else{
            fileTarget = requestTarget; // no query
        }
        if(query!=null){
            if(fileTarget.equals("/api/singleQuestionAnswer")){
                // parsing queryparameters
                parseQuery(query,queryMap);
                String questionId = queryMap.get("questionId");
                HttpMessage questionIDBody = new HttpMessage("HTTP/1.1 200 ok",questionId);
                HttpMessage response = controllers.get(fileTarget).handle(questionIDBody);
                response.write(clientSocket);
                return;
            }
            if(fileTarget.equals("/api/surveyReport")){
                parseQuery(query,queryMap);
                String surveyIdId = queryMap.get("surveyId");
                HttpMessage surveyIDBody = new HttpMessage("HTTP/1.1 200 ok",surveyIdId);
                HttpMessage response = controllers.get(fileTarget).handle(surveyIDBody);
                response.write(clientSocket);
                return;
            }
            else if(fileTarget.equals("/api/listAnswerForm")) {
                parseQuery(query,queryMap);
                String surveyId = queryMap.get("surveyId");
                HttpMessage surveyIDBody = new HttpMessage("HTTP/1.1 200 ok",surveyId);
                HttpMessage response = controllers.get(fileTarget).handle(surveyIDBody);
                response.write(clientSocket);
                return;
            }

        }

        // determining response and controllers:
        if (controllers.containsKey(fileTarget)) {
            HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
            response.write(clientSocket);
            return;
        }

          else{
            //handling file structure with jar file. Where the code is located, and parsing bytes
            InputStream fileResource = getClass().getResourceAsStream(fileTarget);
            if (fileResource != null){
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                fileResource.transferTo(buffer);
                String responseText = buffer.toString();

                // default verdi
                String contentType = "text/plain";
                // men endres om...
                if (requestTarget.endsWith(".html")){
                    contentType = "text/html";
                }else if (requestTarget.endsWith(".css")){
                    contentType ="text/css"; // nå kan vi ha <!DOCTYPE> på toppen av html-sidene.
                }
                writeOkResponse(clientSocket, responseText, contentType);
                return;
            }
            String responseText = "File not found: " + requestTarget;

            String response = "HTTP/1.1 404 Not found\r\n" +
                    "Content-Length: " + responseText.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseText;
            clientSocket.getOutputStream().write(response.getBytes());
        }
    } // end of handleClient

    //HelpMethod
    private void parseQuery(String query, Map<String,String> map) {
        for( String queryParameter : query.split("&") ){
            int equalsPos = queryParameter.indexOf("=");
            String parameterName = queryParameter.substring(0,equalsPos);
            String parameterValue = queryParameter.substring(equalsPos+1);
            map.put(parameterName,parameterValue);
        }
    }

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 ok\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        //  sending response
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static HttpMessage responseWithLocation(String locationValue) {
        HttpMessage response = new HttpMessage("HTTP/1.1 303 SEE OTHER", "");
        response.setHeader("Location", locationValue);
        return response;
    }
    public static HttpMessage responseWith500(String errormessage) {
        String rBody = "<h1 ><strong>500 Internal Server Error</strong> </h1>";
        HttpMessage response = new HttpMessage("HTTP/1.1 500 INTERNAL SERVER ERROR", rBody + errormessage);
        return response;
    }
    // getter og setters
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);
    }


}
