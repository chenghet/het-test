package com.dianwoba.forcestaff.message;


/**
 * Message消息源接口
 *
 * @author Administrator
 */
public interface MessageSource {

    /**
     * 获取消息
     *
     * @return
     */
    public Message fetchMessage();

    /**
     * 批量获取消息
     *
     * @return
     */
    public Message[] batchFetchMessages();

    /**
     * 处理失败的Message落地入口
     *
     * @param msg
     */
    public void failedMessageSink(Message msg);
}
