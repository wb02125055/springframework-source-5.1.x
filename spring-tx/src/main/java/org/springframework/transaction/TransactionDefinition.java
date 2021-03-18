/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.transaction;

import org.springframework.lang.Nullable;

import java.sql.Connection;

/**
 * Interface that defines Spring-compliant transaction properties.
 * Based on the propagation behavior definitions analogous to EJB CMT attributes.
 *
 * <p>Note that isolation level and timeout settings will not get applied unless
 * an actual new transaction gets started. As only {@link #PROPAGATION_REQUIRED},
 * {@link #PROPAGATION_REQUIRES_NEW} and {@link #PROPAGATION_NESTED} can cause
 * that, it usually doesn't make sense to specify those settings in other cases.
 * Furthermore, be aware that not all transaction managers will support those
 * advanced features and thus might throw corresponding exceptions when given
 * non-default values.
 *
 * <p>The {@link #isReadOnly() read-only flag} applies to any transaction context,
 * whether backed by an actual resource transaction or operating non-transactionally
 * at the resource level. In the latter case, the flag will only apply to managed
 * resources within the application, such as a Hibernate {@code Session}.
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see PlatformTransactionManager#getTransaction(TransactionDefinition)
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 */
public interface TransactionDefinition {

	/**
	 * Support a current transaction; create a new one if none exists.
	 * Analogous(类似，相似，[əˈnæləɡəs]) to the EJB transaction attribute of the same name.
	 * <p>This is typically(通常，一般) the default setting of a transaction definition,
	 * and typically defines a transaction synchronization scope.
	 *
	 * 如果当前已经存在着事务，则加入到该事务，如果不存在事务，则创建一个新的事务。
	 *
	 * 使用事务之后，这是事务的默认传播行为。
	 *
	 * 例： service1和service2方法中都声明了事务，默认传播行为为REQUIRED，则在service的调用过程中，当前只存在着一个共享的事务，当有
	 * 	   任何的异常抛出时，所有的操作都会回滚.
	 * @Transctional
	 * public void service() {
	 *     service1();
	 *     service2();
	 * }
	 * @Transactional
	 * service1() {}
	 *
	 * @Transactional
	 * service2() {}
	 */
	int PROPAGATION_REQUIRED = 0;

	/**
	 * Support a current transaction; execute non-transactionally if none exists.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> For transaction managers with transaction synchronization,
	 * {@code PROPAGATION_SUPPORTS} is slightly different from no transaction
	 * at all, as it defines a transaction scope that synchronization might apply to.
	 * As a consequence, the same resources (a JDBC {@code Connection}, a
	 * Hibernate {@code Session}, etc) will be shared for the entire specified
	 * scope. Note that the exact behavior depends on the actual synchronization
	 * configuration of the transaction manager!
	 * <p>In general, use {@code PROPAGATION_SUPPORTS} with care! In particular, do
	 * not rely on {@code PROPAGATION_REQUIRED} or {@code PROPAGATION_REQUIRES_NEW}
	 * <i>within</i> a {@code PROPAGATION_SUPPORTS} scope (which may lead to
	 * synchronization conflicts at runtime). If such nesting is unavoidable, make sure
	 * to configure your transaction manager appropriately (typically switching to
	 * "synchronization on actual transaction").
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
	 *
	 *
	 * A方法调用B方法时。如果A方法中当前已经存在事务，则加入到A方法对应的事务中，否则，将会以无事务的方式执行。
	 * 例： 下述示例中，在service执行时没有事务，所以在service中抛出的异常不会导致service1方法回滚.
	 * public void service() {
	 *     service1();
	 *     throw new RuntimeException();
	 * }
	 * @Transactional(propagation=Propagation.SUPPORTS)
	 * service1() {}
	 *
	 *
	 * 注意：如下示例，如果在service1方法中执行了多次数据库DML操作，中间抛出了异常。service上没开事务，这个时候DML是否生效和数据
	 *      库底层中的defaultAutoCommit属性相关。
	 * (1) defaultAutoCommit=true，则表示自动提交，此时相当于没有事务，异常之前的DML操作都会生效;
	 * (2) 如果defaultAutoCommit=false，此时异常之前的SQL不会生效;
	 * (3) 在service执行的时候如果加入了事务，则service1中的异常会导致所有DML操作都回滚.
	 *
	 * public void service() {
	 *     service1();
	 * }
	 * @Transactional(propagation=Propagation.SUPPORTS)
	 * service1() {
	 *     executeUpdate(sql1); // DML1
	 *     int a = 1 / 0;
	 *     executeUpdate(sql2); // DML2
	 * }
	 */
	int PROPAGATION_SUPPORTS = 1;

