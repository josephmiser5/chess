# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[![Sequence Diagram](10k-architecture.png)]
(https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUmB0KOAAGsYABJNBUYDIDgwABmqAQHGu1VEpQqN2eMAAQsAOASiWAAKIADxU2AIBS51Eod3uxXM9UCTicw3Gc3UwHs83qYwlUG8dRgemZKMJajAULMnEVVFEd0qsnkShU6nqhrAAFUBhi3h8UKSPYplGpVG6jDpagAxJCcGCBygRy2WGAh2ZickoKkybR6AwwSjmzARr3RlWVREqepVqPqaWy+WFeuGXl16oCpve1StlBy-Id3vw1XIdUwACsWp1Yz1qgNRuWpvN9QxHDUICgxHbMAgLLLZugxId6E5aAg6MPt370dqMK4yccwGw2CnMcgMEZCDLnDYBAyZgBo+aFmmsCbEgYDxNmAyhr+CAUhwWYoFKaIaA+6i1lUIgNqmAwRs6rrdnhNT1AoyEFqh7RUugQ4jgq-ITpUaoYBqTjBAuS4rgsa6nhajIoVm8iUugF5OthMZkZ29RoD4CAICRKixtJtS7gW6KQcGRHaOG2jVjh9zGLUCgcMykHEZ2fLcgRhpoJQcgoO0EDiWgGKkp2amGc2qgaRS6IKD4sEYsAIXxBGBmen5sameZzLBbB1l2V29zPnC9TYmieJqIpWAZcqsnji8EwIbmSzGsCyzhbBrnuZVyyXM6RWTiUYD1OE86jGVUwVTAVXfLV8T1RJA1NZJnKeN4fj+NA7CGjECZwBK0hwAoMAADIQFkhTscwCIlY0rQdN0PQGOoo4LjmnyQiCvz-IC+xXC18KVIVFqvOVt37Pd+h-DsXzQo8cKxnJMAIDtyYYttu1inapLgdSdIMkyrLspyNm2UqFrCqKKC2jkjHtq9uH7Zx2o9bxhr8Sagn1FaMA2uKk0qV27q+QOvooAGQY3WGlaczWJnxkmKZWdombwX1cxkhS1KQTA0GwdL7y5kYEBqGgADkzDoWigsxQOuHgwpSlsybR3+tMEXQEgABeKAcMTo6k2R5MwJqACMPHRnxxrrtA9Q+DbsF247uxXJepjSZb+EoPUEvyBbh3x-UNRICylhNP9j0Yg9OyknLBbI2gIDQCi4AwDnWw7CnqcUamofxOHTsu8x45k1OHGe04PtU37NMB-T2bN63keOpjqXYy69noE56KjR5XmpT5RuPjAmnOUl8RhRFUWG5Gxsi5RFkwDvKXx7hH31LDyYZKo+WYB9ceN19MvzONv01RFS+Nc9bs2rTi6pTd+atPhf2qmMYaf9IGs2mr4AIXgUDoBiHERIyDUGw18FgfaYMjoNGkBKTaEp2gSjOhdVQV0x57ntnkAo9QAA8MC3LoHKG7coN9fy-1YYUF+M8kQQx2jgmGwiQrwxJCnco6kYB+h3nvOqvDopH2FuUUyMAxaJX3pLHQWYWHuWLoWFGjIkDMn0egQ+RkZICIImbZSWNygsVqNbYa4925jhxt2cm3tfb6iHgJDco9XG0KdqzaS3ZwYX20GzCoMjtzcCChFBRI0lGWL8hUdR0gUAJMMFE5ODiuHYPEXlBABUQaUAiSVEYVwWJeO7h1EBOoo5OgQbNFEzJ-DYGTNSTaaIYAAHFcwaDwQ3AUDR+mkLOvYXMwxgkO3oWgJh5i0DsNehULhyzn7lKgDYhOEM0SDL1BiQ5agJFgBXvHWJQsfSyJ5vI5ZyirEZPjBo5MWjko6L0TwgxSNaT0hMWY75FjY6VLTjAOxMSZ7PBcbbEJzsZTDhJrUio3i+6+OXP4umgSQ5zIjmE65qhQWzz2Xk4AMTpEEu5mAKiCATmqE8mkgczz6iaKQv+E5xFwm7NvmiRhHLtDsIKdsnlOQ6UPyfh9IlzwRjLGmXqfiDRxhypQDSaQ-EvbhGCIEEEmx4gwRQJBUMexvjJFAJSQ1FVFjfGVQAOUtWMKEMBOg1PHHU9qnVuoyrGMq1QCqlW5lVeqzV2rli6v1Ran6JqEBmojUaB1IJbX2sdc6yepgvCIP8BwAA7G4JwKAnAxAlMEOAK0ABs8BAqGBOTAIo9T8E43qM0NoXRejKtmbC+Z7YllApWQuRNt0Jous8Zw4V3DFHuTWP6uYdqB0AMlY41K9Qt7ohORiOAlaTlnMRvLP5qNTHo2yVPS5ULcYijOe4wBbF6kU3Rf7AJQdLQiiZgTFmzSj3EquevG5cikkPMZdGZlryUykqlps35xi0abJBdy8Fil7HTxPc4mhDs24IqYh4mobr1Q+IHn41cWKH04o7Xit9-71BEsEaS8lMjl0oFXf2gW4S1EvISgM3MGYMSOU2LIgN0g1hHDlFmOA8RownMeekmDyrVU1qFbCAU66tJ0dzOK0pWy5M7MQzAL1Um1X1A1VqmAQ7MMouvTARpoxZW8aDQZ0jrSAiWGyZDbjsQkAJDAA5pSEBuMAClgJoDY3MGIpqQCUlre1etjdmj+hbT0NtyG6FdpgMwnt5QdTrGjQ5qAcAICQygHsAA6iwGkZCeiCk2goOAABpa1Vm9PBsM2skd6n6jLMnel4AmXsu5YK0VkrZWKvVYTbVmA+nAgNfneDAAVn51dvnkybpfQjQxpc93MjZIeyFC6G1CjPYtomaGkWd3dqZzUoDFyD3w4HC0jNmZ2lZjZClX7-K3Opb+nt4mmXMZZW88+2j5CgZ7ct3dAKx0pIMdBrbxL5Jwc204mFYc4UXuReUVF-ddQXdpld4O8XQmka5ZDyjf2yUPZkX6ejvGPsAa+0B5k-L-u6J43MVVphfnVo4JrVQOs9YYRyGR6xPYwUQu8sVbbeNz0HddixLu7re5neppdkeN29v2jxwSijBE6fE+ngTgiIARMhZgJ+DrlAuvQBgKoSGzBsiHmwKg5MAWUDEV+ab2AwkaKiXohhnkmmqIiTou5JHrqTMy81NxCz528OY5Hm7kUHuGoNejiTyl2ZsBaBXbmY57H9J88A0mJSDuMzl1y0rGCcF+aba4XNtAC3H6qclZp6pazg-AM9bZ9Ns0vAdbQa5jBUAu+IALK7j8yAQALJrSMgXkWiEkLIWdYwjWb5qaeDsnXCcQDcDwNIHQa6N9QAjCWFAW7qPJ-iTzR32gGVMfUQPvAbKC-aDWEnYAawqO-P3-oQw5ZoA55g9JQPx7Ic+wCV-9jMUdTM5w5cMdh5Aktwdw9x0NzxVcnt1cE4K9hUb899iwP8VMyl1MpVPojMKlm8e5zNG9LwgA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
