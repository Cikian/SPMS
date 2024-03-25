package com.spms.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {

    public static void customResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
    }
}
