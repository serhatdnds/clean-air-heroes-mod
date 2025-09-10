package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class ResearchDataScreen extends Screen {
    
    private static final int GUI_WIDTH = 1500;
    private static final int GUI_HEIGHT = 950;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private int selectedCategory = 0; // 0: All, 1: Air Quality, 2: Equipment, 3: Environmental, 4: Social
    private int selectedPaper = -1;
    private float dataStreamAnimation = 0.0f;
    private int scrollOffset = 0;
    
    public ResearchDataScreen() {
        super(Text.literal("Environmental Research Database"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
        }
        
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Category filter buttons
        String[] categories = {"All Research", "Air Quality", "Equipment Tech", "Environmental", "Social Impact"};
        int categoryWidth = 180;
        
        for (int i = 0; i < categories.length; i++) {
            final int categoryIndex = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categories[i]), button -> {
                selectedCategory = categoryIndex;
                selectedPaper = -1;
                scrollOffset = 0;
            }).dimensions(centerX + 50 + i * (categoryWidth + 15), centerY + 60, categoryWidth, 25).build());
        }
        
        // Search and filter options
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🔍 Advanced Search"), button -> {
            // Implement advanced search
        }).dimensions(centerX + GUI_WIDTH - 200, centerY + 60, 150, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📊 Data Export"), button -> {
            // Implement data export
        }).dimensions(centerX + GUI_WIDTH - 200, centerY + 90, 150, 25).build());
        
        // Scroll controls
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↑"), button -> {
            scrollOffset = Math.max(0, scrollOffset - 100);
        }).dimensions(centerX + GUI_WIDTH - 40, centerY + 150, 30, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↓"), button -> {
            scrollOffset = Math.min(2000, scrollOffset + 100);
        }).dimensions(centerX + GUI_WIDTH - 40, centerY + 180, 30, 25).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(centerX + 40, centerY + GUI_HEIGHT - 50, 250, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        this.dataStreamAnimation += 0.05f;
        if (this.dataStreamAnimation > 1.0f) this.dataStreamAnimation = 0.0f;
        
        // Render scientific background
        renderScientificBackground(context);
        
        // Main database interface
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xF0F6F9FA);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF0F766E);
        
        // Header
        renderDatabaseHeader(context);
        
        // Left panel: Research papers list
        renderResearchList(context, mouseX, mouseY);
        
        // Right panel: Selected paper details
        renderPaperDetails(context);
        
        // Bottom panel: Live data feeds
        renderLiveDataFeeds(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderScientificBackground(DrawContext context) {
        // Scientific grid background
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int blue = (int) (245 + (255 - 245) * factor);
            int green = (int) (249 + (255 - 249) * factor);
            int color = 0xFF000000 | (250 << 16) | (green << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
        
        // Data visualization elements (floating)
        for (int i = 0; i < 50; i++) {
            int x = (int) ((i * 127 + animationTicks * 0.3) % this.width);
            int y = (int) ((i * 211 + animationTicks * 0.2) % this.height);
            int alpha = (int) (32 + 32 * Math.sin(animationTicks * 0.01 + i));
            
            if (alpha > 40) {
                context.fill(x, y, x + 2, y + 2, (alpha << 24) | 0x000F766E);
            }
        }
    }
    
    private void renderDatabaseHeader(DrawContext context) {
        int headerHeight = 50;
        
        // Header background with data stream effect
        int streamAlpha = (int) (48 + 16 * Math.sin(animationTicks * 0.1));
        context.fill(centerX + 10, centerY + 10, centerX + GUI_WIDTH - 10, centerY + headerHeight, 
                    (streamAlpha << 24) | 0x000F766E);
        context.fill(centerX + 15, centerY + 15, centerX + GUI_WIDTH - 15, centerY + headerHeight - 5, 0xFF0F766E);
        
        // Research database icon
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🔬"),
            centerX + 30, centerY + 25, 0xFFFFFF);
        
        // Main title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("ENVIRONMENTAL RESEARCH DATABASE").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + GUI_WIDTH / 2, centerY + 25, 0xFFFFFF);
        
        // Live status indicator
        String statusText = "🟢 LIVE DATA • " + getResearchPapers().length + " Papers • Real-time Updates";
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(statusText).formatted(Formatting.GREEN),
            centerX + GUI_WIDTH - 350, centerY + 25, 0xFFFFFF);
        
        // Category breadcrumb
        String[] categoryNames = {"All", "Air Quality", "Equipment", "Environmental", "Social"};
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Category: " + categoryNames[selectedCategory]).formatted(Formatting.ITALIC),
            centerX + 30, centerY + 40, 0xFFCBD5E1);
    }
    
    private void renderResearchList(DrawContext context, int mouseX, int mouseY) {
        int listX = centerX + 20;
        int listY = centerY + 120;
        int listWidth = (GUI_WIDTH * 2) / 3 - 30;
        int listHeight = GUI_HEIGHT - 300;
        
        // List background
        context.fill(listX, listY, listX + listWidth, listY + listHeight, 0xFFFFFFFF);
        context.drawBorder(listX, listY, listWidth, listHeight, 0xFFE2E8F0);
        
        // List header
        context.fill(listX, listY, listX + listWidth, listY + 30, 0xFF0F766E);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Research Papers & Studies").formatted(Formatting.BOLD),
            listX + 15, listY + 10, 0xFFFFFF);
        
        // Paper entries
        ResearchPaper[] papers = getFilteredPapers();
        int paperHeight = 80;
        int visiblePapers = (listHeight - 40) / paperHeight;
        
        for (int i = 0; i < Math.min(papers.length, visiblePapers); i++) {
            int paperIndex = i + (scrollOffset / paperHeight);
            if (paperIndex >= papers.length) break;
            
            ResearchPaper paper = papers[paperIndex];
            int paperY = listY + 40 + i * paperHeight;
            
            // Check if mouse is hovering
            boolean isHovered = mouseX >= listX && mouseX <= listX + listWidth &&
                              mouseY >= paperY && mouseY <= paperY + paperHeight - 5;
            
            boolean isSelected = selectedPaper == paperIndex;
            
            if (isHovered || isSelected) {
                selectedPaper = paperIndex;
            }
            
            renderPaperEntry(context, listX + 5, paperY, listWidth - 10, paperHeight - 5, paper, isSelected, isHovered);
        }
        
        // Scroll indicator
        if (papers.length > visiblePapers) {
            int scrollBarHeight = Math.max(20, (visiblePapers * listHeight) / papers.length);
            int scrollBarY = listY + 40 + (scrollOffset * (listHeight - 40 - scrollBarHeight)) / 
                           Math.max(1, (papers.length - visiblePapers) * paperHeight);
            
            context.fill(listX + listWidth - 8, scrollBarY, listX + listWidth - 3, 
                        scrollBarY + scrollBarHeight, 0xFF0F766E);
        }
    }
    
    private void renderPaperEntry(DrawContext context, int x, int y, int width, int height, 
                                ResearchPaper paper, boolean isSelected, boolean isHovered) {
        // Entry background
        int backgroundColor = isSelected ? 0xFFE6FFFA : (isHovered ? 0xFFF0FDFA : 0xFFFFFFFF);
        context.fill(x, y, x + width, y + height, backgroundColor);
        
        if (isSelected) {
            context.drawBorder(x, y, width, height, 0xFF0F766E);
        }
        
        // Paper category color stripe
        context.fill(x, y, x + 4, y + height, paper.category.color);
        
        // Paper title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(paper.title).formatted(Formatting.BOLD),
            x + 15, y + 8, 0xFF1F2937);
        
        // Authors and date
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("By " + paper.authors + " • " + paper.date),
            x + 15, y + 22, 0xFF6B7280);
        
        // Abstract preview
        String[] abstractLines = wrapText(paper.abstractText, width - 30);
        for (int i = 0; i < Math.min(abstractLines.length, 2); i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(abstractLines[i]),
                x + 15, y + 38 + i * 10, 0xFF9CA3AF);
        }
        
        // Paper metrics
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Citations: " + paper.citations).formatted(Formatting.ITALIC),
            x + width - 120, y + 8, paper.category.color);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Impact: " + paper.impactFactor).formatted(Formatting.ITALIC),
            x + width - 120, y + 22, paper.category.color);
        
        // Access level indicator
        String accessIcon = paper.isOpenAccess ? "🔓" : "🔒";
        String accessText = paper.isOpenAccess ? "Open Access" : "Restricted";
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(accessIcon + " " + accessText),
            x + width - 120, y + height - 15, paper.isOpenAccess ? 0xFF10B981 : 0xFFEF4444);
    }
    
    private void renderPaperDetails(DrawContext context) {
        int detailsX = centerX + (GUI_WIDTH * 2) / 3 + 10;
        int detailsY = centerY + 120;
        int detailsWidth = GUI_WIDTH / 3 - 30;
        int detailsHeight = GUI_HEIGHT - 300;
        
        // Details background
        context.fill(detailsX, detailsY, detailsX + detailsWidth, detailsY + detailsHeight, 0xFFFFFFFF);
        context.drawBorder(detailsX, detailsY, detailsWidth, detailsHeight, 0xFFE2E8F0);
        
        if (selectedPaper >= 0) {
            ResearchPaper[] papers = getFilteredPapers();
            if (selectedPaper < papers.length) {
                ResearchPaper paper = papers[selectedPaper];
                renderSelectedPaperDetails(context, detailsX + 15, detailsY + 15, detailsWidth - 30, paper);
            }
        } else {
            renderDatabaseOverview(context, detailsX + 15, detailsY + 15, detailsWidth - 30);
        }
    }
    
    private void renderSelectedPaperDetails(DrawContext context, int x, int y, int width, ResearchPaper paper) {
        int currentY = y;
        
        // Paper title
        String[] titleLines = wrapText(paper.title, width);
        for (String line : titleLines) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(line).formatted(Formatting.BOLD),
                x, currentY, 0xFF1F2937);
            currentY += 12;
        }
        currentY += 10;
        
        // Category and metadata
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Category: " + paper.category.name).formatted(Formatting.ITALIC),
            x, currentY, paper.category.color);
        currentY += 15;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Authors: " + paper.authors),
            x, currentY, 0xFF6B7280);
        currentY += 12;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Published: " + paper.date),
            x, currentY, 0xFF6B7280);
        currentY += 12;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Journal: " + paper.journal),
            x, currentY, 0xFF6B7280);
        currentY += 20;
        
        // Abstract
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Abstract:").formatted(Formatting.BOLD),
            x, currentY, 0xFF1F2937);
        currentY += 15;
        
        String[] abstractLines = wrapText(paper.abstractText, width);
        for (int i = 0; i < Math.min(abstractLines.length, 15); i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(abstractLines[i]),
                x, currentY, 0xFF4B5563);
            currentY += 11;
        }
        currentY += 15;
        
        // Key findings
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Key Findings:").formatted(Formatting.BOLD),
            x, currentY, 0xFF1F2937);
        currentY += 15;
        
        for (String finding : paper.keyFindings) {
            String[] findingLines = wrapText("• " + finding, width);
            for (String line : findingLines) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(line),
                    x, currentY, 0xFF059669);
                currentY += 11;
            }
            currentY += 5;
        }
        currentY += 10;
        
        // Impact metrics
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Impact Metrics:").formatted(Formatting.BOLD),
            x, currentY, 0xFF1F2937);
        currentY += 15;
        
        String[] metrics = {
            "Citations: " + paper.citations,
            "Impact Factor: " + paper.impactFactor,
            "Downloads: " + paper.downloads,
            "Peer Reviews: " + paper.peerReviews
        };
        
        for (String metric : metrics) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(metric),
                x, currentY, 0xFF7C3AED);
            currentY += 12;
        }
        
        // Access information
        currentY += 10;
        String accessStatus = paper.isOpenAccess ? "✅ Open Access Available" : "🔒 Institutional Access Required";
        int accessColor = paper.isOpenAccess ? 0xFF10B981 : 0xFFEF4444;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(accessStatus),
            x, currentY, accessColor);
    }
    
    private void renderDatabaseOverview(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Database Overview").formatted(Formatting.BOLD),
            x, y, 0xFF1F2937);
        
        int currentY = y + 25;
        
        String[] overviewText = {
            "Welcome to the Environmental",
            "Research Database.",
            "",
            "This comprehensive collection",
            "contains peer-reviewed research",
            "papers, studies, and reports",
            "related to air quality, pollution",
            "control, and environmental",
            "protection technologies.",
            "",
            "Select a paper from the list",
            "to view detailed information,",
            "abstracts, and key findings.",
            "",
            "Categories:",
            "• Air Quality Research",
            "• Equipment Technology",
            "• Environmental Impact",
            "• Social Studies",
            "",
            "Total Papers: " + getResearchPapers().length,
            "Open Access: " + countOpenAccess(),
            "Last Updated: Real-time"
        };
        
        for (String line : overviewText) {
            if (line.startsWith("•")) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(line),
                    x + 10, currentY, 0xFF059669);
            } else if (line.contains(":")) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(line).formatted(Formatting.BOLD),
                    x, currentY, 0xFF7C3AED);
            } else {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(line),
                    x, currentY, 0xFF6B7280);
            }
            currentY += 12;
        }
    }
    
    private void renderLiveDataFeeds(DrawContext context) {
        int feedsY = centerY + GUI_HEIGHT - 160;
        int feedsHeight = 110;
        
        // Feeds background
        context.fill(centerX + 20, feedsY, centerX + GUI_WIDTH - 20, feedsY + feedsHeight, 0xFF1F2937);
        context.drawBorder(centerX + 20, feedsY, GUI_WIDTH - 40, feedsHeight, 0xFF374151);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🔴 Live Data Feeds").formatted(Formatting.BOLD),
            centerX + 30, feedsY + 10, 0xFFEF4444);
        
        // Data streams
        renderDataStream(context, centerX + 40, feedsY + 30, "Air Quality Index", "Real-time AQI monitoring", 
                        generateLiveValue(45, 85), "AQI", 0xFF10B981);
        
        renderDataStream(context, centerX + 300, feedsY + 30, "PM2.5 Levels", "Particulate matter tracking", 
                        generateLiveValue(15, 35), "µg/m³", 0xFFF59E0B);
        
        renderDataStream(context, centerX + 560, feedsY + 30, "Equipment Status", "Active monitoring devices", 
                        generateLiveValue(85, 99), "%", 0xFF3B82F6);
        
        renderDataStream(context, centerX + 820, feedsY + 30, "Research Citations", "New citations detected", 
                        generateLiveValue(120, 180), "today", 0xFF8B5CF6);
        
        renderDataStream(context, centerX + 1080, feedsY + 30, "Global Impact", "Pollution reduction rate", 
                        generateLiveValue(2.1f, 3.8f), "kg/min", 0xFF06B6D4);
    }
    
    private void renderDataStream(DrawContext context, int x, int y, String title, String description, 
                                String value, String unit, int color) {
        // Stream indicator (animated)
        int indicatorAlpha = (int) (128 + 64 * Math.sin(animationTicks * 0.15 + x * 0.01));
        context.fill(x - 5, y, x, y + 60, (indicatorAlpha << 24) | (color & 0x00FFFFFF));
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(title).formatted(Formatting.BOLD),
            x + 5, y, 0xFFFFFF);
        
        // Description
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(description),
            x + 5, y + 12, 0xFFD1D5DB);
        
        // Value
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(value + " " + unit).formatted(Formatting.BOLD),
            x + 5, y + 30, color);
        
        // Trend indicator
        String trend = Math.random() > 0.5 ? "↗ +2.1%" : "↘ -1.5%";
        int trendColor = trend.startsWith("↗") ? 0xFF10B981 : 0xFFEF4444;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(trend),
            x + 5, y + 45, trendColor);
    }
    
    private String generateLiveValue(float min, float max) {
        float value = min + (float) Math.random() * (max - min);
        return String.format("%.1f", value);
    }
    
    private ResearchPaper[] getFilteredPapers() {
        ResearchPaper[] allPapers = getResearchPapers();
        
        if (selectedCategory == 0) {
            return allPapers;
        }
        
        ResearchCategory targetCategory = ResearchCategory.values()[selectedCategory - 1];
        return java.util.Arrays.stream(allPapers)
            .filter(paper -> paper.category == targetCategory)
            .toArray(ResearchPaper[]::new);
    }
    
    private ResearchPaper[] getResearchPapers() {
        return new ResearchPaper[] {
            new ResearchPaper(
                "Real-time Air Quality Monitoring in Coastal Industrial Cities: A Machine Learning Approach",
                "Dr. Elena Petrov, Dr. Mehmet Yilmaz, Prof. Irina Komnenos",
                "Environmental Science & Technology",
                "2024-03-15",
                "This study presents a novel machine learning framework for real-time air quality prediction in coastal industrial environments, with specific focus on port cities like Varna and Odesa. Our approach combines satellite data, ground-based sensors, and meteorological information to provide accurate AQI forecasts with 94% accuracy.",
                new String[]{
                    "Machine learning models achieved 94% accuracy in AQI prediction",
                    "Coastal factors significantly impact pollution dispersion patterns",
                    "Real-time monitoring reduces health incidents by 23%"
                },
                ResearchCategory.AIR_QUALITY,
                156,
                8.7f,
                2340,
                12,
                true
            ),
            
            new ResearchPaper(
                "Thermal Power Plant Emission Control: Advanced Filtration Technologies for Coal-Burning Facilities",
                "Prof. Aleksandar Dimitrov, Dr. Ayşe Kaya, Dr. Pavel Ionescu",
                "Journal of Clean Production",
                "2024-02-28",
                "Comprehensive analysis of advanced emission control technologies implemented in coal-burning thermal power plants across the Black Sea region. The study evaluates the effectiveness of electrostatic precipitators, fabric filters, and wet scrubbing systems in reducing particulate matter and sulfur dioxide emissions.",
                new String[]{
                    "Advanced filtration reduces PM emissions by 89%",
                    "Wet scrubbing systems achieve 95% SO2 removal efficiency",
                    "Retrofitting existing plants is 60% more cost-effective than replacement"
                },
                ResearchCategory.EQUIPMENT,
                89,
                7.2f,
                1890,
                8,
                true
            ),
            
            new ResearchPaper(
                "Maritime Shipping Emissions in the Black Sea: Impact Assessment and Mitigation Strategies",
                "Dr. Natasha Kozlova, Capt. Dimitris Papadopoulos, Dr. Oleg Marinescu",
                "Marine Pollution Bulletin",
                "2024-01-20",
                "Detailed assessment of shipping emissions impact on air quality in major Black Sea ports including Varna, Odesa, and Constanta. The research examines the effectiveness of green shipping initiatives, alternative fuels, and port electrification in reducing maritime pollution.",
                new String[]{
                    "Shipping accounts for 35% of port city air pollution",
                    "Shore power systems reduce in-port emissions by 70%",
                    "LNG conversion decreases NOx emissions by 45%"
                },
                ResearchCategory.ENVIRONMENTAL,
                134,
                9.1f,
                2890,
                15,
                false
            ),
            
            new ResearchPaper(
                "Community Engagement in Environmental Monitoring: Citizen Science Applications for Air Quality",
                "Dr. Mariana Popescu, Prof. Georgios Konstantinidis, Dr. Svetlana Nikolaev",
                "Environmental Research Letters",
                "2024-03-10",
                "Investigation of citizen science programs' effectiveness in environmental monitoring across five regions. The study demonstrates how community participation enhances data collection coverage and promotes environmental awareness through gamified monitoring applications.",
                new String[]{
                    "Citizen monitoring increases data points by 340%",
                    "Community engagement improves environmental awareness by 67%",
                    "Gamification increases participation rates to 78%"
                },
                ResearchCategory.SOCIAL,
                67,
                6.8f,
                1450,
                9,
                true
            ),
            
            new ResearchPaper(
                "Urban Tree Canopy Effects on Air Quality: Multi-city Analysis of Pollution Absorption Capacity",
                "Prof. Maria Stefanova, Dr. Ahmet Demir, Dr. Radu Constantinescu",
                "Urban Forestry & Urban Greening",
                "2024-02-15",
                "Comprehensive study of urban forest impact on air quality across multiple cities in the Black Sea region. Research quantifies the pollution absorption capacity of different tree species and optimal urban forest management strategies for maximum air quality improvement.",
                new String[]{
                    "Urban forests reduce particulate matter by 27%",
                    "Native species show 40% higher pollution absorption",
                    "Strategic tree placement increases effectiveness by 55%"
                },
                ResearchCategory.ENVIRONMENTAL,
                201,
                10.3f,
                3120,
                18,
                true
            ),
            
            new ResearchPaper(
                "Smart Sensor Networks for Industrial Emission Monitoring: IoT-Based Solutions",
                "Dr. Vasil Petkov, Eng. Murat Özkan, Dr. Ana Cristescu",
                "Sensors and Actuators B: Chemical",
                "2024-01-08",
                "Development and deployment of IoT-based sensor networks for continuous industrial emission monitoring. The system provides real-time data transmission, automated alert systems, and predictive maintenance capabilities for industrial air quality management.",
                new String[]{
                    "IoT sensors reduce monitoring costs by 65%",
                    "Real-time alerts enable 80% faster response times",
                    "Predictive maintenance prevents 92% of sensor failures"
                },
                ResearchCategory.EQUIPMENT,
                78,
                7.9f,
                1670,
                11,
                false
            ),
            
            new ResearchPaper(
                "Economic Impact Assessment of Air Quality Improvement Programs in Industrial Regions",
                "Prof. Daniela Gheorghiu, Dr. Kemal Arslan, Dr. Borislav Stoyanov",
                "Environmental Economics and Policy Studies",
                "2024-03-05",
                "Comprehensive economic analysis of air quality improvement initiatives across industrial regions. The study evaluates cost-benefit ratios, job creation potential, and long-term economic benefits of environmental protection programs in the Black Sea region.",
                new String[]{
                    "Environmental programs generate 3.4:1 cost-benefit ratio",
                    "Green sector job creation increases by 180%",
                    "Health cost savings reach $2.3M annually per region"
                },
                ResearchCategory.SOCIAL,
                92,
                8.4f,
                2100,
                13,
                true
            ),
            
            new ResearchPaper(
                "Advanced Air Filtration Systems: Nanotechnology Applications in Industrial Settings",
                "Dr. Kristina Petrova, Prof. İbrahim Yılmaz, Dr. Mihai Popescu",
                "Journal of Nanoparticle Research",
                "2024-02-20",
                "Exploration of nanotechnology applications in air filtration systems for industrial environments. The research focuses on nanofiber filters, photocatalytic materials, and electrostatic enhancement technologies for superior particle capture efficiency.",
                new String[]{
                    "Nanofiber filters achieve 99.97% particle capture efficiency",
                    "Photocatalytic materials decompose 85% of organic pollutants",
                    "Energy consumption reduced by 45% compared to traditional systems"
                },
                ResearchCategory.EQUIPMENT,
                145,
                11.2f,
                2780,
                16,
                false
            )
        };
    }
    
    private int countOpenAccess() {
        return (int) java.util.Arrays.stream(getResearchPapers())
            .filter(paper -> paper.isOpenAccess)
            .count();
    }
    
    private String[] wrapText(String text, int maxWidth) {
        int charWidth = 6;
        int maxChars = maxWidth / charWidth;
        
        if (text.length() <= maxChars) {
            return new String[]{text};
        }
        
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        String currentLine = "";
        
        for (String word : words) {
            if ((currentLine + " " + word).length() <= maxChars) {
                currentLine += (currentLine.isEmpty() ? "" : " ") + word;
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine);
                }
                currentLine = word;
            }
        }
        
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }
        
        return lines.toArray(new String[0]);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper enums and classes
    private enum ResearchCategory {
        AIR_QUALITY("Air Quality", 0xFF3B82F6),
        EQUIPMENT("Equipment Tech", 0xFF10B981),
        ENVIRONMENTAL("Environmental", 0xFF059669),
        SOCIAL("Social Impact", 0xFF8B5CF6);
        
        public final String name;
        public final int color;
        
        ResearchCategory(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }
    
    private static class ResearchPaper {
        public final String title;
        public final String authors;
        public final String journal;
        public final String date;
        public final String abstractText;
        public final String[] keyFindings;
        public final ResearchCategory category;
        public final int citations;
        public final float impactFactor;
        public final int downloads;
        public final int peerReviews;
        public final boolean isOpenAccess;
        
        public ResearchPaper(String title, String authors, String journal, String date,
                           String abstractText, String[] keyFindings, ResearchCategory category,
                           int citations, float impactFactor, int downloads, int peerReviews,
                           boolean isOpenAccess) {
            this.title = title;
            this.authors = authors;
            this.journal = journal;
            this.date = date;
            this.abstractText = abstractText;
            this.keyFindings = keyFindings;
            this.category = category;
            this.citations = citations;
            this.impactFactor = impactFactor;
            this.downloads = downloads;
            this.peerReviews = peerReviews;
            this.isOpenAccess = isOpenAccess;
        }
    }
}