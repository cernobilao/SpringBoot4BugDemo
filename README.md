# Spring Boot 4.0.0 Possible Bug Demo

### Exception

```
Caused by: org.hibernate.exception.GenericJDBCException: Could not prepare statement [ORA-17068: Invalid arguments in call]
```
### Cause

Seems to be combination of:
* Oracle Database Driver
* application property `spring.jpa.properties.hibernate.use_sql_comments=true`
* entity annotation @Generated,
* entity annotation @JdbcTypeCode(SqlTypes.JSON),
* call entity save method multiple times


### How to use this demo

* Make sure to set correct Java SDK version (21) for the project.
* Set the your oracle database connection properties in application.properties file.
* Create table in your oracle database:
```sql
CREATE TABLE "CAR" 
(
    "ID" NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL ENABLE,
    "SPECS" VARCHAR2(36 CHAR), 
    "MANUFACTURE_DATE" DATE, 
    CONSTRAINT "CAR_PK" PRIMARY KEY ("ID") ENABLE
);
```
* Run CarRepositoryTest.

### StackTrace
```
Caused by: org.hibernate.exception.GenericJDBCException: Could not prepare statement [ORA-17068: Invalid arguments in call
https://docs.oracle.com/error-help/db/ora-17068/] [/* update for cz.cernobilao.exception.demo.entity.Car */update car set specs=? where id=?]
	at org.hibernate.exception.internal.StandardSQLExceptionConverter.convert(StandardSQLExceptionConverter.java:39)
	at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:115)
	at org.hibernate.engine.jdbc.internal.MutationStatementPreparerImpl$StatementPreparationTemplate.prepareStatement(MutationStatementPreparerImpl.java:112)
	at org.hibernate.engine.jdbc.internal.MutationStatementPreparerImpl.prepareStatement(MutationStatementPreparerImpl.java:78)
	at org.hibernate.id.insert.GetGeneratedKeysDelegate.prepareStatement(GetGeneratedKeysDelegate.java:83)
	at org.hibernate.engine.jdbc.mutation.internal.ModelMutationHelper.delegateStatementPreparation(ModelMutationHelper.java:129)
	at org.hibernate.engine.jdbc.mutation.internal.ModelMutationHelper.lambda$standardPreparation$0(ModelMutationHelper.java:119)
	at org.hibernate.engine.jdbc.mutation.internal.PreparedStatementDetailsStandard.resolveStatement(PreparedStatementDetailsStandard.java:92)
	at org.hibernate.id.insert.GetGeneratedKeysDelegate.performMutation(GetGeneratedKeysDelegate.java:100)
	at org.hibernate.engine.jdbc.mutation.internal.MutationExecutorSingleNonBatched.performNonBatchedOperations(MutationExecutorSingleNonBatched.java:45)
	at org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:66)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.doStaticUpdate(UpdateCoordinatorStandard.java:774)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.performUpdate(UpdateCoordinatorStandard.java:317)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.update(UpdateCoordinatorStandard.java:235)
	at org.hibernate.action.internal.EntityUpdateAction.execute(EntityUpdateAction.java:172)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:646)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:513)
	at org.hibernate.event.internal.AbstractFlushingEventListener.performExecutions(AbstractFlushingEventListener.java:378)
	at org.hibernate.event.internal.DefaultFlushEventListener.onFlush(DefaultFlushEventListener.java:39)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:140)
	at org.hibernate.internal.SessionImpl.doFlush(SessionImpl.java:1447)
	at org.hibernate.internal.SessionImpl.managedFlush(SessionImpl.java:488)
	at org.hibernate.internal.SessionImpl.flushBeforeTransactionCompletion(SessionImpl.java:2325)
	at org.hibernate.internal.SessionImpl.beforeTransactionCompletion(SessionImpl.java:2033)
	at org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl.beforeTransactionCompletion(JdbcCoordinatorImpl.java:394)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl.beforeCompletionCallback(JdbcResourceLocalTransactionCoordinatorImpl.java:167)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commitNoRollbackOnly(JdbcResourceLocalTransactionCoordinatorImpl.java:249)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commit(JdbcResourceLocalTransactionCoordinatorImpl.java:243)
	at org.hibernate.engine.transaction.internal.TransactionImpl.commit(TransactionImpl.java:90)
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:553)
	... 13 more
Caused by: java.sql.SQLException: ORA-17068: Invalid arguments in call
https://docs.oracle.com/error-help/db/ora-17068/
	at oracle.jdbc.driver.AutoKeyInfo.getTableNameForUpdateStmt(AutoKeyInfo.java:809)
	at oracle.jdbc.driver.AutoKeyInfo.getTableName(AutoKeyInfo.java:755)
	at oracle.jdbc.driver.PhysicalConnection.prepareStatement(PhysicalConnection.java:5593)
	at com.zaxxer.hikari.pool.ProxyConnection.prepareStatement(ProxyConnection.java:363)
	at com.zaxxer.hikari.pool.HikariProxyConnection.prepareStatement(HikariProxyConnection.java)
	at org.hibernate.engine.jdbc.internal.MutationStatementPreparerImpl$3.doPrepare(MutationStatementPreparerImpl.java:76)
	at org.hibernate.engine.jdbc.internal.MutationStatementPreparerImpl$StatementPreparationTemplate.prepareStatement(MutationStatementPreparerImpl.java:101)
	... 40 more
```

### Environment

* Oracle Database Version: 23.0.0.0.0
* JDK Version: ms-21.0.8
* Spring Boot Version: 4.0.0
* Hibernate Version: 7.1.8.Final
* ojdbc11 Version: 23.9.0.25.07





