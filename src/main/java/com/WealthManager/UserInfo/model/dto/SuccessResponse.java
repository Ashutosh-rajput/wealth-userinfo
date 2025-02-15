package com.WealthManager.UserInfo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private int statusCode;
    private String message;
    private String path;
    private String timestamp;
    private ResponseData<?> responseData;

    // Custom constructor for some fields, with timestamp handled internally
    public SuccessResponse(int statusCode, String message, String path,ResponseData<?> responseData) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = new Date().toInstant().toString(); // Current timestamp in ISO format
        this.responseData = responseData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData<T> {
        private List<T> data;           // List of items from the database
        private PaginationInfo pagination; // Pagination details
    }

    @Data
    @NoArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalItems;

        public PaginationInfo(int currentPage, int totalPages, long totalItems) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }
}

