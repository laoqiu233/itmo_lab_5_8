package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dmtri.common.LocaleKeys;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;

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
            showError(LocaleManager.getObservableStringByKey(LocaleKeys.INVALID_ADDRESS).get());
            channel.set(null);
        } catch (IOException e) {
            showError(e.getLocalizedMessage());
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
            showError(e.getLocalizedMessage());
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

    public Response sendMessage(Object msg) {
        if (channel.get() == null) {
            return null;
        }

        try {
            lock.lock();
            channel.get().clearInBuffer();
            channel.get().sendMessage(msg);

            while (!channel.get().checkForMessage()) {
                Thread.sleep(SLEEP_TIME);
            }

            Object payload = channel.get().getPayload();

            if (payload instanceof Response) {
                Response resp = (Response) payload;
                if (resp instanceof ResponseWithException) {
                    ResponseWithException rwe = (ResponseWithException) resp;
                    if (rwe.getException() instanceof InvalidRequestException) {
                        showError(LocaleManager.getObservableStringByKey(rwe.getException().getLocalizedMessage()).get());
                    } else {
                        showError(rwe.getException().getLocalizedMessage());
                    }
                }
                return resp;
            }
            showError(LocaleManager.getObservableStringByKey(LocaleKeys.INVALID_RESPONSE).get());

            return null;
        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> {
                disconnect();
                showError(e.getLocalizedMessage());
                e.printStackTrace();
            });
            return null;
        } finally {
            lock.unlock();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message);
        alert.setTitle(LocaleManager.getObservableStringByKey(LocaleKeys.ERROR).get());
        alert.setHeaderText(LocaleManager.getObservableStringByKey(LocaleKeys.NETWORK_ERROR).get());
        alert.showAndWait();
    }
}
