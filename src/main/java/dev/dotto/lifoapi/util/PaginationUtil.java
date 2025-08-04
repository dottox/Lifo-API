package dev.dotto.lifoapi.util;

public class PaginationUtil {

    public static Boolean validatePaginationParams(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortDirection
    ) {
        return (pageNumber != null && pageNumber >= 0) &&
               (pageSize != null && pageSize > 0) &&
               (sortBy != null && !sortBy.isEmpty()) &&
               (sortDirection != null && (sortDirection.equalsIgnoreCase("asc") || sortDirection.equalsIgnoreCase("desc")));
    }
}
