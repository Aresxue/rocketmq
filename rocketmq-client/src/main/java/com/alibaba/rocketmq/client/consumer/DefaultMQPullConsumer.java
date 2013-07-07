/**
 * $Id: DefaultMQPullConsumer.java 1831 2013-05-16 01:39:51Z shijia.wxr $
 */
package com.alibaba.rocketmq.client.consumer;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.rocketmq.client.ClientConfig;
import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import com.alibaba.rocketmq.client.consumer.store.OffsetStore;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.impl.consumer.DefaultMQPullConsumerImpl;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.alibaba.rocketmq.remoting.exception.RemotingException;


/**
 * 消息消费者，主动消费
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class DefaultMQPullConsumer extends ClientConfig implements MQPullConsumer {
    /**
     * 做同样事情的Consumer归为同一个Group，应用必须设置，并保证命名唯一
     */
    private String consumerGroup = MixAll.DEFAULT_CONSUMER_GROUP;
    /**
     * 长轮询模式，Consumer连接在Broker挂起最长时间，不建议修改
     */
    private long brokerSuspendMaxTimeMillis = 1000 * 20;
    /**
     * 长轮询模式，Consumer超时时间（必须要大于brokerSuspendMaxTimeMillis），不建议修改
     */
    private long consumerTimeoutMillisWhenSuspend = 1000 * 30;
    /**
     * 非阻塞拉模式，Consumer超时时间，不建议修改
     */
    private long consumerPullTimeoutMillis = 1000 * 10;
    /**
     * 集群消费/广播消费
     */
    private MessageModel messageModel = MessageModel.BROADCASTING;
    /**
     * 队列变化监听器
     */
    private MessageQueueListener messageQueueListener;
    /**
     * Offset存储，系统会根据客户端配置自动创建相应的实现，如果应用配置了，则以应用配置的为主
     */
    private OffsetStore offsetStore;
    /**
     * 需要监听哪些Topic的队列变化
     */
    private Set<String> registerTopics = new HashSet<String>();
    /**
     * 队列分配算法，应用可重写
     */
    private AllocateMessageQueueStrategy allocateMessageQueueStrategy = new AllocateMessageQueueAveragely();

    private final transient DefaultMQPullConsumerImpl defaultMQPullConsumerImpl =
            new DefaultMQPullConsumerImpl(this);


    public DefaultMQPullConsumer() {
    }


    public DefaultMQPullConsumer(final String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }


    @Override
    public void createTopic(String key, String newTopic, int queueNum) throws MQClientException {
        this.defaultMQPullConsumerImpl.createTopic(key, newTopic, queueNum);
    }


    @Override
    public long earliestMsgStoreTime(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.earliestMsgStoreTime(mq);
    }


    @Override
    public long fetchConsumeOffset(MessageQueue mq, boolean fromStore) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchConsumeOffset(mq, fromStore);
    }


    @Override
    public Set<MessageQueue> fetchMessageQueuesInBalance(String topic) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchMessageQueuesInBalance(topic);
    }


    @Override
    public Set<MessageQueue> fetchSubscribeMessageQueues(String topic) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchSubscribeMessageQueues(topic);
    }


    public AllocateMessageQueueStrategy getAllocateMessageQueueStrategy() {
        return allocateMessageQueueStrategy;
    }


    public long getBrokerSuspendMaxTimeMillis() {
        return brokerSuspendMaxTimeMillis;
    }


    public String getConsumerGroup() {
        return consumerGroup;
    }


    public long getConsumerPullTimeoutMillis() {
        return consumerPullTimeoutMillis;
    }


    public long getConsumerTimeoutMillisWhenSuspend() {
        return consumerTimeoutMillisWhenSuspend;
    }


    public MessageModel getMessageModel() {
        return messageModel;
    }


    public MessageQueueListener getMessageQueueListener() {
        return messageQueueListener;
    }


    public Set<String> getRegisterTopics() {
        return registerTopics;
    }


    @Override
    public long maxOffset(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.maxOffset(mq);
    }


    @Override
    public long minOffset(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.minOffset(mq);
    }


    @Override
    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums);
    }


    @Override
    public void pull(MessageQueue mq, String subExpression, long offset, int maxNums,
            PullCallback pullCallback) throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums, pullCallback);
    }


    @Override
    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }


    @Override
    public void pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums,
            PullCallback pullCallback) throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQPullConsumerImpl.pullBlockIfNotFound(mq, subExpression, offset, maxNums, pullCallback);
    }


    @Override
    public QueryResult queryMessage(String topic, String key, int maxNum, long begin, long end)
            throws MQClientException, InterruptedException {
        return this.defaultMQPullConsumerImpl.queryMessage(topic, key, maxNum, begin, end);
    }


    @Override
    public void registerMessageQueueListener(String topic, MessageQueueListener listener) {
        synchronized (this.registerTopics) {
            this.registerTopics.add(topic);
            if (listener != null) {
                this.messageQueueListener = listener;
            }
        }
    }


    @Override
    public long searchOffset(MessageQueue mq, long timestamp) throws MQClientException {
        return this.defaultMQPullConsumerImpl.searchOffset(mq, timestamp);
    }


    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel) throws RemotingException, MQBrokerException,
            InterruptedException, MQClientException {
        this.defaultMQPullConsumerImpl.sendMessageBack(msg, delayLevel);
    }


    public void setAllocateMessageQueueStrategy(AllocateMessageQueueStrategy allocateMessageQueueStrategy) {
        this.allocateMessageQueueStrategy = allocateMessageQueueStrategy;
    }


    public void setBrokerSuspendMaxTimeMillis(long brokerSuspendMaxTimeMillis) {
        this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
    }


    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }


    public void setConsumerPullTimeoutMillis(long consumerPullTimeoutMillis) {
        this.consumerPullTimeoutMillis = consumerPullTimeoutMillis;
    }


    public void setConsumerTimeoutMillisWhenSuspend(long consumerTimeoutMillisWhenSuspend) {
        this.consumerTimeoutMillisWhenSuspend = consumerTimeoutMillisWhenSuspend;
    }


    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }


    public void setMessageQueueListener(MessageQueueListener messageQueueListener) {
        this.messageQueueListener = messageQueueListener;
    }


    public void setRegisterTopics(Set<String> registerTopics) {
        this.registerTopics = registerTopics;
    }


    @Override
    public void shutdown() {
        this.defaultMQPullConsumerImpl.shutdown();
    }


    @Override
    public void start() throws MQClientException {
        this.defaultMQPullConsumerImpl.start();
    }


    @Override
    public void updateConsumeOffset(MessageQueue mq, long offset) throws MQClientException {
        this.defaultMQPullConsumerImpl.updateConsumeOffset(mq, offset);
    }


    @Override
    public MessageExt viewMessage(String msgId) throws RemotingException, MQBrokerException,
            InterruptedException, MQClientException {
        return this.defaultMQPullConsumerImpl.viewMessage(msgId);
    }


    public OffsetStore getOffsetStore() {
        return offsetStore;
    }


    public void setOffsetStore(OffsetStore offsetStore) {
        this.offsetStore = offsetStore;
    }
}
