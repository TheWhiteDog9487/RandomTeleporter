# 介绍
这个模组增加了一个命令(/rtp)，用于将玩家随机传送到世界的任何一个位置。

# 命令格式
- /rtp
- /rtp <Radius(半径)>
- /rtp <被传送玩家名(PlayerID)>
- /rtp <Radius(半径)> <被传送玩家名(PlayerID)>
- /rtp <被传送玩家名(PlayerID)> <Radius(半径)>
- /rtp <Radius(半径)> <OriginPos(随机中心，坐标)>
- /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginEntity(随机中心，实体)>
- /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginPos(随机中心，坐标)>
- /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginEntity(随机中心，实体)>
- /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginPos(随机中心，坐标)>

## 命令示例
- /rtp  
将执行命令的玩家随机传送到以(0,0)为中心点，2.9e7作为随机半径的范围内的随机一点  
2.9e+7 = 2.9 x 10^7 = 29000000 = 两千九百万  

- /rtp 1000  
将执行命令的玩家随机传送到以(0,0)为中心点，1000作为随机半径的范围内的随机一点

- /rtp TheWhiteDog9487  
将TheWhiteDog9487随机传送到以(0,0)为中心点，2.9e7作为随机半径的范围内的随机一点  

- /rtp TheWhiteDog9487 1000  
将TheWhiteDog9487随机传送到以(0,0)为中心点，1000作为随机半径的范围内的随机一点  

- /rtp 1000 TheWhiteDog9487  
将TheWhiteDog9487随机传送到以(0,0)为中心点，1000作为随机半径的范围内的随机一点  

- /rtp 1000 10000 ~ 10000  
将执行命令的玩家随机传送到以(10000,10000)为中心点，1000作为随机半径的范围内的随机一点  
提示：按照道理来说中心坐标是不需要高度（Y轴）的，但由于坐标的类型是Vec3d，所以还是要写高度的。  
至于高度的具体数值，随便写，代码里也没用到过。  

- /rtp 1000 TheWhiteDog9487 TheWhiteDog_CN  
将TheWhiteDog9487随机传送到以TheWhiteDog_CN所在位置为中心点，1000作为随机半径的范围内的随机一点  

- /rtp 1000 TheWhiteDog9487 10000 ~ 10000  
将TheWhiteDog9487随机传送到以(10000,10000)为中心点，1000作为随机半径的范围内的随机一点  

- /rtp TheWhiteDog9487 1000 TheWhiteDog_CN  
将TheWhiteDog9487随机传送到以TheWhiteDog_CN所在位置为中心点，1000作为随机半径的范围内的随机一点  

- /rtp TheWhiteDog9487 1000 10000 ~ 10000  
将TheWhiteDog9487随机传送到以(10000,10000)为中心点，1000作为随机半径的范围内的随机一点  

### 特别提示
/rtp <Radius(半径)> <Origin(随机中心，实体)> 这种格式不存在。  
因为第二个参数可能是被传送玩家名，也可能是做随机中心点的实体。  
这两种都是实体类型，无法区分到底是哪一种，存在歧义。  

# 依赖项
由于我使用了fabric.api.command.v2中的CommandRegistrationCallback.EVENT来向游戏注册命令，所以这个模组需要依赖Fabric API

# 关于玩家权限
我参照原版的 /tp 命令，给 /rtp 设置了4级的权限要求。  
如果是原版或者类原版，你只需要让玩家有作弊的权限就可以用。  
插件服务器方面那些具体的权限分配，因为我自己没玩过所以我也没法给出参考意见。

# 在客户端还是服务器安装？
分以下情况：  
1. 单人游戏
    1. 物理服务器不存在，所以服务器不用管
    2. 客户端安装即可
2. 单人游戏 + 开放局域网
    1. 使用客户端内置的服务器，开放局域网的那位玩家的客户端需要安装
    2. 其他加入游戏的玩家不需要安装
3. 使用独立服务器（类似server.jar这种文件）
    1. 服务器需要安装
    2. 客户端不需要

# 一些小彩蛋
你可以使用 /随机传送 来替代 /rtp  
没错，Minecraft的命令是可以存在非ASCII字符的，所以我就整了一个  
例如：  
- /rtp TheWhiteDog9487 1000
- /随机传送 TheWhiteDog9487 1000

这两个命令的效果没有任何差别  
玩家权限限制和 /rtp 当然也是一样的，都是4级