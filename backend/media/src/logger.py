
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


log = logging.getLogger()
log.setLevel(logging.INFO)

# 핸들러 및 포매터 설정
formatter = ColoredFormatter('%(message)s')
handler = logging.StreamHandler()
handler.setFormatter(formatter)
log.addHandler(handler)