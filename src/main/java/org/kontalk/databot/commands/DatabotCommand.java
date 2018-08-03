/*
 * Konbot
 * Copyright (C) 2018 Kontalk Devteam <devteam@kontalk.org>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kontalk.databot.commands;

import org.bouncycastle.openpgp.PGPException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.kontalk.konbot.client.XMPPTCPConnection;
import org.kontalk.konbot.shell.HelpableCommand;
import org.kontalk.konbot.shell.ShellCommand;
import org.kontalk.konbot.shell.ShellSession;
import org.kontalk.konbot.shell.commands.AbstractCommand;
import org.kontalk.konbot.shell.commands.ConnectCommand;
import org.kontalk.konbot.shell.commands.HttpServerCommand;
import org.kontalk.konbot.util.MessageUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;


@SuppressWarnings("unused")
public class DatabotCommand extends AbstractCommand implements HelpableCommand {

    @Override
    public String name() {
        return "databot";
    }

    @Override
    public String description() {
        return "Databot that sends random messages from a dataset";
    }

    @Override
    public void run(String[] args, ShellSession session) {
        if (args.length < 2) {
            help();
            return;
        }

        String datasetFile = args[1];
        // load dataset and put in session
        try (InputStream in = new FileInputStream(datasetFile)) {
            Properties dataset = new Properties();
            dataset.load(in);
            session.put("databot.dataset", dataset);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load dataset: " + e);
        }

        // TODO register a random timer for sending a roster broadcast with a random element from the dataset

        // register a message listener and reply with a random element from the dataset
        XMPPTCPConnection conn = ConnectCommand.connection(session);
        ChatManager.getInstanceFor(conn).addIncomingListener((entityBareJid, message, chat) -> {
            String text = randomData(dataset(session));
            Message reply = new Message(entityBareJid, Message.Type.chat);
            reply.setBody(text);
            try {
                chat.send(MessageUtils.signMessage(reply));
            }
            catch (Exception e) {
                println("Unable to send message: " + e);
                e.printStackTrace(out);
            }
        });
    }

    private String randomData(Properties dataset) {
        Random generator = new Random();
        // inefficient, but works
        Object[] values = dataset.values().toArray();
        return (String) values[generator.nextInt(values.length)];
    }

    public static Properties dataset(ShellSession session) {
        return (Properties) session.get("databot.dataset");
    }

    @Override
    public void help() {
        println("Usage: "+name()+" <dataset.properties>");
    }
}
