
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
[ConvertUtils](https://github.com/lshift/bletchley/blob/master/src/main/java/net/lshift/spki/convert/ConvertUtils.java)
which needs a [ReadInfo](https://github.com/lshift/bletchley/blob/master/src/main/java/net/lshift/spki/convert/ReadInfo.java)
defined [here](src/main/java/net/lshift/bletchley/mail/Actions.java), just by
listing the action classes.

For building secure systems, It's just as important a part of Bletchley that it
does this marshalling and unmarshalling for you as it is that does cryptography
for you. The encoding is designed to offer a smaller attack surface than other
competing encodings like XML, and critically, from a cryptography point of
view it's a canonical representation. That is, there is only one way to
represent a given Java object. This eliminates padding attacks and means you
can think of a signature as signing the java object rather than signing a
representation of it, which makes it easier to reason about the system.

The thing Bletchley helps you most with is filtering out data you don't trust,
so code for receiving messages is really simple. The receivers are:

* [Server](src/main/java/net/lshift/bletchley/mail/Server.java)
* [Receiver](src/main/java/net/lshift/bletchley/mail/Receiver.java)

The receivers use the [inference engine](https://github.com/lshift/bletchley/blob/master/src/main/java/net/lshift/spki/suiteb/InferenceEngine.java)
to extract the actions which they are able to decrypt, and which they trust.
For the sake of simplicity in the example, servers don't need to establish
trust in the sender to process actions from the message, so they use
processUntrusted, which you would rarely do. Clients use processUntrusted
to work out who the message claims to be from, and then verifies that by
processing the message again, but this time, the engine is configured only
to trust the claimed sender.

It makes sense to define receivers first: You will have quite a lot of
flexibility in constructing a message, but your receivers will tend to be
very stable. It's worth writing tests which construct the simplest possible
message that will satisfy them, because that's generally very simple. E.g.
[ReceiverTest.testUnwrap](src/test/java/net/lshift/bletchley/mail/ReceiverTest.java).

Now for the hard part: writing code to generate a message that satisfies our
secrecy criteria:

* [Sender](src/main/java/net/lshift/bletchley/mail/Sender.java)

This is documented in the code. I'd recommend you check out the project and look
at it in your favourite IDE, so you have easy access to the javadoc of Bletchley.
