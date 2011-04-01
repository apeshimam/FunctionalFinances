import scala.collection.immutable.List
import java.util.Date

class Account(val balance: Int, val expenses : List[Expense], val deposits: List[Deposit], val pendingExpenses: List[Expense], val pendingDeposits: List[Deposit], val date: Date) {

  def this(balance: Int) = this (balance, List[Expense](), List[Deposit](), List[Expense](), List[Deposit](), new Date())
  
  def deposit(deposit: Deposit): Account = {
    if(deposit.date.after(date))
      new Account(balance, expenses, deposits, pendingExpenses, deposit :: pendingDeposits, date)
    else {
      new Account(balance + deposit.amount, expenses, deposit :: deposits, pendingExpenses, pendingDeposits.filterNot(_ == deposit), date)
    }
  }
  
  def deposit(deposits: List[Deposit]): Account = {  
    var newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, date)
    for(deposit <- deposits) {
      newAccount = newAccount.deposit(deposit)
    }
    return newAccount
  }
  
  def withdraw(expense: Expense): Account = {     
    if(expense.date.after(date)) 
      new Account(balance, expenses, deposits, expense :: pendingExpenses, pendingDeposits, date) 
    else {
      if(expense.amount > balance && expense.label != "Overdraft Fee") withdraw(new Expense(25, "Overdraft Fee", new Date())) 
        else new Account(balance - expense.amount, expense :: expenses, deposits, pendingExpenses.filterNot(_ == expense), pendingDeposits, date)   
    }
  }
  
  def withdraw(expenses: List[Expense]): Account = {  
    var newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, date)
    for(expense <- expenses) {
      newAccount = newAccount.withdraw(expense)
    }
    return newAccount
  }
  
  def project(newDate: Date): Account =  {
    val newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, newDate)
    return newAccount.deposit(pendingDeposits.filter(_.date.before(newDate))).withdraw(pendingExpenses.filter(_.date.before(newDate)))      
  }
  
  override def toString(): String =  { "\nAccount: [Balance: " + balance +"\nExpenses: " + expenses + "\nDeposits: "+ deposits + "\nPending Expenses: "+ pendingExpenses + "\nPendingDeposits: "+ pendingDeposits + "]"}  
}