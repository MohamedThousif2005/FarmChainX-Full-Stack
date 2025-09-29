# enhanced_crop_analyzer.py
import cv2
import numpy as np
import os
import logging
from datetime import datetime

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class EnhancedCropAnalyzer:
    def __init__(self):
        self.model = None
        print("✅ ENHANCED Crop Analyzer Initialized")
        print("   🔬 Using improved feature-based analysis")
        print("   🦠 Better disease detection")
        print("   📊 Realistic health assessment")
    
    def preprocess_image(self, image):
        """Enhanced image preprocessing"""
        # Normalize image
        image = cv2.resize(image, (500, 500))
        return image
    
    def extract_comprehensive_features(self, image):
        """Extract comprehensive features for accurate analysis"""
        features = {}
        
        # Resize for consistency
        img = cv2.resize(image, (300, 300))
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        
        # 1. COLOR ANALYSIS - More accurate ranges
        # Healthy green (vibrant)
        healthy_green_lower = np.array([35, 80, 40])
        healthy_green_upper = np.array([85, 255, 200])
        healthy_green_mask = cv2.inRange(hsv, healthy_green_lower, healthy_green_upper)
        features['healthy_green'] = np.sum(healthy_green_mask > 0) / (img.shape[0] * img.shape[1])
        
        # Disease indicators
        # Yellow (chlorosis - serious issue)
        yellow_lower = np.array([20, 80, 80])
        yellow_upper = np.array([35, 255, 255])
        yellow_mask = cv2.inRange(hsv, yellow_lower, yellow_upper)
        features['disease_yellow'] = np.sum(yellow_mask > 0) / (img.shape[0] * img.shape[1])
        
        # Brown (necrosis - very serious)
        brown_lower = np.array([0, 60, 20])
        brown_upper = np.array([20, 255, 120])
        brown_mask = cv2.inRange(hsv, brown_lower, brown_upper)
        features['disease_brown'] = np.sum(brown_mask > 0) / (img.shape[0] * img.shape[1])
        
        # Dark spots/rot (severe disease)
        dark_lower = np.array([0, 0, 0])
        dark_upper = np.array([180, 255, 40])
        dark_mask = cv2.inRange(hsv, dark_lower, dark_upper)
        features['rot_dark'] = np.sum(dark_mask > 0) / (img.shape[0] * img.shape[1])
        
        # 2. TEXTURE ANALYSIS
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        
        # Edge detection (healthy plants have more edges)
        edges = cv2.Canny(gray, 50, 150)
        features['edge_density'] = np.sum(edges > 0) / (img.shape[0] * img.shape[1])
        
        # Contrast (healthy plants have good contrast)
        features['contrast'] = np.std(gray)
        
        # Sharpness
        features['sharpness'] = cv2.Laplacian(gray, cv2.CV_64F).var()
        
        # 3. COLOR VITALITY
        features['saturation'] = np.mean(hsv[:,:,1])
        features['brightness'] = np.mean(rgb) / 255.0
        features['color_richness'] = np.mean(hsv[:,:,1]) * np.mean(hsv[:,:,2]) / 10000
        
        return features
    
    def calculate_health_score(self, features, crop_type):
        """Calculate realistic health score"""
        base_score = 0.5  # Start neutral
        
        # STRONG PENALTIES for disease (this fixes the "rotten potato = good" issue)
        if features['disease_yellow'] > 0.02:  # 2% yellow area
            base_score -= features['disease_yellow'] * 2.0
        
        if features['disease_brown'] > 0.01:  # 1% brown area
            base_score -= features['disease_brown'] * 3.0
        
        if features['rot_dark'] > 0.005:  # 0.5% dark rot area
            base_score -= features['rot_dark'] * 4.0
        
        # BONUS for healthy indicators
        if features['healthy_green'] > 0.1:
            base_score += features['healthy_green'] * 0.8
        
        if features['saturation'] > 100:
            base_score += 0.1
        
        if features['edge_density'] > 0.02:  # Good edge density = healthy texture
            base_score += 0.1
        
        # Ensure score is within bounds
        return max(0.1, min(0.95, base_score))
    
    def analyze_image(self, image_array, crop_type=None):
        """Analyze crop image using enhanced feature analysis"""
        try:
            # Ensure image is in correct format
            if len(image_array.shape) == 2:  # Grayscale
                image_array = cv2.cvtColor(image_array, cv2.COLOR_GRAY2BGR)
            elif image_array.shape[2] == 4:  # RGBA
                image_array = cv2.cvtColor(image_array, cv2.COLOR_RGBA2BGR)
            
            # Preprocess
            processed_image = self.preprocess_image(image_array)
            
            # Extract features
            features = self.extract_comprehensive_features(processed_image)
            
            # Calculate health score
            health_score = self.calculate_health_score(features, crop_type)
            
            # Generate analysis
            analysis_result = self.generate_enhanced_analysis(health_score, features, crop_type)
            
            print(f"🎯 ENHANCED Analysis: {analysis_result['overall_condition']}, Score: {health_score:.2f}")
            
            return analysis_result
            
        except Exception as e:
            logger.error(f"Enhanced analysis error: {e}")
            return self.get_default_analysis()
    
    def generate_enhanced_analysis(self, health_score, features, crop_type):
        """Generate realistic analysis based on health score"""
        # REALISTIC THRESHOLDS (no more overly optimistic scores)
        if health_score >= 0.75:
            condition = "Excellent"
            shelf_life = 12
            disease_risk = "Very Low"
        elif health_score >= 0.60:
            condition = "Good"
            shelf_life = 8
            disease_risk = "Low"
        elif health_score >= 0.45:
            condition = "Fair"
            shelf_life = 5
            disease_risk = "Medium"
        elif health_score >= 0.30:
            condition = "Poor"
            shelf_life = 3
            disease_risk = "High"
        else:
            condition = "Very Poor"
            shelf_life = 1
            disease_risk = "Very High"
        
        # Generate realistic recommendations
        recommendations = self.generate_realistic_recommendations(condition, disease_risk, features, crop_type)
        
        return {
            'overall_condition': condition,
            'freshness': round(health_score * 100, 1),
            'rigorous': round(health_score * 100, 1),
            'confidence': round(health_score * 100, 1),
            'shelf_life_days': shelf_life,
            'disease_risk': disease_risk,
            'recommendations': recommendations,
            'timestamp': datetime.now().isoformat(),
            'analysis_method': 'Enhanced Feature Analysis',
            'crop_type': crop_type,
            'health_indicators': {
                'healthy_green_area': round(features['healthy_green'] * 100, 1),
                'disease_yellow_area': round(features['disease_yellow'] * 100, 1),
                'disease_brown_area': round(features['disease_brown'] * 100, 1),
                'rot_dark_area': round(features['rot_dark'] * 100, 1),
                'texture_quality': round(features['edge_density'] * 1000, 1),
                'color_vitality': round(features['saturation'], 1)
            }
        }
    
    def generate_realistic_recommendations(self, condition, disease_risk, features, crop_type):
        """Generate realistic recommendations based on actual condition"""
        recommendations = []
        
        if condition in ["Very Poor", "Poor"]:
            recommendations.extend([
                "🚨 SEVERE DISEASE DETECTED - Immediate action needed",
                "🦠 High risk of crop failure",
                "⚠️ Isolate from healthy plants immediately",
                "💊 Consult agricultural expert urgently",
                "🌱 Consider complete replacement",
                "📉 Very limited shelf life - not suitable for market"
            ])
        elif condition == "Fair":
            recommendations.extend([
                "⚠️ Moderate disease signs detected",
                "🔍 Monitor closely for disease progression",
                "💧 Check watering and nutrient levels",
                "🍃 Remove affected parts carefully",
                "📊 Suitable for local markets only",
                "⏳ Limited shelf life expected"
            ])
        elif condition == "Good":
            recommendations.extend([
                "✅ Good overall crop health",
                "🌱 Minor issues detected",
                "📈 Suitable for most markets",
                "💚 Continue current practices",
                "👀 Regular monitoring recommended"
            ])
        else:  # Excellent
            recommendations.extend([
                "🎉 EXCELLENT crop health!",
                "⭐ Premium quality detected",
                "🏆 Ideal for high-value markets",
                "💚 Perfect condition - maintain practices",
                "📦 Good shelf life expected"
            ])
        
        # Specific warnings based on features
        if features['disease_yellow'] > 0.05:
            recommendations.append("🟡 Significant yellowing - possible nutrient deficiency or disease")
        if features['disease_brown'] > 0.03:
            recommendations.append("🟤 Browning detected - likely fungal infection")
        if features['rot_dark'] > 0.01:
            recommendations.append("⚫ Dark rot spots - severe disease present")
        
        # Crop-specific advice
        if crop_type.lower() == 'potato':
            if condition in ["Very Poor", "Poor"]:
                recommendations.append("🥔 Potato blight likely detected - urgent treatment needed")
        
        return recommendations
    
    def get_default_analysis(self):
        """Return realistic default analysis"""
        return {
            'overall_condition': "Fair",
            'freshness': 55.0,
            'rigorous': 52.0,
            'confidence': 54.0,
            'shelf_life_days': 4,
            'disease_risk': "Medium",
            'recommendations': [
                "Analysis incomplete - upload clearer image",
                "Consult expert for accurate diagnosis",
                "Check image quality and retry"
            ],
            'timestamp': datetime.now().isoformat(),
            'analysis_method': 'Enhanced Default',
            'crop_type': 'Unknown'
        }