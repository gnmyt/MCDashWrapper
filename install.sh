#!/bin/bash
export DEBIAN_FRONTEND=noninteractive
export JAVA_VERSION="11"
export SERVER_ROOT="/opt/mcserver"
export JAVA_ROOT=${SERVER_ROOT}/java

function quit() {
    say "Error: $1"
    exit 1
}

function download() {
  if wget -S --spider "$1" 2>&1 | grep -q 'HTTP/1.1 200 OK'; then
    wget -qO "$2" "$1"
  fi
}

# Install dependencies

echo "Installing dependencies"

apt-get update
apt-get install -y wget curl tar

if [ ! -x "$(command -v wget)" ]; then
  quit "Unable to install wget"
fi

if [ ! -x "$(command -v curl)" ]; then
  quit "Unable to install curl"
fi

if [ ! -x "$(command -v tar)" ]; then
  quit "Unable to install tar"
fi

# Install Java
echo "Installing Java"

mkdir -p "${JAVA_ROOT}" || quit "Unable to create java directory"
cd "${JAVA_ROOT}" || quit "Unable to change directory"

if [ "$(uname -m)" == "x86_64" ]; then
  ARCH="x64"
else
  ARCH="x32"
fi

download "https://api.adoptium.net/v3/binary/latest/${JAVA_VERSION}/ga/linux/${ARCH}/jre/hotspot/normal/adoptium" "java.tar.gz"

if [ ! -f "java.tar.gz" ]; then
  quit "Unable to download java"
fi

tar -xzf java.tar.gz
rm java.tar.gz

mv jdk*/* .
rm -rf jdk*

if [ ! -f "bin/java" ]; then
  quit "Unable to find java"
fi

# Install MCDashWrapper

echo "Installing wrapper"

mkdir -p "${SERVER_ROOT}" || quit "Unable to create server directory"

wget -q --show-progress -O ${SERVER_ROOT}/server.jar $(curl -s https://api.github.com/repos/gnmyt/MCDashWrapper/releases/latest | grep "browser_download_url.*\.jar" | cut -d : -f 2,3 | tr -d \")

if [ ! -f "${SERVER_ROOT}/server.jar" ]; then
  quit "Unable to download server"
fi

# Create user

echo "Creating user"

useradd -r -m -d "${SERVER_ROOT}" -s /bin/bash mcdash
chown -R mcdash:mcdash "${SERVER_ROOT}"

# Create unit file

echo "Creating service"

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

# Enable and start service

echo "Starting service"

systemctl enable mcdash
systemctl start mcdash

echo "Installation complete"