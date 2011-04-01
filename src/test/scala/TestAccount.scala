import org.scalatest.FunSuite
import java.util.Date
import java.util.Calendar

class TestAccount extends FunSuite {
  
  test("account creation") { 
    val account = new Account(1000)
    assert(account.balance == 1000)
  }
  
  test("deposit") {
    
    val date = new Date
    val paycheck = new Deposit(500, "Paycheck", date)
    val account = new Account(1000)
    
    val newAccount = account.deposit(paycheck)
    assert(newAccount.deposits.contains(paycheck))
    assert(newAccount.balance == 1500)
    assert(newAccount.deposits(0).date == date)
  }
  
  test("multiple deposits") {
    
    val paycheckDate = new Date()
    val paycheck = new Deposit(500, "Paycheck", paycheckDate)
    
    val interestPaymentDate = new Date()
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
    val expense = new Expense(100, "Mortgage",new Date())
    val account = new Account(1000)
    val newAccount = account.withdraw(expense)
    assert(newAccount.balance == 900)
    assert(newAccount.expenses.contains(expense))
  }
  
  test("test multiple expenses") {
    
    val mortgage = new Expense(100, "Mortgage",new Date())
    val cellphone = new Expense(50, "Cell Phone", new Date())
    val account = new Account(1000)
    
    val accountAfterMortgage = account.withdraw(mortgage)
    assert(accountAfterMortgage.balance == 900)
    val accountAfterCellphone = accountAfterMortgage.withdraw(cellphone)

    assert(accountAfterCellphone.balance == 850)
    assert(accountAfterCellphone.expenses.contains(mortgage))
    assert(accountAfterCellphone.expenses.contains(cellphone))
  }
  
  
  test("withdrawl more than balance") {
    
    val mortgage = new Expense(1100, "Mortgage", new Date())
    val account = new Account(1000)
    val accountWithFine = account.withdraw(mortgage)
    
    assert(accountWithFine.balance == 975)
    assert(accountWithFine.expenses(0).label == "Overdraft Fee")
    assert(accountWithFine.expenses(0).amount == 25)
  }
  
  test("test pending expenses dont effect current balance") {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DATE, 1)
    val firstOfNextMonth = calendar.getTime()
    val pendingExpense = new Expense(100, "Mortgage", firstOfNextMonth)
    val account = new Account(1000)
    
    val accountAfterAddingPendingExpense = account.withdraw(pendingExpense)
    assert(accountAfterAddingPendingExpense.balance == 1000)
  }
  
  test("test pending deposits dont effect current balance") {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DATE, 1)
    val firstOfNextMonth = calendar.getTime()
    val pendingDeposit = new Deposit(1000, "Paycheck", firstOfNextMonth)
    val account = new Account(1000)
    
    val accountAfterAddingPendingDeposit = account.deposit(pendingDeposit)
    assert(accountAfterAddingPendingDeposit.balance == 1000)
  }
  
  test("test account projection") {
    val budget = new Budget()
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DATE, 1)
    
    val later = Calendar.getInstance()
    later.add(Calendar.YEAR,1)
    val firstOfNextMonth = calendar.getTime()
    val nextYear = later.getTime()
    val expense = new Expense(100, "Mortgage", firstOfNextMonth)
    val paycheck = new Deposit(1000, "Paycheck", firstOfNextMonth)
    
     
    val account = new Account(1000)
    val accountAfterExpense = account.withdraw(expense)
    val accountAfterPaycheck = accountAfterExpense.deposit(paycheck)

    val accountWithProjection = accountAfterPaycheck.project(nextYear)
    assert(accountWithProjection.balance == 1900)
    assert(!accountWithProjection.pendingExpenses.contains(expense))
    assert(accountWithProjection.expenses.contains(expense))
    assert(!accountWithProjection.pendingDeposits.contains(paycheck))
    assert(accountWithProjection.deposits.contains(paycheck))
  }
}
