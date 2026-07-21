package com.cloverinfotech.emd_dept.apiDto;

public class ApiResponse<DataType> {

    private int statusCode;      
    private String status;      
    private String message;      
    private DataType data;              // actual response data (optional)

    
    public ApiResponse(int statusCode, String status, String message, DataType data) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, "SUCCESS", message, data);
    }


    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, "SUCCESS", message, null);
    }


    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, "FAILED", message, null);
    }

    // Getters
    public int getStatusCode() 
    { 
    	return statusCode; 
    }
    
    public String getStatus() 
    { 
    	return status; 
    }
    
    public String getMessage() 
    { 
    	return message; 
    }
    
    public DataType getData() 
    { 
    	return data;
    }
}
