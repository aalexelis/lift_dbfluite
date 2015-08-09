package code.lib

import java.sql.SQLException

import net.liftweb.common.Full
import net.liftweb.http.RequestVar
import net.liftweb.util.{DynoVar, LoanWrapper}
import org.seasar.dbflute.unit.core.transaction.{TransactionResource, TransactionPerformer, TransactionPerformFailureException}

/**
 * Created by andreas on 15/06/03.
 */

object MiscTries {
  def buildLoanWrapper(): LoanWrapper =
    new LoanWrapper {
      private object Switch extends RequestVar[Boolean](false)
      def apply[T](f: => T): T = {
        if (Switch.is) f else {
          println("BEFORE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + Switch.is)
          val ret = f
          println("AFTER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + Switch.is)
          Switch.set(true)
          ret
        }
      }
    }


  /**
   * Perform the process in new transaction (even if the transaction has already been begun). <br />
   * You can select commit or roll-back by returned value of the callback method.
   * <pre>
   * performNewTransaction(new TransactionPerformer() {
   *     public boolean perform() { <span style="color: #3F7E5E">// transaction scope</span>
   *         ...
   *         return false; <span style="color: #3F7E5E">// true: commit, false: roll-back</span>
   *     }
   * });
   * </pre>
   * @param performer The callback for the transaction process. (NotNull)
   * @throws TransactionPerformFailureException When the performance fails.
   */
  protected def performNewTransaction(performer: TransactionPerformer): Unit = {
    assertNotNull(performer);
    val resource: TransactionResource = beginNewTransaction();
    var cause: Exception = null;
    var commit = false;
    try {
      commit = performer.perform();
    } catch {
      case e: RuntimeException => {
        cause = e;
      }
      case e: SQLException =>
      {
        cause = e;
      }
        throw e;
    } finally {
      if (commit && cause == null) {
        try {
          commitTransaction(resource);
        } catch {
          case e: RuntimeException => {
            cause = e;
          }
        }
      } else {
        try {
          rollbackTransaction(resource);
        } catch {
          case e: RuntimeException => {
            if (cause != null) {
              log(e.getMessage());
            } else {
              cause = e;
            }
          }
        }
      }
    }
    if (cause != null) {
      val msg: String = "Failed to perform the process in transaction: " + performer;
      throw new TransactionPerformFailureException(msg, cause);
    }
  }

}