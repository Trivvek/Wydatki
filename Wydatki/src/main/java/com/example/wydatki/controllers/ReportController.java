package com.example.wydatki.controllers;

import com.example.wydatki.expenses.ExpenseService;
import com.example.wydatki.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.YearMonth;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public String showReportForm(@AuthenticationPrincipal User user, Model model) {
        List<Integer> years = expenseService.getYearsWithExpenses(user);
        model.addAttribute("years", years);
        return "reports/form";
    }

    @GetMapping("/months")
    @ResponseBody
    public List<Integer> getMonths(@AuthenticationPrincipal User user, @RequestParam int year) {
        return expenseService.getMonthsWithExpenses(user, year)
                .stream()
                .map(YearMonth::getMonthValue)
                .collect(Collectors.toList());
    }

    @GetMapping("/generate")
    public String generateReport(@AuthenticationPrincipal User user,
                                 @RequestParam int year,
                                 @RequestParam int month,
                                 Model model) {
        Map<String, Double> report = expenseService.generateReport(user, year, month);
        model.addAttribute("report", report);
        model.addAttribute("year", year);
        model.addAttribute("monthName", Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault()));
        return "reports/view";
    }
}
