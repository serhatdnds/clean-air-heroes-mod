package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class GlobalReportScreen extends Screen {
    
    private static final int GUI_WIDTH = 1600;
    private static final int GUI_HEIGHT = 1000;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private int currentTab = 0; // 0: Overview, 1: Environmental, 2: Economic, 3: Social
    private float chartAnimation = 0.0f;
    
    public GlobalReportScreen() {
        super(Text.literal("Global Environmental Impact Report"));
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
        // Tab navigation buttons
        String[] tabNames = {"📊 Overview", "🌱 Environmental", "💰 Economic", "👥 Social"};
        int tabWidth = 200;
        
        for (int i = 0; i < tabNames.length; i++) {
            final int tabIndex = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(tabNames[i]), button -> {
                currentTab = tabIndex;
                chartAnimation = 0.0f;
            }).dimensions(centerX + 50 + i * (tabWidth + 10), centerY + 60, tabWidth, 30).build());
        }
        
        // Export options
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📄 Export PDF"), button -> {
            // Implement PDF export
        }).dimensions(centerX + GUI_WIDTH - 300, centerY + 60, 120, 30).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📊 Export CSV"), button -> {
            // Implement CSV export
        }).dimensions(centerX + GUI_WIDTH - 170, centerY + 60, 120, 30).build());
        
        // Time period filters
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 Last Week"), button -> {
            // Implement time filter
        }).dimensions(centerX + 50, centerY + GUI_HEIGHT - 100, 120, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 Last Month"), button -> {
            // Implement time filter
        }).dimensions(centerX + 180, centerY + GUI_HEIGHT - 100, 120, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 All Time"), button -> {
            // Implement time filter
        }).dimensions(centerX + 310, centerY + GUI_HEIGHT - 100, 120, 25).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(centerX + GUI_WIDTH - 250, centerY + GUI_HEIGHT - 50, 230, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        this.chartAnimation += 0.02f;
        if (this.chartAnimation > 1.0f) this.chartAnimation = 1.0f;
        
        // Render clean background
        renderCleanBackground(context);
        
        // Main report background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xFFF8FAFC);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF1E40AF);
        
        // Header
        renderReportHeader(context);
        
        // Tab content
        switch (currentTab) {
            case 0 -> renderOverviewTab(context);
            case 1 -> renderEnvironmentalTab(context);
            case 2 -> renderEconomicTab(context);
            case 3 -> renderSocialTab(context);
        }
        
        // Footer with summary statistics
        renderReportFooter(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderCleanBackground(DrawContext context) {
        // Light gradient background
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int blue = (int) (240 + (255 - 240) * factor);
            int green = (int) (248 + (255 - 248) * factor);
            int color = 0xFF000000 | (255 << 16) | (green << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
    }
    
    private void renderReportHeader(DrawContext context) {
        int headerHeight = 50;
        
        // Header background with professional gradient
        context.fill(centerX + 10, centerY + 10, centerX + GUI_WIDTH - 10, centerY + headerHeight, 0xFF1E40AF);
        
        // Report title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🌍 GLOBAL ENVIRONMENTAL IMPACT REPORT").formatted(Formatting.BOLD, Formatting.WHITE),
            centerX + GUI_WIDTH / 2, centerY + 25, 0xFFFFFF);
        
        // Generation timestamp
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Generated on March 21, 2024 - Real-time Data").formatted(Formatting.ITALIC),
            centerX + GUI_WIDTH / 2, centerY + 40, 0xFFCBD5E1);
        
        // Tab indicators
        renderTabIndicators(context);
    }
    
    private void renderTabIndicators(DrawContext context) {
        String[] tabNames = {"Overview", "Environmental", "Economic", "Social"};
        int indicatorY = centerY + 55;
        
        for (int i = 0; i < tabNames.length; i++) {
            int indicatorX = centerX + 50 + i * 210;
            boolean isActive = i == currentTab;
            
            if (isActive) {
                context.fill(indicatorX, indicatorY + 35, indicatorX + 200, indicatorY + 38, 0xFF1E40AF);
            }
        }
    }
    
    private void renderOverviewTab(DrawContext context) {
        int contentY = centerY + 120;
        int contentHeight = GUI_HEIGHT - 200;
        
        // Key metrics cards
        renderKeyMetricsCards(context, contentY);
        
        // Impact trend chart
        renderImpactTrendChart(context, centerX + 50, contentY + 180, GUI_WIDTH - 100, 200);
        
        // Regional comparison
        renderRegionalComparison(context, centerX + 50, contentY + 400, GUI_WIDTH - 100, 150);
    }
    
    private void renderKeyMetricsCards(DrawContext context, int startY) {
        if (playerProgress == null) return;
        
        MetricCard[] metrics = {
            new MetricCard("🌍", "Total CO2 Reduced", playerProgress.getTotalPollutionReduced() + " kg", 
                          "+15% vs last month", 0xFF10B981),
            new MetricCard("⚡", "Energy Saved", "2,450 kWh", "+8% efficiency", 0xFFF59E0B),
            new MetricCard("💧", "Water Conserved", "1,200 L", "+22% vs baseline", 0xFF3B82F6),
            new MetricCard("🌱", "Trees Equivalent", "126 trees", "Carbon offset impact", 0xFF10B981),
            new MetricCard("🏭", "Equipment Deployed", playerProgress.getEquipmentInstalled() + " units", 
                          "Across 5 regions", 0xFF8B5CF6)
        };
        
        int cardWidth = 280;
        int cardHeight = 120;
        int spacing = 20;
        int cardsPerRow = 5;
        
        for (int i = 0; i < metrics.length; i++) {
            int col = i % cardsPerRow;
            int x = centerX + 50 + col * (cardWidth + spacing);
            int y = startY;
            
            renderMetricCard(context, x, y, cardWidth, cardHeight, metrics[i]);
        }
    }
    
    private void renderMetricCard(DrawContext context, int x, int y, int width, int height, MetricCard metric) {
        // Card background
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        // Accent stripe
        context.fill(x, y, x + width, y + 4, metric.accentColor);
        
        // Icon
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(metric.icon),
            x + 20, y + 20, metric.accentColor);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(metric.title).formatted(Formatting.BOLD),
            x + 50, y + 20, 0xFF1F2937);
        
        // Value
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(metric.value).formatted(Formatting.BOLD),
            x + 20, y + 45, metric.accentColor);
        
        // Change indicator
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(metric.change).formatted(Formatting.ITALIC),
            x + 20, y + 70, 0xFF6B7280);
    }
    
    private void renderImpactTrendChart(DrawContext context, int x, int y, int width, int height) {
        // Chart background
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        // Chart title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Environmental Impact Trend").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Chart area
        int chartX = x + 60;
        int chartY = y + 40;
        int chartWidth = width - 120;
        int chartHeight = height - 80;
        
        // Grid lines
        for (int i = 0; i <= 5; i++) {
            int gridY = chartY + (i * chartHeight / 5);
            context.fill(chartX, gridY, chartX + chartWidth, gridY + 1, 0xFFE5E7EB);
        }
        
        for (int i = 0; i <= 12; i++) {
            int gridX = chartX + (i * chartWidth / 12);
            context.fill(gridX, chartY, gridX + 1, chartY + chartHeight, 0xFFE5E7EB);
        }
        
        // Data lines (animated)
        renderAnimatedDataLine(context, chartX, chartY, chartWidth, chartHeight, 
                             generateTrendData(), 0xFF10B981);
        renderAnimatedDataLine(context, chartX, chartY, chartWidth, chartHeight, 
                             generateSecondaryTrendData(), 0xFF3B82F6);
        
        // Legend
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("— Pollution Reduced"),
            x + width - 180, y + 15, 0xFF10B981);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("— Energy Saved"),
            x + width - 180, y + 30, 0xFF3B82F6);
    }
    
    private void renderAnimatedDataLine(DrawContext context, int chartX, int chartY, 
                                      int chartWidth, int chartHeight, float[] data, int color) {
        int animatedPoints = (int) (data.length * chartAnimation);
        
        for (int i = 1; i < animatedPoints; i++) {
            int x1 = chartX + ((i - 1) * chartWidth / (data.length - 1));
            int y1 = chartY + chartHeight - (int) (data[i - 1] * chartHeight);
            int x2 = chartX + (i * chartWidth / (data.length - 1));
            int y2 = chartY + chartHeight - (int) (data[i] * chartHeight);
            
            drawLine(context, x1, y1, x2, y2, color);
            
            // Data points
            context.fill(x2 - 2, y2 - 2, x2 + 2, y2 + 2, color);
        }
    }
    
    private void renderRegionalComparison(DrawContext context, int x, int y, int width, int height) {
        // Comparison background
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Regional Impact Comparison").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Regional bars
        String[] regions = {"Varna", "Zonguldak", "Odesa", "Trabzon", "Romania"};
        float[] impacts = {0.85f, 0.72f, 0.68f, 0.45f, 0.15f};
        int[] colors = {0xFF10B981, 0xFFF59E0B, 0xFF3B82F6, 0xFFEF4444, 0xFF8B5CF6};
        
        int barHeight = 20;
        int barSpacing = 25;
        int maxBarWidth = width - 200;
        
        for (int i = 0; i < regions.length; i++) {
            int barY = y + 40 + i * barSpacing;
            int barWidth = (int) (maxBarWidth * impacts[i] * chartAnimation);
            
            // Region name
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(regions[i]),
                x + 20, barY + 5, 0xFF1F2937);
            
            // Progress bar
            context.fill(x + 120, barY, x + 120 + maxBarWidth, barY + barHeight, 0xFFF1F5F9);
            context.fill(x + 120, barY, x + 120 + barWidth, barY + barHeight, colors[i]);
            
            // Value
            context.drawTextWithShadow(this.textRenderer,
                Text.literal((int)(impacts[i] * 100) + "%"),
                x + 120 + maxBarWidth + 10, barY + 5, colors[i]);
        }
    }
    
    private void renderEnvironmentalTab(DrawContext context) {
        int contentY = centerY + 120;
        
        // Environmental metrics
        renderEnvironmentalMetrics(context, contentY);
        
        // Pollution reduction chart
        renderPollutionChart(context, centerX + 50, contentY + 200, GUI_WIDTH / 2 - 75, 250);
        
        // Air quality improvement
        renderAirQualityChart(context, centerX + GUI_WIDTH / 2 + 25, contentY + 200, GUI_WIDTH / 2 - 75, 250);
        
        // Species impact
        renderSpeciesImpact(context, centerX + 50, contentY + 470, GUI_WIDTH - 100, 150);
    }
    
    private void renderEnvironmentalMetrics(DrawContext context, int startY) {
        MetricCard[] envMetrics = {
            new MetricCard("🌬️", "Air Quality Index", "Good (45 AQI)", "Improved by 35%", 0xFF10B981),
            new MetricCard("🌡️", "Temperature Impact", "-0.2°C", "Local cooling effect", 0xFF3B82F6),
            new MetricCard("🌊", "Water Quality", "Excellent", "95% purity maintained", 0xFF06B6D4),
            new MetricCard("🦋", "Biodiversity Index", "Stable+", "3 species recovered", 0xFF8B5CF6)
        };
        
        int cardWidth = 350;
        int cardHeight = 120;
        int spacing = 25;
        
        for (int i = 0; i < envMetrics.length; i++) {
            int col = i % 4;
            int x = centerX + 50 + col * (cardWidth + spacing);
            
            renderMetricCard(context, x, startY, cardWidth, cardHeight, envMetrics[i]);
        }
    }
    
    private void renderPollutionChart(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Pollution Reduction by Type").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Pie chart simulation
        String[] pollutants = {"PM2.5", "NO2", "SO2", "CO"};
        float[] percentages = {0.35f, 0.28f, 0.22f, 0.15f};
        int[] pieColors = {0xFFEF4444, 0xFFF59E0B, 0xFF8B5CF6, 0xFF10B981};
        
        int centerPieX = x + width / 2;
        int centerPieY = y + height / 2 + 10;
        int radius = 60;
        
        for (int i = 0; i < pollutants.length; i++) {
            // Simple pie representation using filled rectangles
            int segmentHeight = (int) (percentages[i] * 80);
            context.fill(x + 30 + i * 60, centerPieY + 40 - segmentHeight, 
                        x + 80 + i * 60, centerPieY + 40, pieColors[i]);
            
            // Labels
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(pollutants[i]),
                x + 30 + i * 60, centerPieY + 50, 0xFF1F2937);
            context.drawTextWithShadow(this.textRenderer,
                Text.literal((int)(percentages[i] * 100) + "%"),
                x + 30 + i * 60, centerPieY + 65, pieColors[i]);
        }
    }
    
    private void renderAirQualityChart(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Air Quality Improvement").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Before/After comparison bars
        String[] periods = {"Before", "After"};
        int[] aqiValues = {85, 45};
        int[] barColors = {0xFFEF4444, 0xFF10B981};
        
        int barWidth = 80;
        int maxBarHeight = 150;
        
        for (int i = 0; i < periods.length; i++) {
            int barX = x + 50 + i * 150;
            int barHeight = (aqiValues[i] * maxBarHeight / 100);
            int barY = y + height - 50 - barHeight;
            
            context.fill(barX, barY, barX + barWidth, y + height - 50, barColors[i]);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(periods[i]),
                barX + barWidth / 2, y + height - 40, 0xFF1F2937);
                
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(String.valueOf(aqiValues[i])),
                barX + barWidth / 2, barY - 15, barColors[i]);
        }
        
        // Improvement arrow
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("→ 47% Improvement"),
            x + 300, y + height / 2, 0xFF10B981);
    }
    
    private void renderSpeciesImpact(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Biodiversity Impact Assessment").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        String[] species = {"🐦 Migratory Birds", "🦋 Butterflies", "🐝 Pollinators", "🌊 Marine Life"};
        String[] impacts = {"Population +15%", "Diversity +22%", "Activity +18%", "Health +12%"};
        int[] impactColors = {0xFF10B981, 0xFF8B5CF6, 0xFFF59E0B, 0xFF06B6D4};
        
        for (int i = 0; i < species.length; i++) {
            int itemY = y + 45 + i * 25;
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(species[i]),
                x + 20, itemY, 0xFF1F2937);
                
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(impacts[i]).formatted(Formatting.BOLD),
                x + 200, itemY, impactColors[i]);
        }
    }
    
    private void renderEconomicTab(DrawContext context) {
        int contentY = centerY + 120;
        
        renderEconomicMetrics(context, contentY);
        renderCostBenefitAnalysis(context, centerX + 50, contentY + 200, GUI_WIDTH - 100, 300);
        renderROIChart(context, centerX + 50, contentY + 520, GUI_WIDTH - 100, 200);
    }
    
    private void renderEconomicMetrics(DrawContext context, int startY) {
        MetricCard[] ecoMetrics = {
            new MetricCard("💰", "Cost Savings", "$45,230", "vs traditional methods", 0xFF10B981),
            new MetricCard("⚡", "Energy Efficiency", "89%", "+12% improvement", 0xFFF59E0B),
            new MetricCard("🏭", "ROI", "340%", "Over 2 years", 0xFF3B82F6),
            new MetricCard("💼", "Jobs Created", "28 positions", "Green sector growth", 0xFF8B5CF6)
        };
        
        int cardWidth = 350;
        int cardHeight = 120;
        int spacing = 25;
        
        for (int i = 0; i < ecoMetrics.length; i++) {
            int col = i % 4;
            int x = centerX + 50 + col * (cardWidth + spacing);
            
            renderMetricCard(context, x, startY, cardWidth, cardHeight, ecoMetrics[i]);
        }
    }
    
    private void renderCostBenefitAnalysis(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Cost-Benefit Analysis").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Cost breakdown
        String[] costCategories = {"Equipment", "Installation", "Maintenance", "Training", "Monitoring"};
        int[] costs = {45000, 15000, 8000, 5000, 3000};
        int totalCost = java.util.Arrays.stream(costs).sum();
        
        int currentY = y + 50;
        for (int i = 0; i < costCategories.length; i++) {
            float percentage = (float) costs[i] / totalCost;
            int barWidth = (int) (percentage * 300 * chartAnimation);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(costCategories[i]),
                x + 20, currentY, 0xFF1F2937);
                
            context.fill(x + 150, currentY - 5, x + 150 + barWidth, currentY + 10, 0xFFEF4444);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("$" + costs[i]),
                x + 470, currentY, 0xFFEF4444);
                
            currentY += 30;
        }
        
        // Benefits
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Benefits (Annual)").formatted(Formatting.BOLD),
            x + 600, y + 50, 0xFF1F2937);
            
        String[] benefits = {"Energy Savings: $28,000", "Health Savings: $15,000", "Productivity Gain: $12,000"};
        currentY = y + 80;
        for (String benefit : benefits) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(benefit),
                x + 600, currentY, 0xFF10B981);
            currentY += 25;
        }
    }
    
    private void renderROIChart(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Return on Investment Timeline").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // ROI progression over time
        float[] roiData = {-1.0f, -0.5f, 0.2f, 0.8f, 1.4f, 2.1f, 2.8f, 3.4f};
        String[] timeLabels = {"Q1", "Q2", "Q3", "Q4", "Y2 Q1", "Y2 Q2", "Y2 Q3", "Y2 Q4"};
        
        int chartX = x + 60;
        int chartY = y + 40;
        int chartWidth = width - 120;
        int chartHeight = height - 80;
        
        // Zero line
        int zeroY = chartY + chartHeight / 2;
        context.fill(chartX, zeroY, chartX + chartWidth, zeroY + 1, 0xFF6B7280);
        
        // ROI line
        for (int i = 1; i < roiData.length && i < roiData.length * chartAnimation; i++) {
            int x1 = chartX + ((i - 1) * chartWidth / (roiData.length - 1));
            int y1 = (int) (zeroY - (roiData[i - 1] * chartHeight / 6));
            int x2 = chartX + (i * chartWidth / (roiData.length - 1));
            int y2 = (int) (zeroY - (roiData[i] * chartHeight / 6));
            
            int lineColor = roiData[i] >= 0 ? 0xFF10B981 : 0xFFEF4444;
            drawLine(context, x1, y1, x2, y2, lineColor);
            context.fill(x2 - 2, y2 - 2, x2 + 2, y2 + 2, lineColor);
        }
    }
    
    private void renderSocialTab(DrawContext context) {
        int contentY = centerY + 120;
        
        renderSocialMetrics(context, contentY);
        renderCommunityEngagement(context, centerX + 50, contentY + 200, GUI_WIDTH / 2 - 75, 250);
        renderEducationalImpact(context, centerX + GUI_WIDTH / 2 + 25, contentY + 200, GUI_WIDTH / 2 - 75, 250);
        renderPartnershipNetwork(context, centerX + 50, contentY + 470, GUI_WIDTH - 100, 200);
    }
    
    private void renderSocialMetrics(DrawContext context, int startY) {
        MetricCard[] socialMetrics = {
            new MetricCard("👥", "Community Reach", "12,450 people", "Across 5 regions", 0xFF3B82F6),
            new MetricCard("🎓", "Education Programs", "15 completed", "485 participants", 0xFF8B5CF6),
            new MetricCard("🤝", "Partnerships", "23 active", "International cooperation", 0xFF10B981),
            new MetricCard("📱", "App Downloads", "8,200+", "Mobile engagement", 0xFFF59E0B)
        };
        
        int cardWidth = 350;
        int cardHeight = 120;
        int spacing = 25;
        
        for (int i = 0; i < socialMetrics.length; i++) {
            int col = i % 4;
            int x = centerX + 50 + col * (cardWidth + spacing);
            
            renderMetricCard(context, x, startY, cardWidth, cardHeight, socialMetrics[i]);
        }
    }
    
    private void renderCommunityEngagement(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Community Engagement").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        String[] activities = {"Workshops", "Cleanups", "Monitoring", "Events"};
        int[] participation = {1250, 890, 2100, 650};
        int maxParticipation = java.util.Arrays.stream(participation).max().orElse(1);
        
        for (int i = 0; i < activities.length; i++) {
            int barY = y + 60 + i * 40;
            int barWidth = (int) ((float) participation[i] / maxParticipation * 300 * chartAnimation);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(activities[i]),
                x + 20, barY, 0xFF1F2937);
                
            context.fill(x + 120, barY - 8, x + 120 + barWidth, barY + 8, 0xFF3B82F6);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(String.valueOf(participation[i])),
                x + 430, barY, 0xFF3B82F6);
        }
    }
    
    private void renderEducationalImpact(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Educational Impact").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Knowledge improvement metrics
        String[] metrics = {"Environmental Awareness", "Technical Skills", "Leadership Development", "Policy Understanding"};
        float[] improvements = {0.78f, 0.65f, 0.72f, 0.58f};
        
        for (int i = 0; i < metrics.length; i++) {
            int metricY = y + 60 + i * 40;
            int progressWidth = (int) (improvements[i] * 200 * chartAnimation);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(metrics[i]),
                x + 20, metricY, 0xFF1F2937);
                
            // Progress bar background
            context.fill(x + 220, metricY - 8, x + 420, metricY + 8, 0xFFF1F5F9);
            context.fill(x + 220, metricY - 8, x + 220 + progressWidth, metricY + 8, 0xFF8B5CF6);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal((int)(improvements[i] * 100) + "%"),
                x + 430, metricY, 0xFF8B5CF6);
        }
    }
    
    private void renderPartnershipNetwork(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xFFFFFFFF);
        context.drawBorder(x, y, width, height, 0xFFE2E8F0);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Partnership Network").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFF1F2937);
        
        // Partnership categories
        String[] partnerTypes = {"🏢 Corporations", "🏛️ Government", "🎓 Academic", "🌍 NGOs", "🔬 Research"};
        int[] partnerCounts = {8, 5, 4, 3, 3};
        int[] typeColors = {0xFF3B82F6, 0xFFEF4444, 0xFF8B5CF6, 0xFF10B981, 0xFFF59E0B};
        
        int circleSize = 80;
        int spacing = 150;
        
        for (int i = 0; i < partnerTypes.length; i++) {
            int circleX = x + 100 + i * spacing;
            int circleY = y + 100;
            
            // Partner circle
            context.fill(circleX - circleSize/2, circleY - circleSize/2, 
                        circleX + circleSize/2, circleY + circleSize/2, typeColors[i]);
            
            // Partner count
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(String.valueOf(partnerCounts[i])).formatted(Formatting.BOLD),
                circleX, circleY - 5, 0xFFFFFF);
            
            // Partner type
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(partnerTypes[i]),
                circleX, circleY + 60, 0xFF1F2937);
        }
    }
    
    private void renderReportFooter(DrawContext context) {
        int footerY = centerY + GUI_HEIGHT - 60;
        
        context.fill(centerX + 10, footerY, centerX + GUI_WIDTH - 10, centerY + GUI_HEIGHT - 10, 0xFF1F2937);
        
        // Summary statistics
        if (playerProgress != null) {
            String summaryText = String.format(
                "Overall Impact Score: %d | Environmental Improvement: %.1f%% | Economic ROI: %.0f%% | Social Reach: %,d people",
                playerProgress.getTotalScore(),
                85.5f, // Environmental improvement percentage
                340f, // ROI percentage
                12450 // Social reach
            );
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(summaryText).formatted(Formatting.ITALIC),
                centerX + GUI_WIDTH / 2, footerY + 20, 0xFFFFFF);
        }
        
        // Report generation info
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Report generated by Clean Air Heroes Environmental Management System"),
            centerX + GUI_WIDTH / 2, footerY + 35, 0xFF94A3B8);
    }
    
    // Helper methods
    private float[] generateTrendData() {
        return new float[]{0.1f, 0.15f, 0.25f, 0.35f, 0.45f, 0.6f, 0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.92f};
    }
    
    private float[] generateSecondaryTrendData() {
        return new float[]{0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.55f, 0.65f, 0.72f, 0.78f, 0.82f, 0.87f, 0.9f};
    }
    
    private void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        // Simple line drawing using filled rectangles
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int steps = Math.max(dx, dy);
        
        for (int i = 0; i <= steps; i++) {
            int x = x1 + (x2 - x1) * i / steps;
            int y = y1 + (y2 - y1) * i / steps;
            context.fill(x, y, x + 1, y + 1, color);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper class for metric cards
    private static class MetricCard {
        public final String icon;
        public final String title;
        public final String value;
        public final String change;
        public final int accentColor;
        
        public MetricCard(String icon, String title, String value, String change, int accentColor) {
            this.icon = icon;
            this.title = title;
            this.value = value;
            this.change = change;
            this.accentColor = accentColor;
        }
    }
}