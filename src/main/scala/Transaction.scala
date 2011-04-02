import org.scala_tools.time.Imports._
import org.joda.time.Period
import org.joda.time.Months

trait Transaction {
  val date: DateTime
  val label: String
  val amount: Int
  
  override def hashCode:Int = 41 * (41 + amount)  * ( 41 + label.hashCode) * (41 + date.hashCode) 
  
  override def toString(): String =  { "Transaction: [Amount: " + amount +" Label: " + label + " Date: "+ date +"]"}
}

class Expense(val amount: Int, val label: String, val date: DateTime) extends Transaction {
  
  def canEqual(other: Any): Boolean = other.isInstanceOf[Expense]
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: Expense => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date)
      case _ => false
    }
  }
   
}

class Deposit(val amount: Int, val label: String, val date: DateTime) extends Transaction {
  
  def canEqual(other: Any): Boolean = other.isInstanceOf[Deposit]
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: Deposit => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date)
      case _ => false
    }
  }
  
}

class RecurringDeposit(override val amount: Int, override val label: String, override val date: DateTime, val period: Period) extends Deposit(amount, label, date) {
  
  override def canEqual(other: Any): Boolean = other.isInstanceOf[RecurringDeposit]
  
  override def hashCode:Int = 41 * (41 + amount)  * ( 41 + label.hashCode) * (41 + date.hashCode) * (41 + period.hashCode)
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: RecurringDeposit => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date && this.period == that.period)
      case _ => false
    }
  }
  override def toString(): String =  { "RecurringDeposit: [Amount: " + amount +" Label: " + label + " Date: "+ date +" Period: "+ period +"]"}
  
}

class RecurringExpense(override val amount: Int, override val label: String, override val date: DateTime, val period: Period) extends Expense(amount, label, date) {
  
  override def canEqual(other: Any): Boolean = other.isInstanceOf[RecurringExpense]
  
  override def hashCode:Int = 41 * (41 + amount)  * ( 41 + label.hashCode) * (41 + date.hashCode) * (41 + period.hashCode)
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: RecurringExpense => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date && this.period == that.period)
      case _ => false
    }
  }
  override def toString(): String =  { "RecurringWithdrawal: [Amount: " + amount +" Label: " + label + " Date: "+ date +" Period: "+ period +"]"}
  
}