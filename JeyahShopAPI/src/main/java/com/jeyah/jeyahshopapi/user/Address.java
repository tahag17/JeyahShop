    package com.jeyah.jeyahshopapi.user;

    import jakarta.persistence.Entity;
    import jakarta.persistence.Id;
    import jakarta.persistence.OneToOne;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @Entity
    public class Address {
        @Id
        private Integer id;
        private Integer postalCode;
        private String city;
        private String street;
        @OneToOne
        private User user;
    }
