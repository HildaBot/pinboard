package ch.jamiete.hilda.pinboard;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;

public class PinboardUtil {

    public static Message build(final int count, final TextChannel from, final Message message) {
        final MessageBuilder mb = new MessageBuilder();

        mb.append(PinboardUtil.getHeader(count, from));
        mb.setEmbed(PinboardUtil.getEmbed(message));

        return mb.build();
    }

    public static Message build(final MessageReaction reaction, final Message message) {
        final MessageBuilder mb = new MessageBuilder();

        mb.append(PinboardUtil.getHeader(reaction));
        mb.setEmbed(PinboardUtil.getEmbed(message));

        return mb.build();
    }

    public static MessageEmbed getEmbed(final Message message) {
        final EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl());
        eb.setColor(Color.decode("#FFD700"));
        eb.setDescription(message.getContentRaw());
        eb.setTimestamp(message.getTimeCreated());

        if (!message.getAttachments().isEmpty() && message.getAttachments().get(0).isImage()) {
            eb.setImage(message.getAttachments().get(0).getUrl());
        }

        return eb.build();
    }

    public static String getHeader(final int count, final TextChannel from) {
        final StringBuilder sb = new StringBuilder();

        if (count < 5) {
            sb.append(":star:");
        } else if (count < 10) {
            sb.append(":star2:");
        } else {
            sb.append(":stars:");
        }

        sb.append(" **").append(count).append("**");

        sb.append(" <#").append(from.getId()).append(">");

        return sb.toString();
    }

    public static String getHeader(final MessageReaction reaction) {
        return PinboardUtil.getHeader(reaction.getCount(), reaction.getTextChannel());
    }

}
