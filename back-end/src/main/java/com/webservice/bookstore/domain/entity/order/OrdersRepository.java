package com.webservice.bookstore.domain.entity.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.OrderBy;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("select distinct o from Orders o "
            + "join fetch o.delivery od "
            + "join fetch o.orderItems ooi "
            + "join fetch ooi.item ooii "
            + "where o.member.id = :member_id order by o.createdDate desc")
    List<Orders> getAllByMemberId(@Param(value = "member_id") Long member_id);

}