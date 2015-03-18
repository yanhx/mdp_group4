package common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import utils.Constants;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;

public class CustomRenderer implements Renderer {
	
	// Camera mode
	private int mode = Constants.CAMERA_NORMAL;
	
	// Position the eye behind the origin.
	private float eyeX = 0.0f;
	public float eyeY = -7.0f;
	private float eyeZ = 13.0f;

	// We are looking at the origin
	private float lookX = 0.0f;
	private float lookY = 0.0f;
	private float lookZ = 0.0f;

	// Set our up vector. This is where our head would be pointing were we holding the camera.
	private float upX = 0.0f;
	private float upY = 1.0f;
	private float upZ = 0.0f;
	
	private int[][] grid = new int[15][20];
	private int robotOrientation = 0; // 0-east, 1-south, 2-west, 3-north
	private int[] robotPosition = new int[]{1, 1}; // x, y coordinate of the robot's centre
	private float[] mRobotModelMatrix = new float[16];
	private float[] mRobotHeadModelMatrix = new float[16];
	
	private final int mBytesPerFloat = 4; // how many bytes per float
	
	// Store our model data in buffers
	private final FloatBuffer mFloorVertices;
	private final FloatBuffer mDotVertices;
	
	private final FloatBuffer mWallHorizontalVertices;
	private final FloatBuffer mWallVerticalVertices;
	private final FloatBuffer mWallColors;
	private final FloatBuffer mWallNormals;
	
	private final FloatBuffer mBoxVertices;
	private final FloatBuffer mBoxColors;
	private final FloatBuffer mBoxNormals;
	
	private final FloatBuffer mRobotVertices;
	private final FloatBuffer mRobotHeadVertices;
	
	 /**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightPosInWorldSpace = new float[4];
	private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	
	private int programHandle;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	
	private int programHandle2;
	private int mMVPMatrixHandle2;
	private int mMVMatrixHandle2;
	private int mLightPosHandle2;
	private int mNormalHandle2;
	private int mPositionHandle2;
	private int mColorHandle2;
	
	/** How many elements per vertex. */
	private final int mStrideBytes = 7 * mBytesPerFloat;
	 
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	 
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	 
	/** Offset of the color data. */
	private final int mColorOffset = 3;
	 
	/** Size of the color data in elements. */
	private final int mColorDataSize = 4;
	
	/** Size of the normal data in elements. */
	private final int mNormalDataSize = 3;
	
