import time

from src.channel_manager import listen_for_channel_creation

if __name__ == "__main__":
    while True:  # 계속 실행되도록 무한 루프
        try:
            listen_for_channel_creation()
        except Exception as e:
            print(f"An error occurred: {e}")
            time.sleep(1)
