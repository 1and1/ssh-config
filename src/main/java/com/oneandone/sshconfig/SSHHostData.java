/*
 * Copyright 2018 1&1 Internet SE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.sshconfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** Data gathered directly from the SSH server.
 * @author Stephan Fuhrmann
 * */
public final class SSHHostData {

    /** The default connect timeout in milliseconds. */
    private static final int TIMEOUT = 5000;

    /** The SSH id version string of the server. */
    @Getter @Setter(AccessLevel.PRIVATE)
    private String serverId;

    /** The SSH server address. */
    @Getter @Setter(AccessLevel.PRIVATE)
    private InetSocketAddress address;

    /** Private constructor. */
    private SSHHostData() {
    }

    /**
     * Initializes a SSHHostData from a server address.
     * @param serverAddress the socket address of the server. Usually
     * points to TCP port 22.
     * @return the initialized SSH host data.
     * @throws IOException if there was an error in connecting or
     * reading the socket.
     */
    public static SSHHostData from(
            final InetSocketAddress serverAddress) throws IOException {
        SSHHostData result = new SSHHostData();
        try (Socket socket = new Socket()) {
            socket.connect(serverAddress, TIMEOUT);
            socket.setSoTimeout(TIMEOUT);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream,
                    StandardCharsets.US_ASCII);
            StringBuilder myServerId = new StringBuilder();

            int c;
            while ((c = inputStreamReader.read()) != -1) {
                if (c == '\n' || c == '\r') {
                    break;
                }
                myServerId.append((char) c);
            }
            result.setServerId(myServerId.toString());
            result.setAddress(serverAddress);
        }
        return result;
    }
}
