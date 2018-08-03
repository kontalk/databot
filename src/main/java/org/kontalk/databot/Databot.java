/*
 * Databot
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

package org.kontalk.databot;

import org.apache.commons.lang3.ArrayUtils;
import org.kontalk.konbot.Konbot;
import org.kontalk.konbot.shell.BotShell;


public class Databot extends Konbot {

    private final String datasetFile;
    private final String serverSpec;
    private final String personalKeyFile;
    private final String personalKeyPassphrase;

    public Databot(String[] args) {
        super(null);
        datasetFile = args[0];
        serverSpec = args[1];
        personalKeyFile = args[2];
        personalKeyPassphrase = args[3];
    }

    public void run() {
        try {
            BotShell sh = new BotShell();
            sh.init();
            sh.run(ArrayUtils.addAll(new String[]{"server"}, serverSpec.split(" ")));
            sh.run("personalkey", personalKeyFile, personalKeyPassphrase);
            sh.run("connect");
            sh.run("httpserver");
            sh.run("databot", datasetFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Databot(args).run();
    }

}
