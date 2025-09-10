# 🚀 GitHub Actions Otomatik Build Rehberi

## 📋 **ADIM ADIM TALİMATLAR**

### **1. GitHub Hesabı Oluşturun**
- https://github.com adresine gidin
- "Sign up" ile ücretsiz hesap oluşturun
- Email doğrulaması yapın

### **2. Yeni Repository (Depo) Oluşturun**
1. GitHub'da "+" butonuna tıklayın
2. "New repository" seçin
3. **Repository name:** `clean-air-heroes-mod`
4. **Description:** `Educational Minecraft mod for environmental engineering`
5. ✅ **Public** seçin (herkes görebilir)
6. ✅ **Add a README file** işaretleyin
7. **Create repository** butonuna tıklayın

### **3. Dosyaları GitHub'a Yükleyin**

#### **Seçenek A: Web Arayüzü (Kolay)**
1. Repository sayfasında **"uploading an existing file"** linkine tıklayın
2. Bu klasördeki (`/home/dnds/Masaüstü/mod/`) **TÜM DOSYALARI** sürükleyip bırakın:
   ```
   📁 .github/
   📁 src/
   📄 build.gradle
   📄 gradle.properties
   📄 settings.gradle
   📄 gradlew
   📄 fabric.mod.json
   📄 README.md
   📄 INSTALLATION_GUIDE.md
   📄 WALKTHROUGH_GUIDE.md
   📄 FAQ_TROUBLESHOOTING.md
   📄 KULLANIM_REHBERI.md
   📄 GITHUB_SETUP_GUIDE.md
   ```
3. **Commit message** yazın: `Initial commit - Clean Air Heroes Mod v1.0.0`
4. **Commit changes** butonuna tıklayın

#### **Seçenek B: Git Komutları (Gelişmiş)**
```bash
cd /home/dnds/Masaüstü/mod/
git init
git add .
git commit -m "Initial commit - Clean Air Heroes Mod v1.0.0"
git branch -M main
git remote add origin https://github.com/KULLANICI_ADINIZ/clean-air-heroes-mod.git
git push -u origin main
```

### **4. Otomatik Build'i Başlatın**
1. Dosyaları yükledikten sonra **"Actions"** sekmesine tıklayın
2. **"Build Clean Air Heroes Mod"** workflow'unu göreceksiniz
3. ✅ **Otomatik olarak build başlayacak**
4. ⏱️ **5-10 dakika** bekleyin

### **5. JAR Dosyasını İndirin**

#### **Build Tamamlandığında:**
1. **Actions** sekmesinde yeşil ✅ işareti göreceksiniz
2. Build'e tıklayın
3. **"Artifacts"** bölümünde **"CleanAirHeroes-Mod-JAR"** dosyasını bulun
4. İndirin ve ZIP'i açın
5. 🎉 **cleanairheroes-1.0.0.jar** dosyanız hazır!

#### **Release Sayfasından:**
1. Repository ana sayfasında **"Releases"** sekmesine tıklayın
2. En son release'i bulun: **"Clean Air Heroes v[numara]"**
3. **Assets** altında JAR dosyalarını göreceksiniz
4. **cleanairheroes-1.0.0.jar** dosyasını indirin

## 🔄 **OTOMATİK GÜNCELLEME SİSTEMİ**

### **Her Değişiklikte Otomatik Build:**
- Dosyalarda değişiklik yapıp GitHub'a yüklediğinizde
- Otomatik olarak yeni JAR dosyası oluşur
- Yeni release otomatik yayınlanır
- Herkesle paylaşmak için link verebilirsiniz

### **Güncelleme Yapmak İçin:**
1. Dosyaları değiştirin
2. GitHub'a yükleyin  
3. 5-10 dakika bekleyin
4. Yeni JAR hazır! ✨

## 🎯 **PAYLAŞIM LİNKLERİ**

**Repository Link:** 
```
https://github.com/KULLANICI_ADINIZ/clean-air-heroes-mod
```

**Son Release Link:**
```
https://github.com/KULLANICI_ADINIZ/clean-air-heroes-mod/releases/latest
```

**Direkt JAR İndirme:**
```
https://github.com/KULLANICI_ADINIZ/clean-air-heroes-mod/releases/latest/download/cleanairheroes-1.0.0.jar
```

## 📱 **SOSYAL MEDYA PAYLAŞIMI**

### **Örnek Paylaşım Metni:**
```
🌍 Clean Air Heroes - Eğitimsel Minecraft Modu yayında!

🎮 5 gerçek dünya bölgesinde çevre mühendisliği öğrenin
🏭 21 görev + 16 mini oyun
🌱 Varna, Zonguldak, Odesa, Trabzon, Romanya
📚 Tam İngilizce-Türkçe destek

📦 Ücretsiz İndir: [GitHub Link]
🎓 Eğitim kurumları için hazır!

#MinecraftEducation #ÇevreMühendisliği #Eğitim #OpenSource
```

## ⚡ **HIZLI BAŞLANGIÇ ÖZETİ**

1. **GitHub hesabı** oluştur
2. **Repository** oluştur  
3. **Dosyaları yükle** (drag & drop)
4. **5-10 dakika bekle**
5. **JAR dosyasını indir** ✅
6. **Dünyayla paylaş** 🌍

## 🎉 **TEBRIKLER!**

Artık:
- ✅ Otomatik build sisteminiz var
- ✅ JAR dosyası otomatik oluşuyor  
- ✅ Herkesle paylaşabilirsiniz
- ✅ Güncellemeler otomatik
- ✅ Profesyonel proje yönetimi

**Bu büyük bir başarı!** 🏆

## 🆘 **SORUN YAŞARSANIZ**

### **Build Başarısız Olursa:**
1. Actions sekmesinde kırmızı ❌ işareti göreceksiniz
2. Tıklayıp hata mesajını kontrol edin
3. Genellikle dosya eksikliği veya syntax hatası
4. Dosyaları tekrar kontrol edip yükleyin

### **Dosya Yükleme Sorunu:**
- Dosyaların tümünü seçtiğinizden emin olun
- Klasör yapısını koruyun (.github klasörü önemli)
- Büyük dosyalar varsa parça parça yükleyin

### **GitHub Kullanım Desteği:**
- GitHub documentation: https://docs.github.com
- GitHub community: https://github.community
- YouTube'da "GitHub tutorial" araması

**Herhangi bir sorunla karşılaştığınızda bu rehberi takip edin!** 📚