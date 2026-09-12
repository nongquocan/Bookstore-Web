package com.bookstore.repository;

import com.bookstore.entity.User;
import com.bookstore.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserAndBookId(User user, String bookId);

    @Query("SELECT w FROM Wishlist w WHERE w.user = :user AND w.book.id = :bookId")
    Optional<Wishlist> findByUserAndBook(@Param("user") User user, @Param("bookId") String bookId);

    void deleteByUserAndBookId(User user, String bookId);
}
