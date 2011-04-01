import scala.collection.immutable.List
import java.util.Date

class Account(val balance: Int, val expenses : List[Expense], val deposits: List[Deposit]) {

  def this(balance: Int) = this (balance, List[Expense](), List[Deposit]())
  
  def deposit(deposit: Deposit): Account = new Account(balance + deposit.amount, expenses, deposit :: deposits)
  
  def withdraw(expense: Expense): Account = { 
    if(expense.amount > balance && expense.label != "Overdraft Fee") withdraw(new Expense(25, "Overdraft Fee", new Date())) 
      else new Account(balance - expense.amount, expense :: expenses, deposits)   
  }
  
  override def toString(): String =  { "Balance: " + balance +"\nExpenses: " + expenses + "\nDeposits: "+ deposits}
  
}