# Hibernate ORM ORA-17068 Reproducer (No Spring)

## Problem

The test reproduces:

```
ORA-17068: Invalid arguments in call
```

using only Hibernate ORM + Oracle JDBC driver (no Spring context).

## Likely trigger combination

* Oracle JDBC driver + generated keys path on update
* `@Generated(event = EventType.UPDATE)` on entity field
* `@JdbcTypeCode(SqlTypes.JSON)` on entity field
* SQL comments enabled (`hibernate.use_sql_comments=true`)
* persist, then update the same entity

## Setup

1. Use Java 21.
2. Configure Oracle connection in `src/main/resources/application.properties`.
3. Ensure the DB user can create tables. The test automatically creates `CAR` if it does not exist.

## Run

```bash
./gradlew test --tests "*CarRepositoryTest" --rerun-tasks --info
```

The test passes only if `ORA-17068` is thrown and detected in the exception cause chain.

## StackTrace
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

## Current environment

* Oracle Database: 23.x
* JDK: 21
* Hibernate ORM: 7.1.8.Final
* Oracle JDBC (`ojdbc11`): 23.9.0.25.07





