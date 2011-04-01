import java.util.Date

class Deposit(val amount: Int, val label: String, val date: Date) {
  override def toString(): String =  { "Amount: " + amount +"\n" + "label: " + label}
  
  def canEqual(other: Any): Boolean = other.isInstanceOf[Deposit]
  
  override def equals(other: Any): Boolean  =  {
    other match {
      case that: Deposit => that.canEqual(this) && (this.amount == that.amount && this.label == that.label && this.date == that.date)
      case _ => false
    }
  }
  override def hashCode:Int = 41 * (41 + amount)  * ( 41 + label.hashCode) * (41 + date.hashCode) 
}