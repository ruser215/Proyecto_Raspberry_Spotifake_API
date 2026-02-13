import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlinx.coroutines.Dispatchers

suspend fun <T> suspendTransaction ( code : Transaction.() -> T) : T =
    newSuspendedTransaction(Dispatchers.IO, statement= code)
    