	/**
	 * Support a current transaction; throw an exception if no current transaction
	 * exists. Analogous to the EJB transaction attribute of the same name.
	 * <p>Note that transaction synchronization within a {@code PROPAGATION_MANDATORY}
	 * scope will always be driven by the surrounding transaction.
	 *
	 * 如果当前的方法不存在事务，去调用另外一个带事务的方法时，会直接抛出异常。
	 * 示例：如下，在执行service方法的时候，由于当前没有事务，去调用一个带事务的方法，会直接抛出异常
	 * 	     抛出异常之后，有如下的两种情况：
	 * 	(1) 如果数据库底层设置的defaultAutoCommit=true，则service1中将不存在着事务，sql执行将会生效；
	 * 	(2) 如果数据库底层设置的defaultAutoCommit=false，则service1中的sql执行完成之后未提交，sql将不会生效
	 * public void service() {
	 *     service1();
	 *     service2();
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.MANDATORY)
	 * service2() {
	 *     executeUpdate(sql);
	 * }
	 */
	int PROPAGATION_MANDATORY = 2;

	/**
	 * Create a new transaction, suspending the current transaction if one exists.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box
	 * on all transaction managers. This in particular applies to
	 * {@link org.springframework.transaction.jta.JtaTransactionManager},
	 * which requires the {@code javax.transaction.TransactionManager} to be
	 * made available it to it (which is server-specific in standard Java EE).
	 * <p>A {@code PROPAGATION_REQUIRES_NEW} scope always defines its own
	 * transaction synchronizations. Existing synchronizations will be suspended
	 * and resumed appropriately.
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 *
	 * 如果当前存在事务，则暂停当前的事务，重新创建一个新的事务（如果当前没有事务，则直接创建就行）。具体做法是：将当前事务封装到一个
	 * 实体中，然后去创建一个新的事务，新的事务接收这个实体为参数，用于事务的恢复。
	 * 如果当前有事务，经过这个操作之后，就会存在着两个事务，这两个事务之间没有任何依赖关系，可以实现新事务回滚，外部事务可以继续执行。
	 *
	 * 示例：如下示例中，当前执行的service方法中存在着事务，然后去调用service2的时候，service2中使用了REQUIRES_NEW，所以会重新创建
	 * 		一个新的事务。而在service2中抛出了异常，将会导致service2中的操作都被回滚，但是service2中的异常在service方法中被catch住，
	 * 		所以，service1还是正常执行，提交事务。
	 * 	注意：service中的try catch是必须的，否则，service2中的事务也将会导致service中调用的service1操作也被回滚。
	 *
	 * @Transactional
	 * public void service() {
	 *     service1();
	 *     try {
	 *         service2();
	 *     } catch(Exception e) {}
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.REQUIRES_NEW)
	 * service2() {
	 *     executeUpdate(sql1);
	 *     int i = 1 / 0;
	 *     executeUpdate(sql2);
	 * }
	 */
	int PROPAGATION_REQUIRES_NEW = 3;

