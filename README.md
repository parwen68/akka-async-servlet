akka-async-servlet
==================

This is an example of using Akka in a Servlet version 3.1 that simulates slow content delivery.

Usage
=====
Just run with `mvn jetty:run-forked`

Use `curl -N http://localhost:8080/AsyncServlet` to call the servlet

To load test using for instance [wrk](https://github.com/wg/wrk): ` wrk -c 100 -t 100 -d 120 --latency --timeout 30 http://localhost:8080/AsyncServlet`

