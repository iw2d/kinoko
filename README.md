## Kinoko

Kinoko is a server emulator for the popular mushroom game.

## Setup

Basic configuration is available via environment variables - the names and default values of the configurable options
are defined in [ServerConstants.java](src/main/java/kinoko/server/ServerConstants.java)
and [ServerConfig.java](src/main/java/kinoko/server/ServerConfig.java).

### Client Downloads
[GMS V95 Client Setup](https://ia600809.us.archive.org/19/items/GMSSetup93-133/GMS0095/GMSSetupv95.exe)  
[GMS v95.1 Localhost](https://mega.nz/file/dWIgyR4I#6cDN_ycLLiFtad07Eby3UfjdY3TqGI65g6X-xEqlmds)



> [!NOTE]
> Client .WZ files are expected to be present in the `wz/` directory in order for the provider classes to extract the
> required data. The required files are as follows:
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

#### Environment setup
Before doing any Docker or Database Setup
You should:
1. Make a copy of `.env.example` and rename it to `.env`
2. Adjust the ENV Variables to the database server you will be using.


#### Database setup

It is possible to use either CassandraDB, ScyllaDB, or Postgres.


```bash
# Start CassandraDB
$ docker-compose up -d cassandra
# OR 
$ docker run -d -p 9042:9042 cassandra:5.0.0

# Alternatively, start ScyllaDB
$ docker run -d -p 9042:9042 scylladb/scylla --smp 1

# Alternatively, start PostgreSQL 
$ docker-compose up -d postgres
# OR (CHANGE THE PASSWORD)
$ docker run -d --name postgres_kinoko -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -e POSTGRES_INITDB_ARGS="--auth-host=scram-sha-256 --auth-local=scram-sha-256" -e POSTGRES_DB=kinoko -p 5432:5432 -v "${PWD}\src\main\java\kinoko\database\postgresql\setup\init.sql:/docker-entrypoint-initdb.d/init.sql:ro" postgres:16

Important: If you are using PostgreSQL on a local machine (not using a dockerized server), 
make sure that you have any undockerized postgresql server offline. This can cause conflicts. 

```

You can use [Docker Desktop](https://www.docker.com/products/docker-desktop/) or WSL on Windows.

#### Docker setup

Alternatively, docker can be used to build and start the server and the database using
the [docker-compose.yml](docker-compose.yml) file. The requirements are as follows:

- docker : required for building and running the server and database containers
- cqlsh : required for the health check for the database container

```bash
# Build and start containers

# Cassandra & Server (Recommended, default)
$ docker compose up -d cassandra server

# Postgres & Server (Alternative)
$ docker compose up -d postgres server

```
