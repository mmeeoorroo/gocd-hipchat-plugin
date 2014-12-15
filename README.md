GoCD Hipchat Notification Plugin
================================

To install:

* Add your hipchat token to token.txt
* Run `sbt clean assembly`
* Copy `gocd-hipchat-plugin.jar` from `target/scala-2.10/` to the `plugins/external` folder on your GoCD server
* Restart the server
* HipChat notification is now available as a task plugin
