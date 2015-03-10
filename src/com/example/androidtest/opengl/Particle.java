package com.example.androidtest.opengl;

public class Particle {
	boolean active; // 是否激活状态
	float live;// 粒子生命
	float fade; // 衰减速度

	float r; // 红色值
	float g; // 绿色值
	float b; // 蓝色值

	// 变量x.y和z控制粒子在屏幕上显示的位置.

	float x; // x位置
	float y; // y位置
	float z; // z位置

	// 这三个变量控制粒子在每个轴上移动的快慢和方向.如果xi是负价粒
	// 子将会向左移动,正值将会向右移动.

	float xi; // x方向
	float yi; // y方向
	float zi; // z方向
	/**
	 * 每一个变量可被看成加速度.如果xg正值时,粒子将会被拉倒右边,负值
	 * 将拉向左边.所以如果粒子向左移动(负的)而我们给它一个正的加速度,粒子速度将变慢.
	 */
	float xg; // x方向重力加速度
	float yg; // y方向重力加速度
	float zg; // z方向重力加速度
}
