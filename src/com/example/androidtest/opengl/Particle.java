package com.example.androidtest.opengl;

public class Particle {
	boolean active; // �Ƿ񼤻�״̬
	float live;// ��������
	float fade; // ˥���ٶ�

	float r; // ��ɫֵ
	float g; // ��ɫֵ
	float b; // ��ɫֵ

	// ����x.y��z������������Ļ����ʾ��λ��.

	float x; // xλ��
	float y; // yλ��
	float z; // zλ��

	// ��������������������ÿ�������ƶ��Ŀ����ͷ���.���xi�Ǹ�����
	// �ӽ��������ƶ�,��ֵ���������ƶ�.

	float xi; // x����
	float yi; // y����
	float zi; // z����
	/**
	 * ÿһ�������ɱ����ɼ��ٶ�.���xg��ֵʱ,���ӽ��ᱻ�����ұ�,��ֵ
	 * ���������.����������������ƶ�(����)�����Ǹ���һ�����ļ��ٶ�,�����ٶȽ�����.
	 */
	float xg; // x�����������ٶ�
	float yg; // y�����������ٶ�
	float zg; // z�����������ٶ�
}
