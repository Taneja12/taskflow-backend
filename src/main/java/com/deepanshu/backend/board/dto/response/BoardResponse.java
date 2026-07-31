package com.deepanshu.backend.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private UUID id;
    private String name;
    private String projectName;
}
