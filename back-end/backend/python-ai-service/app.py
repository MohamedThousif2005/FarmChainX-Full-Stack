from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import numpy as np
import base64
from PIL import Image
import io
import os
import logging
from enhanced_crop_analyzer import EnhancedCropAnalyzer
 # ADD THIS
from datetime import datetime  # ADD THIS IMPORT

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

# Initialize the accurate analyzer
analyzer = EnhancedCropAnalyzer()

print("=" * 60)
print("🌱 FarmChainX AI Crop Analysis Service")
print("🎯 SMART MODEL - Optimized for Healthy Crops")
print("🚀 Service starting on http://localhost:5000")
print("=" * 60)

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'healthy',
        'service': 'FarmChainX AI - SMART Model Edition',
        'version': '4.0.0',
        'accuracy': 'High (SMART Feature Analysis)',
        'message': 'Service optimized for healthy crop detection',
        'features': 'Green/Red area detection, Conservative disease scoring'
    })

@app.route('/analyze', methods=['POST'])
def analyze_crop():
    try:
        # Get request data
        data = request.get_json()
        
        if not data or 'image' not in data:
            return jsonify({
                'success': False,
                'error': 'No image data provided. Please upload a crop image.'
            }), 400
        
        # Extract image data
        image_data = data['image']
        crop_type = data.get('crop_type', 'Unknown')
        
        # Validate crop type
        valid_crops = ['Tomato', 'Potato', 'Apple', 'Corn', 'Grape', 'Other']
        if crop_type not in valid_crops:
            crop_type = 'Other'
        
        # Decode base64 image
        if 'base64,' in image_data:
            image_data = image_data.split('base64,')[1]
        
        image_bytes = base64.b64decode(image_data)
        image = Image.open(io.BytesIO(image_bytes))
        
        # Convert to OpenCV format
        image_array = np.array(image)
        
        # Convert to BGR for OpenCV
        if len(image_array.shape) == 3:
            if image_array.shape[2] == 4:  # RGBA
                image_array = cv2.cvtColor(image_array, cv2.COLOR_RGBA2BGR)
            else:  # RGB
                image_array = cv2.cvtColor(image_array, cv2.COLOR_RGB2BGR)
        
        logger.info(f"🔍 Analyzing {crop_type} with SMART model...")
        
        # Analyze crop with SMART model
        analysis_result = analyzer.analyze_image(image_array, crop_type)
        
        return jsonify({
            'success': True,
            'analysis': analysis_result,
            'crop_type': crop_type,
            'message': 'Analysis completed with SMART AI model'
        })
        
    except Exception as e:
        logger.error(f"Analysis error: {str(e)}")
        return jsonify({
            'success': False,
            'error': f'Analysis failed: {str(e)}',
            'analysis': analyzer.get_default_analysis(),
            'message': 'Using default analysis due to error'
        }), 500

# ADD THIS NEW ENDPOINT FOR FILE UPLOADS
@app.route('/analyze/file', methods=['POST'])
def analyze_crop_file():
    try:
        # Check if image file is in the request
        if 'file' not in request.files:
            return jsonify({
                'success': False,
                'error': 'No file uploaded'
            }), 400
        
        file = request.files['file']
        crop_type = request.form.get('crop_type', 'Unknown')
        
        if file.filename == '':
            return jsonify({
                'success': False,
                'error': 'No file selected'
            }), 400
        
        # Validate crop type
        valid_crops = ['Tomato', 'Potato', 'Apple', 'Corn', 'Grape', 'Other']
        if crop_type not in valid_crops:
            crop_type = 'Other'
        
        # Read image file
        image_bytes = file.read()
        image = Image.open(io.BytesIO(image_bytes))
        
        # Convert to OpenCV format
        image_array = np.array(image)
        
        # Convert to BGR for OpenCV
        if len(image_array.shape) == 3:
            if image_array.shape[2] == 4:  # RGBA
                image_array = cv2.cvtColor(image_array, cv2.COLOR_RGBA2BGR)
            else:  # RGB
                image_array = cv2.cvtColor(image_array, cv2.COLOR_RGB2BGR)
        
        logger.info(f"🔍 Analyzing {crop_type} from file with SMART model...")
        
        # Analyze crop with SMART model
        analysis_result = analyzer.analyze_image(image_array, crop_type)
        
        return jsonify({
            'success': True,
            'analysis': analysis_result,
            'crop_type': crop_type,
            'message': 'Analysis completed with SMART AI model'
        })
        
    except Exception as e:
        logger.error(f"File analysis error: {str(e)}")
        return jsonify({
            'success': False,
            'error': f'Analysis failed: {str(e)}',
            'analysis': analyzer.get_default_analysis(),
            'message': 'Using default analysis due to error'
        }), 500

@app.route('/analyze/perfect', methods=['POST'])
def analyze_perfect():
    """ALWAYS return perfect results for testing"""
    try:
        data = request.get_json()
        crop_type = data.get('crop_type', 'Tomato')
        
        # Use super lenient model
        perfect_analyzer = SuperLenientModel()
        
        # Create dummy image
        dummy_image = np.ones((100, 100, 3), dtype=np.uint8) * 255
        
        analysis_result = perfect_analyzer.analyze_image(dummy_image, crop_type)
        
        return jsonify({
            'success': True,
            'analysis': analysis_result,
            'crop_type': crop_type,
            'message': 'PERFECT analysis for testing - Always shows excellent results',
            'model_type': 'Super Lenient (Testing)'
        })
        
    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500

@app.route('/test/healthy-tomato', methods=['GET'])
def test_healthy_tomato():
    """Test endpoint that always returns healthy tomato results"""
    healthy_analysis = {
        'overall_condition': 'Excellent',
        'freshness': 88.5,
        'rigorous': 86.2,
        'confidence': 87.4,
        'shelf_life_days': 11,
        'disease_risk': 'Low',
        'recommendations': [
            '🎉 EXCELLENT TOMATO QUALITY!',
            '💚 Perfect ripeness and color',
            '⭐ Premium market ready',
            '🍅 Ideal tomato characteristics'
        ],
        'timestamp': datetime.now().isoformat(),
        'analysis_method': 'Healthy Tomato Test',
        'crop_type': 'Tomato',
        'health_indicators': {
            'green_area': 32.1,
            'red_area': 41.8,
            'color_vitality': 142.5,
            'texture_quality': 72.3
        }
    }
    
    return jsonify({
        'success': True,
        'analysis': healthy_analysis,
        'message': 'Healthy tomato test analysis'
    })

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    
    print(f"\n✅ SMART Service Ready!")
    print(f"   🌐 Health Check: http://localhost:{port}/health")
    print(f"   🍅 Test Healthy Tomato: http://localhost:{port}/test/healthy-tomato")
    print(f"   ⭐ Perfect Analysis: http://localhost:{port}/analyze/perfect")
    print(f"   📊 Regular Analysis: http://localhost:{port}/analyze")
    print(f"   📁 File Analysis: http://localhost:{port}/analyze/file")
    print(f"   🎯 USING SMART AI - Optimized for Healthy Crops!")
    print(f"   💚 Now shows GOOD/EXCELLENT for healthy tomatoes!\n")
    
    app.run(host='0.0.0.0', port=port, debug=False)