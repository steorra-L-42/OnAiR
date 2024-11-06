# custom_logger.py
import logging
from colorama import Fore, Style, init

# colorama 초기화
init(autoreset=True)

# 색상 설정 (로그 레벨에만 색상을 적용)
LOG_COLORS = {
  'DEBUG': Fore.CYAN,
  'INFO': Fore.GREEN,
  'WARNING': Fore.YELLOW,
  'ERROR': Fore.RED,
  'CRITICAL': Fore.RED + Style.BRIGHT
}

LOG_PADDING = {
  'DEBUG': "    ",
  'INFO': "     ",
  'WARNING': "  ",
  'ERROR': "    ",
  'CRITICAL': " "
}

class ColoredFormatter(logging.Formatter):
  def format(self, record):
    # 로그 레벨에 색상 적용 및 고정 너비 설정 (5칸)
    log_color = LOG_COLORS.get(record.levelname, Fore.WHITE)
    pad_size = LOG_PADDING[record.levelname]

    record.levelname = f"{log_color}{record.levelname}{Style.RESET_ALL}"
    record.msg = f"{record.levelname}:{pad_size}{record.msg}"
    return super().format(record)


def get_logger(name=__name__, level=logging.INFO):
  """사용자가 간단히 호출하여 로거를 설정할 수 있는 함수"""
  logger = logging.getLogger(name)
  logger.setLevel(level)

  # 기존 핸들러가 있으면 제거하여 중복 설정 방지
  if not logger.handlers:
    formatter = ColoredFormatter('%(message)s')
    handler = logging.StreamHandler()
    handler.setFormatter(formatter)
    logger.addHandler(handler)

  return logger


def log_function_call(func):
  logger = get_logger(func.__module__)

  # 함수 호출 로그
  def wrapper(*args, **kwargs):
    logger.info(f"함수 실행 : [{func.__name__}]")
    result = func(*args, **kwargs)
    if result is not None:
      logger.info(f"함수 결과 [{func.__name__}] : {result}")
    return result

  return wrapper