package com.itamnesia.qiwihackathon.model.user;

import com.itamnesia.qiwihackathon.model.Payment;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login")
    private String login;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Column(name = "confirm_token")
    private String confirmToken;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "request_id")
    private String requestId;
    @Column(name = "payment_token")
    private String paymentToken;
    @Column(name = "shop_name")
    private String shopName;
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "purchaser"
    )
    private List<Payment> purchases;
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "shop"
    )
    private List<Payment> sales;
}
