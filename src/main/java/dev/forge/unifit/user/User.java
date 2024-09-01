package dev.forge.unifit.user;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import dev.forge.unifit.authentication.role.Role;
import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "user_account")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Column(length = 60)
    private String password;

    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE} , fetch = FetchType.EAGER)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
    private List<Transaction> transactions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public User() {
        super();
        this.enabled = true;
    }

    public void setTransactions(final List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setBookings(final List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(final String username) {
        this.email = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!getEmail().equals(user.getEmail())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=")
                .append(id)
                .append(", firstName=").append(firstName)
                .append(", lastName=").append(lastName)
                .append(", email=").append(email)
                .append(", enabled=").append(enabled)
                .append(", roles=").append(roles)
                .append("]");
        return builder.toString();
    }

}
