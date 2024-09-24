package com.jeyah.jeyahshopapi.order;

import com.jeyah.jeyahshopapi.common.BaseEntity;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_")
public class Order extends BaseEntity {

private OrderStatus status;

//@ManyToOne
//@JoinColumn(name = "user_id", nullable = false)
//private User user;

@OneToMany(mappedBy = "order")
private List<OrderDetails> orderDetails;
}
