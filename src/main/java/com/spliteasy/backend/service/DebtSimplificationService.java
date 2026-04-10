package com.spliteasy.backend.service;

import com.spliteasy.backend.dto.BalanceResponse;
import com.spliteasy.backend.dto.SettlementSuggestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class DebtSimplificationService {

    public List<SettlementSuggestion> simplify(List<BalanceResponse> balances) {
        // Max heap — largest creditor first
        PriorityQueue<BalanceResponse> creditors = new PriorityQueue<>(
                (a, b) -> Double.compare(b.getNetBalance(), a.getNetBalance())
        );

        // Min heap — largest debtor first (most negative)
        PriorityQueue<BalanceResponse> debtors = new PriorityQueue<>(
                (a, b) -> Double.compare(a.getNetBalance(), b.getNetBalance())
        );

        // Separate creditors and debtors
        for (BalanceResponse b : balances) {
            if (b.getNetBalance() > 0.01) {
                creditors.add(b);
            }
            if (b.getNetBalance() < -0.01) {
                debtors.add(b);
            }
        }

        List<SettlementSuggestion> result = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            BalanceResponse creditor = creditors.poll();
            BalanceResponse debtor = debtors.poll();

            double amount = Math.min(creditor.getNetBalance(), -debtor.getNetBalance());

            // Round to 2 decimal places
            amount = Math.round(amount * 100.0) / 100.0;

            result.add(new SettlementSuggestion(
                    debtor.getName(),    // from — pays
                    creditor.getName(),  // to   — receives
                    amount
            ));

            creditor.setNetBalance(creditor.getNetBalance() - amount);
            debtor.setNetBalance(debtor.getNetBalance() + amount);

            if (creditor.getNetBalance() > 0.01) {
                creditors.add(creditor);
            }
            if (debtor.getNetBalance() < -0.01) {
                debtors.add(debtor);
            }
        }

        return result;
    }
}
