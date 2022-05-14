package budget;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BudgetManager {
    double totalIncome;
    boolean endProgram;
    List<Expense> expenses;
    String filename;
    Scanner scanner;

    public BudgetManager() {
        totalIncome = 0;
        endProgram = false;
        expenses = new ArrayList<>();
        filename = "purchases.txt";
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (!endProgram) {
            printMenu();
            methodSelect();
        }
    }

    private void printMenu() {
        System.out.println("Choose your action:\n" +
                           "1) Add income\n" +
                           "2) Add purchase\n" +
                           "3) Show list of purchases\n" +
                           "4) Balance\n" +
                           "5) Save\n" +
                           "6) Load\n" +
                           "7) Analyze (Sort)\n" +
                           "0) Exit\n");
    }

    private void purchaseMenu() {
        System.out.println("Choose the type of purchase\n" +
                           "1) Food\n" +
                           "2) Clothes\n" +
                           "3) Entertainment\n" +
                           "4) Other\n" +
                           "5) Back\n");
    }

    private void listMenu() {
        System.out.println("Choose the type of purchases\n" +
                           "1) Food\n" +
                           "2) Clothes\n" +
                           "3) Entertainment\n" +
                           "4) Other\n" +
                           "5) All\n" +
                           "6) Back\n");
    }

    private void analyzeMenu() {
        System.out.println("How do you want to sort?\n" +
                           "1) Sort all purchases\n" +
                           "2) Sort by type\n" +
                           "3) Sort certain type\n" +
                           "4) Back\n");
    }

    private void sortMenu() {
        System.out.println("Choose the type of purchase\n" +
                           "1) Food\n" +
                           "2) Clothes\n" +
                           "3) Entertainment\n" +
                           "4) Other\n");
    }

    private void methodSelect() {
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                addIncome();
                break;
            case "2":
                addPurchase();
                break;
            case "3":
                printReceipt();
                break;
            case "4":
                showBalance();
                break;
            case "5":
                saveToSaveFile();
                break;
            case "6":
                loadFromSaveFile();
                break;
            case "7":
                analyze();
                break;
            case "0":
                setEndProgram();
                break;
            default:
                System.out.println("Invalid input\n");
        }
    }

    private void addIncome() {
        System.out.println("Enter income:");
        String input = scanner.nextLine().trim();
        try {
            totalIncome += Double.parseDouble(input);
            System.out.println("Income was added!\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input\n");
        }
    }

    private void addPurchase() {
        String nameOfExpense;
        double priceOfExpense;
        ExpenseType expenseType;

        while (true) {
            purchaseMenu();

            String expenseSelection = scanner.nextLine();
            switch (expenseSelection) {
                case "1":
                    expenseType = ExpenseType.FOOD;
                    break;
                case "2":
                    expenseType = ExpenseType.CLOTHES;
                    break;
                case "3":
                    expenseType = ExpenseType.ENTERTAINMENT;
                    break;
                case "4":
                    expenseType = ExpenseType.OTHER;
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid input\n");
                    return;
            }

            System.out.println("Enter purchase name:");
            nameOfExpense = scanner.nextLine();
            System.out.println("Enter its price:");
            String input = scanner.nextLine();

            try {
                priceOfExpense = Double.parseDouble(input);
                if (priceOfExpense <= totalIncome) {
                    totalIncome -= priceOfExpense;
                    Expense expense = new Expense(nameOfExpense, priceOfExpense, expenseType);
                    expenses.add(expense);

                    System.out.println("Purchase was added!\n");
                } else {
                    System.out.println("Not enough funds to purchase\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input\n");
            }
        }
    }

    private double calculate(List<Expense> expenseList) {
        return expenseList.stream().mapToDouble(Expense::getValue).sum();
    }

    private void printReceipt() {
        if (expenses.isEmpty()) {
            System.out.println("The purchase list is empty!\n");
        } else {
            while (true) {
                listMenu();
                ExpenseType currentExpenses;
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1":
                        currentExpenses = ExpenseType.FOOD;
                        break;
                    case "2":
                        currentExpenses = ExpenseType.CLOTHES;
                        break;
                    case "3":
                        currentExpenses = ExpenseType.ENTERTAINMENT;
                        break;
                    case "4":
                        currentExpenses = ExpenseType.OTHER;
                        break;
                    case "5":
                        currentExpenses = null;
                        break;
                    case "6":
                        //quit
                        return;
                    default:
                        //quit menu due to invalid input
                        System.out.println("Invalid input\n");
                        return;
                }
                if (input.equals("5")) {
                    // full list
                    System.out.println("All:");
                    expenses.forEach(System.out::println);
                    System.out.printf("Total sum: $%.2f\n\n", calculate(expenses));
                } else {
                    //create sublist based off enum
                    List<Expense> subList = expenses.stream().filter(expense -> expense.getExpenseType() == currentExpenses).collect(Collectors.toList());

                    if (subList.isEmpty()) {
                        System.out.println("The purchase list is empty!\n");
                    } else {
                        subList.forEach(System.out::println);
                        System.out.printf("Total sum: $%.2f\n\n", calculate(subList));
                    }
                }
            }
        }
    }

    private void showBalance() {
        System.out.printf("Balance: $%.2f\n\n", totalIncome);
    }

    private void saveToSaveFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
            bufferedWriter.write(totalIncome + "\n");
            for (Expense expense : expenses) {
                String expenseToString = expense.getName() + "\t" + expense.getValue() + "\t" + expense.getExpenseType().name() + "\n";
                bufferedWriter.write(expenseToString);
            }
            System.out.println("Purchases were saved!\n");
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }

    }

    private void loadFromSaveFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("purchases.txt"))) {
            totalIncome = Double.parseDouble(bufferedReader.readLine());
            String bufferedLine;
            while ((bufferedLine = bufferedReader.readLine()) != null) {
                String[] expenseData = bufferedLine.split("\t");

                String expenseName = expenseData[0];
                double expenseValue = Double.parseDouble(expenseData[1]);
                ExpenseType expenseType = ExpenseType.valueOf(expenseData[2]);

                Expense currentExpense = new Expense(expenseName, expenseValue, expenseType);
                expenses.add(currentExpense);
            }
            System.out.println("Purchases were loaded!\n");
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND");
        }
    }

    private void analyze() {
        while (true) {
            analyzeMenu();
            String selection = scanner.nextLine();
            switch (selection) {
                case "1":
                    sortAllPurchases();
                    break;
                case "2":
                    sortAllByType();
                    break;
                case "3":
                    sortCertainType();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid entry");
                    return;
            }
        }
    }

    private void sortAllPurchases() {
        if (expenses.isEmpty()) {
            System.out.println("The purchase list is empty!\n");
            return;
        }

        List<Expense> sortedExpenses = new ArrayList<>(expenses);
        Collections.sort(sortedExpenses);
        for (Expense expense : sortedExpenses) {
            System.out.println(expense);
        }
        System.out.println();
    }

    private void sortAllByType() {
        double total = 0;
        for (ExpenseType expenseType : ExpenseType.values()) {
            List<Expense> subList = expenses.stream().filter(expense -> expense.getExpenseType() == expenseType).collect(Collectors.toList());
            double currentListTotal = calculate(subList);
            System.out.printf(expenseType + " - $%.2f\n", currentListTotal);
            total += currentListTotal;
        }
        System.out.printf("Total sum: $%.2f\n\n", total);
    }

    private void sortCertainType() {
        ExpenseType expenseType;
        List<Expense> subList;
        sortMenu();

        String selection = scanner.nextLine();
        switch (selection) {
            case "1":
                expenseType = ExpenseType.FOOD;
                break;
            case "2":
                expenseType = ExpenseType.CLOTHES;
                break;
            case "3":
                expenseType = ExpenseType.ENTERTAINMENT;
                break;
            case "4":
                expenseType = ExpenseType.OTHER;
                break;
            default:
                System.out.println("Invalid selection");
                return;
        }

        subList = expenses.stream().filter(expense -> expense.getExpenseType() == expenseType).collect(Collectors.toList());
        Collections.sort(subList);

        if (subList.isEmpty()) {
            System.out.println("The purchase list is empty!\n");
        } else {
            System.out.println(expenseType + ":");
            subList.forEach(System.out::println);
            System.out.printf("Total sum: $%.2f\n\n", calculate(subList));
        }

    }

    private void setEndProgram() {
        System.out.println("\nBye!");
        endProgram = true;
    }
}