	/**
	 * Do not support a current transaction; rather always execute non-transactionally.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box
	 * on all transaction managers. This in particular applies to
	 * {@link org.springframework.transaction.jta.JtaTransactionManager},
	 * which requires the {@code javax.transaction.TransactionManager} to be
	 * made available it to it (which is server-specific in standard Java EE).
	 * <p>Note that transaction synchronization is <i>not</i> available within a
	 * {@code PROPAGATION_NOT_SUPPORTED} scope. Existing synchronizations
	 * will be suspended and resumed appropriately.
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 *
	 * 如果当前方法中存在事务，则当前的事务会被挂起。然后在当前方法中调用的新方法都会以无事务的方式执行。如果其他方法在无事务的环境下
	 * 执行，能否生效将会取决于数据库底层的defaultAutoCommit属性。
	 *
	 * 示例：如下示例，在service方法中存在事务，调用service2方法时，由于service2是NOT_SUPPORTED类型的，所以执行service2的时候将会以
	 * 		无事务的方式运行，此时service1方法中的执行结果将会被回滚，但是service2中的sql1执行结果是否生效取决于数据库底层的defaultAutoCommit
	 * 		属性，如果为true，sql1的执行结果将会生效；如果为false，sql1的执行结果将不会生效。
	 *
	 * @Transactional
	 * public void service() {
	 *     service1();
	 *     service2();
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.NOT_SUPPORTED)
	 * service2() {
	 *     executeUpdate(sql1);
	 *     int i = 1 / 0;
	 *     executeUpdate(sql2);
	 * }
	 */
	int PROPAGATION_NOT_SUPPORTED = 4;

	/**
	 * Do not support a current transaction; throw an exception if a current transaction
	 * exists. Analogous to the EJB transaction attribute of the same name.
	 * <p>Note that transaction synchronization is <i>not</i> available within a
	 * {@code PROPAGATION_NEVER} scope.
	 *
	 * 如果当前存在事务，则会直接抛出异常。如果当前不存在事务，将会以无事务的方式执行。
	 *
	 * 示例：如下示例，service上如果标注异常，则会直接抛出异常，都不会执行。
	 * 		service上不存在事务，而service2方法上标注了NEVER类型的传播行为，此时service2将会以无事务的方式执行，service1和service2中的
	 * 		sql1是否生效取决于数据库底层的defaultAutoCommit属性，如果为true，则都会生效，否则都不生效。
	 *
	 * public void service() {
	 *     service1();
	 *     service2();
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.NEVER)
	 * service2() {
	 *     executeUpdate(sql1);
	 *     int i = 1 / 0;
	 *     executeUpdate(sql2);
	 * }
	 */
	int PROPAGATION_NEVER = 5;

