import logging
from colorama import Fore, Style, init

# Initialize colorama
init(autoreset=True)

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
    log_color = LOG_COLORS.get(record.levelname, Fore.WHITE)
    pad_size = LOG_PADDING[record.levelname]

    record.levelname = f"{log_color}{record.levelname}{Style.RESET_ALL}"
    record.msg = f"{record.levelname}:{pad_size}{record.msg}"
    return super().format(record)

log = logging.getLogger('media')
log.setLevel(logging.INFO)

formatter = ColoredFormatter('%(message)s')
handler = logging.StreamHandler()
handler.setFormatter(formatter)
log.addHandler(handler)
