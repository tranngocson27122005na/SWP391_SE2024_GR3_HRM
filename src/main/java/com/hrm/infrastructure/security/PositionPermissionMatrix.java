package com.hrm.infrastructure.security;

import com.hrm.dto.response.PositionPermissionGrant;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.enums.PermissionAction;
import com.hrm.persistence.mapper.PositionPermissionMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Cache position_permission → URL paths. Resolve by {@code positionId} (not role).
 */
public final class PositionPermissionMatrix {

    /** url path → required permission_name (e.g. employee:READ) */
    private static final Map<String, String> urlToPermission = new HashMap<>();

    /** positionId → set of permission_name */
    private static final Map<Integer, Set<String>> positionPermissions = new HashMap<>();

    private static volatile boolean loaded = false;

    private PositionPermissionMatrix() {
    }

    public static synchronized void load() {
        urlToPermission.clear();
        positionPermissions.clear();
        try {
            List<PositionPermissionGrant> grants = SqlExecutor.execute(
                    PositionPermissionMapper.class, PositionPermissionMapper::selectAllGrants);
            if (grants != null) {
                for (PositionPermissionGrant grant : grants) {
                    if (grant.getPositionId() == null || grant.getPermissionName() == null) {
                        continue;
                    }
                    positionPermissions
                            .computeIfAbsent(grant.getPositionId(), k -> new HashSet<>())
                            .add(grant.getPermissionName());

                    String actionName = resolveActionName(grant);
                    if (grant.getResource() == null || actionName == null) {
                        continue;
                    }
                    for (String url : mapToUrls(grant.getResource(), actionName)) {
                        urlToPermission.putIfAbsent(url, grant.getPermissionName());
                    }
                }
            }
        } catch (Exception e) {
            // Keep empty matrix on bootstrap failure; Filter will deny USER business paths.
        }
        loaded = true;
    }

    public static synchronized void reload() {
        load();
    }

    public static boolean hasPermission(Long positionId, String urlPath) {
        if (!loaded) {
            load();
        }
        if (positionId == null || urlPath == null) {
            return false;
        }
        String required = findRequiredPermission(normalize(urlPath));
        if (required == null) {
            return false;
        }
        Set<String> granted = positionPermissions.get(positionId.intValue());
        return granted != null && granted.contains(required);
    }

    public static Set<String> permissionsOf(Long positionId) {
        if (!loaded) {
            load();
        }
        if (positionId == null) {
            return Set.of();
        }
        Set<String> granted = positionPermissions.get(positionId.intValue());
        if (granted == null || granted.isEmpty()) {
            return Set.of();
        }
        return Collections.unmodifiableSet(granted);
    }

    static Set<String> mapToUrls(String resource, String action) {
        Set<String> urls = new HashSet<>();
        String res = resource.trim();
        String act = action.trim().toUpperCase(Locale.ROOT);

        switch (act) {
            case "READ" -> {
                if ("employee-self".equals(res)) {
                    // Quyền self được AuthFilter kiểm tra trên /employee/detail?id=session
                    // (không map URL cố định để tránh mở mọi /employee/detail).
                } else {
                    urls.add("/" + res + "/list");
                    urls.add("/" + res + "/detail");
                }
            }
            case "CREATE" -> urls.add("/" + res + "/create");
            case "UPDATE" -> {
                urls.add("/" + res + "/edit");
                urls.add("/" + res + "/update");
            }
            case "DELETE" -> urls.add("/" + res + "/delete");
            case "EXPORT" -> urls.add("/" + res + "/export");
            case "IMPORT" -> urls.add("/" + res + "/import");
            case "APPROVE" -> urls.add("/" + res + "/approve");
            case "REJECT" -> urls.add("/" + res + "/reject");
            case "SUBMIT" -> urls.add("/" + res + "/submit");
            case "CANCEL" -> urls.add("/" + res + "/cancel");
            default -> {
            }
        }
        return urls;
    }

    private static String resolveActionName(PositionPermissionGrant grant) {
        PermissionAction fromCode = PermissionAction.fromCode(grant.getAction());
        if (fromCode != null) {
            return fromCode.name();
        }
        String name = grant.getPermissionName();
        if (name == null) {
            return null;
        }
        int colon = name.indexOf(':');
        return colon >= 0 ? name.substring(colon + 1) : null;
    }

    private static String normalize(String urlPath) {
        int q = urlPath.indexOf('?');
        return q >= 0 ? urlPath.substring(0, q) : urlPath;
    }

    private static String findRequiredPermission(String urlPath) {
        String exact = urlToPermission.get(urlPath);
        if (exact != null) {
            return exact;
        }
        String bestPattern = null;
        for (String pattern : urlToPermission.keySet()) {
            if (matchesWildcard(pattern, urlPath)) {
                if (bestPattern == null || pattern.length() > bestPattern.length()) {
                    bestPattern = pattern;
                }
            }
        }
        return bestPattern != null ? urlToPermission.get(bestPattern) : null;
    }

    private static boolean matchesWildcard(String pattern, String urlPath) {
        if (!pattern.endsWith("/*")) {
            return pattern.equals(urlPath);
        }
        String prefix = pattern.substring(0, pattern.length() - 1);
        return urlPath.startsWith(prefix)
                || urlPath.equals(prefix.substring(0, prefix.length() - 1));
    }
}
