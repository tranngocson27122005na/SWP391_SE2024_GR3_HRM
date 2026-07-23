package com.hrm.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/error")
public class ErrorController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardError(request, response);
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer status = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (status == null) {
            status = response.getStatus();
        }
        String view;
        if (status != null && status == 403) {
            view = "/WEB-INF/views/common/error-403.jsp";
        } else if (status != null && status == 404) {
            view = "/WEB-INF/views/common/error-404.jsp";
        } else {
            view = "/WEB-INF/views/common/error-500.jsp";
        }
        request.setAttribute("statusCode", status);
        request.getRequestDispatcher(view).forward(request, response);
    }
}