	public CustomRenderer() {
		
		// Populate the grid array with 0s (like a reset)
		for (int a=0; a<15; a++) {
			for (int b=0; b<20; b++) {
				grid[a][b] = 0;
			}
		}
		
		final float[] floorVerticesData = {
				// X, Y, Z
				// R, G, B, A
				-7.5f,  10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f,
				
				-7.5f, -10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f,
				
				 7.5f, -10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f,
					
				-7.5f,  10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f,
				
				 7.5f, -10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f,
				
				 7.5f,  10.0f, 0.0f,
				0.4f, 0.4f, 0.4f, 1.0f
		};
		
		final float[] dotVerticesData = {
				// X, Y, Z
				// R, G, B, A
				-0.05f,  0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f,
				
				-0.05f, -0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f,
				
				 0.05f, -0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f,

				-0.05f,  0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f,
				
				 0.05f, -0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f,
				
				 0.05f,  0.05f, 0.05f,
				1.0f, 1.0f, 0.0f, 1.0f
		};
		
		// Initialise the buffers
		mFloorVertices = ByteBuffer.allocateDirect(floorVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloorVertices.put(floorVerticesData).position(0);
		
		mDotVertices = ByteBuffer.allocateDirect(dotVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mDotVertices.put(dotVerticesData).position(0);
		
		
		
		final float[] normalData = new float[108];
		int index = 0;
		for (int a=0; a<6; a++) {
			for (int b=0; b<6; b++) {
				switch (a) {
				case 0: // Front
					normalData[index] = 0.0f;
					normalData[index+1] = 0.0f;
					normalData[index+2] = 1.0f;
					index += 3;
					break;
				case 1: // Right
					normalData[index] = 1.0f;
					normalData[index+1] = 0.0f;
					normalData[index+2] = 0.0f;
					index += 3;
					break;
				case 2: // Back
					normalData[index] = 0.0f;
					normalData[index+1] = 0.0f;
					normalData[index+2] = -1.0f;
					index += 3;
					break;
				case 3: // Left
					normalData[index] = -1.0f;
					normalData[index+1] = 0.0f;
					normalData[index+2] = 0.0f;
					index += 3;
					break;
				case 4: // Top
					normalData[index] = 0.0f;
					normalData[index+1] = 1.0f;
					normalData[index+2] = 0.0f;
					index += 3;
					break;
				case 5: // Bottom
					normalData[index] = 0.0f;
					normalData[index+1] = -1.0f;
					normalData[index+2] = 0.0f;
					index += 3;
					break;
				}
			}
		}
		
		final float[] wallColorsData = new float[144];
		index = 0;
		for (int a=0; a<6; a++) {
			for (int b=0; b<6; b++) {
				switch (a) {
				case 0: case 2:// Front, back
					wallColorsData[index] = 1.0f;
					wallColorsData[index+1] = 1.0f;
					wallColorsData[index+2] = 1.0f;
					wallColorsData[index+3] = 1.0f;
					index += 4;
					break;
				case 1: case 3: // Right, left
					wallColorsData[index] = 1.0f;
					wallColorsData[index+1] = 0.5f;
					wallColorsData[index+2] = 0.0f;
					wallColorsData[index+3] = 1.0f;
					index += 4;
					break;
				case 4: case 5: // Top, bottom
					wallColorsData[index] = 0.0f;
					wallColorsData[index+1] = 0.5f;
					wallColorsData[index+2] = 1.0f;
					wallColorsData[index+3] = 1.0f;
					index += 4;
					break;
				}
			}
		}
		
		final float[] wallHorizontalVerticesData = {
				// Front face
				-7.5f,  0.05f, 1.5f,				
				-7.5f, -0.05f, 1.5f,				
				 7.5f,  0.05f, 1.5f,
				-7.5f, -0.05f, 1.5f,
				 7.5f, -0.05f, 1.5f,
				 7.5f,  0.05f, 1.5f,
				
				// Right face
				7.5f, -0.05f, 1.5f,
				7.5f, -0.05f, 0.0f,
				7.5f,  0.05f, 1.5f,
				7.5f, -0.05f, 0.0f,
				7.5f,  0.05f, 0.0f,
				7.5f,  0.05f, 1.5f,
				
				// Back face
				 7.5f,  0.05f, 0.0f,
				 7.5f, -0.05f, 0.0f,
				-7.5f,  0.05f, 0.0f,
				 7.5f, -0.05f, 0.0f,
				-7.5f, -0.05f, 0.0f,
				-7.5f,  0.05f, 0.0f,
				
				// Left face
				-7.5f, -0.05f, 0.0f,
				-7.5f, -0.05f, 1.5f,
				-7.5f,  0.05f, 0.0f,
				-7.5f, -0.05f, 1.5f,
				-7.5f,  0.05f, 1.5f,
				-7.5f,  0.05f, 0.0f,
				
				// Top face
				-7.5f, 0.05f, 0.0f,
				-7.5f, 0.05f, 1.5f,				
				 7.5f, 0.05f, 0.0f,
				-7.5f, 0.05f, 1.5f,
				 7.5f, 0.05f, 1.5f,				
				 7.5f, 0.05f, 0.0f,
				
				// Bottom face
				 7.5f, -0.05f, 0.0f,
				 7.5f, -0.05f, 1.5f,
				-7.5f, -0.05f, 0.0f,				
				 7.5f, -0.05f, 1.5f,
				-7.5f, -0.05f, 1.5f,
				-7.5f, -0.05f, 0.0f
		};
		
		final float[] wallVerticalVerticesData = {
				// Front face
				-0.05f,  10.0f, 1.5f,
				-0.05f, -10.0f, 1.5f,
				 0.05f,  10.0f, 1.5f,
				-0.05f, -10.0f, 1.5f,
				 0.05f, -10.0f, 1.5f,
				 0.05f,  10.0f, 1.5f,
					
				// Right face
				0.05f, -10.0f, 1.5f,
				0.05f, -10.0f, 0.0f,
				0.05f,  10.0f, 1.5f,
				0.05f, -10.0f, 0.0f,
				0.05f,  10.0f, 0.0f,
				0.05f,  10.0f, 1.5f,
				
				// Back face
				 0.05f,  10.0f, 0.0f,
				 0.05f, -10.0f, 0.0f,
				-0.05f,  10.0f, 0.0f,
				 0.05f, -10.0f, 0.0f,
				-0.05f, -10.0f, 0.0f,
				-0.05f,  10.0f, 0.0f,
				
				// Left face
				-0.05f, -10.0f, 0.0f,
				-0.05f, -10.0f, 1.5f,
				-0.05f,  10.0f, 0.0f,
				-0.05f, -10.0f, 1.5f,
				-0.05f,  10.0f, 1.5f,
				-0.05f,  10.0f, 0.0f,
				
				// Top face
				-0.05f, 10.0f, 0.0f,
				-0.05f, 10.0f, 1.5f,
				 0.05f, 10.0f, 0.0f,
				-0.05f, 10.0f, 1.5f,
				 0.05f, 10.0f, 1.5f,
				 0.05f, 10.0f, 0.0f,
				
				// Bottom face
				 0.05f, -10.0f, 0.0f,
				 0.05f, -10.0f, 1.5f,				
				-0.05f, -10.0f, 0.0f,
				 0.05f, -10.0f, 1.5f,
				-0.05f, -10.0f, 1.5f,
				-0.05f, -10.0f, 0.0f
		};
		
		// Initialise the buffers
		mWallHorizontalVertices = ByteBuffer.allocateDirect(wallHorizontalVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mWallHorizontalVertices.put(wallHorizontalVerticesData).position(0);
		mWallVerticalVertices = ByteBuffer.allocateDirect(wallVerticalVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mWallVerticalVertices.put(wallVerticalVerticesData).position(0);
		
		mWallColors = ByteBuffer.allocateDirect(wallColorsData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mWallColors.put(wallColorsData).position(0);
		mWallNormals = ByteBuffer.allocateDirect(normalData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mWallNormals.put(normalData).position(0);
		
		
		
		final float[] boxColorsData = new float[144];
		index = 0;
		for (int a=0; a<6; a++) {
			for (int b=0; b<6; b++) {
				switch (a) {
				case 0: case 2:// Front, back
					boxColorsData[index] = 0.0f;
					boxColorsData[index+1] = 0.0f;
					boxColorsData[index+2] = 0.5f;
					boxColorsData[index+3] = 1.0f;
					index += 4;
					break;
				case 1: case 3: // Right, left
					boxColorsData[index] = 0.5f;
					boxColorsData[index+1] = 0.0f;
					boxColorsData[index+2] = 0.0f;
					boxColorsData[index+3] = 1.0f;
					index += 4;
					break;
				case 4: case 5: // Top, bottom
					boxColorsData[index] = 0.0f;
					boxColorsData[index+1] = 0.5f;
					boxColorsData[index+2] = 0.0f;
					boxColorsData[index+3] = 1.0f;
					index += 4;
					break;
				}
			}
		}
		
		final float[] boxVerticesData = {
				// Front face
				-7.5f, 10.0f, 1.0f,				
				-7.5f,  9.0f, 1.0f,				
				-6.5f, 10.0f, 1.0f,				
				-7.5f,  9.0f, 1.0f,				
				-6.5f,  9.0f, 1.0f,				
				-6.5f, 10.0f, 1.0f,
				
				// Right face
				-6.5f,  9.0f, 1.0f,				
				-6.5f,  9.0f, 0.0f,				
				-6.5f, 10.0f, 1.0f,				
				-6.5f,  9.0f, 0.0f,				
				-6.5f, 10.0f, 0.0f,				
				-6.5f, 10.0f, 1.0f,
				
				// Back face
				-6.5f, 10.0f, 0.0f,				
				-6.5f,  9.0f, 0.0f,				
				-7.5f, 10.0f, 0.0f,				
				-6.5f,  9.0f, 0.0f,
				-7.5f,  9.0f, 0.0f,	
				-7.5f, 10.0f, 0.0f,
				
				// Left face
				-7.5f,  9.0f, 0.0f,				
				-7.5f,  9.0f, 1.0f,				
				-7.5f, 10.0f, 0.0f,				
				-7.5f,  9.0f, 1.0f,				
				-7.5f, 10.0f, 1.0f,				
				-7.5f, 10.0f, 0.0f,
				
				// Top face
				-7.5f, 10.0f, 0.0f,				
				-7.5f, 10.0f, 1.0f,
				-6.5f, 10.0f, 0.0f,				
				-7.5f, 10.0f, 1.0f,				
				-6.5f, 10.0f, 1.0f,				
				-6.5f, 10.0f, 0.0f,
				
				// Bottom face
				-6.5f, 9.0f, 0.0f,				
				-6.5f, 9.0f, 1.0f,				
				-7.5f, 9.0f, 0.0f,				
				-6.5f, 9.0f, 1.0f,				
				-7.5f, 9.0f, 1.0f,				
				-7.5f, 9.0f, 0.0f
		};
		
		// Initialise the buffers
		mBoxVertices = ByteBuffer.allocateDirect(boxVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mBoxVertices.put(boxVerticesData).position(0);
		mBoxColors = ByteBuffer.allocateDirect(boxColorsData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mBoxColors.put(boxColorsData).position(0);
		mBoxNormals = ByteBuffer.allocateDirect(normalData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mBoxNormals.put(normalData).position(0);
		
		
		
		int vertexCount = 20;
		float radius = 1.3f;
		float center_x = -7.0f;
		float center_y = 9.5f;
		float height = 0.2f;
		float color = 0.2f;
		float alpha = 1.0f;
		
		float robotVerticesData[] = new float[vertexCount * 7]; // (x, y, z, r, g, b, a) for each vertices
		int idx = 0;
		
		// center vertex for triangle fan
		robotVerticesData[idx++] = center_x;
		robotVerticesData[idx++] = center_y;
		robotVerticesData[idx++] = height;
		robotVerticesData[idx++] = color;
		robotVerticesData[idx++] = color;
		robotVerticesData[idx++] = color;
		robotVerticesData[idx++] = alpha;
		
		// outer vertices of the circle
		int outerVertexCount = vertexCount - 1;
		
		for (int i=0; i<outerVertexCount; ++i) {
		    float percent = (i / (float) (outerVertexCount-1));
		    float rad = (float) (percent * 2*Math.PI);

		    //vertex position
		    float outer_x = (float) (center_x + radius * Math.cos(rad));
		    float outer_y = (float) (center_y + radius * Math.sin(rad));

		    robotVerticesData[idx++] = outer_x;
		    robotVerticesData[idx++] = outer_y;
		    robotVerticesData[idx++] = height;
		    robotVerticesData[idx++] = color;
		    robotVerticesData[idx++] = color;
		    robotVerticesData[idx++] = color;
		    robotVerticesData[idx++] = alpha;
		}
		
		final float[] robotHeadVerticesData = {
				// X, Y, Z
				// R, G, B, A
				-7.3f,  9.8f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				-7.3f,  9.2f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				-6.7f,  9.2f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f,

				-7.3f,  9.8f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				-6.7f,  9.2f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				-6.7f,  9.8f, 0.25f,
				1.0f, 0.0f, 0.0f, 1.0f
		};
		
		mRobotVertices = ByteBuffer.allocateDirect(robotVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mRobotVertices.put(robotVerticesData).position(0);

		mRobotHeadVertices = ByteBuffer.allocateDirect(robotHeadVerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mRobotHeadVertices.put(robotHeadVerticesData).position(0);
	    
	}
	
	protected String getVertexShader()	{
		final String vertexShader =
			    "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
				 
				  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
				  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
				 
				  + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
				 
				  + "void main()                    \n"     // The entry point for our vertex shader.
				  + "{                              \n"
				  + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
				                                            // It will be interpolated across the triangle.
				  + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
				  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
				  + "}                              \n";    // normalized screen coordinates.
		
		return vertexShader;
	}
	
	protected String getFragmentShader() {
		final String fragmentShader =
			    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                										// precision in the fragment shader.
				+ "varying vec4 v_Color;          \n"   // This is the color from the vertex shader interpolated across the
				                						// triangle per fragment.
				+ "void main()                    \n"   // The entry point for our fragment shader.
				+ "{                              \n"
				+ "   gl_FragColor = v_Color;     \n"   // Pass the color directly through the pipeline.
				+ "}                              \n";
		
		return fragmentShader;
	}
	
	protected String getVertexShader2()	{
		final String vertexShader =
				"uniform mat4 u_MVPMatrix; \n"	// A constant representing the combined model/view/projection matrix.
				+ "uniform mat4 u_MVMatrix; \n"	// A constant representing the combined model/view matrix.

				+ "attribute vec4 a_Position; \n"	// Per-vertex position information we will pass in.
				+ "attribute vec4 a_Color; \n"	// Per-vertex color information we will pass in.
				+ "attribute vec3 a_Normal; \n"	// Per-vertex normal information we will pass in.

				+ "varying vec3 v_Position; \n"	// This will be passed into the fragment shader.
				+ "varying vec4 v_Color; \n"	// This will be passed into the fragment shader.
				+ "varying vec3 v_Normal; \n"	// This will be passed into the fragment shader.

				// The entry point for our vertex shader.
				+ "void main() \n"
				+ "{ \n"
				// Transform the vertex into eye space.
				+ " v_Position = vec3(u_MVMatrix * a_Position); \n"
				// Pass through the color.
				+ " v_Color = a_Color; \n"
				// Transform the normal's orientation into eye space.
				+ " v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0)); \n"
				// gl_Position is a special variable used to store the final position.
				// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
				+ " gl_Position = u_MVPMatrix * a_Position; \n"
				+ "} \n"; 
		
		return vertexShader;
	}
	
	protected String getFragmentShader2() {
		final String fragmentShader =
				"precision mediump float; \n"	// Set the default precision to medium. We don't need as high of a
				// precision in the fragment shader.
				+ "uniform vec3 u_LightPos; \n"	// The position of the light in eye space.

				+ "varying vec3 v_Position; \n"	// Interpolated position for this fragment.
				+ "varying vec4 v_Color; \n"	// This is the color from the vertex shader interpolated across the triangle per fragment.
				+ "varying vec3 v_Normal; \n"	// Interpolated normal for this fragment.

				// The entry point for our fragment shader.
				+ "void main() \n"	
				+ "{ \n"
				
				// Will be used for attenuation.
				+ " float distance = length(u_LightPos - v_Position); \n"
				
				// Get a lighting direction vector from the light to the vertex.
				+ " vec3 lightVector = normalize(u_LightPos - v_Position); \n"
				
				// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
				// pointing in the same direction then it will get max illumination.
				+ " float diffuse = max(dot(v_Normal, lightVector), 0.1); \n"
				
				// Add attenuation.
				+ " diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance))); \n"
				
				// Multiply the color by the diffuse illumination level to get final output color.
				+ " gl_FragColor = (v_Color * (diffuse + 0.2)) + (v_Color * 0.3); \n"
				
				+ "} \n";	
		
		return fragmentShader;
	}
	
	/**
	* Helper function to compile a shader.
	*
	* @param shaderType The shader type.
	* @param shaderSource The shader source code.
	* @return An OpenGL handle to the shader.
	*/
	private int compileShader(final int shaderType, final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);
		
		if (shaderHandle != 0)
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);
			
			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);
			
			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			
			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0)
			{
				//Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}
		
		if (shaderHandle == 0)
		{	
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	* Helper function to compile and link a program.
	*
	* @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	* @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	* @param attributes Attributes that need to be bound to the program.
	* @return An OpenGL handle to the program.
	*/
	private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0)
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);	
			
			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}	
			}
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);
			
			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			
			// If the link failed, delete the program.
			if (linkStatus[0] == 0)
			{	
				//Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// Set the background clear color.
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		// Reset the model matrix for the robot and robot head
		Matrix.setIdentityM(mRobotModelMatrix, 0);
		Matrix.setIdentityM(mRobotHeadModelMatrix, 0);

		// Position the eye behind the origin.
		eyeX = 0.0f;
		eyeY = -7.0f;
		eyeZ = 13.0f;

		// We are looking at the origin
		lookX = 0.0f;
		lookY = 0.0f;
		lookZ = 0.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		upX = 0.0f;
		upY = 1.0f;
		upZ = 0.0f;
		
		final String vertexShader = getVertexShader();
		final String fragmentShader = getFragmentShader();	
		
		final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);	
		final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
				new String[] {"a_Position", "a_Color"});
		
	    // Set program handles. These will later be used to pass in values to the program.
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
	    mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
	    mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
	    
	    
	    
	    final String vertexShader2 = getVertexShader2();
	    final String fragmentShader2 = getFragmentShader2();
	    
	    final int vertexShaderHandle2 = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader2);
	    final int fragmentShaderHandle2 = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader2);
	    
	    programHandle2 = createAndLinkProgram(vertexShaderHandle2, fragmentShaderHandle2,
				new String[] {"a_Position", "a_Color", "a_Normal"});
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
	    final float left = -ratio;
	    final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 2.0f * (float) 20.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT); 

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle2);
	
        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle2 = GLES20.glGetUniformLocation(programHandle2, "u_MVPMatrix");
	    mMVMatrixHandle2 = GLES20.glGetUniformLocation(programHandle2, "u_MVMatrix");
	    mLightPosHandle2 = GLES20.glGetUniformLocation(programHandle2, "u_LightPos");
        mPositionHandle2 = GLES20.glGetAttribLocation(programHandle2, "a_Position");
        mColorHandle2 = GLES20.glGetAttribLocation(programHandle2, "a_Color");
	    mNormalHandle2 = GLES20.glGetAttribLocation(programHandle2, "a_Normal");
	    
