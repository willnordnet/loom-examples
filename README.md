# loom-examples

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/thread %.0s' {1..5})

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/syncThread %.0s' {1..5})

curl -H "Authorization: nn-jwt" http://localhost:8080/threadLocal

curl -H "Authorization: nn-jwt" http://localhost:8080/scopedValue

curl --parallel --parallel-immediate $(printf 'http://localhost:8080/hello-stream %.0s' {1..5})

