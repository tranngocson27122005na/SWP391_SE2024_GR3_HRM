package com.hrm.infrastructure.security;

import com.hrm.dto.session.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Manages the authenticated {@link UserSession} stored in HttpSession.
 */
public final class SessionManager {

    private static final String SESSION_KEY = "userSession";

    private SessionManager() {
    }

    public static UserSession getUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_KEY);
        return value instanceof UserSession ? (UserSession) value : null;
    }

    public static void setUserSession(HttpServletRequest request, UserSession user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_KEY, user);
    }

    public static void removeUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_KEY);
        }
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUserSession(request) != null;
    }
}
