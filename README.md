GoCD Hipchat Notification Plugin [![Build Status](https://travis-ci.org/DrStrangepork/gocd-hipchat-plugin.svg?branch=master)](https://travis-ci.org/DrStrangepork/gocd-hipchat-plugin)
================================

Fork from [https://github.com/PagerDuty/gocd-hipchat-plugin/tree/7452a3e8cdcbfe63090cf93291155cc769b21583](https://github.com/PagerDuty/gocd-hipchat-plugin/tree/7452a3e8cdcbfe63090cf93291155cc769b21583)

####To install:

* Add your hipchat token to `src/main/resources/token.txt`
* Run `sbt clean assembly`
* Copy `gocd-hipchat-plugin.jar` from `target/scala-2.10/` to the `plugins/external` folder on your GoCD server* Restart the server* HipChat notification is now available as a task plugin
Environment variables can be inserted into messages using bash $ notation. If the GO_BASE_URL environment variable is set, you can use $BUILD_URL to insert a link to the current build

####Some enhancements:

1. Can change message format
2. Can set room id through environment/security variables or parameters (insert with ${var_name} or #{param} name)
