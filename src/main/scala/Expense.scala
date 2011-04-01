import java.util.Date

class Expense(val amount: Int, val label: String, val date:Date) {
  override def toString(): String =  { "Amount: " + amount +"\n" + "label: " + label}
}