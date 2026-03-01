package com.example.ecommerceorderservicespring.entity;
//Importing jpa annotations like @Id,@Column used for mapping classes to a Db structure
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// Instant class from java time api -> tells timestamp in UTC
import java.time.Instant;

// Mapped super class?
// Is a jpa annotation used to define base class that contains the common field which will be inherited
// and mapped into the child entity tables.
// Mapped super class vs Entity
/* Unlike @Entity, Baseclass doesnt gets its own table. */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key auto generated and auto incremented
    //Id is the primary key and we are updating the primary key using the above strategy.
    private long id;

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    // these methods are automatically called before the entitty is saved for the 1st time
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        Instant now = Instant.now();
        this.updatedAt = now;
    }
}
