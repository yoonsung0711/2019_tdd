package com.goos.auctionSniper;

import com.goos.auctionSniper.ui.MainWindow;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static final String SNIPER_STATUS_LABEL = "Label-SniperStatus";
    private static final String AUCTION_RESOURCE = "Auction";
    private static MainWindow ui;

    public Main() throws InvocationTargetException, InterruptedException {
        startUserInterface();
    }

    public static void main(String... args) throws InvocationTargetException, InterruptedException, XMPPException {
        Main main = new Main();

        XMPPConnection connection = connectTo(args[0], args[1], args[2]);

        joinAuction(connection, args[3]);
    }

    public static void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(
                getUserJID(itemId, connection),
                new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ui.showStatus("Sniper Status: Logged Out");
                            }
                        });
                    }
                }
        );
        chat.sendMessage(new Message());
    }

    public static String getUserJID(String itemId, XMPPConnection connection) {
        return String.format("auction-%s@%s/%s",
                itemId,
                connection.getServiceName(),
                AUCTION_RESOURCE);
    }

    public static XMPPConnection connectTo(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    public static void startUserInterface() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
