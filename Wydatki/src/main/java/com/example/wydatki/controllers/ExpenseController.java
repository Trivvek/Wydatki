package com.example.wydatki.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.wydatki.expenses.ExpenseService;
import com.example.wydatki.expenses.Expense;
import com.example.wydatki.user.User;
import com.example.wydatki.categories.CategoryService;
import com.example.wydatki.categories.Category;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listExpenses(@AuthenticationPrincipal User user,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(required = false) String sortOrder,
                               Model model) {
        List<Expense> allExpenses = expenseService.getExpensesForUser(user);

        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortBy.equals("amount")) {
                allExpenses.sort(Comparator.comparing(Expense::getAmount));
            } else if (sortBy.equals("date")) {
                allExpenses.sort(Comparator.comparing(Expense::getDate));
            }

            if (sortOrder != null && sortOrder.equals("desc")) {
                Collections.reverse(allExpenses);
            }
        }

        Map<Category, List<Expense>> expensesByCategory = allExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory));

        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "expenses/list";
    }

    @PostMapping
    public String addExpense(@AuthenticationPrincipal User user,
                             @RequestParam Long categoryId,
                             @RequestParam BigDecimal amount,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam String description) {
        expenseService.addExpense(categoryId, amount, date, description, user);
        return "redirect:/expenses";
    }

    @PostMapping("/delete/{id}")
    public String deleteExpense(@AuthenticationPrincipal User user, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (expenseService.deleteExpense(id, user)) {
            redirectAttributes.addFlashAttribute("message", "Expense deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to delete expense. It may not exist or you may not have permission.");
        }
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal User user, @PathVariable Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        if (expense != null && expense.getUser().getId().equals(user.getId())) {
            model.addAttribute("expense", expense);
            model.addAttribute("categories", categoryService.getCategoriesForUser(user));
            return "expenses/edit";
        }
        return "redirect:/expenses";
    }

    @PostMapping("/edit/{id}")
    public String updateExpense(@AuthenticationPrincipal User user,
                                @PathVariable Long id,
                                @RequestParam Long categoryId,
                                @RequestParam BigDecimal amount,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                @RequestParam String description,
                                RedirectAttributes redirectAttributes) {
        Expense updatedExpense = expenseService.updateExpense(id, categoryId, amount, date, description, user);
        if (updatedExpense != null) {
            redirectAttributes.addFlashAttribute("message", "Expense updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update expense. It may not exist or you may not have permission.");
        }
        return "redirect:/expenses";
    }
}
