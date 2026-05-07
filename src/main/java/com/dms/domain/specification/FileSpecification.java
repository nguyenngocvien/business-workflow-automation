package com.dms.domain.specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.dms.domain.entity.AttributeFilter;
import com.dms.domain.entity.FileAttributeValue;
import com.dms.domain.entity.FileEntity;
import com.dms.domain.enums.AttributeDataType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class FileSpecification {

    public static Specification<FileEntity> byAttributeFilters(
            List<AttributeFilter> filters,
            Map<String, AttributeDataType> attrTypeMap) {

        return (root, query, cb) -> {

            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            for (AttributeFilter filter : filters) {

                AttributeDataType type = attrTypeMap.get(filter.getKey());
                if (type == null) {
                    throw new IllegalArgumentException("Unknown attribute: " + filter.getKey());
                }

                Subquery<Long> subquery = query.subquery(Long.class);
                Root<FileAttributeValue> fav = subquery.from(FileAttributeValue.class);
                Join<Object, Object> attr = fav.join("attribute");

                List<Predicate> subPredicates = new ArrayList<>();

                // match file
                subPredicates.add(cb.equal(fav.get("file").get("id"), root.get("id")));

                // match attribute key
                subPredicates.add(cb.equal(attr.get("keyCode"), filter.getKey()));

                // value predicate
                subPredicates.add(buildValuePredicate(cb, fav, filter, type));

                subquery.select(fav.get("file").get("id"))
                        .where(cb.and(subPredicates.toArray(new Predicate[0])));

                predicates.add(cb.exists(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildValuePredicate(
            CriteriaBuilder cb,
            Root<FileAttributeValue> fav,
            AttributeFilter filter,
            AttributeDataType type) {

        String op = filter.getOperator();
        Object value = filter.getValue();

        switch (type) {

            case STRING:
                return buildStringPredicate(cb, fav.get("valueString"), op, value);

            case NUMBER:
                return buildNumberPredicate(cb, fav.get("valueNumber"), op, value);

            case DATE:
                return buildDatePredicate(cb, fav.get("valueDate"), op, value);

            case BOOLEAN:
                return cb.equal(fav.get("valueBoolean"), value);

            case LIST:
                // assume value = option_id
                return cb.equal(fav.get("option").get("id"), value);

            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    // ===== STRING =====
    private static Predicate buildStringPredicate(
            CriteriaBuilder cb,
            Expression<String> expr,
            String op,
            Object value) {

        String val = value.toString();

        switch (op) {
            case "eq":
                return cb.equal(expr, val);

            case "like":
                return cb.like(cb.lower(expr), "%" + val.toLowerCase() + "%");

            default:
                throw new IllegalArgumentException("Unsupported operator for STRING: " + op);
        }
    }

    // ===== NUMBER =====
    private static Predicate buildNumberPredicate(
            CriteriaBuilder cb,
            Expression<BigDecimal> expr,
            String op,
            Object value) {

        BigDecimal val = castToBigDecimal(value);

        switch (op) {
            case "eq":
                return cb.equal(expr, val);

            case "gt":
                return cb.greaterThan(expr, val);

            case "lt":
                return cb.lessThan(expr, val);

            default:
                throw new IllegalArgumentException("Unsupported operator for NUMBER: " + op);
        }
    }

    // ===== DATE =====
    private static Predicate buildDatePredicate(
            CriteriaBuilder cb,
            Expression<Instant> expr,
            String op,
            Object value) {

        Instant val = castToInstant(value);

        switch (op) {
            case "eq":
                return cb.equal(expr, val);

            case "gt":
                return cb.greaterThan(expr, val);

            case "lt":
                return cb.lessThan(expr, val);

            default:
                throw new IllegalArgumentException("Unsupported operator for DATE: " + op);
        }
    }

    // ===== CAST HELPERS =====
    private static BigDecimal castToBigDecimal(Object value) {
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        throw new IllegalArgumentException("Invalid number value: " + value);
    }

    private static Instant castToInstant(Object value) {
        if (value instanceof Instant) return (Instant) value;
        throw new IllegalArgumentException("Invalid date value: " + value);
    }
}