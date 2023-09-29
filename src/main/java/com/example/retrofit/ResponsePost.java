package com.example.retrofit;

public class ResponsePost {
    public Result result = null;
    public Result error = null;
    public static class Result {
        public String access_token;
        public String token_type;
        public String message;
    }
}
