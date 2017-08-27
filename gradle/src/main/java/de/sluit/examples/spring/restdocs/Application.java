package de.sluit.examples.spring.restdocs;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @RestController
    @RequestMapping("/api/movies")
    public static class MoviesController {

        private final Clock clock;
        private final MovieRepository repository;

        public MoviesController(Clock clock, MovieRepository repository) {
            this.clock = clock;
            this.repository = repository;
        }

        @GetMapping("/{id}")
        public Movie get(@PathVariable Long id) {
            return repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NotFoundException.class)
        public Error handle(NotFoundException e) {
            Error error = new Error();
            error.description = e.getMessage();
            error.timestamp = OffsetDateTime.now(clock).toString();
            return error;
        }

    }

    public interface MovieRepository {

        Optional<Movie> findById(Long id);

    }

    @Service
    public static class EmptyMovieRepository implements MovieRepository {

        @Override
        public Optional<Movie> findById(Long id) {
            return Optional.empty();
        }

    }

    public static class Movie {
        public String title;
        public Float imdbScore;
    }

    public static class Error {
        public String timestamp;
        public String description;
    }

    public static class NotFoundException extends RuntimeException {

        public Long id;

        public NotFoundException(Long id) {
            super("Movie with ID '" + id + "' not found!");
        }

    }

}
