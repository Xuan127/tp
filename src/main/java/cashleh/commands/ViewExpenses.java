package cashleh.commands;

import cashleh.transaction.ExpenseStatement;

public class ViewExpenses extends Command {
    private final ExpenseStatement expenseStatement;

    public ViewExpenses(ExpenseStatement expenseStatement) {
        this.expenseStatement = expenseStatement;
    }
    @Override
    public void execute() {
        expenseStatement.printExpenses();
    }
}
