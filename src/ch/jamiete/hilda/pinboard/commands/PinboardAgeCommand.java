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

import java.util.Arrays;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import net.dv8tion.jda.core.entities.Message;

public class PinboardAgeCommand extends ChannelSubCommand {
    private final PinboardPlugin plugin;

    protected PinboardAgeCommand(final Hilda hilda, final ChannelSeniorCommand senior, final PinboardPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("age");
        this.setAliases(Arrays.asList("maxage"));
        this.setDescription("Sets the maximum age in days of a message that can be pinned.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.usage(message, "<days>", label);
            return;
        }

        final Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        int number;

        try {
            number = Integer.valueOf(arguments[0]);
        } catch (final NumberFormatException e) {
            this.usage(message, "<days>", label);
            return;
        }

        cfg.get().addProperty("maxage", number);

        if (number == 0) {
            this.reply(message, "OK, any message regardless of age can now be pinned.");
        } else {
            this.reply(message, "OK, messages must now be less than " + number + " " + (number == 1 ? "day" : "days") + " old to be pinned.");
        }
    }

}
