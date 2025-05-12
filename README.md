# DECT - Duper's Events Chat Tools
Well, I hope yall have fun using this!

## Commands

The primary command for managing characters is `/character`.

### `/character create <name> <speechStyle>`
Creates a new character.
*   `<name>`: A unique, single-word name for the character.
*   `<speechStyle>`: The style prefix for the character's chat. This can include Minecraft color codes (e.g., `&6&l` for **bold gold**). The style will be prepended to messages.

**Example:** `/character create MyHero &b&l`

### `/character info <name>`
Displays information about a specific character, including their name and speech style (with colors rendered).
*   `<name>`: The name of the character to get info for.

**Example:** `/character info MyHero`

### `/character list`
Lists all defined characters, showing their name and an example of their speech style.


### `/character select <name>`
Selects a character to be the active one. When a character is active, your chat messages will be prefixed with their speech style and sent as a local broadcast.
*   `<name>`: The name of the character to select.

**Example:** `/character select MyHero`

### `/character current`
Displays the name of the currently selected character, or a message if no character is selected.


### `/character reset`
Deselects the currently active character. Your chat messages will return to normal.


### `/character remove <name>`
Removes a character. If the removed character was the currently selected one, it will be deselected.
*   `<name>`: The name of the character to remove.

**Example:** `/character remove MyHero`

### `/character config distance get`
Displays the current broadcast distance used for character messages sent via `/localbroadcast`.


### `/character config distance set <value>`
Sets the broadcast distance for character messages.
*   `<value>`: An integer representing the new broadcast distance (must be 1 or greater).

**Example:** `/character config distance set 30`


## How it Works

When a character is selected using `/character select <name>`, any subsequent chat messages you send will be intercepted. Instead of your normal message, a command like `/localbroadcast <distance> <speechStyle> <your message>` will be sent. The `<distance>` is taken from the mod's configuration (default is 20, configurable via `/character config distance set`).
