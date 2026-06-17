package com.iymen.campusroombooking.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.iymen.campusroombooking.model.Booking;
import com.iymen.campusroombooking.entity.BookingEntity;

@Repository
public class BookingRepository {

    private final BookingJpaRepository bookingJpaRepository;

    public BookingRepository(BookingJpaRepository bookingJpaRepository) {
        this.bookingJpaRepository = bookingJpaRepository;
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        List<BookingEntity> bookingEntities = bookingJpaRepository.findAll();
        for (BookingEntity entity : bookingEntities) {
            bookings.add(toBooking(entity));
        }
        return bookings;
    }

    private Booking toBooking(BookingEntity bookingEntity) {
        return new Booking(bookingEntity.getId(), bookingEntity.getRoomId(), bookingEntity.getBookedBy(),
                bookingEntity.getDate(), bookingEntity.getStartTime(), bookingEntity.getEndTime(),
                bookingEntity.getStatus());
    }

    public Booking save(Booking booking) {

        BookingEntity savedEntity = bookingJpaRepository.save(toBookingEntity(booking));

        return toBooking(savedEntity);
    }

    private BookingEntity toBookingEntity(Booking booking) {
        return new BookingEntity(booking.id(),
                booking.roomId(), booking.bookedBy(), booking.date(),
                booking.startTime(), booking.endTime(), booking.status());
    }

    public Optional<Booking> findById(Long id) {
        Optional<BookingEntity> bookingEntity = bookingJpaRepository.findById(id);

        if (bookingEntity.isPresent()) {
            return Optional.of(toBooking(bookingEntity.get()));
        }

        return Optional.empty();
    }

    public boolean replace(Booking booking) {
        if (bookingJpaRepository.findById(booking.id()).isEmpty()) {
            return false;
        }
        bookingJpaRepository.save(toBookingEntity(booking));
        return true;
    }

}
