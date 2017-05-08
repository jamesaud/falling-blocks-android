package com.example.android.opengl;

import javax.microedition.khronos.opengles.GL10;
// GL10 is only needed for Android GLSurfaceView.Renderer callbacks, e.g. onSurfaceCreated() etc.

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import android.app.Activity;

import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import com.example.android.opengl.common.RawResourceReader;
import com.example.android.opengl.common.ShaderHelper;
import com.example.android.opengl.common.*;

import java.util.Collections;
import java.util.Calendar;
import java.util.Random;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.widget.Toast;


/**
 * MyGLRenderer provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    //Solution
    public int num_verts = 0, high_vert = 0, num_high_verts = 0;
    public int mode = 1;    // mode 0 for proximity, mode 1 for insert
    private int ROTATE = 0, TXY = 1, TXZ = 2, SCALE = 3;
    private int OBJ1 = 0, OBJ2 = 1, OBJ3 = 2, CAMERA = 3;
    private int topSelection = ROTATE, bottomSelection = OBJ1;
    //End

    private int index_mode = 0;


    private final Context mActivityContext;
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeNormals;
    private final FloatBuffer mAxisPositions;


    private final int mBytesPerFloat = 4;
    private final int mPositionDataSize = 3;
    private final int mNormalDataSize = 3;
    private final int mTextureCoordinateDataSize = 2;
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] mLightPosInWorldSpace = new float[4];
    private final float[] mLightPosInEyeSpace = new float[4];
    private int mProgramHandle;
    private int mPointProgramHandle;
    private int mBrickDataHandle;
    private int mGrassDataHandle;
    private int mPaperDataHandle;

    private int mQueuedMinFilter;
    private int mQueuedMagFilter;
    private int cubeLocation = 0;

    //Triangle
    private final FloatBuffer mTriangle1Vertices;

    //Texture
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;
    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;
    private final FloatBuffer mCubeTextureCoordinates;

    private final FloatBuffer mCubeTextureCoordinatesForPlane;



    public volatile float mDeltaX;
    public volatile float mDeltaY;

    public int currentTime;
    public int previousTime;


    Cube playercube;


    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private final float[] mAccumulatedRotation = new float[16];
    private final float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private float[] mLightModelMatrix = new float[16];
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle;
    private int mPositionHandle;
    private int mAxisPositionHandle;

    private int mNormalHandle;
    private float[] mModelMatrix = new float[16];



    //xdxd
    public Cube[] cubes = new Cube[5];


    final float[] cubeTextureCoordinateData =
            {
                    // Front face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Right face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Left face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Top face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Bottom face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f
            };

    final float[] cubeTextureCoordinateDataForPlane =
            {
                    // Front face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Right face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Left face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Top face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Bottom face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f
            };


    //Triangel
    //Triangel
    final float[] triangle1VerticesData = {
            // X, Y, Z,
            // R, G, B, A
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f};

    //TrianglemTri


    // Constructor added for the Cube copied from previous assignment
    /**
     * Initialize the model data.
     */


    public MyGLRenderer(final Context activityContext)
    {
        mActivityContext = activityContext;

        // Define points for a cube.

        final float[] axisPositionData =
                {
                        0, 0, 0,
                        5, 0, 0,
                        0, 0, 0,
                        0, 5, 0,
                        0, 0, 0,
                        0, 0, 5
                };


        //Triangle
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        final float[] triangle1VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f};

        //Triangle
        final FloatBuffer mTriangle1Vertices;




        // X, Y, Z
        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,

                        // Right face
                        1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Back face
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,

                        // Left face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f,

                        // Top face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                };

        // X, Y, Z
        // The normal is used in light calculations and is a vector which points
        // orthogonal to the plane of the surface. For a cube model, the normals
        // should be orthogonal to the points of each face.
        final float[] cubeNormalData =
                {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.

        // Initialize the buffers.
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mAxisPositions = ByteBuffer.allocateDirect(axisPositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mAxisPositions.put(axisPositionData).position(0);

        //Texture
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);


        mCubeTextureCoordinatesForPlane = ByteBuffer.allocateDirect(cubeTextureCoordinateDataForPlane.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinatesForPlane.put(cubeTextureCoordinateDataForPlane).position(0);

        //Triangle
        // Initialize the buffers.
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle1Vertices.put(triangle1VerticesData).position(0);



    }

    private static final String TAG = "MyGLRenderer";





    //Triangle
    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    /** Offset of the position data. */
    private final int mPositionOffset = 0;
    private final int mStrideBytes = 7 * mBytesPerFloat;
    /** Offset of the color data. */
    private final int mColorOffset = 3;
    /** This will be used to pass in model color information. */
    private int mColorHandle;
    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;


    private void drawTriangle(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }


    // ------------------------------------------------------------------------
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // called when the surface is created or recreated

        // this method is a convenient place to put code to create EGL resources
        // that need to be created when the rendering starts,
        // and that need to be recreated when the EGL context is lost.

        //  this.setupGL();
        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // Use culling to remove back faces.
            GLES20.glDisable(GLES20.GL_CULL_FACE);

            // Enable depth testing
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
            // Enable texture mapping
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);

            // Position the eye in front of the origin.
            final float eyeX = 0f;
            final float eyeY = 4f;
            final float eyeZ = 8f;

            // We are looking toward the distance
            final float lookX = 0.0f;
            final float lookY = 0.0f;
            final float lookZ = -5.0f;

            // Set our up vector. This is where our head would be pointing were we holding the camera.
            final float upX = 0.0f;
            final float upY = 1.0f;
            final float upZ = 0.0f;

            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

            final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
            final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);

            final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
            final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

            mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                    new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});

            // Define a simple shader program for our point.
            final String pointVertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.point_vertex_shader);
            final String pointFragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.point_fragment_shader);

            final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
            final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
            mPointProgramHandle = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                    new String[]{"a_Position"});

            // Load the texture
            mBrickDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.stone_wall_public_domain);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            mGrassDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.noisy_grass_public_domain);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            mPaperDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.paper);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            // Initialize the accumulated rotation matrix
            Matrix.setIdentityM(mAccumulatedRotation, 0);
    }


    // ------------------------------------------------------------------------
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // called after the surface is created and whenever the OpenGL ES surface size changes

        // Adjust the viewport based on geometry changes,
        // such as screen rotation

        /*GLES20.glViewport(0, 0, width, height);

        this.myViewPortHeight = height;
        this.myViewPortWidth = width;


        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        */

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        GLES20.glViewport(0, 0, width, height);
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }




    // ------------------------------------------------------------------------
    @Override
    public void onDrawFrame(GL10 unused) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        long slowTime = SystemClock.uptimeMillis() % 100000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);


        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 3.5f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);



        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // xdxd
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        this.currentTime = seconds;
        this.previousTime = seconds - 1;


        int xmax = 3;
        int speedmax = 5;
        int zmax = 3;
        // Set cubes if the cubes array doesnt have any cubes in it.


        if (playercube==null){
            playercube = new Cube(0, -1, 0, 0);
        }

        //xdxd
        for (int i=0; i<cubes.length; i++){

            if (cubes[i] != null && cubes[i].y <= 0){
                cubes[i] = null;
            }

            if (cubes[i] == null){
                Random rand = new Random();
                int x = rand.nextInt(xmax*2) - (1 - rand.nextInt(2))*xmax;
                int z = 0 - rand.nextInt(zmax) * 2;
                int y = 8;
                int speed = rand.nextInt(speedmax);
                Random randy = new Random();
                int a = 5 + randy.nextInt(10);
                cubes[i] = new Cube(x, y, z, .3f/a);
            }

        }


        // xdxds
        // Draw the Player

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0,  playercube.x, playercube.y,  playercube.z);
        //Matrix.scaleM(mModelMatrix, 0,  .5f, .5f, .5f);



        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPaperDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);


        drawCube();


        Random r = new Random();
            for (int i=0; i<cubes.length; i++) {
                Cube cube = cubes[i];
                System.out.println("CUBE:");
                System.out.println(cubes[i].x);
                System.out.println(cubes[i].y);
                System.out.println(cubes[i].z);
                System.out.println();

                if (currentTime > previousTime) {
                    cubes[i].y -= cube.speed;
                }



                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0,  cube.x, cube.y,  cube.z);


                //Texture

                // Set the active texture unit to texture unit 0.
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                // Bind the texture to this unit.
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBrickDataHandle);

                // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
                GLES20.glUniform1i(mTextureUniformHandle, 0);

                // Pass in the texture coordinate information
                mCubeTextureCoordinates.position(0);
                GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                        0, mCubeTextureCoordinates);

                GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

                //xdxd
                drawCube();

            }



        if (index_mode==3){}

        else if(index_mode==4) {
            Matrix.scaleM(mModelMatrix, 0,  1 + mDeltaY * .01f, 1 + mDeltaY * .01f, 1 + mDeltaY * .01f);
        }

        else if(index_mode==5) {


        }

        else{

            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0,  playercube.x, playercube.y,  playercube.z);
            drawCube();
        }




        // Texture plane
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -2.0f, -5.0f);
        Matrix.scaleM(mModelMatrix, 0, 25.0f, 1.0f, 25.0f);
        Matrix.rotateM(mModelMatrix, 0, slowAngleInDegrees, 0.0f, 1.0f, 0.0f);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGrassDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mCubeTextureCoordinatesForPlane.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinatesForPlane);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        drawCube();


        // Draw a point to indicate the light.
        GLES20.glUseProgram(mPointProgramHandle);
        drawLight();

        detectCollision();

    }






    /**
     * Draws a cube.
     */

    private void detectCollision(){

    final Random rand = new Random();

        if (currentTime > 2) {
            for (Cube cube : cubes) {
                if ((Math.abs(cube.x - playercube.x) <= 1) && (Math.abs(cube.y - playercube.y) <= 1) && (Math.abs(cube.z - playercube.z) == 0)) {
                    ((Activity) mActivityContext).runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            Toast.makeText(mActivityContext, "You Lost, Resetting...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    reset();

            }
        }

        }}


    private void reset(){
        for (int i=0; i< cubes.length; i++){
            cubes[i].y = 14;
        }
    }
    private void drawCube()
    {
        // Pass in the position information
        mCubePositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mCubePositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the normal information
        mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mCubeNormals);


        GLES20.glEnableVertexAttribArray(mNormalHandle);


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

		/* Mine */

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }


    private void drawAxis(){
        mAxisPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mAxisPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);


        // Draw the Axis.
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
    }


    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }



    public void topSelectionChanged(int index) {
        System.out.println("topSelectionChanged() ---------------------------------- ");
        System.out.println("topSelectionChanged() - received " + index);
        System.out.println("topSelectionChanged() ---------------------------------- ");

        if(index == 0 && playercube.x <= 4 && playercube.x > -4)
        {
            playercube.x -= 1;
        }

        else if(index == 3 && playercube.x >= -4 && playercube.x < 4)
        {
            playercube.x += 1;
        }
        else if(index == 5 && playercube.z < 0)
        {
            playercube.z += 1;
        }
        else if(index == 6 && playercube.z > -2)
        {
            playercube.z -= 1;
        }


    }

    public void bottomSelectionChanged(int index) {
        System.out.println("bottomSelectionChanged() ---------------------------------- ");
        System.out.println("bottomSelectionChanged() - received " + index);
        System.out.println("bottomSelectionChanged() ---------------------------------- ");
    }


}
