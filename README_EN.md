<div align="center">
    <a href="https://github.com/TheWhiteDog9487/RandomTeleporter/blob/%E4%B8%BB%E8%A6%81/README.md">简体中文（GitHub）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://git.thewhitedog9487.xyz/TheWhiteDog9487/RandomTeleporter/src/branch/%E4%B8%BB%E8%A6%81/README.md">简体中文（Gitea）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://github.com/TheWhiteDog9487/RandomTeleporter/blob/%E4%B8%BB%E8%A6%81/README_EN.md">English（GitHub）</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://git.thewhitedog9487.xyz/TheWhiteDog9487/RandomTeleporter/src/branch/%E4%B8%BB%E8%A6%81/README_EN.md">English（Gitea）</a>
</div>

# Introduction
This mod adds a command `/rtp` for randomly teleporting players to any location in the world.

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
Teleports the player executing the command to a random point within a radius of $2.9 \times 10^7 - 10^4$ centered at `(0,0)`.  
$2.9 \times 10^7 = 29,000,000$  
$10^4 = 10,000$

- `/rtp back`  
Teleports the player back to their position before the last random teleport.  
**Note: The previous position information is only stored during the game (server) session and will be lost after the game (server) is closed.**

- `/rtp back TheWhiteDog9487`  
Teleports `TheWhiteDog9487` back to their position before their last random teleport.

- `/rtp TheWhiteDog9487 back`  
Teleports `TheWhiteDog9487` back to their position before their last random teleport.

- `/rtp 1000`  
Teleports the player to a random point within a radius of `1000` centered at `(0,0)`.

- `/rtp TheWhiteDog9487`  
Teleports `TheWhiteDog9487` to a random point within a radius of $2.9 \times 10^7 - 10^4$ centered at `(0,0)`.

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
Teleports the player to a random point within a rectangular region formed by vertices `(10000,10000)`, `(20000,10000)`, `(20000,20000)`, and `(10000,20000)`.  
You need to provide any vertex and its diagonally opposite vertex of this rectangle.

- `/rtp TheWhiteDog9487 10000.0 10000.0 20000.0 20000.0`  
Teleports `TheWhiteDog9487` to a random point within the same rectangular region described above.

- `/rtp 10000.0 10000.0 20000.0 20000.0 TheWhiteDog9487`  
Teleports `TheWhiteDog9487` to a random point within the same rectangular region described above.

- `/rtp TheWhiteDog9487 TheWhiteDog_CN`  
Teleports the command executor to a random point within a rectangular region where `TheWhiteDog9487` and `TheWhiteDog_CN`'s current positions are diagonals.

- `/rtp TheWhiteDog9487 TheWhiteDog_CN TheWhiteDog4568`  
Teleports `TheWhiteDog4568` to a random point within a rectangular region where `TheWhiteDog9487` and `TheWhiteDog_CN`'s current positions are diagonals.

### Special Notes
The format `/rtp <Radius> <OriginEntity>` does not exist because the second parameter could be either the teleported player's name or the entity serving as the random center. Both are entity types, leading to ambiguity. Similarly, `/rtp <PlayerID> <RegionFromEntity> <RegionToEntity>` is also absent as all three parameters are entity types, making it impossible to distinguish the player ID.

# Dependencies
Fabric API

# Player Permissions
Following the standard `/tp` command, `/rtp` requires a permission level of `2`. For vanilla or vanilla-like servers, players only need "cheats" enabled. For plugin-based servers, specific permission management depends on your setup.

# Installation: Client or Server?
- **Singleplayer**: Install on the client.
- **Singleplayer + Open to LAN**: Install on the host client. Other players do not need to install it.
- **Dedicated Server**: Install on the server. Clients do not strictly need to install it.

**Note**: In multiplayer scenarios, if clients do not have the mod installed, they will see command feedback in the server's default language (currently Chinese). Installing the mod on both sides is recommended for full multi-language support.