	    Matrix.setIdentityM(mLightModelMatrix, 0);
	    Matrix.translateM(mLightModelMatrix, 0, 1.0f, 0.0f, 2.0f);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        
        // Draw the obstacles
        float offsetX = 0f;
        for (int a=0; a<15; a++) {
        	float offsetY = 0f;
        	for (int b=0; b<20; b++) {
        		if (grid[a][b] == 1) { // obstacles
            		Matrix.setIdentityM(mModelMatrix, 0);
            		Matrix.translateM(mModelMatrix, 0, offsetX, offsetY, 0.0f);
            		drawCubicObject(mBoxVertices, mBoxColors, mBoxNormals);
        		}
        		offsetY -= 1f;
        	}
        	offsetX += 1f;
        }
        
        // Draw the walls
        // Top
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 10.0f, 0.0f);
		drawCubicObject(mWallHorizontalVertices, mWallColors, mWallNormals);
		// Bottom
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, -10.0f, 0.0f);
		drawCubicObject(mWallHorizontalVertices, mWallColors, mWallNormals);
		// Left
        Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, -7.5f, 0.0f, 0.0f);
		drawCubicObject(mWallVerticalVertices, mWallColors, mWallNormals);
        // Right
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 7.5f, 0.0f, 0.0f);
		drawCubicObject(mWallVerticalVertices, mWallColors, mWallNormals);
    
        
   	 
	    // Tell OpenGL to use this program when rendering.
	    GLES20.glUseProgram(programHandle);
		
        // Draw the floor
        Matrix.setIdentityM(mModelMatrix, 0);
        drawFloor(mFloorVertices); 
        
        // Draw the dots
        float posX = -6.5f;
        for (int i=0; i<14; i++) {
        	float posY = -9.0f;
        	for (int j=0; j<19; j++) {
        		Matrix.setIdentityM(mModelMatrix, 0);
        		Matrix.translateM(mModelMatrix, 0, posX, posY, 0.0f);
        		drawDot(mDotVertices);
        		posY += 1.0f;
        	}
        	posX += 1.0f;
        }
        
        // Draw the robot
        drawRobot(mRobotVertices, mRobotHeadVertices);

	}

	private void drawFloor(final FloatBuffer aFloorBuffer) {
		
	    // Pass in the position information
		aFloorBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aFloorBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aFloorBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aFloorBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    
	}

	private void drawDot(final FloatBuffer aDotBuffer) {
		
	    // Pass in the position information
		aDotBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aDotBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aDotBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aDotBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    
	}
	
	private void drawRobot(final FloatBuffer aRobotBuffer, final FloatBuffer aRobotHeadBuffer) {
		
		updateRobotPosition();
	    
	    // Pass in the position information
		aRobotBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aRobotBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aRobotBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aRobotBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRobotModelMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

	    //draw circle as filled shape
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 20);
	    

		
	    // Pass in the position information
	    aRobotHeadBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aRobotHeadBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aRobotHeadBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aRobotHeadBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRobotHeadModelMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

	    //draw circle as filled shape
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    
	}
	
	private void updateRobotPosition() {
		
		Matrix.setIdentityM(mRobotModelMatrix, 0);
		Matrix.translateM(mRobotModelMatrix, 0,  robotPosition[0],  -robotPosition[1], 0.0f);
		
		mRobotHeadModelMatrix = mRobotModelMatrix.clone();
		
		switch(robotOrientation) {
		case Constants.EAST: // facing east
			Matrix.translateM(mRobotHeadModelMatrix, 0,  1.0f,  0.0f, 0.0f);
			break;
		case Constants.SOUTH: // facing south
			Matrix.translateM(mRobotHeadModelMatrix, 0,  0.0f, -1.0f, 0.0f);
			break;
		case Constants.WEST: // facing west
			Matrix.translateM(mRobotHeadModelMatrix, 0, -1.0f,  0.0f, 0.0f);
			break;
		case Constants.NORTH: // facing north
			Matrix.translateM(mRobotHeadModelMatrix, 0,  0.0f,  1.0f, 0.0f);
			break;
		}
	}

	private void drawCubicObject(final FloatBuffer aBoxBuffer, final FloatBuffer aBoxColorBuffer, final FloatBuffer aBoxNormalBuffer) {
		
	    // Pass in the position information
		aBoxBuffer.position(0);
	    GLES20.glVertexAttribPointer(mPositionHandle2, mPositionDataSize, GLES20.GL_FLOAT, false, 0, aBoxBuffer);
	    GLES20.glEnableVertexAttribArray(mPositionHandle2);
	 
	    // Pass in the color information
	    aBoxColorBuffer.position(0);
	    GLES20.glVertexAttribPointer(mColorHandle2, mColorDataSize, GLES20.GL_FLOAT, false, 0, aBoxColorBuffer);
	    GLES20.glEnableVertexAttribArray(mColorHandle2);
	    
	    // Pass in the normal information
	    aBoxNormalBuffer.position(0);
	    GLES20.glVertexAttribPointer(mNormalHandle2, mNormalDataSize, GLES20.GL_FLOAT, false, 0, aBoxNormalBuffer);
	    GLES20.glEnableVertexAttribArray(mNormalHandle2);
	 
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    
	    // Pass in the modelview matrix.
	    GLES20.glUniformMatrix4fv(mMVMatrixHandle2, 1, false, mMVPMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	
	    // Pass in the combined matrix.
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle2, 1, false, mMVPMatrix, 0);
	    
	    // Pass in the light position in eye space.
	    GLES20.glUniform3f(mLightPosHandle2, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
	    
	    // Draw the cube.
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
	    
	}
	
	/**
	 * Updates the position of the newly discovered obstacle
	 * @param col - x value of the obstacle
	 * @param row - y value of the obstacle
	 */
	public void updatesGrid(int col, int row) {
		this.grid[col][row] = 1;
	}
	
	/**
	 * Update the position of the robot, using the position of its centre square
	 * @param col - x value of the robot
	 * @param row - y value of the robot
	 */
	public void updateRobot(int col, int row) {
		robotPosition[0] = col;
		robotPosition[1] = row;
		
		if (mode == Constants.CAMERA_FPV)
			changeCameraView(mode);
	}
	
	/**
	 * Update the orientation of the robot
	 * @param dir - direction which the robot is now facing
	 */
	public void updateRobotOrientation(int dir) {
		robotOrientation = dir;
		
		if (mode == Constants.CAMERA_FPV)
			changeCameraView(mode);
	}
	
	
	/**
	 * Moves the robot forward
	 */
	public void moveForward() {
		switch(robotOrientation) {
		case Constants.EAST: // facing east
			robotPosition[0]++;
			break;
		case Constants.SOUTH: // facing south
			robotPosition[1]++;
			break;
		case Constants.WEST: // facing west
			robotPosition[0]--;
			break;
		case Constants.NORTH: // facing north
			robotPosition[1]--;
			break;
		}
		
		if (mode == Constants.CAMERA_FPV)
			changeCameraView(mode);
	}
	
	/**
	 * Rotate the robot right (clockwise)
	 */
	public void rotateRight() {
		switch(robotOrientation) {
		case Constants.EAST: // facing east
			robotOrientation = 1;
			break;
		case Constants.SOUTH: // facing south
			robotOrientation = 2;
			break;
		case Constants.WEST: // facing west
			robotOrientation = 3;
			break;
		case Constants.NORTH: // facing north
			robotOrientation = 0;
			break;
		}
		
		if (mode == Constants.CAMERA_FPV)
			changeCameraView(mode);
	}
	
	/**
	 * Rotate the robot left (anti-clockwise)
	 */
	public void rotateLeft() {
		switch(robotOrientation) {
		case Constants.EAST: // facing east
			robotOrientation = 3;
			break;
		case Constants.SOUTH: // facing south
			robotOrientation = 0;
			break;
		case Constants.WEST: // facing west
			robotOrientation = 1;
			break;
		case Constants.NORTH: // facing north
			robotOrientation = 2;
			break;
		}
		
		if (mode == Constants.CAMERA_FPV)
			changeCameraView(mode);
	}

	public void changeCameraView(int mode) {
		this.mode = mode;
		
		if (mode == Constants.CAMERA_NORMAL) {
			eyeX = 0.0f;
			eyeY = -7.0f;
			eyeZ = 13.0f;
			
			lookX = 0.0f;
			lookY = 0.0f;
			lookZ = 0.0f;

			upX = 0.0f;
			upY = 1.0f;
			upZ = 0.0f;
		}
		else if (mode == Constants.CAMERA_FPV) {
			eyeX = -7.0f + robotPosition[0];
			eyeY = 9.5f - robotPosition[1];
			eyeZ = 1.5f;

			upX = 0.0f;
			upY = 0.0f;
			upZ = 1.0f;
			
			lookZ = 0.0f;
			switch (robotOrientation) {
			case Constants.EAST: // facing east
				lookX = eyeX + 3.0f;
				lookY = eyeY;
				break;
			case Constants.SOUTH: // facing south
				lookX = eyeX;
				lookY = eyeY - 3.0f;
				break;
			case Constants.WEST: // facing west
				lookX = eyeX - 3.0f;
				lookY = eyeY;
				break;
			case Constants.NORTH: // facing north
				lookX = eyeX;
				lookY = eyeY + 3.0f;
				break;
			}
		}
	}

	public void setMode(int mode) {
		switch (mode) {
		case Constants.EXPLORE:
			updateRobot(7, 9);
			updateRobotOrientation(2);
			break;
			
		case Constants.SPEEDRUN:
			updateRobot(1, 18);
			updateRobotOrientation(3);
			break;
		}
	}

}
