package no.kristiania.http;

import no.kristiania.questions.*;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
    //starting server
    private final HttpServer server = new HttpServer(0);

    // throws exception.
    HttpServerTest() throws IOException {
    }

    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpGetClient client = new HttpGetClient("localhost",server.getPort(),"/non-existing");
        assertEquals(404,client.getStatusCode());
    }

    @Test
    void ShouldRespondWithRequestTargetIn404() throws IOException {
        HttpGetClient client = new HttpGetClient("localhost",server.getPort(),"/non-existing");
        assertEquals("File not found: /non-existing",client.getMessageBody());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        HttpGetClient client = new HttpGetClient("localhost",server.getPort(),"/index.html");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html", client.getHeader("Content-Type")),
                () -> assertEquals(true, client.getMessageBody().startsWith("<!DOCTYPE html>"))
        );
    }


    @Test
    void shouldHandleMoreThanOneRequests() throws IOException {
        assertEquals(200, new HttpGetClient("localhost", server.getPort(), "/index.html").getStatusCode());
        assertEquals(200, new HttpGetClient("localhost", server.getPort(), "/index.html").getStatusCode());
    }

    @Test
    void shouldServeFiles() throws IOException {
        // look for files here
        Paths.get("target/test-classes");
        //  writing content to a file in the directory
        String fileContent = "A file created at " + LocalTime.now();
        Files.write(Paths.get("target/test-classes/example-file.txt"),fileContent.getBytes());
        //getting the file, and asserting
        HttpGetClient client = new HttpGetClient("localhost",server.getPort(),"/example-file.txt");
        assertEquals(fileContent,client.getMessageBody());
        assertEquals("text/plain",client.getHeader("Content-Type"));
    }

    @Test
    void shouldUseFileExtensionForContentType() throws IOException {
        Paths.get("target/test-classes");
        String fileContent = "<p> Hello</p>";
        Files.write(Paths.get("target/test-classes/example-file.html"),fileContent.getBytes());
        HttpGetClient client = new HttpGetClient("localhost", server.getPort(),"/example-file.html");
        assertEquals("text/html",client.getHeader("Content-Type"));
    }

    //  does not pass with nordic characters, due to readBody in httpMessage
    @Test
    void shouldCreateNewQuestion() throws IOException, SQLException {
        // Adding survey in order to add question
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        Survey survey = TestData.pkSurvey();
        surveyDao.save(survey);

        QuestionDao qDao = new QuestionDao(TestData.testDataSource());
        server.addController("/api/newQuestion",new AddQuestionController(qDao,surveyDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "title=Spm1&text=tekst&id=1"
        );
        assertEquals(303,postClient.getStatusCode());
        assertThat(qDao.listAll())
                .anySatisfy(p -> {
                    assertThat(p.getTitle()).isEqualTo("Spm1");
                    assertThat(p.getText()).isEqualTo("tekst");
                });
    }

}
