#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

export DEBIAN_FRONTEND=noninteractive
export JAVA_VERSION="11"
export SERVER_ROOT="/opt/mcserver"
export JAVA_ROOT="${SERVER_ROOT}/java"

function error() {
    echo -e "${RED}Error: $1${NC}"
    exit 1
}

function download() {
    if wget -S --spider "$1" 2>&1 | grep -q 'HTTP/1.1 200 OK'; then
        wget -qO "$2" "$1"
    fi
}

echo -e "${GREEN}Installing dependencies${NC}"

apt-get update
apt-get install -y wget curl tar || error "Failed to install dependencies"

for cmd in wget curl tar; do
    if ! command -v $cmd &> /dev/null; then
        error "Unable to install $cmd"
    fi
done

echo -e "${GREEN}Installing Java${NC}"

mkdir -p "${JAVA_ROOT}" || error "Unable to create java directory"
cd "${JAVA_ROOT}" || error "Unable to change directory"

ARCH=$(uname -m)
if [ "${ARCH}" == "x86_64" ]; then
    ARCH="x64"
else
    ARCH="x32"
fi

download "https://api.adoptium.net/v3/binary/latest/${JAVA_VERSION}/ga/linux/${ARCH}/jre/hotspot/normal/adoptium" "java.tar.gz"

if [ ! -f "java.tar.gz" ]; then
    error "Unable to download Java"
fi

tar -xzf java.tar.gz
rm java.tar.gz

mv jdk*/* .
rm -r jdk*

if [ ! -f "bin/java" ]; then
    error "Unable to find java"
fi

echo -e "${GREEN}Installing wrapper${NC}"

mkdir -p "${SERVER_ROOT}" || error "Unable to create server directory"

wget -q --show-progress -O "${SERVER_ROOT}/server.jar" $(curl -s https://api.github.com/repos/gnmyt/MCDashWrapper/releases/latest | grep "browser_download_url.*\.jar" | cut -d : -f 2,3 | tr -d \")

if [ ! -f "${SERVER_ROOT}/server.jar" ]; then
    error "Unable to download server"
fi

echo -e "${GREEN}Creating user${NC}"

useradd -r -m -d "${SERVER_ROOT}" -s /bin/bash mcdash
chown -R mcdash:mcdash "${SERVER_ROOT}"

echo -e "${GREEN}Creating service${NC}"

cat > /etc/systemd/system/mcdash.service <<EOF
[Unit]
Description=MCDash Wrapper
After=network.target

[Service]
Type=simple
User=mcdash
WorkingDirectory=${SERVER_ROOT}
ExecStart=${JAVA_ROOT}/bin/java -jar ${SERVER_ROOT}/server.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

echo -e "${GREEN}Starting service${NC}"

systemctl enable mcdash
systemctl start mcdash

echo -e "${GREEN}Installation complete${NC}"