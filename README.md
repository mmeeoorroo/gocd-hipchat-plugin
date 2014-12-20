GoCD Hipchat Notification Plugin
================================

To install:

* Add your hipchat token to `src/main/resources/token.txt`
* Run `sbt clean assembly`
* Copy `gocd-hipchat-plugin.jar` from `target/scala-2.10/` to the `plugins/external` folder on your GoCD server
* Restart the server
* HipChat notification is now available as a task plugin

Environment variables can be inserted into messages using bash $ notation. If the GO_BASE_URL
environment variable is set, you can use $BUILD_URL to insert a link to the current build