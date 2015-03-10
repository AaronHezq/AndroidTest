package com.example.androidtest.opengl;

import java.nio.ByteBuffer;  
import java.nio.ByteOrder;  
import java.nio.FloatBuffer;  
import java.util.Random;  
  
import javax.microedition.khronos.egl.EGLConfig;  
import javax.microedition.khronos.opengles.GL10;  
  
import android.graphics.Bitmap;  
import android.opengl.GLUtils;  
import android.opengl.GLSurfaceView.Renderer; 
/**
 * ����ϵͳЧ��ʵ����
 * 
 * date:2012-07-03
 */
public class ParticleRenderer implements Renderer {

	private int[] textures = new int[1];
	private Bitmap bitmap = initBitmap.bitmap;
	private static final int MAX_PARTICLE = 1000; // �����
	private boolean rainbow = true; // �Ƿ�Ϊ�ʺ�ģʽ
	private float slowdown = 2.0f;// ��������
	private float xspeed; // x�����ϵ��ٶ�
	private float yspeed;// y�����ϵ��ٶ�
	private float zoom = -30.0f;// ��Z������
	private int loop; // ѭ������
	private int color; // ��ǰ����ɫ
	private int delay; // �ʺ�Ч���ӳ�
	private Random random = new Random();
	// ����һ����������
	private Particle particles[] = new Particle[MAX_PARTICLE];
	// �洢12�в�ͬ����ɫ.��ÿһ����ɫ��0��11
	private float colors[][] = { { 1.0f, 0.5f, 0.5f }, { 1.0f, 0.75f, 0.5f }, { 1.0f, 1.0f, 0.5f }, { 0.75f, 1.0f, 0.5f },
			{ 0.5f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.75f }, { 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f }, { 0.5f, 0.5f, 1.0f },
			{ 0.75f, 0.5f, 1.0f }, { 1.0f, 0.5f, 1.0f }, { 1.0f, 0.5f, 0.75f } };

	FloatBuffer vertexBuffer;
	FloatBuffer coordBuffer;
	private float[] vertexs = new float[12];
	private float[] coords = new float[8];

	// ��ʼ������
	public void initBuffer() {
		ByteBuffer verbb = ByteBuffer.allocateDirect(vertexs.length * 4);
		verbb.order(ByteOrder.nativeOrder());
		vertexBuffer = verbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);

