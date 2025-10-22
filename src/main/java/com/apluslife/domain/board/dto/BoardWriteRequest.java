package com.apluslife.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardWriteRequest {
    private String boardType;
    private String title;
    private String content;
    private Boolean isNotice = false;
}
