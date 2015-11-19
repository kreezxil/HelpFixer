package com.matthewprenger.helpfixer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import javax.annotation.Nonnull;
import java.util.*;

@Mod(
        modid = HelpFixer.MOD_ID,
        name = HelpFixer.MOD_ID,
        acceptableRemoteVersions = "*"
)
public final class HelpFixer {

    public static final String MOD_ID = "HelpFixer";

    @Mod.EventHandler
    public void onServerStarting(final FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHelp() {

            @SuppressWarnings("unchecked")
            @Override
            protected List<ICommand> getSortedPossibleCommands(final ICommandSender sender) {
                List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender);

                Iterator<ICommand> iterator = list.iterator();
                while (iterator.hasNext()) {
                    ICommand command = iterator.next();
                    if (command.getCommandName() == null) {
                        FMLLog.warning(String.format("[HelpFixer] Identified command with null name, Ignoring: %s", command.getClass().getName()));
                        iterator.remove();
                    } else if (command.getCommandUsage(sender) == null) {
                        FMLLog.warning(String.format("[HelpFixer] Identified command with null usage, Ignoring: %s", command.getClass().getName()));
                        iterator.remove();
                    }
                }

                Collections.sort(list, new Comparator<ICommand>() {

                    @Override
                    public int compare(ICommand o1, ICommand o2) {
                        return o1.getCommandName().compareTo(o2.getCommandName());
                    }
                });
                return list;
            }

        });
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void onServerStarted(final FMLServerStartedEvent event) {
        Collection<ICommand> commands = MinecraftServer.getServer().getCommandManager().getCommands().values();

        for (final ICommand command : commands) {
            if (!(validCompareTo(command))) {
                FMLLog.warning(String.format("[HelpFixer] Command %s incorrectly overrides compareTo: %s", command.getCommandName(), command.getClass().getName()));
            }
        }
    }

    /**
     * Checks to see if an {@link net.minecraft.command.ICommand ICommand} has a valid compareTo method
     *
     * @param command the command
     * @return {@code true} if the compareTo method is valid, {@code false} if not
     */
    @SuppressWarnings("unchecked")
    public static boolean validCompareTo(@Nonnull final ICommand command) {
        return command.compareTo(testCmd1) != command.compareTo(testCmd2);
    }

    private static final ICommand testCmd1 = new CommandBase() {
        @Override
        public String getCommandName() {
            return "a";
        }

        @Override
        public String getCommandUsage(final ICommandSender sender) {
            return null;
        }

        @Override
        public void processCommand(final ICommandSender sender, String[] args) {
        }
    };

    private static final ICommand testCmd2 = new CommandBase() {
        @Override
        public String getCommandName() {
            return "z";
        }

        @Override
        public String getCommandUsage(final ICommandSender sender) {
            return null;
        }

        @Override
        public void processCommand(final ICommandSender sender, final String[] args) {
        }
    };
}
