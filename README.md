# loom-examples

-Xms1G -Xmx1G -XX:StartFlightRecording=duration=60s,filename=recording-vt-sync.jfr


curl --parallel --parallel-immediate $(printf 'http://localhost:8080/io %.0s' {1..5})

curl --parallel --parallel-immediate --parallel-max 1000  $(printf 'http://localhost:8080/io %.0s' {1..100})

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/syncThread %.0s' {1..5})

curl -H "Authorization: nn-jwt" http://localhost:8080/threadLocal

curl -H "Authorization: nn-jwt" http://localhost:8080/scopedValue

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/hello-stream %.0s' {1..5})

