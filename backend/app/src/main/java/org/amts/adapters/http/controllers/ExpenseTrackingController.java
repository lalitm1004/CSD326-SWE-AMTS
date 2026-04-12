package org.amts.adapters.http.controllers;

import java.util.List;
import java.util.UUID;

import org.amts.application.usecases.finance.ExpenseTrackingUseCase;
import org.amts.domain.entities.finance.BalanceSheet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseTrackingController {

    private final ExpenseTrackingUseCase expenseTracking;

    public ExpenseTrackingController(ExpenseTrackingUseCase expenseTracking) {
        this.expenseTracking = expenseTracking;
    }

    record CreateExpenseRequest(
            UUID clerkUserId,
            UUID showId,
            String name,
            String description,
            double amount
    ) {}

    @PostMapping
    public ResponseEntity<Void> createExpense(@RequestBody CreateExpenseRequest request) {
        expenseTracking.createExpense(
                request.clerkUserId(),
                request.showId(),
                request.name(),
                request.description(),
                request.amount()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteExpense(
            @RequestParam UUID clerkUserId,
            @RequestParam UUID expenseId
    ) {
        expenseTracking.deleteExpense(clerkUserId, expenseId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/name")
    public ResponseEntity<Void> updateExpenseName(
            @RequestParam UUID clerkUserId,
            @RequestParam UUID expenseId,
            @RequestParam String newName
    ) {
        expenseTracking.updateExpenseName(clerkUserId, expenseId, newName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> updateExpenseDescription(
            @RequestParam UUID clerkUserId,
            @RequestParam UUID expenseId,
            @RequestParam String newDescription
    ) {
        expenseTracking.updateExpenseDescription(clerkUserId, expenseId, newDescription);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/amount")
    public ResponseEntity<Void> updateExpenseAmount(
            @RequestParam UUID clerkUserId,
            @RequestParam UUID expenseId,
            @RequestParam double newAmount
    ) {
        expenseTracking.updateExpenseAmount(clerkUserId, expenseId, newAmount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance-sheet")
    public ResponseEntity<BalanceSheet> getBalanceSheetByShow(
            @RequestParam UUID userId,
            @RequestParam UUID showId
    ) {
        return expenseTracking.getBalanceSheetByShow(userId, showId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/balance-sheet/by-event")
    public ResponseEntity<List<BalanceSheet>> getBalanceSheetsByEvent(
            @RequestParam UUID userId,
            @RequestParam UUID eventId
    ) {
        return ResponseEntity.ok(expenseTracking.getBalanceSheetsByEvent(userId, eventId));
    }

    @GetMapping("/balance-sheet/by-year")
    public ResponseEntity<List<BalanceSheet>> getBalanceSheetsByYear(
            @RequestParam UUID userId,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(expenseTracking.getBalanceSheetsByYear(userId, year));
    }
}
