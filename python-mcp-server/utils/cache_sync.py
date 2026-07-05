"""多实例缓存同步模块。

使用 Redis Pub/Sub 实现多实例间的缓存同步，确保数据一致性。
"""
import logging
import threading
import json
import time

import redis

from config.constants import (
    REDIS_HOST,
    REDIS_PORT,
    REDIS_PASSWORD,
    REDIS_TIMEOUT,
    CACHE_SYNC_CHANNEL,
    REDIS_HOT_DATA_DB
)

logger = logging.getLogger(__name__)


class CacheSyncPublisher:
    """缓存同步发布器。
    
    当本地缓存发生变更时，向其他实例发布同步消息。
    """
    
    def __init__(self):
        """初始化同步发布器。"""
        self.pubsub_client = redis.Redis(
            host=REDIS_HOST,
            port=REDIS_PORT,
            db=REDIS_HOT_DATA_DB,  # 使用 DB 1 作为同步通道
            password=REDIS_PASSWORD,
            socket_timeout=REDIS_TIMEOUT,
            decode_responses=True
        )
    
    def publish(self, event_type: str, key: str) -> None:
        """发布缓存同步事件。
        
        Args:
            event_type: 事件类型 (SET/DELETE/CLEAR)
            key: 受影响的缓存键
        """
        message = json.dumps({
            'event': event_type,
            'key': key,
            'timestamp': time.time()
        })
        
        channel = f"{CACHE_SYNC_CHANNEL}:all"
        try:
            self.pubsub_client.publish(channel, message)
            logger.debug(f"发布同步事件: {event_type} - {key}")
        except redis.exceptions.ConnectionError as e:
            logger.error(f"发布同步事件失败: {e}")


class CacheSyncSubscriber:
    """缓存同步订阅器。
    
    监听其他实例发布的缓存变更事件，并更新本地缓存。
    """
    
    def __init__(self, on_sync_event=None):
        """初始化同步订阅器。
        
        Args:
            on_sync_event: 同步事件回调函数，接收事件字典参数
        """
        self.on_sync_event = on_sync_event
        self.running = False
        
        self.subscriber_client = redis.Redis(
            host=REDIS_HOST,
            port=REDIS_PORT,
            db=REDIS_HOT_DATA_DB,
            password=REDIS_PASSWORD,
            socket_timeout=REDIS_TIMEOUT,
            decode_responses=True
        )
    
    def start(self) -> None:
        """启动同步监听。"""
        if self.running:
            logger.warning("同步监听已在运行")
            return
        
        self.running = True
        
        def listen():
            """监听同步消息的后台线程函数。"""
            pubsub = self.subscriber_client.pubsub()
            channel = f"{CACHE_SYNC_CHANNEL}:all"
            pubsub.subscribe(channel)
            
            logger.info(f"缓存同步监听已启动，监听频道: {channel}")
            
            while self.running:
                try:
                    message = pubsub.get_message(timeout=1.0)
                    if message and message['type'] == 'message':
                        self._handle_message(message['data'])
                except redis.exceptions.ConnectionError as e:
                    logger.error(f"同步监听连接错误: {e}")
                    time.sleep(5)  # 重连前等待
                except Exception as e:
                    logger.error(f"同步监听异常: {e}", exc_info=True)
        
        thread = threading.Thread(target=listen, daemon=True)
        thread.start()
    
    def stop(self) -> None:
        """停止同步监听。"""
        self.running = False
        logger.info("缓存同步监听已停止")
    
    def _handle_message(self, data: str) -> None:
        """处理同步消息。
        
        Args:
            data: JSON 格式的同步消息
        """
        try:
            event = json.loads(data)
            logger.debug(f"收到同步事件: {event}")
            
            if self.on_sync_event:
                self.on_sync_event(event)
        except json.JSONDecodeError:
            logger.error(f"无效的同步消息: {data}")
