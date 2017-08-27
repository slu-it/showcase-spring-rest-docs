package de.sluit.examples.spring.restdocs;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import de.sluit.examples.spring.restdocs.Application.Movie;
import de.sluit.examples.spring.restdocs.Application.MovieRepository;
import de.sluit.examples.spring.restdocs.Application.MoviesController;


@RunWith(SpringRunner.class)
@WebMvcTest(MoviesController.class)
@AutoConfigureRestDocs("target/generated-snippets/movies")
public class MoviesControllerTest {

    @TestConfiguration
    public static class FixedClockConfiguration {

        @Bean
        public Clock clock() {
            return Clock.fixed(OffsetDateTime.parse("2017-08-27T12:34:56.789Z").toInstant(), ZoneId.of("UTC"));
        }

    }

    @MockBean
    MovieRepository repository;

    @Autowired
    MockMvc mvc;

    @Test
    public void gettingMovieById_found() throws Exception {
        Movie movie = new Movie();
        movie.title = "Iron Man";
        movie.imdbScore = 7.9f;

        given(repository.findById(42L)).willReturn(Optional.of(movie));

        String expectedResponse = new JSONObject()//
            .put("title", movie.title)//
            .put("imdbScore", movie.imdbScore)//
            .toString();

        mvc.perform(get("/api/movies/42"))//
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(expectedResponse, true))
            .andDo(document("getMovieById-found"));
    }

    @Test
    public void gettingMovieById_notFound() throws Exception {
        given(repository.findById(anyLong())).willReturn(Optional.empty());

        String expectedResponse = new JSONObject()//
            .put("timestamp", "2017-08-27T12:34:56.789Z")//
            .put("description", "Movie with ID '42' not found!")//
            .toString();

        mvc.perform(get("/api/movies/42"))//
            .andExpect(status().is(404))
            .andExpect(content().json(expectedResponse, true))
            .andDo(document("getMovieById-notFound"));
    }

    private RestDocumentationResultHandler document(String identifier, Snippet... snippets) {
        return MockMvcRestDocumentation.document(identifier, preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), snippets);
    }

}
