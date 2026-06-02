package com.iymen.campusroombooking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.iymen.campusroombooking.model.Booking;
import com.iymen.campusroombooking.model.BookingStatus;

@Repository
public class BookingRepository {

    private final List<Booking> bookings = new ArrayList<>();

    public BookingRepository() {
        loadHardcodedBookings();
    }

    public List<Booking> findAll() {
        return bookings;
    }

    private void loadHardcodedBookings() {
        bookings.add(new Booking(1L, 1L, "Alice", LocalDate.of(2024, 6, 20), LocalTime.of(10, 0), LocalTime.of(11, 0), BookingStatus.ACTIVE));
        bookings.add(new Booking(2L, 2L, "Bob", LocalDate.of(2024, 6, 20), LocalTime.of(9, 0), LocalTime.of(10, 0), BookingStatus.ACTIVE));
        bookings.add(new Booking(3L, 1L, "Charlie", LocalDate.of(2024, 6, 21), LocalTime.of(14, 0), LocalTime.of(15, 0), BookingStatus.ACTIVE));
    }

    public void save(Booking booking) {
        bookings.add(booking);
    }

    public Optional<Booking> findById(Long id) {
        for (Booking booking : bookings) {
            if (booking.id().equals(id)) {
                return Optional.of(booking);
            }
        }

        return Optional.empty();
    }

    public boolean replace(Booking booking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).id().equals(booking.id())) {
                bookings.set(i, booking);
                return true;
            }
        }

        return false;
    }

    public Long nextId() {
        Long maxId = 0L;

        for (Booking booking : bookings) {
            if (booking.id() > maxId) {
                maxId = booking.id();
            }
        }

        return maxId + 1;
    }
}
