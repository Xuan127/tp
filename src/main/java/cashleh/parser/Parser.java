package cashleh.parser;

import cashleh.transaction.Expense;
import cashleh.transaction.ExpenseStatement;
import cashleh.transaction.Income;
import cashleh.transaction.IncomeStatement;
import exceptions.CashLehParsingException;
import cashleh.commands.Command;
import cashleh.commands.AddIncome;
import cashleh.commands.AddExpense;
import cashleh.commands.ViewIncomes;
import cashleh.commands.ViewExpenses;
import cashleh.commands.DeleteExpense;
import cashleh.commands.DeleteIncome;
import cashleh.commands.Exit;

import java.time.LocalDate;
import java.util.HashMap;

public class Parser {
    private static final String ADD_INCOME = "addIncome";
    private static final String DELETE_INCOME = "deleteIncome";
    private static final String VIEW_INCOMES = "viewIncomes";
    private static final String ADD_EXPENSE = "addExpense";
    private static final String DELETE_EXPENSE = "deleteExpense";
    private static final String VIEW_EXPENSES = "viewExpenses";
    private static final String EXIT = "exit";

    private final ExpenseStatement expenseStatement;
    private final IncomeStatement incomeStatement;

    public Parser(ExpenseStatement expenseStatement, IncomeStatement incomeStatement) {
        this.expenseStatement = expenseStatement;
        this.incomeStatement = incomeStatement;
    }

    public Command parse(String input) throws CashLehParsingException {
        String command = input.contains(" ") ? input.split(" ")[0] : input;
        switch (command) {
        case ADD_INCOME:
            Income income = getIncome(input);
            return new AddIncome(income, incomeStatement);
        case DELETE_INCOME:
            return getDeleteTransaction(input, DELETE_INCOME);
        case VIEW_INCOMES:
            return new ViewIncomes(incomeStatement);
        case ADD_EXPENSE:
            Expense expense = getExpense(input);
            return new AddExpense(expense, expenseStatement);
        case DELETE_EXPENSE:
            return getDeleteTransaction(input, DELETE_EXPENSE);
        case VIEW_EXPENSES:
            return new ViewExpenses(expenseStatement);
        case EXIT:
            return new Exit();
        default:
            throw new CashLehParsingException("Aiyoh! Your input blur like sotong... Clean your input for CashLeh!");
        }
    }


    private Expense getExpense(String input) throws CashLehParsingException {
        String[] format = {ADD_EXPENSE, "/amt", "/date:optional"};
        HashMap<String, String> inputDetails = StringTokenizer.tokenize(input, format);
        String expenseName = inputDetails.get(ADD_EXPENSE);
        String expenseAmtString = inputDetails.get("/amt");
        String expenseDateString = inputDetails.get("/date");

        if (expenseName.isEmpty()) {
            throw new CashLehParsingException(
                "Oopsie! An expense without a description is like a CashLeh transaction without its story - not as fun!"
            );
        }
        double expenseAmt;
        try {
            expenseAmt = Double.parseDouble(expenseAmtString);
        } catch (NumberFormatException e) {
            throw new CashLehParsingException("Please enter a valid expense amount!");
        }

        // default to current date if no date is specified
        if (expenseDateString == null || expenseDateString.isEmpty()) {
            return new Expense(expenseName, expenseAmt);
        }
        LocalDate parsedDate = DateParser.parse(expenseDateString);
        return new Expense(expenseName, expenseAmt, parsedDate);
    }

    private Income getIncome(String input) throws CashLehParsingException {
        String[] format = {ADD_INCOME, "/amt", "/date:optional"};
        HashMap<String, String> inputDetails = StringTokenizer.tokenize(input, format);
        String incomeName = inputDetails.get(ADD_INCOME);
        String incomeAmtString = inputDetails.get("/amt");
        String incomeDateString = inputDetails.get("/date");

        if (incomeName.isEmpty()) {
            throw new CashLehParsingException(
                    "Oopsie! An income without a description is like a CashLeh transaction without " +
                            "its story - not as fun!"
            );
        }
        double incomeAmt;
        try {
            incomeAmt = Double.parseDouble(incomeAmtString);
        } catch (NumberFormatException e) {
            throw new CashLehParsingException("Please enter a valid expense amount!");
        }

        // default to current date if no date is specified
        if (incomeDateString == null || incomeDateString.isEmpty()) {
            return new Income(incomeName, incomeAmt);
        }
        LocalDate parsedDate = DateParser.parse(incomeDateString);
        return new Income(incomeName, incomeAmt, parsedDate);
    }

    private Command getDeleteTransaction(String input, String transactionType) throws CashLehParsingException {
        String trimmedInput = input.trim();
        if (trimmedInput.equals(transactionType)) {
            throw new CashLehParsingException("Which transaction kena the chop today? Make your choice!");
        }
        String transactionString = input.split(" ", 2)[1];
        int transactionIndex;
        try {
            transactionIndex = Integer.parseInt(transactionString);
        } catch (NumberFormatException e) {
            throw new CashLehParsingException("Eh, that's not the kind of number we flaunt in CashLeh!");
        }
        return transactionType.equals(DELETE_EXPENSE) ?
            new DeleteExpense(transactionIndex, expenseStatement) : new DeleteIncome(transactionIndex, incomeStatement);
    }
}
