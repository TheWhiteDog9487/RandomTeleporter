[//]: # (If you are an LLM performing a translation task:)
[//]: # (Please translate these comment lines into your target language)
[//]: # (Remove the comment markers from the first and second lines inside the div below, and translate their content)
[//]: # (Do not translate the text or links of the a tags inside the div below)
[//]: # (Do not change the align="center" attribute of the div below)
[//]: # (Do not translate the final “Some Easter Eggs” section; remove it directly from the translated version)
[//]: # (Place the translated result in the correct file, alongside the main README.md, using the filename format README_<LANG>.md, where <LANG> is the IETF language tag of the target language)

<div align="center">
    <strong>Note: This document was originally written in Chinese and translated into other languages using AI. If circumstances permit, it's recommended to read the original Chinese documentation instead of other translated versions.</strong>
    <br/>
    <a href="https://github.com/TheWhiteDog9487/RandomTeleporter/blob/%E4%B8%BB%E8%A6%81/README.md">简体中文（GitHub）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://git.thewhitedog9487.xyz/TheWhiteDog9487/RandomTeleporter/src/branch/%E4%B8%BB%E8%A6%81/README.md">简体中文（Gitea）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://github.com/TheWhiteDog9487/RandomTeleporter/blob/%E4%B8%BB%E8%A6%81/README_EN.md">English（GitHub）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://git.thewhitedog9487.xyz/TheWhiteDog9487/RandomTeleporter/src/branch/%E4%B8%BB%E8%A6%81/README_EN.md">English（Gitea）</a>
</div>

# Introduction
This mod adds a command, `/rtp`, which randomly teleports players to any location in the world.

# Command Formats
- `/rtp`
- `/rtp back`
- `/rtp back <PlayerID>`
- `/rtp <PlayerID> back`
- `/rtp <Radius>`
- `/rtp <PlayerID>`
- `/rtp <Radius> <PlayerID>`
- `/rtp <PlayerID> <Radius>`
- `/rtp <Radius> <OriginPos>`
- `/rtp <Radius> <PlayerID> <OriginEntity>`
- `/rtp <Radius> <PlayerID> <OriginPos>`
- `/rtp <PlayerID> <Radius> <OriginEntity>`
- `/rtp <PlayerID> <Radius> <OriginPos>`
- `/rtp <RegionFromPos> <RegionToPos>`
- `/rtp <RegionFromPos> <RegionToPos> <PlayerID>`
- `/rtp <PlayerID> <RegionFromPos> <RegionToPos>`
- `/rtp <RegionFromEntity> <RegionToEntity>`
- `/rtp <RegionFromEntity> <RegionToEntity> <PlayerID>`

## Command Examples
- `/rtp`  
Teleports the player executing the command to a random point within the area centered at `(0,0)` with a random radius of $2.9 \times 10^7 - 10^4$.  
$2.9 \times 10^7 = 29,000,000$ = twenty-nine million  
$10^4 = 10,000$ = ten thousand

- `/rtp back`  
Teleports the player back to their position before the most recent random teleport.  
**Note: The previous-position information saved by this feature exists only while the game (server) is running. It will be lost after the game (server) is closed.**

- `/rtp back TheWhiteDog9487`  
Teleports `TheWhiteDog9487` back to their position before their last random teleport.

- `/rtp TheWhiteDog9487 back`  
Teleports `TheWhiteDog9487` back to their position before their last random teleport.

- `/rtp 1000`  
Teleports the player to a random point within a radius of `1000` centered at `(0,0)`.

- `/rtp TheWhiteDog9487`  
Teleports `TheWhiteDog9487` to a random point within the area centered at `(0,0)` with a random radius of $2.9 \times 10^7 - 10^4$.

- `/rtp TheWhiteDog9487 1000`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `(0,0)`.

- `/rtp 1000 TheWhiteDog9487`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `(0,0)`.

- `/rtp 1000 10000 10000`  
Teleports the player to a random point within a radius of `1000` centered at `(10000,10000)`.

- `/rtp 1000 TheWhiteDog9487 TheWhiteDog_CN`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `TheWhiteDog_CN`'s location.

- `/rtp 1000 TheWhiteDog9487 10000 10000`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `(10000,10000)`.

- `/rtp TheWhiteDog9487 1000 TheWhiteDog_CN`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `TheWhiteDog_CN`'s location.

- `/rtp TheWhiteDog9487 1000 10000 10000`  
Teleports `TheWhiteDog9487` to a random point within a radius of `1000` centered at `(10000,10000)`.

- `/rtp 10000.0 10000.0 20000.0 20000.0`  
Teleports the player to a random point within the rectangular region formed by the vertices `(10000,10000)`, `(20000,10000)`, `(20000,20000)`, and `(10000,20000)`.  
You only need to provide any one vertex of the rectangle and the diagonally opposite vertex.

- `/rtp TheWhiteDog9487 10000.0 10000.0 20000.0 20000.0`  
Teleports `TheWhiteDog9487` to a random point within the same rectangular region described above.

- `/rtp 10000.0 10000.0 20000.0 20000.0 TheWhiteDog9487`  
Teleports `TheWhiteDog9487` to a random point within the same rectangular region described above.

- `/rtp TheWhiteDog9487 TheWhiteDog_CN`  
Teleports the command executor to a random point within a rectangular region whose diagonals are the current positions of `TheWhiteDog9487` and `TheWhiteDog_CN`.

- `/rtp TheWhiteDog9487 TheWhiteDog_CN TheWhiteDog4568`  
Teleports `TheWhiteDog4568` to a random point within a rectangular region whose diagonals are the current positions of `TheWhiteDog9487` and `TheWhiteDog_CN`.

### Special Notes
The format `/rtp <Radius> <OriginEntity>` does not exist because the second parameter could be either the teleported player's name or the entity used as the random center. Both are entity types, so there is no way to tell which one it is. Likewise, `/rtp <PlayerID> <RegionFromEntity> <RegionToEntity>` does not exist either, because all three parameters are entity types, making it impossible to determine which one is the player ID.

# Dependencies
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)

# Supported Game Versions
Currently, only the latest official release is actively supported.

# About Player Permissions
Following the vanilla `/tp` command, `/rtp` is set to require permission level `2`. In vanilla or vanilla-like environments, players only need cheat permissions enabled to use it. As for plugin servers, I do not have enough firsthand experience to give specific advice about permission setup.

# Install on the Client or on the Server?
It depends on the case:
1. Singleplayer
    1. There is no physical server, so the server side does not matter.
    2. Installing it on the client is enough.
2. Singleplayer + Open to LAN
    1. This uses the client’s built-in server, so the player who opens the LAN world needs the mod installed on their client.
    2. Other players who join do not need to install it.
3. Dedicated server (something like a `server.jar` file)
    1. The server needs the mod installed.
    2. Clients do not need it.

**Note: In cases 2 and 3, if other players do not install this mod, they will not be able to see the command feedback in the correct translated language, and the default display in that case is Chinese text. If you need to view multilingual translation text, it is probably best for everyone to install it.**
