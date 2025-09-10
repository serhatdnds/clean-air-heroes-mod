# Clean Air Heroes - FAQ & Troubleshooting Guide

## Frequently Asked Questions (FAQ)

### English FAQ

#### Game Basics

**Q: How do I start the game?**
A: After installing the mod, create a new world with cheats enabled, then type `/cleanair start tutorial` in chat.

**Q: What are the system requirements?**
A: Java 17+, Minecraft 1.20.4, Fabric Loader 0.15.0+, Fabric API 0.91.0+, and at least 4GB RAM allocated to Minecraft.

**Q: Can I play this mod in multiplayer?**
A: Yes! The mod supports multiplayer with synchronized progress tracking and team missions.

**Q: How long does it take to complete all levels?**
A: Approximately 15-20 hours for casual play, 8-12 hours for focused gameplay.

#### Gameplay Mechanics

**Q: How do I check my mission progress?**
A: Press `M` to open the Mission Control GUI, or type `/cleanair mission status` in chat.

**Q: What happens if I fail a mission?**
A: You can retry missions unlimited times. Failed missions reset automatically after 5 minutes.

**Q: How do I travel between regions?**
A: Complete all missions in your current region (70%+ efficiency), then use the Clean Air Portal in the Environmental Protection Center.

**Q: Can I skip regions or play them out of order?**
A: No, regions must be completed sequentially to follow the educational storyline.

#### Equipment and Crafting

**Q: Where do I find crafting recipes?**
A: Press `H` to open the Travel Hub which includes a Recipe Guide, or check the Achievement Gallery for unlocked recipes.

**Q: Why can't I craft certain items?**
A: Some recipes unlock as you progress. Check your current level and completed achievements in the Mission GUI.

**Q: How do I get better equipment?**
A: Equipment upgrades unlock automatically as you complete regions and achieve higher Clean Air Hero levels.

#### Technical Issues

**Q: The mod won't load / crashes on startup**
A: Ensure you have the correct versions: Minecraft 1.20.4, Fabric Loader 0.15.0+, and Fabric API 0.91.0+1.20.4. Remove conflicting mods.

**Q: Commands don't work**
A: Make sure cheats are enabled in your world settings. Commands require operator permissions in multiplayer.

**Q: Missing textures or broken graphics**
A: Reinstall the mod ensuring all files are properly placed in the mods folder. Check that your graphics drivers are updated.

**Q: Low FPS or performance issues**
A: Allocate more RAM to Minecraft (recommended 4-6GB), reduce render distance, or disable other resource-intensive mods.

#### Achievements and Progress

**Q: How do I unlock achievements?**
A: Achievements unlock automatically as you complete missions and reach efficiency targets. View progress in the Achievement Gallery (press `M` → Achievements).

**Q: Can I view my global ranking?**
A: Yes, access the Global Leaderboard through the Environmental Protection Center or type `/cleanair leaderboard`.

**Q: What happens after I complete all regions?**
A: You unlock Master Clean Air Hero status, receive the International Certificate, and gain access to New Game+ mode with advanced challenges.

---

### Türkçe SSS

#### Oyun Temelleri

**S: Oyunu nasıl başlatırım?**
C: Mod'u kurduktan sonra, hile açık yeni bir dünya oluşturun, sonra sohbete `/cleanair start tutorial` yazın.

**S: Sistem gereksinimleri nelerdir?**
C: Java 17+, Minecraft 1.20.4, Fabric Loader 0.15.0+, Fabric API 0.91.0+, ve Minecraft'a en az 4GB RAM ayrılmış.

**S: Bu mod'u çok oyunculu oynayabilir miyim?**
C: Evet! Mod çok oyunculu desteği sunar, senkronize ilerleme takibi ve takım görevleri ile.

**S: Tüm seviyeleri tamamlamak ne kadar sürer?**
C: Normal oyun için yaklaşık 15-20 saat, odaklı oyun için 8-12 saat.

#### Oyun Mekanikleri

**S: Görev ilerlememi nasıl kontrol ederim?**
C: Görev Kontrol GUI'sini açmak için `M` tuşuna basın veya sohbete `/cleanair mission status` yazın.

**S: Bir görevi başarısız olursam ne olur?**
C: Görevleri sınırsız kez tekrar deneyebilirsiniz. Başarısız görevler 5 dakika sonra otomatik sıfırlanır.

**S: Bölgeler arasında nasıl seyahat ederim?**
C: Mevcut bölgedeki tüm görevleri tamamlayın (%70+ verimlilik), sonra Çevre Koruma Merkezi'ndeki Temiz Hava Portalı'nı kullanın.

#### Ekipman ve Üretim

**S: Üretim tariflerini nerede bulabilirim?**
C: Tarif Rehberi içeren Seyahat Merkezi'ni açmak için `H` tuşuna basın veya kilit açılmış tarifler için Başarım Galerisi'ni kontrol edin.

**S: Neden bazı eşyaları üretemiyorum?**
C: Bazı tarifler ilerledikçe kilit açılır. Görev GUI'sinde mevcut seviyenizi ve tamamlanan başarımlarınızı kontrol edin.

---

## Troubleshooting Guide

### Common Installation Issues

#### Issue: "Fabric Loader Not Found"
**Symptoms:** Game won't start, missing Fabric profile
**Solutions:**
1. Reinstall Fabric Loader from https://fabricmc.net/use/installer/
2. Ensure you selected Minecraft version 1.20.4
3. Restart Minecraft Launcher and verify "fabric-loader-1.20.4" appears in versions

#### Issue: "Mod Loading Error"
**Symptoms:** Crash during mod loading, error messages about dependencies
**Solutions:**
1. Verify Fabric API version (must be 0.91.0+1.20.4 or compatible)
2. Remove conflicting mods from mods folder
3. Check mod compatibility list in fabric.mod.json
4. Ensure Java 17+ is installed and selected

