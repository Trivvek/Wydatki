package com.example.wydatki.expenses;

import com.example.wydatki.categories.Category;
import com.example.wydatki.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    List<Expense> findByCategory(Category category);
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    boolean existsByCategory(Category category);

    @Query("SELECT DISTINCT YEAR(e.date) FROM Expense e WHERE e.user = :user ORDER BY YEAR(e.date)")
    List<Integer> findDistinctYearsByUser(@Param("user") User user);

    @Query("SELECT DISTINCT YEAR(e.date) as year, MONTH(e.date) as month " +
            "FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year " +
            "ORDER BY YEAR(e.date), MONTH(e.date)")
    List<Object[]> findDistinctYearMonthsByUserAndYear(@Param("user") User user, @Param("year") int year);
}