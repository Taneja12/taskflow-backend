package com.deepanshu.backend.board.controller;

import com.deepanshu.backend.board.dto.request.AddBoardRequest;
import com.deepanshu.backend.board.dto.response.BoardResponse;
import com.deepanshu.backend.board.dto.response.BoardStatisticsResponse;
import com.deepanshu.backend.board.service.BoardService;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.project.dto.response.ProjectStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Boards")
@SecurityRequirement(name = "Bearer Authentication")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/{projectId}/boards")
    @Operation(summary = "Create new board")
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody AddBoardRequest request, @PathVariable UUID projectId)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addBoard(request, projectId));
    }

    @GetMapping("/{projectId}/boards")
    @Operation(summary = "Get all boards")
    public ResponseEntity<PageResponse<BoardResponse>> getBoardsByProjectId(
            @PathVariable UUID projectId, @ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(service.getBoardsByProjectId(projectId, pageable));
    }

    @GetMapping("/boards/{boardId}")
    @Operation(summary = "Get board by id")
    public ResponseEntity<BoardResponse> getBoardById(
            @PathVariable UUID boardId)
    {
        return ResponseEntity.ok(service.getBoardById(boardId));
    }

    @PutMapping("/boards/{boardId}")
    @Operation(summary = "Update board")
    public ResponseEntity<BoardResponse> updateBoard(@Valid @RequestBody AddBoardRequest request, @PathVariable UUID boardId)
    {
        return ResponseEntity.ok(service.updateBoard(request, boardId));
    }

    @DeleteMapping("/boards/{boardId}")
    @Operation(summary = "Delete board")
    public ResponseEntity<?> deleteBoard(@PathVariable UUID boardId)
    {
        service.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/boards/{boardId}/dashboard")
    @Tag(name = "Dashboard")
    @Operation(summary = "Board Statistics")
    public ResponseEntity<BoardStatisticsResponse> boardStatistics(@PathVariable UUID boardId)
    {
        return ResponseEntity.ok(service.getBoardStatistics(boardId));
    }

}