#### Issue: "Missing Textures"
**Symptoms:** Purple/black blocks, missing item textures
**Solutions:**
1. Reinstall the mod completely
2. Clear Minecraft cache: Delete `.minecraft/logs` and `.minecraft/crash-reports`
3. Update graphics drivers
4. Check for corrupted mod file (redownload if necessary)

### Gameplay Issues

#### Issue: "Commands Not Working"
**Symptoms:** `/cleanair` commands don't respond
**Solutions:**
1. Enable cheats in world settings:
   - ESC → Open to LAN → Allow Cheats: ON
   - Or create new world with cheats enabled
2. In multiplayer: Ensure you have operator permissions
3. Check command syntax: `/cleanair help` for command list

#### Issue: "Missions Not Starting"
**Symptoms:** No mission objectives appear, NPCs don't respond
**Solutions:**
1. Verify you've completed tutorial: `/cleanair start tutorial`
2. Check current region: `/cleanair region status`
3. Ensure you're in correct location (use `/cleanair teleport region`)
4. Reset mission progress: `/cleanair mission reset current`

#### Issue: "Portal Not Working"
**Symptoms:** Can't travel between regions
**Solutions:**
1. Complete all required missions in current region (minimum 70% efficiency)
2. Obtain Regional Clean Air Certificate
3. Visit Environmental Protection Center building
4. Check portal activation requirements: `/cleanair portal status`

#### Issue: "Progress Not Saving"
**Symptoms:** Achievement/mission progress resets after restart
**Solutions:**
1. Ensure world is properly saved before closing
2. Check file permissions in world save directory
3. Backup world save regularly
4. Use `/cleanair save progress` command before exiting

### Performance Issues

#### Issue: "Low FPS/Lag"
**Symptoms:** Game runs slowly, choppy movement
**Solutions:**
1. **Increase RAM allocation:**
   - Minecraft Launcher → Installations → Edit Profile
   - More Options → JVM Arguments
   - Change `-Xmx4G` to `-Xmx6G` or higher
2. **Optimize settings:**
   - Reduce render distance to 8-12 chunks
   - Turn off fancy graphics
   - Disable unnecessary resource packs
3. **Close other applications**
4. **Update Java and graphics drivers**

#### Issue: "Memory Errors"
**Symptoms:** "Out of memory" crashes
**Solutions:**
1. Allocate more RAM (minimum 4GB, recommended 6GB)
2. Close other memory-intensive applications
3. Use 64-bit Java version
4. Add JVM arguments for memory optimization:
   ```
   -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200
   ```

### Network/Multiplayer Issues

#### Issue: "Desync in Multiplayer"
**Symptoms:** Different progress shown for different players
**Solutions:**
1. Ensure all players have same mod version
2. Server admin run: `/cleanair sync all`
3. Players rejoin server
4. Use `/cleanair player sync` for individual sync

#### Issue: "Server Crash"
**Symptoms:** Server shuts down unexpectedly
**Solutions:**
1. Check server logs for error messages
2. Ensure server has adequate RAM (minimum 6GB for 4+ players)
3. Update server to latest Fabric version
4. Remove conflicting server-side mods

### Data/Save Issues

#### Issue: "Corrupted World Save"
**Symptoms:** World won't load, missing progress
**Solutions:**
1. **Restore from backup:**
   - Navigate to `.minecraft/saves/[world_name]/`
   - Replace corrupted files with backup versions
2. **Repair world data:**
   - Use `/cleanair repair world` command
   - Or delete playerdata files to reset player progress only
3. **Create new world:**
   - Export important builds with structure blocks
   - Start fresh with `/cleanair import progress [old_world]`

### Advanced Troubleshooting

#### Debug Mode
Enable detailed logging for problem diagnosis:
1. Add to JVM arguments: `-Dcleanairheroes.debug=true`
2. Check logs in `.minecraft/logs/latest.log`
3. Share relevant log sections when reporting issues

#### Clean Installation
Complete mod reinstallation procedure:
1. Remove mod from mods folder
2. Delete `.minecraft/config/cleanairheroes/`
3. Clear mod cache: Delete `.minecraft/logs/` and `.minecraft/crash-reports/`
4. Restart Minecraft
5. Reinstall mod and dependencies

#### Compatibility Check
Verify mod compatibility:
```bash
# Check installed mods
ls ~/.minecraft/mods/

# Verify versions match requirements
grep -r "minecraft.*1.20.4" ~/.minecraft/mods/
```

### Getting Support

#### Before Reporting Issues:
1. **Check this FAQ first**
2. **Update to latest mod version**
3. **Test with minimal mod setup** (only Clean Air Heroes + Fabric API)
4. **Gather information:**
   - Minecraft version
   - Fabric Loader version  
   - Mod version
   - Operating system
   - Crash logs (if applicable)

#### Where to Get Help:
- **GitHub Issues:** Report bugs and technical problems
- **Documentation:** Check WALKTHROUGH_GUIDE.md for gameplay help
- **Community:** Join discussions in project forums
- **Emergency:** Use `/cleanair help` command for immediate assistance

#### Creating Good Bug Reports:
Include this information:
- **Clear description** of the problem
- **Steps to reproduce** the issue
- **Expected vs actual behavior**
- **Screenshots/videos** if relevant
- **Log files** for crashes
- **System specifications**
- **List of other installed mods**

### Contact Information

**Project Repository:** [GitHub URL]
**Issue Tracker:** [GitHub Issues URL]
**Documentation:** Check included .md files
**Version:** 1.0.0
**Last Updated:** [Current Date]

---

*This troubleshooting guide covers the most common issues. For additional support, check the project documentation or create an issue in the project repository.*