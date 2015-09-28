GoCD Hipchat Notification Plugin [![Build Status](https://travis-ci.org/PagerDuty/gocd-hipchat-plugin.svg?branch=master)](https://travis-ci.org/PagerDuty/gocd-hipchat-plugin)
================================

** This software is no longer actively being maintained by PagerDuty.  We've switched over to Slack, and can no longer easily develop for this plugin.  If you want to take over ownership, please email opensource@pagerduty.com. **

To install:

* Add your hipchat token to `src/main/resources/token.txt`
* Run `sbt clean assembly`
* Copy `gocd-hipchat-plugin.jar` from `target/scala-2.10/` to the `plugins/external` folder on your GoCD server
* Restart the server
* HipChat notification is now available as a task plugin

Environment variables can be inserted into messages using bash $ notation. If the GO_BASE_URL
environment variable is set, you can use $BUILD_URL to insert a link to the current build
