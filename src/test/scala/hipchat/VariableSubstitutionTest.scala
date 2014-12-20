package hipchat

import org.specs2.mutable._

class VariableSubstitutionTest extends Specification {

  import HipChatNotificationTaskExecutor._

  "Variable replacement" should {
    "replace a variable" in {
      val input = "$GO_PIPELINE_NAME build finished"
      val replacement = Map("GO_PIPELINE_NAME" -> "Chef")
      replaceEnvVars(input, replacement) mustEqual "Chef build finished"
    }

    "replace multiple variables" in {
      val input = "Build for $GO_PIPELINE_NAME finished, tests passed in $TEST_DURATION seconds"
      val replacement = Map("GO_PIPELINE_NAME" -> "mercury", "TEST_DURATION" -> "75")
      replaceEnvVars(input, replacement) mustEqual "Build for mercury finished, tests passed in 75 seconds"
    }

    "replace variables with brackets" in {
      val input = "Build for $GO_PIPELINE_NAME finished, tests passed in ${TEST_DURATION}ms"
      val replacement = Map("GO_PIPELINE_NAME" -> "apollo", "TEST_DURATION" -> "650")
      replaceEnvVars(input, replacement) mustEqual "Build for apollo finished, tests passed in 650ms"
    }

    "replace the same variable multiple times" in {
      val input = "$GO_PIPELINE_NAME I said $GO_PIPELINE_NAME I said"
      val replacement = Map("GO_PIPELINE_NAME" -> "mercury")
      replaceEnvVars(input, replacement) mustEqual "mercury I said mercury I said"
    }

    "not replace variables in an escaped statement" in {
      val input = "Build of $GO_PIPELINE_NAME failed, $${BUILD_COST} in the jar"
      val replacement = Map("GO_PIPELINE_NAME" -> "mercury", "BUILD_COST" -> "1.25")
      replaceEnvVars(input, replacement) mustEqual "Build of mercury failed, $1.25 in the jar"

    }
  }
}
