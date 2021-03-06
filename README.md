# Pubnub-JTools
> Pubnub-JTools is collection of useful java tools for Pubnub which consists of three modules:

- **pubnub-jtools-pusher** is enterprise quality tool which useful for publish messages from high-load backend servers.
- **pubnub-jtools-benchmark** is collection of benchmarks for Pubnub service.

## pubnub-jtools-pusher
### Motivation to fork official Punub library
The [official Pubnub java library](https://github.com/pubnub/java) was designed to be used to everywhere: java ME, android, GWT, java SE, blackberry, 
but there are big differences between sending 5-10 messages per hour to 2-3 consumers from a smartphone and sending 100-200 messages per second from big server to tens thousands consumers.
The main architectural mistake in the official Pubnub library - is mixing heavy-weight and light-weights entities in single place, instance of "com.pubnub.api.Pubnub" object holds
heavy-weight resources like threads and HTTP connections and in same time it holds light-weights entities like publisher_key, subscriber_key, encryption_key.
Due to any applications always limited by count of threads and tcp connections, if you are using official java library for big servers you always lose scalability in the following scenarios:
* For example you need to send encrypted messages to one million different clients, and by security reasons you should use different encryption keys for each client. 
It is impossible to do with official Pubnub library because encryption_key is part of "com.pubnub.api.Pubnub" instance and in same time each instance of "com.pubnub.api.Pubnub" holds two thread and two HTTP connection, as result your application will crashed in case of two million open connection or two million threads.   
* The encryption is not single point when official library lose scalability. If you use mobile notification clouds through Pubnub like SCM or APNS, then maybe your know that application certificate is bound to publisher_key/subscriber_key one-to-one relation,
so you need to create one instance of "com.pubnub.api.Pubnub" per one application certificate as result you lose scalability in same manner as your lose scalability with encryption in case of you have many versions of your application for many mobile platforms.
You can argue that this is an unconvincing argument, for example if you have 200 application that you need only for 400 threads and 400 http connections and this is not a big deal for modern servers,
but brother do not forget that if you using on worker thread per one Pubnub instance then you can achieve only 10-20 RPS, 
so to close problem with high outgoing rate for example 200 RPS you should configure Pubnub library for 10-20 worker thread, as result you will get 2000-4000 worker thread for 200 applications.
 
In opposite to official library pubnub-jtools-pusher never mixes heavy-weight and light-weights entities in same place, as result it has no scalability bottlenecks.
In additionally to scalable architecture pubnub-jtools-pusher provides:
* Does not restrict you by concrete JSON serialization library, moreover pusher does not known anything about JSON. 
* Provides configurable way to sending requests in synchronous mode. Official library missed this feature. There is two implementation for synchronous publishing:
  * Implemented across [JDK HttpURLConnection](http://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html). This is well tested and recommended implementation.
  * Implemented across [Apache Http Client](https://hc.apache.org/httpcomponents-client-ga/). In theory you can achieve better results with this transport, but it is too hard to tune for robust results.
* Provides true way for sending requests in asynchronous mode. It can be surprised, however official Pubnub library provides only API which looks as async, but internal implementation is totally synchronous and uses blocked operations.
In opposite, Pubnub JTools provides true NIO implemented across [Apache Async Http Client](https://hc.apache.org/httpcomponents-asyncclient-dev/).
* You free to configure way to send HTTP requests:
  * It is possible to configure synchronous sending via implementation of [Transport](https://github.com/vladimir-bukhtoyarov/pubnub-jtools/blob/master/pusher/src/main/java/com/github/pubnubjtools/pusher/transport/Transport.java) interface. 
  * It is possible to configure asynchronous sending via implementation of [AsyncTransport](https://github.com/vladimir-bukhtoyarov/pubnub-jtools/blob/master/pusher/src/main/java/com/github/pubnubjtools/pusher/transport/AsyncTransport.java) interface.
* Java 8, yeh baby, lets use power of CompletableFeature together with NIO.
* Built-in monitoring. This library collects useful statistics about publishing rate, publishing latency(includes percentiles), count of failed publishes. Statistics is optional and turned-off by default.
library provides statistic capturing implementation based on [Metrics Core](https://dropwizard.github.io/metrics/3.1.0/manual/core/), and you are free to provide uour custom implementation of statistics capturing via providing implementation of interface [Monitoring](https://github.com/vladimir-bukhtoyarov/pubnub-jtools/blob/master/pusher/src/main/java/com/ringcentral/pubnubjtools/pusher/monitoring/Monitoring.java)

### pubnub-jtools-pusher state
Version 1.0.0 is released. This release contains only features which used and extremely tested by The [Ringcentral](http://www.ringcentral.com/) company. 
At the moment pubnub-jtools-pusher provides three operation:
* Sending unencrypted message.
* Sending encrypted messages.
* Binding/unbinding device token(registrationId) to Pubnub channel for APNS, GCM, MPNS.

Feel free to use [pubnub-jtools issue tracker](https://github.com/vladimir-bukhtoyarov/pubnub-jtools/issues) to ask for implementation any functionality which currently missed for your needs,
but remember that implementation any functionality related to reading messages from Pubnub is out of scope of pubnub-jtools-pusher, because this library is addressed strongly for publishing.
 
### Get pubnub-jtools-pusher library
 
#### By direct link
[Download compiled jar, sources, javadocs](https://github.com/vladimir-bukhtoyarov/pubnub-jtools/releases/tag/1.0.0)
 
#### You can build pubnub-jtools-pusher from sources
```bash
git clone https://github.com/vladimir-bukhtoyarov/pubnub-jtools.git
cd pubnub-jtools/pusher
mvn clean install -DpublisherKey=... -DsubscriberKey=... -DsecretKey=...
```
 
#### You can add pubnub-jtools-pusher to your project as maven dependency 
The pubnub-jtools-pusher library is distributed through [Bintray](http://bintray.com/), so you need to add Bintray repository to your `pom.xml`
```xml
      <repositories>
          <repository>
              <id>jcenter</id>
              <url>http://jcenter.bintray.com</url>
          </repository>
      </repositories>
```
 
Then include pubnub-jtools-pusher as dependency to your `pom.xml`
 
```xml
<dependency>
     <groupId>com.ringcentral.pubnub-jtools</groupId>
     <artifactId>pusher</artifactId>
     <version>1.0.0</version>
</dependency>
```

#### Get started with pubnub-jtools-pusher
*TBD*

## pubnub-jtools-benchmark
Is under development and is not ready for production usage


## Licenses
1. Same amount of code related to cryptography are copied form official Pubnub library as is, so you should read [PUBNUB license](https://github.com/pubnub/java/blob/master/PUBNUB-LICENSE) before using.
2. Pubnub-JTools is licensed by [Apache license](http://www.apache.org/licenses/LICENSE-2.0)
