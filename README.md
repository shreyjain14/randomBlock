# 🎲 RandomBlock Plugin

A Minecraft Paper plugin designed for **ThrowbackSMP events** that adds magical Random Blocks - special items that transform into random Minecraft blocks when placed!

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.7-brightgreen)
![Paper](https://img.shields.io/badge/Paper-Required-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🎮 Purpose

This plugin was created specifically for **events on ThrowbackSMP**, providing a fun and unpredictable element to server activities. Players can obtain special Random Blocks that transform into any possible Minecraft block when placed, creating exciting surprises and unique gameplay moments during events.

## ✨ Features

- 🎁 **Special Random Block Item** - A glowing purple diamond block that transforms when placed
- 🔀 **Random Transformation** - Transforms into any solid, placeable Minecraft block
- ♻️ **Reusable** - Breaking the transformed block gives you the Random Block back (not the placed material)
- 🔧 **Piston Compatible** - Random Blocks maintain their special properties even when moved by pistons
- 🎨 **Visual Feedback** - See what block it transformed into with chat messages
- ⚡ **Performance Optimized** - Efficient tracking system with minimal overhead

## 📥 Installation

1. Download the latest `randomblock-X.X.X.jar` from the [Releases](../../releases) page
2. Place the JAR file in your Paper server's `plugins` folder
3. Restart or reload your server
4. Done! The plugin is ready to use

## 🎯 Usage

### Getting Random Blocks

Use the command to obtain Random Block items:

```
/randomblock [amount]
```

**Aliases:** `/rb`, `/rblock`

**Examples:**
- `/randomblock` - Get 1 Random Block
- `/randomblock 16` - Get 16 Random Blocks
- `/rb 32` - Get 32 Random Blocks

### How It Works

1. **Place the Random Block** - Place the special purple diamond block anywhere
2. **Watch the Magic** ✨ - It instantly transforms into a random Minecraft block
3. **Break to Reuse** - Breaking it gives you the Random Block item back, not the material
4. **Repeat** - Place it again for a different random block!

### Permission

- `randomblock.give` - Required to use the `/randomblock` command (OP by default)

## 🔧 Technical Details

### Requirements

- **Minecraft Version:** 1.21.7
- **Server Software:** Paper (or Paper-based forks like Purpur, Pufferfish)
- **Java Version:** 21 or higher

### How Tracking Works

The plugin maintains an internal map of placed Random Blocks by their world coordinates. When blocks are moved by pistons, the plugin automatically updates the tracking to the new location. This ensures you always get your Random Block item back, regardless of how the block was manipulated.

### Valid Random Blocks

The plugin selects from all Minecraft blocks that are:
- Solid blocks (not air or non-solid)
- Placeable blocks
- Available in the current Minecraft version

This includes everything from common blocks like Stone and Dirt to rare blocks like Ancient Debris and Dragon Eggs!

## 🎪 Event Ideas

Here are some ways to use Random Blocks in your SMP events:

- **Building Challenges** - Build with only random blocks for chaotic creations
- **Treasure Hunts** - Hide Random Blocks as mystery prizes
- **Trading Games** - Exchange Random Blocks for different results
- **Luck-Based Contests** - First to get a specific block type wins
- **PvP Arenas** - Random terrain generation during fights

## 📝 Commands Reference

| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/randomblock [amount]` | Get Random Block items (1-64) | `randomblock.give` | `/rb`, `/rblock` |

## 🐛 Known Issues

None at this time! If you encounter any bugs, please open an issue on GitHub.

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs or issues
- Suggest new features
- Submit pull requests
- Improve documentation

## 📜 License

This plugin is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Shrey Jain**
- Website: [shreyjain.me](https://shreyjain.me/)
- Server: ThrowbackSMP

## 🌟 Support

If you enjoy this plugin, consider:
- ⭐ Starring this repository
- 🐛 Reporting bugs or issues
- 💡 Suggesting new features
- 📢 Sharing it with other server owners

---

Made with ❤️ for ThrowbackSMP Events
name: Build and Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven
        
    - name: Extract version from tag
      id: version
      run: |
        VERSION=${GITHUB_REF#refs/tags/v}
        echo "VERSION=$VERSION" >> $GITHUB_OUTPUT
        echo "Extracted version: $VERSION"
        
    - name: Update pom.xml version
      run: |
        mvn versions:set -DnewVersion=${{ steps.version.outputs.VERSION }}
        mvn versions:commit
        
    - name: Update plugin.yml version
      run: |
        sed -i "s/version: '.*'/version: '${{ steps.version.outputs.VERSION }}'/" src/main/resources/plugin.yml
        
    - name: Build with Maven
      run: mvn clean package
      
    - name: Get artifact name
      id: artifact
      run: |
        ARTIFACT=$(ls target/*.jar | grep -v original)
        echo "ARTIFACT=$ARTIFACT" >> $GITHUB_OUTPUT
        echo "JAR_NAME=$(basename $ARTIFACT)" >> $GITHUB_OUTPUT
        
    - name: Create Release
      id: create_release
      uses: softprops/action-gh-release@v1
      with:
        files: ${{ steps.artifact.outputs.ARTIFACT }}
        generate_release_notes: true
        draft: false
        prerelease: false
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        
    - name: Commit version changes
      run: |
        git config --local user.email "github-actions[bot]@users.noreply.github.com"
        git config --local user.name "github-actions[bot]"
        git add pom.xml src/main/resources/plugin.yml
        git diff --staged --quiet || git commit -m "chore: bump version to ${{ steps.version.outputs.VERSION }}"
        
    - name: Push changes
      uses: ad-m/github-push-action@master
      with:
        github_token: ${{ secrets.GITHUB_TOKEN }}
        branch: ${{ github.ref }}

