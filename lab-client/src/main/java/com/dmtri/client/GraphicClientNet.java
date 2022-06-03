package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dmtri.common.LocaleKeys;
import com.dmtri.common.network.Response;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility class with helper methods suited for a graphical client
 */
public class GraphicClientNet {
    private static final long SLEEP_TIME = 100;
    private final Lock lock = new ReentrantLock(true);

    private ObjectProperty<ObjectSocketChannelWrapper> channel = new SimpleObjectProperty<>();

    public ObjectProperty<ObjectSocketChannelWrapper> channelProperty() {
        return channel;
    }
    public void connect(InetSocketAddress address) {
        try {
            lock.lock();
            SocketChannel socket = SocketChannel.open();
            socket.connect(address);
            socket.configureBlocking(false);
            channel.set(new ObjectSocketChannelWrapper(socket));
        } catch (UnresolvedAddressException e) {
            new Alert(AlertType.ERROR, LocaleManager.getObservableStringByKey(LocaleKeys.INVALID_ADDRESS).get()).showAndWait();
            channel.set(null);
        } catch (IOException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
            channel.set(null);
        } finally {
            lock.unlock();
        }
    }
    public void disconnect() {
        try {
            lock.lock();
            closeSocket();
        } catch (IOException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
        } finally {
            channel.set(null);
            lock.unlock();
        }
    }

    public void closeSocket() throws IOException {
        try {
            lock.lock();
            if (channel.get() != null) {
                channel.get().getSocket().close();
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized Response sendMessage(Object msg) {
        if (channel.get() == null) {
            return null;
        }

        try {
            lock.lock();
            channel.get().sendMessage(msg);

            while (!channel.get().checkForMessage()) {
                Thread.sleep(SLEEP_TIME);
            }

            Object payload = channel.get().getPayload();

            if (payload instanceof Response) {
                Response resp = (Response) payload;
                channel.get().clearInBuffer();
                return resp;
            }
            new Alert(AlertType.ERROR, LocaleManager.getObservableStringByKey(LocaleKeys.INVALID_RESPONSE).get()).showAndWait();
            channel.get().clearInBuffer();
            return null;
        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> {
                disconnect();
                new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
            });
            return null;
        } finally {
            lock.unlock();
        }
    }
}
