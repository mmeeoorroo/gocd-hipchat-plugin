package hipchat

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult
import com.thoughtworks.go.plugin.api.task._
import java.io.FileNotFoundException
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import scala.io.Source
import scala.util.Try
import scalaj.http._
import collection.JavaConverters._

object HipChatNotificationTaskExecutor {
  val BASE_URL = "GO_BASE_URL"
  val PIPELINE_NAME = "GO_PIPELINE_NAME"
  val BUILD_NUMBER = "GO_PIPELINE_COUNTER"
  val SERVER_URL = "GO_SERVER_URL"
  val STAGE_NAME = "GO_STAGE_NAME"
  val STAGE_NUMBER = "GO_STAGE_COUNTER"

  def getToken() = Try(Source.fromURL(getClass.getResource("/token.txt")).getLines.toList(0)).getOrElse(throw new FileNotFoundException("HipChat token not found"))

  def replaceEnvVars(msg: String, vars: Map[String, String]): String = {
    val regexes = vars.collect { case (varName, value) => ((raw"\$$\{?" + varName + raw"\}?").r, value) }
    regexes.foldLeft(msg) { (line, regexAndReplacement) =>
      regexAndReplacement._1.replaceAllIn(line, regexAndReplacement._2)
    }
  }
}

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
    val token = getToken()

    val roomName = Option(taskConfig.getValue(HipChatNotificationTask.ROOM)).filterNot(_.trim.isEmpty).getOrElse(throw new Exception("HipChat room not found"))

    val buildUrl: Option[String] = {
      val envVars = taskContext.environment.asMap.asScala
      (for {
        baseUrl <- envVars.get(BASE_URL)
        pipelineName <- envVars.get(PIPELINE_NAME)
        buildNumber <- envVars.get(BUILD_NUMBER)
        stageName <- envVars.get(STAGE_NAME)
        stageNumber <- envVars.get(STAGE_NUMBER)
      } yield {
        s"${baseUrl}go/pipelines/$pipelineName/$buildNumber/$stageName/$stageNumber"
      })
    }

    val systemEnvironmentVars = taskContext.environment.asMap.asScala.toMap

    val environmentVars = buildUrl.map { url =>
      systemEnvironmentVars.updated("BUILD_URL", url)
    } getOrElse systemEnvironmentVars

    val notificationType = taskConfig.getValue(HipChatNotificationTask.NOTIFICATION_TYPE)

    val msg = Option(taskConfig.getValue(HipChatNotificationTask.MESSAGE)).filterNot(_.trim.isEmpty)

    val defaultPassed = s"$$${PIPELINE_NAME} - Build #$$${BUILD_NUMBER} passed" + buildUrl.map(": " + _).getOrElse("")
    val defaultFailed = s"$$${PIPELINE_NAME} - Build #$$${BUILD_NUMBER} failed" + buildUrl.map(": " + _).getOrElse("")
    val defaultOther = s"$$${PIPELINE_NAME} - Build #$$${BUILD_NUMBER} finished" + buildUrl.map(": " + _).getOrElse("")

    val hipchatMsg = {
      notificationType.toLowerCase match {
        case "success" =>
          ("color" -> "green") ~
            ("message" -> replaceEnvVars(msg.getOrElse(defaultPassed), environmentVars)) ~
            ("message_format" -> "text")
        case "failure" =>
          ("color" -> "red") ~
            ("message" -> replaceEnvVars(msg.getOrElse(defaultFailed), environmentVars)) ~
            ("message_format" -> "text")
        case _ =>
          ("message" -> replaceEnvVars(msg.getOrElse(defaultOther), environmentVars)) ~
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
