package com.hrm.utility;

import jakarta.servlet.http.HttpServletRequest;

public class Paging {

    /** Số bản ghi mỗi trang mặc định cho mọi list. */
    public static final int DEFAULT_NRPP = 7;

    private int size, nrpp, index;

    public Paging() {
    }

    public Paging(int size, int nrpp, int index) {
        this.size = size;
        this.nrpp = nrpp;
        this.index = index;
    }

    private int totalPage, start, end, pageStart, pageEnd;

    /**
     * Đọc điều hướng phân trang từ request.
     * nrpp lấy từ {@link #DEFAULT_NRPP}. Chưa gọi {@link #calc()}.
     */
    public static Paging fromRequest(HttpServletRequest request) {
        return fromRequest(request, DEFAULT_NRPP);
    }

    public static Paging fromRequest(HttpServletRequest request, int defaultNrpp) {
        Paging paging = new Paging();
        paging.setNrpp(defaultNrpp > 0 ? defaultNrpp : DEFAULT_NRPP);
        paging.setIndex(resolveIndex(request));
        return paging;
    }

    private static int resolveIndex(HttpServletRequest request) {
        int index = parseIntOrDefault(request.getParameter("index"), 0);

        if (request.getParameter("Home") != null) {
            return 0;
        }
        if (request.getParameter("End") != null) {
            return Integer.MAX_VALUE;
        }
        if (request.getParameter("Pre") != null) {
            return index - 1;
        }
        if (request.getParameter("Next") != null) {
            return index + 1;
        }

        String pageRaw = request.getParameter("page");
        if (pageRaw != null && !pageRaw.isBlank()) {
            try {
                // Nút trang và GET ?page= đều dùng số hiển thị 1-based
                return Integer.parseInt(pageRaw.trim()) - 1;
            } catch (NumberFormatException ignored) {
                return index;
            }
        }
        return index;
    }

    private static int parseIntOrDefault(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNrpp() {
        return nrpp;
    }

    public void setNrpp(int nrpp) {
        this.nrpp = nrpp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(int pageEnd) {
        this.pageEnd = pageEnd;
    }

    public void calc() {
        if (size <= 0 || nrpp <= 0) {
            totalPage = 0;
            index = 0;
            start = 0;
            end = -1;
            pageStart = 0;
            pageEnd = 0;
            return;
        }
        totalPage = (size + nrpp - 1) / nrpp;
        index = Math.max(index, 0);
        index = Math.min(index, totalPage - 1);
        start = index * nrpp;
        end = start + nrpp - 1;
        end = end >= size ? size - 1 : end;
        pageStart = Math.max(index - 2, 0);
        pageEnd = Math.min(index + 2, totalPage - 1);
    }

    public int getDisplayIndex() {
        return index + 1;
    }
}