		ByteBuffer coordbb = ByteBuffer.allocateDirect(coords.length * 4);
		coordbb.order(ByteOrder.nativeOrder());
		coordBuffer = coordbb.asFloatBuffer();
		coordBuffer.put(coords);
		coordBuffer.position(0);
	}

	// �������0��999��
	public int rand() {
		return Math.abs(random.nextInt(1000));
	}

	// ��ʼ������
	public void initParticle(int num, int color, float xDir, float yDir, float zDir) {
		Particle par = new Particle();
		par.active = true;
		par.live = 1.0f;
		par.fade = rand() % 100 / 1000.0f + 0.003f;
		// ���Ƿ�������һ���µ���ɫ.
		par.r = colors[color][0];
		par.g = colors[color][1];
		par.b = colors[color][2];

		// �����Ӵ�������֮��,�������µ��ƶ��ٶ�/����
		par.xi = xDir;
		par.yi = yDir;
		par.zi = zDir;

		par.xg = 0.0f;
		par.yg = -0.5f;
		// zg
		par.zg = 0.0f;

		particles[loop] = par;
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		initBuffer();// ��ʼ��

		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// �������������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordBuffer);

		for (loop = 0; loop < MAX_PARTICLE; loop++) {
			if (particles[loop].active) { // ����
				// ����X���λ��
				float x = particles[loop].x;
				// ����Y���λ��
				float y = particles[loop].y;
				// ����Z���λ��,zoom����ԭ�ӽǻ����ϼ���zoom
				float z = particles[loop].z + zoom;
				// ����������ɫ
				gl.glColor4f(particles[loop].r, particles[loop].g, particles[loop].b, particles[loop].live);

				coordBuffer.clear();
				vertexBuffer.clear();

				coordBuffer.put(1.0f);
				coordBuffer.put(1.0f);
				vertexBuffer.put(x + 0.5f);
				vertexBuffer.put(y + 0.5f);
				vertexBuffer.put(z);

				coordBuffer.put(1.0f);
				coordBuffer.put(0.0f);
				vertexBuffer.put(x + 0.5f);
				vertexBuffer.put(y);
				vertexBuffer.put(z);

				coordBuffer.put(0.0f);
				coordBuffer.put(1.0f);
				vertexBuffer.put(x);
				vertexBuffer.put(y + 0.5f);
				vertexBuffer.put(z);

				coordBuffer.put(0.0f);
				coordBuffer.put(0.0f);
				vertexBuffer.put(x);
				vertexBuffer.put(y);
				vertexBuffer.put(z);
				// ����
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				// ����Ҳ�����趨��������Ļ���ķ���.���������趨���ӵ�x,y��zλ��Ϊ��
				particles[loop].x += particles[loop].xi / (slowdown * 1000);
				// ����Y�����λ��
				particles[loop].y += particles[loop].yi / (slowdown * 1000);
				// ����Z�����λ��
				particles[loop].z += particles[loop].zi / (slowdown * 1000);

				// ����X�᷽���ٶȴ�С
				particles[loop].xi += particles[loop].xg;
				// ����Y�᷽���ٶȴ�С
				particles[loop].yi += particles[loop].yg;
				// ����Z�᷽���ٶȴ�С
				particles[loop].zi += particles[loop].zg;

				// �������ӵ�����ֵ
				particles[loop].live -= particles[loop].fade;

				// �����������С��0
				if (particles[loop].live < 0.0f) {

					particles[loop] = new Particle();
					particles[loop].active = true;
					particles[loop].live = 1.0f;
					particles[loop].fade = (float) (rand() % 100) / 1000.0f + 0.003f;
					// ����Ҳ�����趨��������Ļ���ķ���.���������趨���ӵ�x,y��zλ��Ϊ��
					particles[loop].x = 0.0f;
					particles[loop].y = 0.0f;
					particles[loop].z = 0.0f;
					// �����Ӵ�������֮��,�������µ��ƶ��ٶ�/����
					particles[loop].xi = xspeed + (float) ((rand() % 50) - 33.0f) * 12.0f;// x����
					particles[loop].yi = yspeed + (float) ((rand() % 50) - 33.0f) * 12.0f;// y����
					particles[loop].zi = (float) ((rand() % 50) - 33.0f) * 12.0f; // z����
					// ������Ƿ�������һ���µ���ɫ.
					particles[loop].r = colors[color][0];
					particles[loop].g = colors[color][1];
					particles[loop].b = colors[color][2];

				}

			}
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glFinish();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// ���ó�����С
		gl.glViewport(0, 0, width, height);
		if (height == 0) {
			height = 1;
		}
		float ratio = (float) width / height;
		// ͶӰ����
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// ������ͼ
		gl.glLoadIdentity();
		// ������ͼ�Ĵ�С
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 200);
		// ���ù۲�ģ��
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// ��ɫ����ɫ
		gl.glClearColorx(0, 0, 0, 0);
		// ������Ӱƽ��
		gl.glShadeModel(GL10.GL_SMOOTH);
		// ע�⣺�ر���Ȳ���
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// ���û��
		gl.glEnable(GL10.GL_BLEND);

		// ��������
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// ��������
		gl.glGenTextures(1, textures, 0);
		// ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		// ��������
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// �����˲�
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);// �Ŵ�ʱ
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);// ��Сʱ

		/**
		 * ��ʼ�����е�����
		 */
		for (loop = 0; loop < MAX_PARTICLE; loop++) {

			particles[loop] = new Particle();
			particles[loop].active = true; // ����
			particles[loop].live = 1.0f; // ��ʼ����Ϊ1
			particles[loop].fade = (float) (rand() % 100) / 1000.0f + 0.003f;// �������˥������
			particles[loop].r = colors[loop * (12 / MAX_PARTICLE)][0];
			particles[loop].g = colors[loop * (12 / MAX_PARTICLE)][1];
			particles[loop].b = colors[loop * (12 / MAX_PARTICLE)][2];

			particles[loop].xi = (float) ((rand() % 50) - 26.0f) * 12.0f;// x����
			particles[loop].yi = (float) ((rand() % 50) - 25.0f) * 12.0f;// y����
			particles[loop].zi = (float) ((rand() % 50) - 25.0f) * 12.0f;// z����

			particles[loop].xg = 0.0f; // x�����ϵļ��ٶ�
			particles[loop].yg = -0.9f;// y�����ϵļ��ٶ�
			particles[loop].zg = 0.0f;// z�����ϵļ��ٶ�

		}
	}

}