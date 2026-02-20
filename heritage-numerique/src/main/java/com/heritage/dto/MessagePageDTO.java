package com.heritage.dto;

import java.util.List;

public class MessagePageDTO {

    private List<MessageResponseDTO> messages;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}