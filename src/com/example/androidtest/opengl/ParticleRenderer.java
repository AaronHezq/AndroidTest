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
 * 粒子系统效果实现类
 * 
 * date:2012-07-03
 */
public class ParticleRenderer implements Renderer {

	private int[] textures = new int[1];
	private Bitmap bitmap = initBitmap.bitmap;
	private static final int MAX_PARTICLE = 1000; // 最大数
	private boolean rainbow = true; // 是否为彩虹模式
	private float slowdown = 2.0f;// 减速粒子
	private float xspeed; // x方向上的速度
	private float yspeed;// y方向上的速度
	private float zoom = -30.0f;// 沿Z轴缩放
	private int loop; // 循环变量
	private int color; // 当前的颜色
	private int delay; // 彩虹效果延迟
	private Random random = new Random();
	// 创建一个粒子数组
	private Particle particles[] = new Particle[MAX_PARTICLE];
	// 存储12中不同的颜色.对每一个颜色从0到11
	private float colors[][] = { { 1.0f, 0.5f, 0.5f }, { 1.0f, 0.75f, 0.5f }, { 1.0f, 1.0f, 0.5f }, { 0.75f, 1.0f, 0.5f },
			{ 0.5f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.75f }, { 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f }, { 0.5f, 0.5f, 1.0f },
			{ 0.75f, 0.5f, 1.0f }, { 1.0f, 0.5f, 1.0f }, { 1.0f, 0.5f, 0.75f } };

	FloatBuffer vertexBuffer;
	FloatBuffer coordBuffer;
	private float[] vertexs = new float[12];
	private float[] coords = new float[8];

	// 初始化缓冲
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

	// 随机生成0～999数
	public int rand() {
		return Math.abs(random.nextInt(1000));
	}

	// 初始化粒子
	public void initParticle(int num, int color, float xDir, float yDir, float zDir) {
		Particle par = new Particle();
		par.active = true;
		par.live = 1.0f;
		par.fade = rand() % 100 / 1000.0f + 0.003f;
		// 我们分配粒子一种新的颜色.
		par.r = colors[color][0];
		par.g = colors[color][1];
		par.b = colors[color][2];

		// 在粒子从新设置之后,将给它新的移动速度/方向
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

		initBuffer();// 初始化

		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// 顶点和纹理设置
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordBuffer);

		for (loop = 0; loop < MAX_PARTICLE; loop++) {
			if (particles[loop].active) { // 激活
				// 返回X轴的位置
				float x = particles[loop].x;
				// 返回Y轴的位置
				float y = particles[loop].y;
				// 返回Z轴的位置,zoom则在原视角基础上加上zoom
				float z = particles[loop].z + zoom;
				// 设置粒子颜色
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
				// 绘制
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				// 我们也重新设定粒子在屏幕中心放置.我们重新设定粒子的x,y和z位置为零
				particles[loop].x += particles[loop].xi / (slowdown * 1000);
				// 更新Y坐标的位置
				particles[loop].y += particles[loop].yi / (slowdown * 1000);
				// 更新Z坐标的位置
				particles[loop].z += particles[loop].zi / (slowdown * 1000);

				// 更新X轴方向速度大小
				particles[loop].xi += particles[loop].xg;
				// 更新Y轴方向速度大小
				particles[loop].yi += particles[loop].yg;
				// 更新Z轴方向速度大小
				particles[loop].zi += particles[loop].zg;

				// 减少粒子的生命值
				particles[loop].live -= particles[loop].fade;

				// 如果粒子生命小于0
				if (particles[loop].live < 0.0f) {

					particles[loop] = new Particle();
					particles[loop].active = true;
					particles[loop].live = 1.0f;
					particles[loop].fade = (float) (rand() % 100) / 1000.0f + 0.003f;
					// 我们也重新设定粒子在屏幕中心放置.我们重新设定粒子的x,y和z位置为零
					particles[loop].x = 0.0f;
					particles[loop].y = 0.0f;
					particles[loop].z = 0.0f;
					// 在粒子从新设置之后,将给它新的移动速度/方向
					particles[loop].xi = xspeed + (float) ((rand() % 50) - 33.0f) * 12.0f;// x方向
					particles[loop].yi = yspeed + (float) ((rand() % 50) - 33.0f) * 12.0f;// y方向
					particles[loop].zi = (float) ((rand() % 50) - 33.0f) * 12.0f; // z方向
					// 最后我们分配粒子一种新的颜色.
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
		// 设置场景大小
		gl.glViewport(0, 0, width, height);
		if (height == 0) {
			height = 1;
		}
		float ratio = (float) width / height;
		// 投影矩阵
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 重置视图
		gl.glLoadIdentity();
		// 设置视图的大小
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 200);
		// 设置观察模型
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// 黑色背景色
		gl.glClearColorx(0, 0, 0, 0);
		// 启用阴影平滑
		gl.glShadeModel(GL10.GL_SMOOTH);
		// 注意：关闭深度测试
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// 启用混合
		gl.glEnable(GL10.GL_BLEND);

		// 启用纹理
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 创建纹理
		gl.glGenTextures(1, textures, 0);
		// 绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		// 生成纹理
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// 线性滤波
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);// 放大时
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);// 缩小时

		/**
		 * 初始化所有的粒子
		 */
		for (loop = 0; loop < MAX_PARTICLE; loop++) {

			particles[loop] = new Particle();
			particles[loop].active = true; // 激活
			particles[loop].live = 1.0f; // 开始生命为1
			particles[loop].fade = (float) (rand() % 100) / 1000.0f + 0.003f;// 随机生成衰减速率
			particles[loop].r = colors[loop * (12 / MAX_PARTICLE)][0];
			particles[loop].g = colors[loop * (12 / MAX_PARTICLE)][1];
			particles[loop].b = colors[loop * (12 / MAX_PARTICLE)][2];

			particles[loop].xi = (float) ((rand() % 50) - 26.0f) * 12.0f;// x方向
			particles[loop].yi = (float) ((rand() % 50) - 25.0f) * 12.0f;// y方向
			particles[loop].zi = (float) ((rand() % 50) - 25.0f) * 12.0f;// z方向

			particles[loop].xg = 0.0f; // x方向上的加速度
			particles[loop].yg = -0.9f;// y方向上的加速度
			particles[loop].zg = 0.0f;// z方向上的加速度

		}
	}

}