package com.jeyah.jeyahshopapi.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {

    @Query("""
    SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END
    FROM OrderDetails od
    WHERE od.user.id = :userId
      AND od.product.id = :productId
      AND od.order.status = :status
""")
    boolean existsByUserIdAndProductIdAndOrderStatus(
            @Param("userId") Integer userId,
            @Param("productId") Integer productId,
            @Param("status") OrderStatus status
    );

}
