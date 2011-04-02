import scala.collection.immutable.List
import org.scala_tools.time.Imports._
import org.joda.time.DateTimeComparator
import org.joda.time.Days

class Account(val balance: Int, val expenses : List[Expense], val deposits: List[Deposit], val pendingExpenses: List[Expense], val pendingDeposits: List[Deposit], val recurringExpenses: List[RecurringExpense],  val recurringDeposits: List[RecurringDeposit], val date: DateTime) {

  def this(balance: Int) = this (balance, List[Expense](), List[Deposit](), List[Expense](), List[Deposit](),  List[RecurringExpense](), List[RecurringDeposit](), new DateTime())
  
  def deposit(deposit: Deposit): Account = {
    deposit match {
      case deposit : RecurringDeposit => new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, recurringExpenses, deposit::recurringDeposits, date)
      case _ => if(deposit.date.toDateMidnight().toDate().after(date.toDateMidnight().toDate()))
                  new Account(balance, expenses, deposits, pendingExpenses, deposit :: pendingDeposits, recurringExpenses, recurringDeposits, date)
                else {
                  new Account(balance + deposit.amount, expenses, deposit :: deposits, pendingExpenses, pendingDeposits.filterNot(_ == deposit), recurringExpenses, recurringDeposits, date)
                }
              }
  }
  
  def deposit(newDeposits: List[Deposit]): Account = {  
    var newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, recurringExpenses, recurringDeposits, date)
    for(deposit <- newDeposits) {
      newAccount = newAccount.deposit(deposit)
    }
    return newAccount
  }
  
  def withdraw(expense: Expense): Account = {   
    
    expense match {
      case expense: RecurringExpense => new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, expense :: recurringExpenses, recurringDeposits, date)
      case _ =>
    
        if(expense.date.toDateMidnight().toDate().after(date.toDateMidnight().toDate())) 
          new Account(balance, expenses, deposits, expense :: pendingExpenses, pendingDeposits, recurringExpenses, recurringDeposits, date) 
        else {
          if(expense.amount > balance && expense.label != "Overdraft Fee") withdraw(new Expense(25, "Overdraft Fee", date)) 
            else new Account(balance - expense.amount, expense :: expenses, deposits, pendingExpenses.filterNot(_ == expense), pendingDeposits, recurringExpenses, recurringDeposits, date)   
        }
    }
  }
  
  def withdraw(newExpenses: List[Expense]): Account = {  
    
    var newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, recurringExpenses, recurringDeposits,  date)
    for(expense <- newExpenses) {
      newAccount = newAccount.withdraw(expense)
    }
    return newAccount
  }
  
  def project(newDate: DateTime): Account =  {
    var newAccount = new Account(balance, expenses, deposits, pendingExpenses, pendingDeposits, recurringExpenses, recurringDeposits, newDate)
    
    //appy recurring deposits
    //boo to the imperative style, but it works for now 
    for(recurringDeposit <- recurringDeposits ) {
      val days = Days.daysBetween(recurringDeposit.date, newDate)
      val numberOfDepositsToApply = (days.dividedBy(recurringDeposit.period.getDays()))
      var i = 0;
      while(i< numberOfDepositsToApply.getDays) {
        val deposit = new Deposit(recurringDeposit.amount, recurringDeposit.label, recurringDeposit.date + (i*recurringDeposit.period.getDays()).days)
        newAccount = newAccount.deposit(deposit)
        i+=1
      }
    }  
    
    //appy recurring deposits
    //boo to the imperative style, but it works for now 
    for(recurringExpense <- recurringExpenses ) {
      val days = Days.daysBetween(recurringExpense.date, newDate)
      val numberOfDepositsToApply = (days.dividedBy(recurringExpense.period.getDays()))
      var i = 0;
      while(i< numberOfDepositsToApply.getDays) {
        val expense = new Expense(recurringExpense.amount, recurringExpense.label, recurringExpense.date + (i*recurringExpense.period.getDays()).days)
        newAccount = newAccount.withdraw(expense)
        i+=1
      }
    }  
    
    val mergedTranscations = pendingDeposits.filter(_.date.toDateMidnight().toDate().before(newDate.toDateMidnight().toDate())) ::: pendingExpenses.filter(_.date.toDateMidnight().toDate().before(newDate.toDateMidnight().toDate()))

    val sortedTransactions = mergedTranscations.sortWith((a: Transaction, b: Transaction) => DateTimeComparator.getDateOnlyInstance().compare(a.date,b.date)< 0)

    for(transaction <- sortedTransactions) {
      transaction match {
        case deposit: Deposit => newAccount = newAccount.deposit(deposit)
        case expense: Expense => newAccount = newAccount.withdraw(expense)
      }
    }
    return newAccount
  }
  
  override def toString(): String =  { "\nAccount: [Balance: " + balance +" Expenses: " + expenses + " Deposits: "+ deposits + " Pending Expenses: "+ pendingExpenses + " PendingDeposits: "+ pendingDeposits + "]"}  
}