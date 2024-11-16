FROM ubuntu:22.04

# Set Environment Variables
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

# Install dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    python3.11 \
    python3-pip && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# update pip to 24.2
RUN python3 -m pip install --upgrade pip==24.2

# Set default Python and pip commands
RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.11 1 && \
    update-alternatives --install /usr/bin/pip pip /usr/bin/pip3 1

# UTF-8 환경 설정
ENV PYTHONENCODING=utf-8

# Set a working directory
WORKDIR /app

# Make src directory and copy the source code
RUN mkdir src

# Copy the current directory contents into the container at /app
COPY requirements.txt /app/

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Install dependencies for Firefox
RUN apt-get update && apt-get install -y \
    wget \
    bzip2 \
    libgtk-3-0 \
    libdbus-glib-1-2 \
    libxt6 \
    libx11-xcb1 \
    libpci3 \
    libasound2 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libcairo2 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libxtst6 \
    libgtk2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
# RUN apt-get update && apt-get install -y firefox==132.0.2
WORKDIR /tmp
RUN wget https://download-installer.cdn.mozilla.net/pub/firefox/releases/132.0.2/linux-x86_64/en-US/firefox-132.0.2.tar.bz2
RUN tar xjf firefox-132.0.2.tar.bz2 && mv firefox /opt/firefox && ln -s /opt/firefox/firefox /usr/local/bin/firefox

# Install geckodriver
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.35.0/geckodriver-v0.35.0-linux64.tar.gz
RUN tar -xvzf geckodriver-v0.35.0-linux64.tar.gz
RUN chmod +x geckodriver && mv geckodriver /usr/local/bin/

# Remove unnecessary files
RUN rm -rf /tmp/*

# Copy source code
WORKDIR /app
COPY src /app/src

CMD ["python3", "/app/src/main.py"]