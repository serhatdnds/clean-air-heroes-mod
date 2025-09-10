# Clean Air Heroes - Installation Guide

## English Installation Guide

### Prerequisites

**System Requirements:**
- Java 17 or higher
- Minecraft Java Edition 1.20.4
- Fabric Loader 0.15.0 or higher
- Fabric API 0.91.0+1.20.4 or higher
- At least 4GB RAM allocated to Minecraft
- 500MB free disk space

### Step 1: Install Fabric Loader

1. **Download Fabric Installer:**
   - Go to https://fabricmc.net/use/installer/
   - Download the latest Fabric Installer (.jar file)

2. **Run Fabric Installer:**
   - Double-click the downloaded .jar file
   - Select "Client" tab
   - Choose Minecraft version: **1.20.4**
   - Choose Loader version: **0.15.0** or newer
   - Click "Install"

3. **Verify Installation:**
   - Open Minecraft Launcher
   - You should see "fabric-loader-1.20.4" in the versions list

### Step 2: Install Fabric API

1. **Download Fabric API:**
   - Go to https://modrinth.com/mod/fabric-api
   - Download version **0.91.0+1.20.4** or compatible
   - Save to your `mods` folder: `%appdata%\.minecraft\mods\` (Windows) or `~/.minecraft/mods/` (Linux/Mac)

### Step 3: Install Clean Air Heroes Mod

1. **Build the Mod:**
   ```bash
   cd /path/to/clean-air-heroes-mod
   ./gradlew build
   ```

2. **Locate Built Mod:**
   - Find the built .jar file in `build/libs/`
   - File name: `cleanairheroes-1.0.0.jar`

3. **Install Mod:**
   - Copy the .jar file to your Minecraft `mods` folder
   - Path: `%appdata%\.minecraft\mods\` (Windows) or `~/.minecraft/mods/` (Linux/Mac)

### Step 4: Launch and Play

1. **Start Minecraft:**
   - Open Minecraft Launcher
   - Select "fabric-loader-1.20.4" profile
   - Click "Play"

2. **Create New World:**
   - Click "Singleplayer" → "Create New World"
   - **Important:** Enable cheats (required for commands)
   - Click "Create New World"

3. **Start Your Journey:**
   - Open chat with `T` key
   - Type: `/cleanair start tutorial`
   - Follow the on-screen instructions

### Step 5: Key Controls

- **Mission GUI:** Press `M` key
- **Air Quality Check:** Press `Q` key  
- **Travel Hub:** Press `H` key
- **Pollution Overlay:** Press `P` key

---

## Türkçe Kurulum Rehberi

### Ön Gereksinimler

**Sistem Gereksinimleri:**
- Java 17 veya üzeri
- Minecraft Java Edition 1.20.4
- Fabric Loader 0.15.0 veya üzeri
- Fabric API 0.91.0+1.20.4 veya üzeri
- Minecraft'a en az 4GB RAM ayrılmış
- 500MB boş disk alanı

### Adım 1: Fabric Loader Kurulumu

1. **Fabric Installer İndirin:**
   - https://fabricmc.net/use/installer/ adresine gidin
   - En son Fabric Installer (.jar dosyası) indirin

2. **Fabric Installer Çalıştırın:**
   - İndirilen .jar dosyasına çift tıklayın
   - "Client" sekmesini seçin
   - Minecraft versiyonu: **1.20.4** seçin
   - Loader versiyonu: **0.15.0** veya daha yeni seçin
   - "Install" tıklayın

3. **Kurulumu Doğrulayın:**
   - Minecraft Launcher açın
   - Versiyonlar listesinde "fabric-loader-1.20.4" görmelisiniz

### Adım 2: Fabric API Kurulumu

1. **Fabric API İndirin:**
   - https://modrinth.com/mod/fabric-api adresine gidin
   - **0.91.0+1.20.4** versiyonunu veya uyumlu olanı indirin
   - `mods` klasörünüze kaydedin: `%appdata%\.minecraft\mods\` (Windows) veya `~/.minecraft/mods/` (Linux/Mac)

### Adım 3: Clean Air Heroes Mod Kurulumu

1. **Mod'u Derleyin:**
   ```bash
   cd /path/to/clean-air-heroes-mod
   ./gradlew build
   ```

2. **Derlenmiş Mod'u Bulun:**
   - `build/libs/` klasöründe derlenmiş .jar dosyasını bulun
   - Dosya adı: `cleanairheroes-1.0.0.jar`

3. **Mod'u Kurun:**
   - .jar dosyasını Minecraft `mods` klasörüne kopyalayın
   - Yol: `%appdata%\.minecraft\mods\` (Windows) veya `~/.minecraft/mods/` (Linux/Mac)

### Adım 4: Oyunu Başlatın

1. **Minecraft'ı Başlatın:**
   - Minecraft Launcher açın
   - "fabric-loader-1.20.4" profilini seçin
   - "Play" tıklayın

2. **Yeni Dünya Oluşturun:**
   - "Singleplayer" → "Create New World" tıklayın
   - **Önemli:** Cheats'i etkinleştirin (komutlar için gerekli)
   - "Create New World" tıklayın

3. **Yolculuğunuzu Başlatın:**
   - `T` tuşu ile sohbeti açın
   - Yazın: `/cleanair start tutorial`
   - Ekrandaki talimatları takip edin

### Adım 5: Temel Kontroller

- **Görev Arayüzü:** `M` tuşu
- **Hava Kalitesi Kontrolü:** `Q` tuşu
- **Seyahat Merkezi:** `H` tuşu
- **Kirlilik Katmanı:** `P` tuşu

---

## Troubleshooting / Sorun Giderme

### Common Issues / Yaygın Sorunlar

**Problem:** Mod loading error / Mod yükleme hatası
- **Solution:** Ensure Fabric API is installed and compatible version
- **Çözüm:** Fabric API'nin kurulu ve uyumlu versiyonda olduğundan emin olun

**Problem:** Commands not working / Komutlar çalışmiyor
- **Solution:** Make sure cheats are enabled in world settings
- **Çözüm:** Dünya ayarlarında cheats'in etkin olduğundan emin olun

**Problem:** Low FPS / Düşük FPS
- **Solution:** Allocate more RAM to Minecraft (recommended: 4-6GB)
- **Çözüm:** Minecraft'a daha fazla RAM ayırın (önerilen: 4-6GB)

**Problem:** Missing textures / Eksik dokular
- **Solution:** Reinstall the mod and ensure all resource files are included
- **Çözüm:** Mod'u yeniden kurun ve tüm kaynak dosyalarının dahil olduğundan emin olun

### Support / Destek

For additional support, check the mod's documentation or create an issue in the project repository.

Ek destek için mod'un belgelerini kontrol edin veya proje deposunda bir konu açın.