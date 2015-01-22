
Bletchley for (dark)mail
========================

[Darkmail](http://darkmail.info/) is an proposal for a snoop resistant email
system. Servers in the system see only the data they need in order to deliver 
the message, rather than the message itself.

Darkmail has a 100+ page specification which I have really only skimmed, but
in abstract: The senders server is able to see which server the recipient uses,
and nothing else. The recipient server is able to see the user id of the 
recipient, and nothing else. It's the principal of least privilege applied to 
an email system. The pictures in the specification do a good job of describing
the system.

With the aid of Bletchley, it's possible to implement a message encoder and
decoder that satisfies this abstract in about 200 lines of Java.
This example is the result of doing exactly that. The result isn't a mail client
and server, but the code you would add to an existing mail server and client
written in Java to add this capability.

Note that an aim of Darkmail is to hide who is talking to who from attackers 
with resources to see all traffic, and compromise some mail servers. This
is extremely difficult. For most users of Bletchley, the participants are
not going to be secret, and are often fixed. Hence in the example, the
message passes through the system unmodified.

Disclaimer
----------
Building a secure email system, with the aims of Darkmail, requires far more
care than has been applied to this example. As much as Bletchley eliminates 
many common errors in the implementation of cryptographic systems, a great deal
is still left to the user.

Walk through
------------
We start by defining Actions. Actions are what you want to send and receive. In
our case, our sender wants to send:

* The [message](src/main/java/net/lshift/bletchley/mail/Message.java) itself 
  to the recipient (and end user)
* [Relay](src/main/java/net/lshift/bletchley/mail/Relay.java) which tells the 
  senders mail server to relay the message to the recipients server
* [Deliver](src/main/java/net/lshift/bletchley/mail/Deliver.java) which tells 
  the recipients mail server, to deliver the message to the recipient.

These are all Java classes, annotated to tell Bletchley how they should be
serialised. The annotations are well documented in Javadoc.

To be able to read these actions from encoded data, you use 
[ConvertUtils](https://github.com/lshift/bletchley/src/main/java/net/lshift/bletchley/convert/ConvertUtils.java)
which needs a [ReadInfo](https://github.com/lshift/bletchley/src/main/java/net/lshift/bletchley/convert/ReadInfo.java)
[defined here](src/main/java/net/lshift/bletchley/mail/Actions.java) in READ_INFO.

The thing Bletchley helps you most with is filtering out data you don't trust,
so code for receiving messages is really simple. The receivers are:

* [Server](src/main/java/net/lshift/bletchley/mail/Server.java)
* [Receiver](src/main/java/net/lshift/bletchley/mail/Receiver.java)

It makes sense to define receivers first: You will have quite a lot of
flexibility in constructing a message, but your receivers will tend to be
very stable. It's worth writing tests which construct the simplest possible
message that will satisfy them, because that's generally very simple. E.g.
[ReceiverTest.testUnwrap](src/test/java/net/lshift/bletchley/mail/ReceiverTest.java).

No for the hard part: writing code to generate a message that satisfies our
secrecy criteria:

* [Sender](src/main/java/net/lshift/bletchley/mail/Sender.java)

