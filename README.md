# **HoursAndCounting**

HoursAndCounting is a Discord bot project developed using JDA. It aims to be a flexible iCal interface for Discord users, with the ability to load their own iCal URLs.

### Requirements

- JDA
- slf4j-nop (Optional, silences logging warning on startup)
- [A Discord bot token](https://discord.com/developers/applications).

JDA and slf4j-nop dependencies should both be handled by Gradle.

### Building

- Clone this repository
- Make sure all dependencies are imported
- Run shadowJar Gradle task

Build is exported to `<project root>/build/libs/HoursAndCounting-VERSION.jar`.

### Running

Simply run the compiled jar file with the following arguments :

`java -jar HoursAndCounting-VERSION.jar <bot token>`

Make sure to adapt this line with the correct version tag and your bot's token.

Be careful, as this bot uses `AccountType.BOT`. I don't think I need to address this, but if you use a non bot account token, it may result in a ban from Discord.

In general, avoid selfbotting. It is against Discord's Developer ToS. Read more here : [Discord Developer ToS](https://discord.com/developers/docs/legal)