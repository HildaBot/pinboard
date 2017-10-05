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
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import ch.jamiete.hilda.pinboard.PinboardUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;

public class PinboardMinimumCommand extends ChannelSubCommand {
    private final PinboardPlugin plugin;

    protected PinboardMinimumCommand(final Hilda hilda, final ChannelSeniorCommand senior, final PinboardPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("minimum");
        this.setAliases(Arrays.asList("min", "required", "req"));
        this.setDescription("Sets the minimum number of reactions necessary to pin a message.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.usage(message, "<number>", label);
            return;
        }

        final Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        int number;

        try {
            number = Integer.valueOf(arguments[0]);
        } catch (NumberFormatException e){
            this.usage(message, "<number>", label);
            return;
        }

        cfg.get().addProperty("required", number);
        this.reply(message, "OK, messages now require " + number + (number == 1 ? "star": "stars") + " to will be pinned.");
    }

}
