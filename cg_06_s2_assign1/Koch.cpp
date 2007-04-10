
#include <GL/glut.h>
#include <math.h>
#include <stdbool.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#define Pi 3.14159265

//
// Forward declarations
//
void DrawScene();
void DrawTriangle();
void DrawClickedPoint(int x, int y);
void AdjustPoints();
void PointsSwap();
void DrawKoch(float* vx, float* vy, double angle, int times);
void MouseClickFunc(int button, int state, int x, int y);
void CompTriCenter();
void ProcessArrowKey(int Key, int x, int y);
void renderBitmapString(float x, float y, void *font, char *string);
void sleepControl();


//Global variables
int PointsCounter = 0;
float ClickedPoints[3][2] = {{0, 0}, {0, 0}, {0, 0}};
float TriCenter[2] = {0, 0};
int RotationAngle = 0;
int RotationLeftRate, RotationRightRate = 0;
bool TurnLeft = false;
bool TurnRight = false;
bool NextRotation = false;


//
// Initialisation functions
//
static void DrawInit() 
{
	// OpenGL initialisation
	glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
}

static void CameraInit () 
{
    // Orthographic view
	glMatrixMode(GL_PROJECTION);
    gluOrtho2D( 0.0, 640.0, 480.0, 0.0 );

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

//
// Callbacks
//
void DisplayFunc () 
{
	glClear(GL_COLOR_BUFFER_BIT);
	glLoadIdentity();
	DrawScene();
	glutSwapBuffers();
}

void KeyboardFunc(unsigned char key, int x, int y) 
{
	switch(key) 
	{
		case 27: // escape key pressed
			exit(0);
	}
}	

void IdleFunc () 
{
	glutPostRedisplay();
}

// Notifying the press of arrow key by modifying the relavent variables.
void ProcessArrowKey(int Key, int x, int y)
{
	switch(Key){
		case GLUT_KEY_LEFT :
			if (TurnRight == true)
				TurnRight = false;
			TurnLeft = true;
			NextRotation = true;
			RotationLeftRate++;
			RotationRightRate = 0;
			break;
		case GLUT_KEY_RIGHT :
			if (TurnLeft == true)
				TurnLeft = false;
			TurnRight = true;
			NextRotation = true;
			RotationRightRate++;
			RotationLeftRate = 0;
			break;
	}			
}

/*Record the clicked points to the array */
void DrawClickedPoint(int x, int y)
{
	if (PointsCounter == 3){
		PointsCounter = 1;
	}else
		PointsCounter++;
		
	switch (PointsCounter){
		case 1:
			ClickedPoints[0][0] = x;
			ClickedPoints[0][1] = 480 - y;
			glBegin(GL_POINT);
				glColor3f(0, 255, 0);
				glVertex2fv(ClickedPoints[0]);
			glEnd();
			TurnRight = false;
			TurnLeft = false;
			RotationLeftRate = 0;
			RotationRightRate = 0;
			break;
		case 2:
			ClickedPoints[1][0] = x;
			ClickedPoints[1][1] = 480 - y;			
			glBegin(GL_POINT);
				glColor3f(255, 255, 0);
				glVertex2fv(ClickedPoints[1]);
			glEnd();
			TurnRight = false;
			TurnLeft = false;
			RotationLeftRate = 0;
			RotationRightRate = 0;
			break;
		case 3:
			ClickedPoints[2][0] = x;
			ClickedPoints[2][1] = 480 - y;
			glBegin(GL_POINT);
				glColor3f(0, 0, 255);
				glVertex2fv(ClickedPoints[2]);
			glEnd();
			AdjustPoints();
			CompTriCenter();
			break;
	}
}

/* Ensure that the points in the array are stored in the clockwise order */
void AdjustPoints()
{
	
	if( (ClickedPoints[0][0] < ClickedPoints[1][0]) && (ClickedPoints[0][0] < ClickedPoints[2][0]) ){
		if( (ClickedPoints[1][1] < ClickedPoints[2][1]) )
			PointsSwap();
		return;
	}
	
	if( (ClickedPoints[0][0] > ClickedPoints[1][0]) && (ClickedPoints[0][0] > ClickedPoints[2][0]) ){
		if( (ClickedPoints[1][1] > ClickedPoints[2][1]) )
			PointsSwap();
		return;
	}
	
	if( (ClickedPoints[0][0] == ClickedPoints[1][0]) && (ClickedPoints[0][1] > ClickedPoints[1][1]) ){
		PointsSwap();
		return;
	}
		
	if( (ClickedPoints[0][0] == ClickedPoints[2][0]) && (ClickedPoints[0][1] < ClickedPoints[2][1]) ){
		PointsSwap();
		return;
	}
	
	if( (ClickedPoints[0][0] > ClickedPoints[1][0]) && (ClickedPoints[0][0] < ClickedPoints[2][0])){
		PointsSwap();
		return;
	}		
}

/* Swap the 2nd and 3nd Points' coordinates in case of forming counter-clockwise triangles */
void PointsSwap()
{
	float tempPoint[2] = {0, 0};
	
	tempPoint[0] = ClickedPoints[1][0];
	tempPoint[1] = ClickedPoints[1][1];
	ClickedPoints[1][0] = ClickedPoints[2][0];
	ClickedPoints[1][1] = ClickedPoints[2][1];
	ClickedPoints[2][0] = tempPoint[0];
	ClickedPoints[2][1] = tempPoint[1];
}

void MouseClickFunc(int button, int state, int x, int y)
{
	if ((button==GLUT_LEFT_BUTTON || button==GLUT_RIGHT_BUTTON) && (state == GLUT_UP)){
		DrawClickedPoint(x, y);
//	printf("intx: %d, int y: %d PG: %d! ", x, y, PointsCounter );
	}
}


//
// Draw the scene
//
void DrawScene() 
{
	renderBitmapString(20, 20, GLUT_BITMAP_HELVETICA_18, "Press <- or -> ro rotate the object.");
	renderBitmapString(20, 40, GLUT_BITMAP_HELVETICA_18, "Repeatedly press will increase the rotating speed.");
	renderBitmapString(20, 60, GLUT_BITMAP_HELVETICA_18, "Press Esc to exit.ENJOY ^_^");
	if (PointsCounter == 3 && TurnLeft == true){
//		DrawTriangle();
/*		glBegin(GL_LINE_LOOP);
			glColor3f(0, 255, 255);
			glVertex2f(ClickedPoints[0][0], 480 - ClickedPoints[0][1]);
			glVertex2f(ClickedPoints[1][0], 480 - ClickedPoints[1][1]);
			glVertex2f(ClickedPoints[2][0], 480 - ClickedPoints[2][1]);
		glEnd();
*/		
		glLoadIdentity();
		glTranslatef(TriCenter[0], TriCenter[1], 0);
		glRotatef(-RotationAngle, 0.0, 0.0, 0.000001);
		glTranslatef(-TriCenter[0], -TriCenter[1], 0);
		sleepControl();
		DrawKoch(ClickedPoints[0], ClickedPoints[1], Pi/3, 4);
		DrawKoch(ClickedPoints[1], ClickedPoints[2], Pi/3, 4);
		DrawKoch(ClickedPoints[2], ClickedPoints[0], Pi/3, 4);
	}
	
	if (PointsCounter == 3 && TurnRight == true){
		glLoadIdentity();
		glTranslatef(TriCenter[0], TriCenter[1], 0);
		glRotatef(RotationAngle, 0.0, 0.0, 1.0);
		glTranslatef(-TriCenter[0], -TriCenter[1], 0);
		sleepControl();
		DrawKoch(ClickedPoints[0], ClickedPoints[1], Pi/3, 4);
		DrawKoch(ClickedPoints[1], ClickedPoints[2], Pi/3, 4);
		DrawKoch(ClickedPoints[2], ClickedPoints[0], Pi/3, 4);
	}
	
	if (PointsCounter == 3 && TurnRight == false && TurnLeft == false){
//		renderBitmapString(20, 20, GLUT_BITMAP_HELVETICA_18, "Press <- or -> ro rotate the object.");
// 		renderBitmapString(20, 40, GLUT_BITMAP_HELVETICA_18, "Repeatedly press will increase the rotating speed.");
//		renderBitmapString(20, 50, GLUT_BITMAP_HELVETICA_18, "Press Esc to exit.ENJOY ^_^");

		DrawKoch(ClickedPoints[0], ClickedPoints[1], Pi/3, 4);
		DrawKoch(ClickedPoints[1], ClickedPoints[2], Pi/3, 4);
		DrawKoch(ClickedPoints[2], ClickedPoints[0], Pi/3, 4);
	}		
}

/* this section of codes should be isolated from DrawKoch, since the DrawKoch 
 * recursive function, resulting in invoking usleep() in succssion and sleep the routine forever. */
void sleepControl(){
	if ( TurnLeft == true ){
		if (RotationLeftRate > 30)
			RotationLeftRate = 30;
		usleep(1000000/RotationLeftRate);
	}
	if ( TurnRight == true ){
		if (RotationRightRate > 30)
			RotationRightRate = 30;
		 usleep(1000000/RotationRightRate);	
	}
	
	if (RotationAngle > 360)
		RotationAngle = 0;
	RotationAngle = 30 + RotationAngle;
}


void DrawKoch(float* vx, float* vy, double angle, int times)
{
	float Vsnow[3][2] = {{0, 0}, {0, 0}, {0, 0}};
	
	if( times == 0 ){
		glBegin(GL_LINES);
			glColor3f(0, 255, 0);
			glVertex2f(vx[0], 480 - vx[1]);
			glVertex2f(vy[0], 480 - vy[1]);
		glEnd();
	}else{
		times--;
		/* Compute the 1st and 3rd Point along the input line */
		if ((vx[0] <= vy[0]) && (vx[1] <= vy[1])){
			Vsnow[0][0] = vx[0] + (vy[0] - vx[0])/3;
			Vsnow[2][0] = vx[0] + 2*(vy[0] - vx[0])/3;
			Vsnow[0][1] = vx[1] + (vy[1] - vx[1])/3;
			Vsnow[2][1] = vx[1] + 2*(vy[1] - vx[1])/3;			
		}
		
		if ((vx[0] <= vy[0]) && (vx[1] >=  vy[1])){
			Vsnow[0][0] = vx[0] + (vy[0] - vx[0])/3;
			Vsnow[2][0] = vx[0] + 2*(vy[0] - vx[0])/3;
			Vsnow[0][1] = vy[1] + 2*(vx[1] - vy[1])/3;
			Vsnow[2][1] = vy[1] + (vx[1] - vy[1])/3;			
		}
		
		if ((vx[0] >= vy[0]) && (vx[1] >= vy[1])){
			Vsnow[0][0] = vy[0] + 2*(vx[0] - vy[0])/3;
			Vsnow[2][0] = vy[0] + (vx[0] - vy[0])/3;
			Vsnow[0][1] = vy[1] + 2*(vx[1] - vy[1])/3;
			Vsnow[2][1] = vy[1] + (vx[1] - vy[1])/3;			
		}

		if ((vx[0] >= vy[0]) && (vx[1] <= vy[1])){
			Vsnow[0][0] = vy[0] + 2*(vx[0] - vy[0])/3;
			Vsnow[2][0] = vy[0] + (vx[0] - vy[0])/3;
			Vsnow[0][1] = vx[1] + (vy[1] - vx[1])/3;
			Vsnow[2][1] = vx[1] + 2*(vy[1] - vx[1])/3;			
		}
		
		/* Compute the coordinate of the Middle Point*/		
		Vsnow[1][0] = Vsnow[2][0]/2 - Vsnow[2][1]*(sqrt(3)/2) + Vsnow[0][0]/2 \
				+ Vsnow[0][1]*(sqrt(3)/2);

		Vsnow[1][1] = Vsnow[2][0]*(sqrt(3)/2) + Vsnow[2][1]/2 + Vsnow[0][1]/2 \
				- Vsnow[0][0]*(sqrt(3)/2);

		DrawKoch(vx, Vsnow[0], Pi/3, times);
		DrawKoch(Vsnow[0], Vsnow[1], Pi/3, times);
		DrawKoch(Vsnow[1], Vsnow[2], Pi/3, times);
		DrawKoch(Vsnow[2], vy, Pi/3, times);
		
	}
			
}

void DrawTriangle()
{
	glClear(GL_COLOR_BUFFER_BIT);
	glBegin(GL_TRIANGLES);
		glColor3f(0, 255, 255);
		glVertex2f(ClickedPoints[0][0], 480 - ClickedPoints[0][1]);
//		glColor3f(255, 255, 0);
		glVertex2f(ClickedPoints[1][0], 480 - ClickedPoints[1][1]);
//		glColor3f(0, 0, 255);
		glVertex2f(ClickedPoints[2][0], 480 - ClickedPoints[2][1]);
	glEnd();
	glutSwapBuffers();
	
}

// To simplify the algorithm, the triangle center is computed with inaccurate approach 
void CompTriCenter()
{	
	TriCenter[0] = (ClickedPoints[0][0] + ClickedPoints[1][0])/2;
	TriCenter[1] = (ClickedPoints[0][1] + ClickedPoints[1][1])/2;
	
	TriCenter[0] = (TriCenter[0] + ClickedPoints[2][0])/2;
	TriCenter[1] = (TriCenter[1] + ClickedPoints[2][1])/2;	
}

void renderBitmapString(float x, float y, void *font, char *string) {  
  char *c;
  glRasterPos2f(x, y);
  for (c=string; *c != '\0'; c++) {
    glutBitmapCharacter(font, *c);
  }
}

int main (int argc, char **argv)
{
	// Glut Init
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE);
	glutInitWindowPosition(200, 0);
	glutInitWindowSize(640, 480);
	glutCreateWindow("Rotatable Koch snowflake. <Click mouse to form Tri>");

	// Set Callbacks
	glutDisplayFunc(DisplayFunc);
	glutKeyboardFunc(KeyboardFunc);
	glutMouseFunc(MouseClickFunc);
	glutSpecialFunc(ProcessArrowKey);
	glutIdleFunc(IdleFunc);
	
	// Program Inits
	DrawInit();
	CameraInit();

	// Go!
	glutSwapBuffers();

	glutMainLoop();

	return 0;
}
