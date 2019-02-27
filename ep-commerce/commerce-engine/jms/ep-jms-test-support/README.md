# ep-jms-test-support

This framework allows you to connect to a JMS provider - currently ActiveMQ.
It can create and listen to queues and topics. 
It can send/receive messages from/to queues/topics.

It reads the `ep.jms.url` property defined in the `ep-test-plugin.properties` file to connect 
to the appropriate ActiveMQ instance. 

This framework can be used for testing ActiveMQ messages and verifying that their content is valid. 
It allows you to read and write messages to queues and topics in activeMQ in JSON format and 
simple text format and allows you to write tests to validate the messages' content.  

You will need to be running the following servers:
* **activeMQ server**.
* **search server**
* **cortex server** - for tests that create orders.
* **integration server** if you want to test emails.

See an example of the usage of this framework in 
`ep-commerce/extensions/cortex/system-tests/cucumber/src/test/resources/features/jms`
