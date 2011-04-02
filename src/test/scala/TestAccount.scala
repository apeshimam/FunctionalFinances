import org.scalatest.FunSuite
import org.scala_tools.time.Imports._
import org.joda.time.Days

class TestAccount extends FunSuite {
  
  test("account creation") { 
    val account = new Account(1000)
    assert(account.balance == 1000)
  }
  
  test("deposit") {
    
    val date = DateTime.now
    val paycheck = new Deposit(500, "Paycheck", date)
    val account = new Account(1000)
    
    val newAccount = account.deposit(paycheck)
    assert(newAccount.deposits.contains(paycheck))
    assert(newAccount.balance == 1500)
    assert(newAccount.deposits(0).date == date)
  }
  
  test("multiple deposits") {
    
    val paycheckDate = DateTime.now
    val paycheck = new Deposit(500, "Paycheck", paycheckDate)
    
    val interestPaymentDate = DateTime.now
    val interestPayment = new Deposit(10,"Interest Payment",interestPaymentDate)
    val account = new Account(1000)
    val accountAfterPaycheck = account.deposit(paycheck)
    assert(accountAfterPaycheck.balance == 1500)
    assert(accountAfterPaycheck.deposits.contains(paycheck))
    
    val accountAfterInterestPayment = accountAfterPaycheck.deposit(interestPayment)
    assert(accountAfterInterestPayment.balance == 1510)
    assert(accountAfterInterestPayment.deposits.contains(paycheck))
    assert(accountAfterInterestPayment.deposits.contains(interestPayment))
  }
  
  test("widthdraw") {
    val expense = new Expense(100, "Mortgage", DateTime.now)
    val account = new Account(1000)
    val newAccount = account.withdraw(expense)
    assert(newAccount.balance == 900)
    assert(newAccount.expenses.contains(expense))
  }
  
  test("test multiple expenses") {
    
    val mortgage = new Expense(100, "Mortgage", DateTime.now)
    val cellphone = new Expense(50, "Cell Phone", DateTime.now)
    val account = new Account(1000)
    
    val accountAfterMortgage = account.withdraw(mortgage)
    assert(accountAfterMortgage.balance == 900)
    val accountAfterCellphone = accountAfterMortgage.withdraw(cellphone)

    assert(accountAfterCellphone.balance == 850)
    assert(accountAfterCellphone.expenses.contains(mortgage))
    assert(accountAfterCellphone.expenses.contains(cellphone))
  }
  
  
  test("withdrawl more than balance") {
    
    val mortgage = new Expense(1100, "Mortgage", DateTime.now)
    val account = new Account(1000)
    val accountWithFine = account.withdraw(mortgage)
    
    assert(accountWithFine.balance == 975)
    assert(accountWithFine.expenses(0).label == "Overdraft Fee")
    assert(accountWithFine.expenses(0).amount == 25)
  }
  
  test("test pending expenses dont effect current balance") {
    val nextMonth = (DateTime.now + 1.month).withDayOfMonth(1)
    val pendingExpense = new Expense(100, "Mortgage", nextMonth)
    val account = new Account(1000)
    
    val accountAfterAddingPendingExpense = account.withdraw(pendingExpense)
    assert(accountAfterAddingPendingExpense.balance == 1000)
  }
  
  test("test pending deposits dont effect current balance") {
    val nextMonth = (DateTime.now + 1.month).withDayOfMonth(1)
    val pendingDeposit = new Deposit(1000, "Paycheck", nextMonth)
    val account = new Account(1000)
    
    val accountAfterAddingPendingDeposit = account.deposit(pendingDeposit)
    assert(accountAfterAddingPendingDeposit.balance == 1000)
  }
  
  test("test account projection") {

    val nextMonth = (DateTime.now + 1.month).withDayOfMonth(1)
    val twoMonths = (DateTime.now + 2.month).withDayOfMonth(1)
    val nextYear = DateTime.now + 1.year
   
    val expense = new Expense(100, "Mortgage", nextMonth)
    val paycheck = new Deposit(1000, "Paycheck", twoMonths)
    
    val account = new Account(1000)
    val accountAfterExpense = account.withdraw(expense)
    val accountAfterPaycheck = accountAfterExpense.deposit(paycheck)
    val accountWithProjection = accountAfterPaycheck.project(nextYear)
    
    assert(accountWithProjection.deposits.size === 1)
    assert(accountWithProjection.expenses.size === 1)
    assert(accountWithProjection.balance == 1900)
    assert(!accountWithProjection.pendingExpenses.contains(expense))
    assert(accountWithProjection.expenses.contains(expense))
    assert(!accountWithProjection.pendingDeposits.contains(paycheck))
    assert(accountWithProjection.deposits.contains(paycheck))
  }
  
  test("projection sequence") {
    val nextMonth = (DateTime.now + 1.month).withDayOfMonth(1)
    val twoMonths = (DateTime.now + 2.month).withDayOfMonth(1)
    val account = new Account(1000)
    
    val expense = new Expense(1500, "Mortgage", nextMonth)
    val paycheck = new Deposit(1000, "Paycheck", twoMonths)
    val accountAfterDeposit = account.deposit(paycheck)
    val accountAfterExpense = accountAfterDeposit.withdraw(expense)
    val projectedAccount = accountAfterExpense.project(DateTime.now + 3.months)

    assert(projectedAccount.expenses(0).label == "Overdraft Fee")
  }
  
  test("Recurring deposits") {
    //create a recurring deposit with monthly recurrence 
    val recurringDeposit = new RecurringDeposit(100, "Paycheck", (DateTime.now + 1.month).withDayOfMonth(1), Days.ONE.toPeriod())

    val account = new Account(1000)
    
    val accountWithDeposit = account.deposit(recurringDeposit)
    val projectedAccount = accountWithDeposit.project( (DateTime.now + 1.month).withDayOfMonth(1) + 10.days )
    
    assert(projectedAccount.balance===2000)
  }
  
  test("Recurring withdrawals") {
    val recurringExpense = new RecurringExpense(100, "Mortgage", (DateTime.now + 1.month).withDayOfMonth(1), Days.ONE.toPeriod())

    val account = new Account(2000)
    
    val accountWithWithDrawal = account.withdraw(recurringExpense)
    val projectedAccount = accountWithWithDrawal.project( (DateTime.now + 1.month).withDayOfMonth(1) + 10.days )
    
    
    assert(projectedAccount.balance===1000)
  }
}
