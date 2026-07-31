package com.deepanshu.backend.board.service;


import com.deepanshu.backend.board.dto.request.AddBoardRequest;
import com.deepanshu.backend.board.dto.response.BoardResponse;
import com.deepanshu.backend.board.dto.response.BoardStatisticsResponse;
import com.deepanshu.backend.common.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BoardService {
    PageResponse<BoardResponse> getBoardsByProjectId(UUID projectId, Pageable pageable);

    BoardResponse addBoard(AddBoardRequest request, UUID projectId);

    BoardResponse getBoardById(UUID boardId);

    BoardResponse updateBoard(@Valid AddBoardRequest request, UUID boardId);

    void deleteBoard( UUID boardId) ;

    BoardStatisticsResponse getBoardStatistics(UUID boardId);
}
