# loom-examples

A demo application showcasing Project Loom features in Java, including virtual threads, structured concurrency, and
performance monitoring.

## Running the Application

Start the application with 1GB heap and enable Java Flight Recorder for 60 seconds:

```bash
-Xms1G -Xmx1G -XX:StartFlightRecording=duration=60s,filename=recording-vt.jfr
```

## Testing Endpoints

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/io %.0s' {1..5})

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/prime %.0s' {1..20})

## Analyzing Results

After running tests, analyze the JFR recording file recording-vt.jfr using:
JDK Mission Control (JMC)
Command line: jfr print --events jdk.VirtualThreadPinned recording-vt.jfr
