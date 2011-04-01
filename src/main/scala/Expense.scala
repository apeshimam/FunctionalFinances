import java.util.Date

class Expense(val amount: Int, val label: String, val date:Date) {
  override def toString(): String =  { "Amount: " + amount +"\nLabel: " + label}
  
  def canEqual(other: Any): Boolean = other.isInstanceOf[Expense]
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: Expense => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date)
      case _ => false
    }
  }
  override def hashCode:Int = 41 * (41 + amount)  * ( 41 + label.hashCode) * (41 + date.hashCode) 
}