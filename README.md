## Kinoko
Kinoko is a server emulator for the popular mushroom game.


## Setup
Basic configuration is available via environment variables - the names and default values of the configurable options are defined in [ServerConstants.java](src/main/java/kinoko/server/ServerConstants.java) and [ServerConfig.java](src/main/java/kinoko/server/ServerConfig.java).

> [!NOTE]
> Client WZ files are expected to be present in the `wz/` directory in order for the provider classes to extract the required data. The required files are as follows:
> ```
> Character.wz
> Item.wz
> Skill.wz
> Morph.wz
> Map.wz
> Mob.wz
> Npc.wz
> Reactor.wz
> Quest.wz
> String.wz
> Etc.wz
> ```

#### Java setup
Building the project requires Java 21 and maven.

```bash
# Build jar
$ mvn clean package
```


#### Database setup
It is possible to use either CassandraDB or ScyllaDB, no setup is required other than starting the database.
```bash
# Start CassandraDB
$ docker run -d -p 9042:9042 cassandra:5.0.0

# Alternatively, start ScyllaDB
$ docker run -d -p 9042:9042 scylladb/scylla --smp 1
```

#### Docker setup
Alternatively, docker can be used to build and start the server and the database using the [docker-compose.yml](docker-compose.yml) file. The requirements are as follows:
- docker : required for building and running the server and database containers
- cqlsh : required for the health check for the database container

```bash
# Build and start containers
$ docker compose up -d
```
