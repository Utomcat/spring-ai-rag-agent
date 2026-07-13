package com.ranyk.spring.ai.rag.common.constant;

/**
 * CLASS_NAME: DatabaseType.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用于设置当前系统支持的数据库类型
 * @date: 2026-06-26
 */
@SuppressWarnings("unused")
public enum DatabaseTypeEnum {
    /**
     * 关系型数据库(RDBMS) - MySQL
     */
    MYSQL,
    /**
     * 关系型数据库(RDBMS) - PostgreSQL
     */
    POSTGRESQL,
    /**
     * 关系型数据库(RDBMS) - Oracle
     */
    ORACLE,
    /**
     * 关系型数据库(RDBMS) - SQL Server
     */
    SQL_SERVER,
    /**
     * 关系型数据库(RDBMS) - MariaDB
     */
    MARIADB,
    /**
     * 关系型数据库(RDBMS) - SQLite
     */
    SQLITE,
    /**
     * 关系型数据库(RDBMS) - DB2
     */
    DB2,
    /**
     * 关系型数据库(RDBMS) - H2
     */
    H2,
    /**
     * 关系型数据库(RDBMS) - HSQLDB
     */
    HSQLDB,
    /**
     * 关系型数据库(RDBMS) - Derby
     */
    DERBY,
    /**
     * 非关系型数据库(NOSQL) - 键值存储 - Redis
     */
    REDIS,
    /**
     * 非关系型数据库(NOSQL) - 键值存储 - Memcached
     */
    MEMCACHED,
    /**
     * 非关系型数据库(NOSQL) - 键值存储 - DynamoDB
     */
    DYNAMODB,
    /**
     * 非关系型数据库(NOSQL) - 键值存储 - Riak
     */
    RIAK,
    /**
     * 非关系型数据库(NOSQL) - 键值存储 - etcd
     */
    ETCD,
    /**
     * 非关系型数据库(NOSQL) - 文档数据库 - MongoDB
     */
    MONGODB,
    /**
     * 非关系型数据库(NOSQL) - 文档数据库 - CouchDB
     */
    COUCHDB,
    /**
     * 非关系型数据库(NOSQL) - 文档数据库 - Couchbase
     */
    COUCHBASE,
    /**
     * 非关系型数据库(NOSQL) - 文档数据库 - RethinkDB
     */
    RETHINKDB,
    /**
     * 非关系型数据库(NOSQL) - 文档数据库 - RavenDB
     */
    RAVENDB,
    /**
     * 非关系型数据库(NOSQL) - 列族数据库 - Cassandra
     */
    CASSANDRA,
    /**
     * 非关系型数据库(NOSQL) - 列族数据库 - HBase
     */
    HBASE,
    /**
     * 非关系型数据库(NOSQL) - 列族数据库 - ScyllaDB
     */
    SCYLLADB,
    /**
     * 非关系型数据库(NOSQL) - 列族数据库 - ClickHouse
     */
    CLICKHOUSE,
    /**
     * 非关系型数据库(NOSQL) - 图数据库 - Neo4j
     */
    NEO4J,
    /**
     * 非关系型数据库(NOSQL) - 图数据库 - JanusGraph
     */
    JANUSGRAPH,
    /**
     * 非关系型数据库(NOSQL) - 图数据库/多模型数据 - ArangoDB
     */
    ARANGODB,
    /**
     * 非关系型数据库(NOSQL) - 图数据库 - TigerGraph
     */
    TIGERGRAPH,
    /**
     * 非关系型数据库(NOSQL) - 图数据库 - NebulaGraph
     */
    NEBULAGRAPH,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Milvus
     */
    MILVUS,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Pinecone
     */
    PINECONE,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Weaviate
     */
    WEAVIATE,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Chroma
     */
    CHROMA,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Qdrant
     */
    QDRANT,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Faiss
     */
    FAISS,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - Vespa
     */
    VESPA,
    /**
     * 非关系型数据库(NOSQL) - 向量数据库 - LanceDB
     */
    LANCEDB,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - InfluxDB
     */
    INFLUXDB,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - TimescaleDB
     */
    TIMESCALEDB,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - OpenTSDB
     */
    OPENTSDB,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - Prometheus
     */
    PROMETHEUS,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - TDengine
     */
    TDENGINE,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - QuestDB
     */
    QUESTDB,
    /**
     * 非关系型数据库(NOSQL) - 时间序列数据库 - KairosDB
     */
    KAIROSDB,
    /**
     * 非关系型数据库(NOSQL) - 搜索引擎数据库 - Elasticsearch
     */
    ELASTICSEARCH,
    /**
     * 非关系型数据库(NOSQL) - 搜索引擎数据库 - Solr
     */
    SOLR,
    /**
     * 非关系型数据库(NOSQL) - 搜索引擎数据库 - MeiliSearch
     */
    MEILISEARCH,
    /**
     * 非关系型数据库(NOSQL) - 多模型数据 - CosmosDB
     */
    COSMOSDB,
    /**
     * 非关系型数据库(NOSQL) - 多模型数据 - OrientDB
     */
    ORIENTDB,
    /**
     * 非关系型数据库(NOSQL) - 搜索数据库 - FoundationDB
     */
    FOUNDATION
}
