# Stock Quote Generator

This project generates a conStant flow of messages that are produced to a kafka topic and simulate 
instrument ticks from the market. 
I've build this project to help me with my studies of kafka in the domain of Bank Securities. 

Current implementation version which will be tagged as 1.0 uses the following approach: 

1. Loads CSV files with metadata of the traded stocks in the AMEX, NASDAQ and NYSE exchanges. This files
were downloaded from the NASDAQ website and hence they represent real information of the traded instruments
at the moment where those files got downloaded. The prices for those stocks are totally fake / generated 
randomically by this app.

2. Once the CSV metadata is loaded it is kept in memory in a HashMap and it's keys used to randomically 
generate values for it's stocks. 

3. The meta data of each instrument with it's randomically generated values are then joined in memory and
the result object is serialized to json and sent to a kafka topic. 

This application, as noted after a few tests is capable of generating thousands of ticks per second without 
too much overhead which makes it great for kafka playgrounds! 


## Configurations

The generator and kafka endpoints where the quotes should be sent can be configured by properties in the
application.yml file of this project.

## Run the project

#### Pre -reqs
 - java 8
 - recent version of docker
 - recent version of docker-compose
 - maven

#### Use docker-compose / maven

There is, for convenience a docker-compose file with the full infra required to run a test demo of this
application, with recent versions of docker and docker-compose installed the infra-structure can be run 
from the root folder of this application with: 

`docker-compose up -d` and logs from the containers can be followed using `docker-compose logs -f`

Once the required infra is running the application can be build and run with maven. It's a spring boot
application so once the project is checked out one can build and 
[run it in multiple different ways](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html) 
like from an IDE or using command line: 

`mvn clean install && mvn spring-boot:run`

Once the application is started it immediately starts to produce messages to kafka topic based on the configurations.

The docker compose file has by default portainer application to help visualize the docker infra-structure and 
also a kafka-manager console to help to visualize the Kafka broker cluster that is started and by default contains 3 brokers. 

# License

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
Copyright (c) 2018 - Maia, M. P. A.