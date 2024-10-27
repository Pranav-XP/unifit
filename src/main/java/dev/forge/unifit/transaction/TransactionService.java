package dev.forge.unifit.transaction;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction saveTransactionWithBookings(User user, List<Booking> bookings, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBookings(bookings);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreatedDate(Instant.now());
        transaction.setLastModifiedDate(Instant.now());

        // Set the transaction for each booking
        for (Booking booking : bookings) {
            booking.setTransaction(transaction);
        }

        return transactionRepository.save(transaction);
    }

    // Add a booking to an existing transaction
    @Transactional
    public Transaction addBookingToTransaction(Long transactionId, Booking booking) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        booking.setTransaction(transaction);
        transaction.getBookings().add(booking);

        return transactionRepository.save(transaction);
    }

    // Remove a booking from a transaction
    @Transactional
    public Transaction removeBookingFromTransaction(Long transactionId, Booking booking) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.getBookings().remove(booking);
        booking.setTransaction(null);

        return transactionRepository.save(transaction);
    }

    // Get all transactions for a specific user
    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.findAllByUser(user);
    }

    // Find transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }
}
