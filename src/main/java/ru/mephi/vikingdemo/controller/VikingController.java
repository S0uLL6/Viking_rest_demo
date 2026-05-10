package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }

    @GetMapping
    @Operation(summary = "Получить список созданных викингов",
            operationId = "getAllVikings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<Viking> getAllVikings() {
        System.out.println("GET /api/vikings called");
        return vikingService.findAll();
    }

    @GetMapping("/test")
    @Operation(summary = "Получить список тестовых викингов",
            operationId = "getTest")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<String> test() {
        System.out.println("GET /api/vikings/test called");
        return List.of("Ragnar", "Bjorn");
    }

    @PostMapping("/post")
    @Operation(summary = "Создать викинга со случайными параметрами",
            operationId = "post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно создан")
    })
    public void addViking() {
        System.out.println("POST api/vikings/post called");
        vikingListener.testAdd();
    }

    // ==================== ПРАКТИКУМ Spring Web ====================

    @PostMapping
    @Operation(summary = "Добавить конкретного викинга",
            operationId = "createViking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Викинг успешно создан")
    })
    public ResponseEntity<Viking> createViking(@RequestBody Viking viking) {
        System.out.println("POST /api/vikings called with body: " + viking);
        Viking created = vikingService.create(viking);
        URI location = URI.create("/api/vikings/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить викинга по id",
            operationId = "deleteViking")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Викинг удалён (или его и не было)")
    })
    public ResponseEntity<Void> deleteViking(@PathVariable int id) {
        System.out.println("DELETE /api/vikings/" + id + " called");
        vikingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полностью перезаписать параметры викинга",
            operationId = "updateViking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг обновлён"),
            @ApiResponse(responseCode = "404", description = "Викинг не найден")
    })
    public Viking updateViking(@PathVariable int id, @RequestBody Viking viking) {
        System.out.println("PUT /api/vikings/" + id + " called with body: " + viking);
        try {
            return vikingService.update(id, viking);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
