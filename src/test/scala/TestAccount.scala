import org.scalatest.FunSuite
import java.util.Date

class TestAccount extends FunSuite {
  
  test("account creation") { 
    val account = new Account(1000)
    assert(account.balance == 1000)
  }
  
  test("deposit") {
    val account = new Account(1000)
    assert(account.balance == 1000)
    
    val date = new Date
    val paycheck = new Deposit(500, "Paycheck", date)
    val newAccount = account.deposit(paycheck)
    assert(newAccount.deposits.contains(paycheck))
    assert(newAccount.balance == 1500)
    assert(newAccount.deposits(0).date == date)
  }
  
  test("multiple deposits") {
    val account = new Account(1000)
    assert(account.balance == 1000)
    val paycheckDate = new Date()
    val paycheck = new Deposit(500, "Paycheck", paycheckDate)
    
    val interestPaymentDate = new Date()
    val interestPayment = new Deposit(10,"Interest Payment",interestPaymentDate)
    
    val accountAfterPaycheck = account.deposit(paycheck)
    assert(accountAfterPaycheck.balance == 1500)
    assert(accountAfterPaycheck.deposits.contains(paycheck))
    
    val accountAfterInterestPayment = accountAfterPaycheck.deposit(interestPayment)
    assert(accountAfterInterestPayment.balance == 1510)
    assert(accountAfterInterestPayment.deposits.contains(paycheck))
    assert(accountAfterInterestPayment.deposits.contains(interestPayment))
  }
  
  test("widthdraw") {
    val account = new Account(1000)
    val expense = new Expense(100, "Mortgage",new Date())
    val newAccount = account.withdraw(expense)
    
    assert(newAccount.balance == 900)
    assert(newAccount.expenses.contains(expense))
  }
  
  test("test multiple expenses") {
    val account = new Account(1000)
    val mortgage = new Expense(100, "Mortgage",new Date())
    val cellphone = new Expense(50, "Cell Phone", new Date())
    
    val accountAfterMortgage = account.withdraw(mortgage)
    assert(accountAfterMortgage.balance == 900)
    val accountAfterCellphone = accountAfterMortgage.withdraw(cellphone)

    assert(accountAfterCellphone.balance == 850)
    assert(accountAfterCellphone.expenses.contains(mortgage))
    assert(accountAfterCellphone.expenses.contains(cellphone))
  }
  
  test("withdrawl more than balance") {
    val account = new Account(1000)
    val mortgage = new Expense(1100, "Mortgage", new Date())
    val accountWithFine = account.withdraw(mortgage)
    
    assert(accountWithFine.balance == 975)
    assert(accountWithFine.expenses(0).label == "Overdraft Fee")
    assert(accountWithFine.expenses(0).amount == 25)
  }
  
}
