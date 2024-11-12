import logging
import signal
import time

import consumer_manager
import instance


def handle_shutdown(signum, frame):
    """서버 종료 시 안전하게 Consumer 정리"""
    logging.info(f"Received shutdown signal: {signum}. Shutting down...")
    consumer_manager.close_all_consumers()
    logging.info("All consumers closed successfully.")
    producer.close()
    logging.info("Producer closed successfully.")
    exit(0)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s', force=True)

    # Graceful Shutdown을 위한 신호 처리기 등록
    signal.signal(signal.SIGINT, handle_shutdown)
    signal.signal(signal.SIGTERM, handle_shutdown)

    consumer_manager.create_consumers()

    producer = instance.producer
    channel_manager = instance.channel_manager

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        handle_shutdown(None, None)
