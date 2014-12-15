package hipchat

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult
import com.thoughtworks.go.plugin.api.task._
import java.io.FileNotFoundException
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import scala.io.Source
import scalaj.http._

class HipChatNotificationTaskExecutor extends TaskExecutor {
  import HipChatNotificationTaskExecutor._

  override def execute(taskConfig: TaskConfig, taskContext: TaskExecutionContext): ExecutionResult = {
    try {
      notifyHipchat(taskConfig, taskContext)
    } catch {
      case e: Exception =>
        ExecutionResult.failure("Failed to notify hipchat", e)
    }
  }

  private def notifyHipchat(taskConfig: TaskConfig, taskContext: TaskExecutionContext): ExecutionResult = {

    //todo: fail sbt build if not found
    val token = getToken().getOrElse(throw new FileNotFoundException("HipChat token not found"))

    //todo: get name from config
    val roomName = "DevTools"
    //todo: link to build (stage? pipeline?)
    val environmentVars = taskContext.environment.asMap
    val notificationType = taskConfig.getValue(HipChatNotificationTask.NOTIFICATION_TYPE)
    //todo: replace env vars
    val msg = Option(taskConfig.getValue(HipChatNotificationTask.MESSAGE)).filterNot(_.trim.isEmpty)

    val hipchatMsg = {
      notificationType.toLowerCase match {
        case "success" =>
          ("color" -> "green") ~
            ("message" -> msg.getOrElse(s"${environmentVars.get(PIPELINE_NAME)} - Build #${environmentVars.get(BUILD_NUMBER)} passed")) ~
            ("message_format" -> "text")
        case "failure" =>
          ("color" -> "red") ~
            ("message" -> msg.getOrElse(s"${environmentVars.get(PIPELINE_NAME)} - Build #${environmentVars.get(BUILD_NUMBER)} failed")) ~
            ("message_format" -> "text")
        case _ =>
          ("message" -> msg.getOrElse(s"${environmentVars.get(PIPELINE_NAME)} - Build #${environmentVars.get(BUILD_NUMBER)} finished")) ~
            ("message_format" -> "text")

      }
    }

    taskContext.console.printLine(s"Sending notification to $roomName: $msg")

    val hipchat = Http(s"http://api.hipchat.com/v2/room/$roomName/notification").header("Authorization", s"Bearer $token")
      .header("content-type", "application/json")
      .postData(compact(render(hipchatMsg))).asString

    if (hipchat.code == 204) {
      ExecutionResult.success("Hipchat notified")
    } else {
      ExecutionResult.failure(s"Hipchat notification failed (${hipchat.code}): ${hipchat.body}")
    }

  }

}

object HipChatNotificationTaskExecutor {
  val PIPELINE_NAME = "GO_PIPELINE_NAME"
  val BUILD_NUMBER = "GO_PIPELINE_COUNTER"

  def getToken() = Source.fromURL(getClass.getResource("/token.txt")).getLines.toList.headOption
}