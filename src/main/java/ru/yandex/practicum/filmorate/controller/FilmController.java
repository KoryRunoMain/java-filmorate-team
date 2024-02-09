package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 11, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к /films. Тело запроса: {}", film);
        if (isNotCorrectDate(film.getReleaseDate())) {
            log.warn("The release date of the film cannot be earlier than 28.12.1895.");
            // Не совсем понял, почему нужно возвращать тело в запросах с ошибкой. Этого требуют тесты в Postman...
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(film);
        }
        film.setId(currentId++);
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к /films. Тело запроса: {}", film);
        if (films.containsKey(film.getId())) {
            if (isNotCorrectDate(film.getReleaseDate())) {
                log.warn("The release date of the film cannot be earlier than 28.12.1895.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(film);
            }
            films.put(film.getId(), film);
            return ResponseEntity.ok(film);
        }
        log.warn("Пользователь с id {} не найден.", film.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        log.info("Получен GET-запрос к /films.");
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    private boolean isNotCorrectDate(LocalDate releaseDate) {
        return releaseDate.isBefore(MIN_FILM_DATE);
    }
}