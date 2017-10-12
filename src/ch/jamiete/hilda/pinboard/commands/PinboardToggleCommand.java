/*
 * Copyright 2017 jamietech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.jamiete.hilda.pinboard.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import java.util.Arrays;

public class PinboardToggleCommand extends ChannelSubCommand {
    private final PinboardPlugin plugin;

    protected PinboardToggleCommand(final Hilda hilda, final ChannelSeniorCommand senior, final PinboardPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("toggle");
        this.setAliases(Arrays.asList("stop", "off", "enable", "disable"));
        this.setDescription("Temporarily toggles checking for messages to put on the starboard.");
        this.setMinimumPermission(Permission.MANAGE_SERVER);
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        Boolean listen = null;

        if (label.equalsIgnoreCase("enable")) {
            listen = true;
        }

        if (label.equalsIgnoreCase("disable")) {
            listen = false;
        }

        if (listen == null) {
            listen = this.plugin.ignoring.contains(message.getGuild().getId());
        }

        if (listen) {
            this.plugin.ignoring.remove(message.getGuild().getId());
        } else {
            this.plugin.ignoring.add(message.getGuild().getId());
        }

        this.reply(message, "OK, I'm " + (listen ? "no longer" : "now") + " ignoring this server's reactions.");
    }

}
