package com.example.wydatki.expenses;

import com.example.wydatki.categories.Category;
import com.example.wydatki.categories.CategoryRepository;
import com.example.wydatki.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Expense addExpense(Long categoryId, BigDecimal amount, LocalDate date, String description, User user) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setUser(user);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setDescription(description);

        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesForUser(User user) {
        return expenseRepository.findByUser(user);
    }

    public List<Expense> getExpensesForCategory(Category category) {
        return expenseRepository.findByCategory(category);
    }

    public boolean deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense != null && expense.getUser().getId().equals(user.getId())) {
            expenseRepository.delete(expense);
            return true;
        }
        return false;
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense updateExpense(Long id, Long categoryId, BigDecimal amount, LocalDate date, String description, User user) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense != null && expense.getUser().getId().equals(user.getId())) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            expense.setCategory(category);
            expense.setAmount(amount);
            expense.setDate(date);
            expense.setDescription(description);

            return expenseRepository.save(expense);
        }
        return null;
    }

    public List<Integer> getYearsWithExpenses(User user) {
        return expenseRepository.findDistinctYearsByUser(user);
    }

    public List<YearMonth> getMonthsWithExpenses(User user, int year) {
        List<Object[]> results = expenseRepository.findDistinctYearMonthsByUserAndYear(user, year);
        return results.stream()
                .map(result -> YearMonth.of((int)result[0], (int)result[1]))
                .collect(Collectors.toList());
    }

    public Map<String, Double> generateReport(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getDate().toString(),
                        Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
    }
}
