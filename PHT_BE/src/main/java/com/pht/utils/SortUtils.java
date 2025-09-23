package com.pht.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import org.springframework.data.domain.Sort;

import com.pht.common.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SortUtils {
    public static List<Sort.Order> buildOrders(List<OrderBy> orders) {
        if (ValidationUtils.isNullOrEmpty(orders)) {
            return new ArrayList<>();
        }
        return orders.stream().map((it) -> new Sort.Order(Sort.Direction.fromString(it.getDirection()), it.getProperty())).collect(Collectors.toList());
    }

    public static Sort buildSort(List<OrderBy> orders) {
        return Sort.by(buildOrders(orders));
    }

    public static Order getQueryOrder(CriteriaBuilder cb, From<?, ?> from, OrderBy orderBy) {
        if (cb != null && from != null && orderBy != null) {
            Path<?> path = from.get(orderBy.getProperty());
            if (orderBy.getDirection().equalsIgnoreCase("asc")) {
                return cb.asc(path);
            } else if (orderBy.getDirection().equalsIgnoreCase("desc")) {
                return cb.desc(path);
            }
        }

        return null;
    }

    public static List<Order> getQueryOrders(CriteriaBuilder cb, From<?, ?> from, List<OrderBy> orderByList) {
        List<Order> orders = new ArrayList<>();
        if (cb != null && from != null && orderByList != null) {
            orderByList.forEach(orderBy -> orders.add(getQueryOrder(cb, from, orderBy)));
        }

        return orders;
    }
}
