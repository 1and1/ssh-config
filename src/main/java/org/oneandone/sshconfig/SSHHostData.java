package org.oneandone.sshconfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/** Data gathered directly from the SSH server.
 * @author Stephan Fuhrmann
 * */
public class SSHHostData {

    private final static int TIMEOUT = 5000;

    /** The SSH id version string of the server. */
    @Getter @Setter(AccessLevel.PRIVATE)
    private String serverId;

    /** The SSH server address. */
    @Getter @Setter(AccessLevel.PRIVATE)
    private InetSocketAddress address;

    private SSHHostData() {

    }

    public static SSHHostData from(InetSocketAddress serverAddress) throws IOException {
        Socket socket = new Socket();
        socket.connect(serverAddress, TIMEOUT);
        socket.setSoTimeout(TIMEOUT);
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("ASCII"));

        StringBuilder myServerId = new StringBuilder();

        SSHHostData result = new SSHHostData();

        int c;
        while ((c = inputStreamReader.read()) != -1) {
            if (c == '\n') {
                break;
            }
            myServerId.append((char)c);
        }
        result.setServerId(myServerId.toString());
        result.setAddress(serverAddress);
        return result;
    }
}
