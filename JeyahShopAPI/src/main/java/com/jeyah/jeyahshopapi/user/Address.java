    package com.jeyah.jeyahshopapi.user;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @Entity
    public class Address {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private Integer postalCode;
        private String city;
        private String street;
        @OneToOne
        @JoinColumn(name = "user_id", nullable = false, unique = true) // owning side
        @JsonBackReference
        private User user;
    }