	/**
	 * Execute within a nested transaction if a current transaction exists,
	 * behave like {@link #PROPAGATION_REQUIRED} otherwise. There is no
	 * analogous feature in EJB.
	 * <p><b>NOTE:</b> Actual creation of a nested transaction will only work on
	 * specific transaction managers. Out of the box, this only applies to the JDBC
	 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
	 * when working on a JDBC 3.0 driver. Some JTA providers might support
	 * nested transactions as well.
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 *
	 *
	 *
	 * 嵌套事务：如果当前存在事务，则使用SavePoint将当前的事务状态保存起来，然后底层和嵌套事务使用同一个连接，当嵌套事务出现异常时，将会
	 * 自动回滚到SavePoint这个状态，如果嵌套事务出现的异常被当前事务捕获到之后，当前事务可以继续向下执行，而不会影响到当前事务。但是如果
	 * 当前的事务中出现了异常，将会导致当前事务以及所有的内嵌事务全部回滚。
	 *
	 * Spring中配置事务管理器时，默认不配置的话是不支持嵌套事务的。如果需要启用，需要在事务管理器中指定：
	 * (1) xml方式：
	 * <bean id="dataTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
	 *    <property name="dataSource" ref="dataDataSource" />
	 *    <property name="nestedTransactionAllowed" value="true" />
	 * </bean>
	 * (2) JavaConfig方式：
	 * @Bean
	 * public PlatformTransactionManager transactionManager() throws Exception {
	 * 		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
	 * 		transactionManager.setNestedTransactionAllowed(true);
	 * 		return transactionManager;
	 * }
	 *
	 *
	 * 示例1：如下示例中，由于service2属于service的嵌套事务，所以service2中出现的异常将会导致service2方法中的操作被回滚。但是service1
	 * 		中的操作不会受影响，catch到异常之后将会继续向下执行。
	 * @Transactional
	 * public void service() {
	 *     service1();
	 *     try {
	 *         service2();
	 *     } catch(Exception e) {}
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.NESTED)
	 * service2() {
	 *     executeUpdate(sql1);
	 *     int i = 1 / 0;
	 *     executeUpdate(sql2);
	 * }
	 *
	 *
	 * 示例2：如下示例，在service中开启了事务，然后调用了嵌套事务service2，当service中发生异常时，除了service中本身的操作被回滚之外，
	 * 		service2中的操作也将会被全部回滚。
	 *
	 * 	这是和PROPAGATION_REQUIRES_NEW方式不同的地方，NESTED下的嵌套事务将会受到外部事务中的异常而回滚。而PROPAGATION_REQUIRES_NEW
	 * 	中，内嵌的事务不会因为外部事务异常而回滚。
	 *
	 * @Transactional
	 * public void service() {
	 *     service1();
	 *     service2();
	 *     int i = 1 / 0;
	 * }
	 * service1() {
	 *     executeUpdate(sql);
	 * }
	 * @Transactional(propagation=Propagation.NESTED)
	 * service2() {
	 *     executeUpdate(sql1);
	 *     executeUpdate(sql2);
	 * }
	 *
	 */
	int PROPAGATION_NESTED = 6;


	/**
	 * Use the default isolation level of the underlying datastore.
	 * All other levels correspond to the JDBC isolation levels.
	 * @see java.sql.Connection
	 *
	 * 该种事务隔离级别取决于底层数据库中的默认隔离级别。
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * Indicates(表示) that dirty reads, non-repeatable reads and phantom reads
	 * can occur.
	 * <p>This level allows a row changed by one transaction to be read by another
	 * transaction before any changes in that row have been committed (a "dirty read").
	 * If any of the changes are rolled back, the second transaction will have
	 * retrieved an invalid row.
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 *
	 * 读未提交：允许脏读，不可重复读和幻读
	 */
	int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

	/**
	 * Indicates that dirty reads are prevented; non-repeatable reads and
	 * phantom reads can occur.
	 * <p>This level only prohibits a transaction from reading a row
	 * with uncommitted changes in it.
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 *
	 * 读已提交：避免了脏读，但是允许不可重复读和幻读
	 */
	int ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;

	/**
	 * Indicates that dirty reads and non-repeatable reads are prevented;
	 * phantom reads can occur.
	 * <p>This level prohibits a transaction from reading a row with uncommitted changes
	 * in it, and it also prohibits the situation where one transaction reads a row,
	 * a second transaction alters the row, and the first transaction re-reads the row,
	 * getting different values the second time (a "non-repeatable read").
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 *
	 * 可重复读：避免了脏读和不可重复读，但是允许幻读。MySQL的默认隔离级别
	 */
	int ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;

	/**
	 * Indicates that dirty reads, non-repeatable reads and phantom reads
	 * are prevented.
	 * <p>This level includes the prohibitions in {@link #ISOLATION_REPEATABLE_READ}
	 * and further prohibits the situation where one transaction reads all rows that
	 * satisfy a {@code WHERE} condition, a second transaction inserts a row
	 * that satisfies that {@code WHERE} condition, and the first transaction
	 * re-reads for the same condition, retrieving the additional "phantom" row
	 * in the second read.
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 *
	 * 串行方式的读：避免了脏读，不可重复读和幻读，但是效率低下，基本上所有的并发操作都变成串行操作了。
	 */
	int ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;


	/**
	 * Use the default timeout of the underlying transaction system,
	 * or none if timeouts are not supported.
	 *
	 * 使用该种方式，则事务超时时间取决于底层的事务系统。
	 */
	int TIMEOUT_DEFAULT = -1;


