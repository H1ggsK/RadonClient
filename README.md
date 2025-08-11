# Radon Client

This is a fork of Krypton Client, designed by me (`h1ggsk`) to be my idea of the perfect client. Take that as you will.

I am happy to take suggestions or hear about bugs, you can DM me on Discord `@h1ggsk` or join my server [Bugs and Exploits Central](https://discord.com/invite/VPHpTe6Z2U)

## Modules
### Client
- Radon: Holds all the settings for the client, including the command prefix and visual effects and colors
- Self-Destruct: Removes all traces of Radon from your client
### Combat
- AnchorMacro: Automatically blows up nearby respawn anchors
- AutoCrystal: Automatically blows up crystals at a high speed
- Auto Inventory Totem: Automatically equips totems for you
- DoubleAnchor: Automatically places two anchors
- Elytra Swap: Swaps an elytra and chestplate from your hotbar
- Hitboxes: Expands players' hitboxes
- HoverTotem: Automatically equips totems when hovered over in inventory
- KillauraLegit: Attacks players near you
- MaceSwap: Switches to a mace when attacking
- Static Hitboxes: Permanenently sets a player's hitbox to standing even when they are crouching, crawling, or swimming
### Donut
- AntiTrap: Kills entities client-side to escape Polish traps
- Auction Sniper: Snipes items on auction house for cheap
- AutoMap: Auto-sells maps from your hotbar for a specified price
- Auto Sell: Auto `/sell`s a specified item
- Auto Spawner Sell: Automatically drops and sells bones on `/orders` from skeleton spawners <font color="red">(untested)</font>
- Bone Dropper: Automatically drops bones from skeleton spawners
- RTPBaseFinder: <font color="red">Currently broken</font>
- Shulker Dropper: Automatically buys shulkers from `/shop` and drops them on the ground
- Tunnel Base Finder: <font color="red">Untested</font>
### Misc
- Anti Consume: Doesn't consume fireworks on boost (doesn't work on all anticheats)
- AutoEat: Automatically eats food from your hotbar when your hunger drops below a certain threshold
- Auto Firework: Switches to a firework in your hotbar and uses it when you press a keybind (it can automatically switch back)
- Auto Mine: Automatically mine in the chosen direction
- Auto Tool: Automatically switches to the best tool in your hotbar
- AutoTPA: Automatically TPs you to streamers or TPs streamers to you
- Cord Snapper: Sends base coordinates to a Discord webhook
- Elytra Glide: Starts flying when attempting to use a firework
- FastPlace: Place things fast lol
- Freecam: Move the camera freely around the player without actually moving (like spectator mode)
- Key Pearl: Switches to an ender pearl in your hotbar and throws it when you press a keybind
- NameProtect: Replaces your name with given one
### Movement
- AntiHunger: Reduces (DOES NOT ELIMINATE) hunger consumption (doesn't work on Donut)
- Flight: Fly like the wind
- NoFall: Take no fall damage
### Render
- Fullbright: See at full brightness level in the darkest of locations
- HUD: Customizable heads-up display for client information
- NethFinder: Highlights ancient debris in the nether when given a server's seed (does not work on Donut, `archivePedro` shuffled debris around after I first released this lol)
- NoFluidOverlay: Removes the water and lava overlays when submerged in a fluid
- Player ESP: Highlights nearby players
- Storage ESP: Highlights nearby containers (shulker boxes, chests, barrels, furnaces, etc.)
- Target HUD: Displays detailed information about your target

## Commands
- `.disconnect [reason]`: Disconnects from server with optional reason
- `.panic`: Disables all currently enabled modules
- `.say [arguments]`: Prints out anything passed in, useful for typing chat messages that start with `.` (they are normally interpreted as a command)

Credits:
- `TwoNick` for the crack of Krypton
- `ryduzzz` for the sick intro animation
- Apologies to `01rin_nac`