	/**
	 * Return the propagation behavior.
	 * <p>Must return one of the {@code PROPAGATION_XXX} constants
	 * defined on {@link TransactionDefinition this interface}.
	 * @return the propagation behavior
	 * @see #PROPAGATION_REQUIRED
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
	 *
	 * 获取事务的传播行为
	 */
	int getPropagationBehavior();

	/**
	 * Return the isolation level.
	 * <p>Must return one of the {@code ISOLATION_XXX} constants defined on
	 * {@link TransactionDefinition this interface}. Those constants are designed
	 * to match the values of the same constants on {@link java.sql.Connection}.
	 * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or
	 * {@link #PROPAGATION_REQUIRES_NEW} since it only applies to newly started
	 * transactions. Consider switching the "validateExistingTransactions" flag to
	 * "true" on your transaction manager if you'd like isolation level declarations
	 * to get rejected when participating in an existing transaction with a different
	 * isolation level.
	 * <p>Note that a transaction manager that does not support custom isolation levels
	 * will throw an exception when given any other level than {@link #ISOLATION_DEFAULT}.
	 * @return the isolation level
	 * @see #ISOLATION_DEFAULT
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 *
	 * 获取事务的隔离级别
	 */
	int getIsolationLevel();

	/**
	 * Return the transaction timeout.
	 * <p>Must return a number of seconds, or {@link #TIMEOUT_DEFAULT}.
	 * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or
	 * {@link #PROPAGATION_REQUIRES_NEW} since it only applies to newly started
	 * transactions.
	 * <p>Note that a transaction manager that does not support timeouts will throw
	 * an exception when given any other timeout than {@link #TIMEOUT_DEFAULT}.
	 * @return the transaction timeout
	 *
	 * 获取事务的超时时间（单位：s），如果事务管理器中不支持超时，则设置了超时将会导致抛出异常。
	 */
	int getTimeout();

	/**
	 * Return whether to optimize as a read-only transaction.
	 * <p>The read-only flag applies to any transaction context, whether backed
	 * by an actual resource transaction ({@link #PROPAGATION_REQUIRED}/
	 * {@link #PROPAGATION_REQUIRES_NEW}) or operating non-transactionally at
	 * the resource level ({@link #PROPAGATION_SUPPORTS}). In the latter case,
	 * the flag will only apply to managed resources within the application,
	 * such as a Hibernate {@code Session}.
	 * <p>This just serves as a hint for the actual transaction subsystem;
	 * it will <i>not necessarily</i> cause failure of write access attempts.
	 * A transaction manager which cannot interpret the read-only hint will
	 * <i>not</i> throw an exception when asked for a read-only transaction.
	 * @return {@code true} if the transaction is to be optimized as read-only
	 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 *
	 * 返回当前的事务是否为只读事务。当事务被设置为只读事务之后，如果有写的操作，将会抛出异常。
	 *
	 * 只读事务：
	 * 	在数据库的查询操作中，如果一个事务中只有一次查询操作，这个时候，数据库默认支持SQL执行期间的读一致性。
	 *	但是如果在一个事务中同时有多个查询操作，期间如果有其他的写操作，可能会导致前半部分的查询操作和后半部分查询操作的结果不一致，这个时候，
	 *	 如果要保证多次查询结果的一致，需要使用只读事务。
	 */
	boolean isReadOnly();

	/**
	 * Return the name of this transaction. Can be {@code null}.
	 * <p>This will be used as the transaction name to be shown in a
	 * transaction monitor, if applicable (for example, WebLogic's).
	 * <p>In case of Spring's declarative transactions, the exposed name will be
	 * the {@code fully-qualified class name + "." + method name} (by default).
	 * @return the name of this transaction
	 * @see org.springframework.transaction.interceptor.TransactionAspectSupport
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
	 *
	 * 获取事务的名称
	 */
	@Nullable
	String getName();

}
